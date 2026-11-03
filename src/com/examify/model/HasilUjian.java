package com.examify.model;

import com.examify.exception.JawabanKosongException;
import com.examify.exception.TimeOutException;
import com.examify.utils.AppConfig;
import com.examify.utils.StringUtil;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Lembar jawaban satu siswa pada satu ujian, sekaligus penilaiannya. */
public class HasilUjian implements Gradable, Comparable<HasilUjian> {

    private final Siswa siswa;
    private final Ujian ujian;
    private final Clock clock;
    private final Map<Integer, String> jawaban = new LinkedHashMap<>();
    private final LocalDateTime waktuMulai;
    private final LocalDateTime batasWaktu;

    private double skor;
    private int jumlahBenar;
    private int jumlahSalah;
    private int jumlahKosong;
    private LocalDateTime waktuSelesai;
    private StatusUjian status = StatusUjian.BERLANGSUNG;
    private ModeSubmit modeSubmit;

    public HasilUjian(Siswa siswa, Ujian ujian) {
        this(siswa, ujian, Clock.systemDefaultZone());
    }

    /** Clock dapat diganti agar perilaku batas waktu dapat diuji. */
    public HasilUjian(Siswa siswa, Ujian ujian, Clock clock) {
        this.siswa = siswa;
        this.ujian = ujian;
        this.clock = clock;
        this.waktuMulai = LocalDateTime.now(clock);
        this.batasWaktu = waktuMulai.plusSeconds(ujian.getDurasiDetik() + AppConfig.GRACE_PERIOD_DETIK);
    }

    public void simpanJawaban(int idSoal, String jwb) throws JawabanKosongException, TimeOutException {
        if (status != StatusUjian.BERLANGSUNG || isLewatBatasWaktu()) {
            throw new TimeOutException(ujian.getKodeUjian());
        }
        if (StringUtil.isKosong(jwb)) {
            throw new JawabanKosongException(idSoal);
        }
        jawaban.put(idSoal, jwb.trim());
    }

    public void hapusJawaban(int idSoal) {
        if (status == StatusUjian.BERLANGSUNG) {
            jawaban.remove(idSoal);
        }
    }

    public Optional<String> getJawaban(int idSoal) {
        return Optional.ofNullable(jawaban.get(idSoal));
    }

    public boolean isTerjawab(int idSoal) {
        return jawaban.containsKey(idSoal);
    }

    public int getJumlahTerjawab() {
        return jawaban.size();
    }

    /** @return proporsi soal terjawab, 0.0-1.0 */
    public float getProporsiTerjawab() {
        return (float) jawaban.size() / ujian.getJumlahSoal();
    }

    public boolean isLewatBatasWaktu() {
        return LocalDateTime.now(clock).isAfter(batasWaktu);
    }

    /** Idempoten: hanya panggilan pertama yang memfinalisasi; panggilan berikutnya mengembalikan false. */
    public synchronized boolean finalisasi(ModeSubmit mode) {
        if (status == StatusUjian.SELESAI) {
            return false;
        }
        status = StatusUjian.SELESAI;
        modeSubmit = mode;
        waktuSelesai = LocalDateTime.now(clock);
        hitungNilaiAkhir();
        return true;
    }

    @Override
    public double hitungNilaiAkhir() {
        double bobotBenar = 0;
        double totalBobot = 0;
        jumlahBenar = jumlahSalah = jumlahKosong = 0;
        for (Soal soal : ujian.getDaftarSoal()) {
            totalBobot += soal.getBobot();
            switch (nilaiJawaban(soal)) {
                case BENAR -> {
                    bobotBenar += soal.getBobot();
                    jumlahBenar++;
                }
                case SALAH -> jumlahSalah++;
                case KOSONG -> jumlahKosong++;
            }
        }
        skor = totalBobot == 0 ? 0 : Math.round(bobotBenar / totalBobot * 10_000.0) / 100.0;
        assert skor >= 0 && skor <= NILAI_MAKSIMUM : "Skor di luar rentang: " + skor;
        return skor;
    }

    /** Menilai satu soal melalui referensi superclass (dynamic dispatch). */
    public StatusJawaban nilaiJawaban(Soal soal) {
        String jwb = jawaban.get(soal.getIdSoal());
        if (StringUtil.isKosong(jwb)) {
            return StatusJawaban.KOSONG;
        }
        return soal.evaluasiJawaban(jwb) ? StatusJawaban.BENAR : StatusJawaban.SALAH;
    }

    public boolean isLulus() {
        return skor >= ujian.getKkm();
    }

    /** Urut skor menurun (ranking). */
    @Override
    public int compareTo(HasilUjian lain) {
        return Double.compare(lain.skor, skor);
    }

    public Duration getDurasiPengerjaan() {
        LocalDateTime akhir = waktuSelesai != null ? waktuSelesai : LocalDateTime.now(clock);
        return Duration.between(waktuMulai, akhir);
    }

    public Siswa getSiswa() {
        return siswa;
    }

    public Ujian getUjian() {
        return ujian;
    }

    public double getSkor() {
        return skor;
    }

    public int getJumlahBenar() {
        return jumlahBenar;
    }

    public int getJumlahSalah() {
        return jumlahSalah;
    }

    public int getJumlahKosong() {
        return jumlahKosong;
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public LocalDateTime getBatasWaktu() {
        return batasWaktu;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public StatusUjian getStatus() {
        return status;
    }

    public ModeSubmit getModeSubmit() {
        return modeSubmit;
    }
}
