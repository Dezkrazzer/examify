package com.examify.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Satu baris rekap nilai permanen (rekap_nilai.txt). Record immutable karena riwayat
 * tidak pernah diubah setelah ditulis. Urutan alami: skor tertinggi lebih dulu.
 */
public record GradeHistory(LocalDateTime waktu, String nis, String nama, String kelas,
                           String kodeUjian, String mataPelajaran,
                           int benar, int salah, int kosong, double skor, String predikat,
                           boolean lulus, SubmitMode modeSubmit, Duration durasi)
        implements Comparable<GradeHistory> {

    public static GradeHistory dari(ExamResult hasil) {
        Student siswa = hasil.getSiswa();
        Exam ujian = hasil.getUjian();
        return new GradeHistory(hasil.getWaktuSelesai(), siswa.getNis(), siswa.getNama(), siswa.getKelas(),
                ujian.getKodeUjian(), ujian.getMataPelajaran(),
                hasil.getJumlahBenar(), hasil.getJumlahSalah(), hasil.getJumlahKosong(),
                hasil.getSkor(), hasil.getPredikat(), hasil.isLulus(), hasil.getModeSubmit(),
                hasil.getDurasiPengerjaan());
    }

    public boolean milik(Student siswa) {
        return nis.equals(siswa.getNis());
    }

    @Override
    public int compareTo(GradeHistory lain) {
        return Double.compare(lain.skor, skor);
    }
}
