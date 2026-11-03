package com.examify.service;

import com.examify.exception.TimeOutException;
import com.examify.exception.TokenTidakValidException;
import com.examify.model.HasilUjian;
import com.examify.model.ModeSubmit;
import com.examify.model.Siswa;
import com.examify.model.Ujian;
import com.examify.utils.AppConfig;

import java.io.IOException;
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

    public UjianService(List<Ujian> daftarUjian, RekapService rekapService) {
        this(daftarUjian, rekapService, Clock.systemDefaultZone());
    }

    public UjianService(List<Ujian> daftarUjian, RekapService rekapService, Clock clock) {
        daftarUjian.forEach(ujian -> katalog.put(ujian.getKodeUjian(), ujian));
        this.rekapService = rekapService;
        this.clock = clock;
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

    public Optional<Ujian> cariUjian(String kodeUjian) {
        return Optional.ofNullable(katalog.get(kodeUjian.trim().toUpperCase()));
    }

    public int getJumlahUjian() {
        return katalog.size();
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

    public HasilUjian mulaiUjian(Siswa siswa, Ujian ujian) {
        return new HasilUjian(siswa, ujian, clock);
    }

    /**
     * @throws TimeOutException bila submit manual datang setelah batas waktu;
     *                          pemanggil sebaiknya memakai {@link #selesaikanPaksa(HasilUjian)}
     */
    public HasilUjian selesaikan(HasilUjian hasil, ModeSubmit mode) throws TimeOutException, IOException {
        if (mode == ModeSubmit.MANUAL && hasil.isLewatBatasWaktu()) {
            throw new TimeOutException(hasil.getUjian().getKodeUjian(), "Submit manual melewati batas waktu");
        }
        return finalisasiDanCatat(hasil, mode);
    }

    public HasilUjian selesaikanPaksa(HasilUjian hasil) throws IOException {
        return finalisasiDanCatat(hasil, ModeSubmit.AUTO_SUBMIT);
    }

    private HasilUjian finalisasiDanCatat(HasilUjian hasil, ModeSubmit mode) throws IOException {
        if (hasil.finalisasi(mode)) { // idempoten: rekap tidak pernah tertulis dua kali
            rekapService.catat(hasil);
        }
        return hasil;
    }
}
