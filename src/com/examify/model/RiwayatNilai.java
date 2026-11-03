package com.examify.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Satu baris rekap nilai permanen (rekap_nilai.txt). Record immutable karena riwayat
 * tidak pernah diubah setelah ditulis. Urutan alami: skor tertinggi lebih dulu.
 */
public record RiwayatNilai(LocalDateTime waktu, String nis, String nama, String kelas,
                           String kodeUjian, String mataPelajaran,
                           int benar, int salah, int kosong, double skor, String predikat,
                           boolean lulus, ModeSubmit modeSubmit, Duration durasi)
        implements Comparable<RiwayatNilai> {

    public static RiwayatNilai dari(HasilUjian hasil) {
        Siswa siswa = hasil.getSiswa();
        Ujian ujian = hasil.getUjian();
        return new RiwayatNilai(hasil.getWaktuSelesai(), siswa.getNis(), siswa.getNama(), siswa.getKelas(),
                ujian.getKodeUjian(), ujian.getMataPelajaran(),
                hasil.getJumlahBenar(), hasil.getJumlahSalah(), hasil.getJumlahKosong(),
                hasil.getSkor(), hasil.getPredikat(), hasil.isLulus(), hasil.getModeSubmit(),
                hasil.getDurasiPengerjaan());
    }

    public boolean milik(Siswa siswa) {
        return nis.equals(siswa.getNis());
    }

    @Override
    public int compareTo(RiwayatNilai lain) {
        return Double.compare(lain.skor, skor);
    }
}
