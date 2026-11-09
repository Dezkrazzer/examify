package com.examify.model;

import com.examify.exception.EmptyAnswerException;
import com.examify.exception.TimeOutException;
import com.examify.utils.AppConfig;
import com.examify.utils.StringUtil;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/** Lembar jawaban satu siswa pada satu ujian, sekaligus penilaiannya. */
public class ExamResult implements Gradable, Comparable<ExamResult> {

    private final Student siswa;
    private final Exam ujian;
    private final Clock clock;
    private final Map<Integer, String> jawaban = new LinkedHashMap<>();
    private final Set<Integer> soalDitandai = new LinkedHashSet<>();
    private final LocalDateTime waktuMulai;
    private final LocalDateTime batasWaktu;

    private double skor;
    private int jumlahBenar;
    private int jumlahSalah;
    private int jumlahKosong;
    private LocalDateTime waktuSelesai;
    private ExamStatus status = ExamStatus.BERLANGSUNG;
    private SubmitMode modeSubmit;

    public ExamResult(Student siswa, Exam ujian) {
        this(siswa, ujian, Clock.systemDefaultZone());
    }

    /** Clock dapat diganti agar perilaku batas waktu dapat diuji. */
    public ExamResult(Student siswa, Exam ujian, Clock clock) {
        this.siswa = siswa;
        this.ujian = ujian;
        this.clock = clock;
        this.waktuMulai = LocalDateTime.now(clock);
        this.batasWaktu = waktuMulai.plusSeconds(ujian.getDurasiDetik() + AppConfig.GRACE_PERIOD_DETIK);
    }

    public void simpanJawaban(int idSoal, String jwb) throws EmptyAnswerException, TimeOutException {
        if (status != ExamStatus.BERLANGSUNG || isLewatBatasWaktu()) {
            throw new TimeOutException(ujian.getKodeUjian());
        }
        if (StringUtil.isKosong(jwb)) {
            throw new EmptyAnswerException(idSoal);
        }
        jawaban.put(idSoal, jwb.trim());
    }

    public void setDitandai(int idSoal, boolean ditandai) {
        if (status == ExamStatus.BERLANGSUNG) {
            if (ditandai) {
                soalDitandai.add(idSoal);
            } else {
                soalDitandai.remove(idSoal);
            }
        }
    }

    public void toggleTandai(int idSoal) {
        if (status == ExamStatus.BERLANGSUNG) {
            if (!soalDitandai.add(idSoal)) {
                soalDitandai.remove(idSoal);
            }
        }
    }

    public boolean isDitandai(int idSoal) {
        return soalDitandai.contains(idSoal);
    }

    public int getJumlahDitandai() {
        return soalDitandai.size();
    }

    public Set<Integer> getSoalDitandai() {
        return Collections.unmodifiableSet(soalDitandai);
    }

    public void hapusJawaban(int idSoal) {
        if (status == ExamStatus.BERLANGSUNG) {
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
    public synchronized boolean finalisasi(SubmitMode mode) {
        if (status == ExamStatus.SELESAI) {
            return false;
        }
        status = ExamStatus.SELESAI;
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
        for (Question soal : ujian.getDaftarSoal()) {
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
    public AnswerStatus nilaiJawaban(Question soal) {
        String jwb = jawaban.get(soal.getIdSoal());
        if (StringUtil.isKosong(jwb)) {
            return AnswerStatus.KOSONG;
        }
        return soal.evaluasiJawaban(jwb) ? AnswerStatus.BENAR : AnswerStatus.SALAH;
    }

    public boolean isLulus() {
        return skor >= ujian.getKkm();
    }

    /** Urut skor menurun (ranking). */
    @Override
    public int compareTo(ExamResult lain) {
        return Double.compare(lain.skor, skor);
    }

    public Duration getDurasiPengerjaan() {
        LocalDateTime akhir = waktuSelesai != null ? waktuSelesai : LocalDateTime.now(clock);
        return Duration.between(waktuMulai, akhir);
    }

    public Student getSiswa() {
        return siswa;
    }

    public Exam getUjian() {
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

    public ExamStatus getStatus() {
        return status;
    }

    public SubmitMode getModeSubmit() {
        return modeSubmit;
    }
}
