package com.examify.service;

import com.examify.exception.TimeOutException;
import com.examify.exception.TokenTidakValidException;
import com.examify.io.BankSoalWriter;
import com.examify.model.HasilUjian;
import com.examify.model.ModeSubmit;
import com.examify.model.Siswa;
import com.examify.model.Ujian;
import com.examify.utils.AppConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Use case ujian: katalog, verifikasi token, memulai, dan menyelesaikan ujian. */
public class UjianService {

    private final Map<String, Ujian> katalog = new LinkedHashMap<>();
    private final Map<String, Integer> percobaanGagal = new HashMap<>();
    private final Map<String, Instant> terkunciSampai = new HashMap<>();
    private final RekapService rekapService;
    private final Clock clock;
    private final BankSoalWriter writer;
    private final Path fileBankSoal;

    public UjianService(List<Ujian> daftarUjian, RekapService rekapService) {
        this(daftarUjian, rekapService, Clock.systemDefaultZone(), null, null);
    }

    public UjianService(List<Ujian> daftarUjian, RekapService rekapService, Clock clock) {
        this(daftarUjian, rekapService, clock, null, null);
    }

    public UjianService(List<Ujian> daftarUjian, RekapService rekapService, Clock clock,
                        BankSoalWriter writer, Path fileBankSoal) {
        daftarUjian.forEach(ujian -> katalog.put(ujian.getKodeUjian(), ujian));
        this.rekapService = rekapService;
        this.clock = clock;
        this.writer = writer;
        this.fileBankSoal = fileBankSoal;
    }

    public List<String> getDaftarMapel() {
        return katalog.values().stream()
                .map(Ujian::getMataPelajaran)
                .distinct()
                .toList();
    }

    public List<Ujian> getUjianByMapel(String mataPelajaran) {
        return katalog.values().stream()
                .filter(ujian -> ujian.getMataPelajaran().equals(mataPelajaran))
                .toList();
    }

    public List<Ujian> getDaftarUjian() {
        return List.copyOf(katalog.values());
    }

    public Optional<Ujian> cariUjian(String kodeUjian) {
        if (kodeUjian == null) return Optional.empty();
        return Optional.ofNullable(katalog.get(kodeUjian.trim().toUpperCase()));
    }

    public int getJumlahUjian() {
        return katalog.size();
    }

    public synchronized void perbaruiUjian(Ujian ujian) throws IOException {
        katalog.put(ujian.getKodeUjian(), ujian);
        simpanKeFile();
    }

    public synchronized void tambahUjian(Ujian ujian) throws IOException {
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

    /** Ujian boleh dimulai bila belum pernah dikerjakan, atau konfigurasinya ULANG=true. */
    public boolean bolehMulai(Siswa siswa, Ujian ujian) {
        return ujian.isBolehUlang() || !rekapService.sudahMengerjakan(siswa, ujian);
    }

    /**
     * Setelah {@link AppConfig#MAKS_PERCOBAAN_TOKEN} kali salah, token dikunci
     * selama {@link AppConfig#JEDA_RESET_TOKEN_DETIK} detik.
     */
    public void verifikasiToken(Siswa siswa, Ujian ujian, String token) throws TokenTidakValidException {
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
            throw new TokenTidakValidException(0, "Token salah " + AppConfig.MAKS_PERCOBAAN_TOKEN
                    + " kali. Coba lagi setelah " + AppConfig.JEDA_RESET_TOKEN_DETIK + " detik.");
        }
        throw new TokenTidakValidException(sisa, "Token tidak valid. Sisa percobaan: " + sisa);
    }

    private void pastikanTidakTerkunci(String kunci) throws TokenTidakValidException {
        Instant batas = terkunciSampai.get(kunci);
        if (batas == null) {
            return;
        }
        Instant sekarang = clock.instant();
        if (sekarang.isBefore(batas)) {
            long sisaDetik = Duration.between(sekarang, batas).toSeconds() + 1;
            throw new TokenTidakValidException(0, "Token terkunci. Coba lagi dalam " + sisaDetik + " detik.");
        }
        terkunciSampai.remove(kunci);
    }

    /** Memulai sesi ujian siswa dengan soal yang diacak. */
    public HasilUjian mulaiUjian(Siswa siswa, Ujian ujian) {
        return new HasilUjian(siswa, ujian.denganSoalDiacak(), clock);
    }

    /**
     * @throws TimeOutException bila submit manual datang setelah batas waktu;
     *                          pemanggil sebaiknya memakai {@link #selesaikanPaksa(HasilUjian)}
     */
    public HasilUjian selesaikan(HasilUjian hasil, ModeSubmit mode) throws TimeOutException, IOException {
        if (mode == ModeSubmit.MANUAL && hasil.isLewatBatasWaktu()) {
            throw new TimeOutException(hasil.getUjian().getKodeUjian());
        }
        if (hasil.finalisasi(mode)) {
            rekapService.catat(hasil);
        }
        return hasil;
    }

    public void selesaikanPaksa(HasilUjian hasil) throws IOException {
        if (hasil.finalisasi(ModeSubmit.AUTO_SUBMIT)) {
            rekapService.catat(hasil);
        }
    }
}
