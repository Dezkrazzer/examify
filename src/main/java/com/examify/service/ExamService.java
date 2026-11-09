package com.examify.service;

import com.examify.exception.TimeOutException;
import com.examify.exception.InvalidTokenException;
import com.examify.io.QuestionBankWriter;
import com.examify.model.ExamResult;
import com.examify.model.SubmitMode;
import com.examify.model.Student;
import com.examify.model.Exam;
import com.examify.utils.AppConfig;
import com.examify.utils.FormatUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Use case ujian: katalog, verifikasi token, memulai, dan menyelesaikan ujian. */
public class ExamService {

    private final Map<String, Exam> katalog = new LinkedHashMap<>();
    private final Map<String, Integer> percobaanGagal = new HashMap<>();
    private final Map<String, Instant> terkunciSampai = new HashMap<>();
    private final GradeRecapService rekapService;
    private final Clock clock;
    private final QuestionBankWriter writer;
    private final Path fileBankSoal;

    public ExamService(List<Exam> daftarUjian, GradeRecapService rekapService) {
        this(daftarUjian, rekapService, Clock.systemDefaultZone(), null, null);
    }

    public ExamService(List<Exam> daftarUjian, GradeRecapService rekapService, Clock clock) {
        this(daftarUjian, rekapService, clock, null, null);
    }

    public ExamService(List<Exam> daftarUjian, GradeRecapService rekapService, Clock clock,
                        QuestionBankWriter writer, Path fileBankSoal) {
        daftarUjian.forEach(ujian -> katalog.put(ujian.getKodeUjian(), ujian));
        this.rekapService = rekapService;
        this.clock = clock;
        this.writer = writer;
        this.fileBankSoal = fileBankSoal;
    }

    public List<String> getDaftarMapel() {
        return katalog.values().stream()
                .map(Exam::getMataPelajaran)
                .distinct()
                .toList();
    }

    public List<Exam> getUjianByMapel(String mataPelajaran) {
        return katalog.values().stream()
                .filter(ujian -> ujian.getMataPelajaran().equals(mataPelajaran))
                .toList();
    }

    public List<Exam> getDaftarUjian() {
        return List.copyOf(katalog.values());
    }

    public Optional<Exam> cariUjian(String kodeUjian) {
        if (kodeUjian == null) return Optional.empty();
        return Optional.ofNullable(katalog.get(kodeUjian.trim().toUpperCase()));
    }

    public int getJumlahUjian() {
        return katalog.size();
    }

    public synchronized void perbaruiUjian(Exam ujian) throws IOException {
        katalog.put(ujian.getKodeUjian(), ujian);
        simpanKeFile();
    }

    public synchronized void tambahUjian(Exam ujian) throws IOException {
        if (katalog.containsKey(ujian.getKodeUjian())) {
            throw new IllegalArgumentException("Kode ujian sudah ada: " + ujian.getKodeUjian());
        }
        katalog.put(ujian.getKodeUjian(), ujian);
        simpanKeFile();
    }

    public synchronized void hapusUjian(String kodeUjian) throws IOException {
        if (kodeUjian != null) {
            katalog.remove(kodeUjian.trim().toUpperCase());
            simpanKeFile();
        }
    }

    public synchronized void simpanKeFile() throws IOException {
        if (writer != null && fileBankSoal != null) {
            writer.tulis(fileBankSoal, getDaftarUjian());
        }
    }

    /** Ujian boleh dimulai bila belum pernah dikerjakan (atau ULANG=true) dan waktu akses saat ini dibuka. */
    public boolean bolehMulai(Student siswa, Exam ujian) {
        LocalDateTime sekarang = LocalDateTime.now(clock);
        return (ujian.isBolehUlang() || !rekapService.sudahMengerjakan(siswa, ujian)) && ujian.isAksesDibuka(sekarang);
    }

    /** Menghasilkan pesan status jika akses ujian belum dibuka atau sudah berakhir, atau null jika dibuka. */
    public String getPesanStatusAkses(Exam ujian) {
        LocalDateTime sekarang = LocalDateTime.now(clock);
        if (ujian.isBelumMulai(sekarang)) {
            return "Ujian belum dibuka. Akses dibuka pada " + FormatUtil.formatWaktuAkses(ujian.getWaktuMulai()) + ".";
        }
        if (ujian.isSudahBerakhir(sekarang)) {
            return "Waktu akses ujian telah berakhir pada " + FormatUtil.formatWaktuAkses(ujian.getWaktuSelesai()) + ".";
        }
        return null;
    }

    /**
     * Setelah {@link AppConfig#MAKS_PERCOBAAN_TOKEN} kali salah, token dikunci
     * selama {@link AppConfig#JEDA_RESET_TOKEN_DETIK} detik.
     */
    public void verifikasiToken(Student siswa, Exam ujian, String token) throws InvalidTokenException {
        String kunci = siswa.getNis() + "#" + ujian.getKodeUjian();
        pastikanTidakTerkunci(kunci);
        if (ujian.validasiToken(token)) {
            percobaanGagal.remove(kunci);
            return;
        }
        int gagal = percobaanGagal.merge(kunci, 1, Integer::sum);
        int sisa = AppConfig.MAKS_PERCOBAAN_TOKEN - gagal;
        if (sisa <= 0) {
            percobaanGagal.remove(kunci);
            terkunciSampai.put(kunci, clock.instant().plusSeconds(AppConfig.JEDA_RESET_TOKEN_DETIK));
            throw new InvalidTokenException(0, "Token salah " + AppConfig.MAKS_PERCOBAAN_TOKEN
                    + " kali. Coba lagi setelah " + AppConfig.JEDA_RESET_TOKEN_DETIK + " detik.");
        }
        throw new InvalidTokenException(sisa, "Token tidak valid. Sisa percobaan: " + sisa);
    }

    private void pastikanTidakTerkunci(String kunci) throws InvalidTokenException {
        Instant batas = terkunciSampai.get(kunci);
        if (batas == null) {
            return;
        }
        Instant sekarang = clock.instant();
        if (sekarang.isBefore(batas)) {
            long sisaDetik = Duration.between(sekarang, batas).toSeconds() + 1;
            throw new InvalidTokenException(0, "Token terkunci. Coba lagi dalam " + sisaDetik + " detik.");
        }
        terkunciSampai.remove(kunci);
    }

    /** Memulai sesi ujian siswa dengan soal yang diacak. */
    public ExamResult mulaiUjian(Student siswa, Exam ujian) {
        return new ExamResult(siswa, ujian.denganSoalDiacak(), clock);
    }

    /**
     * @throws TimeOutException bila submit manual datang setelah batas waktu;
     *                          pemanggil sebaiknya memakai {@link #selesaikanPaksa(ExamResult)}
     */
    public ExamResult selesaikan(ExamResult hasil, SubmitMode mode) throws TimeOutException, IOException {
        if (mode == SubmitMode.MANUAL && hasil.isLewatBatasWaktu()) {
            throw new TimeOutException(hasil.getUjian().getKodeUjian());
        }
        if (hasil.finalisasi(mode)) {
            rekapService.catat(hasil);
        }
        return hasil;
    }

    public void selesaikanPaksa(ExamResult hasil) throws IOException {
        if (hasil.finalisasi(SubmitMode.AUTO_SUBMIT)) {
            rekapService.catat(hasil);
        }
    }
}
