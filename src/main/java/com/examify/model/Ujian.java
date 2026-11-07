package com.examify.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Paket ujian CBT: identitas, token, daftar soal, KKM, durasi, dan jadwal akses. */
public class Ujian {

    public static final short DURASI_MIN_MENIT = 1;
    public static final short DURASI_MAKS_MENIT = 180;

    private final String kodeUjian;
    private final String mataPelajaran;
    private final String token;
    private final int durasiMenit;
    private final double kkm;
    private final List<Soal> daftarSoal;
    private final boolean bolehUlang;
    private final LocalDateTime waktuMulai;
    private final LocalDateTime waktuSelesai;

    public Ujian(String kodeUjian, String mataPelajaran, String token, int durasiMenit,
                 double kkm, List<Soal> daftarSoal) {
        this(kodeUjian, mataPelajaran, token, durasiMenit, kkm, daftarSoal, false, null, null);
    }

    public Ujian(String kodeUjian, String mataPelajaran, String token, int durasiMenit,
                 double kkm, List<Soal> daftarSoal, boolean bolehUlang) {
        this(kodeUjian, mataPelajaran, token, durasiMenit, kkm, daftarSoal, bolehUlang, null, null);
    }

    public Ujian(String kodeUjian, String mataPelajaran, String token, int durasiMenit,
                 double kkm, List<Soal> daftarSoal, boolean bolehUlang,
                 LocalDateTime waktuMulai, LocalDateTime waktuSelesai) {
        if (kodeUjian == null || kodeUjian.isBlank()) {
            throw new IllegalArgumentException("Kode ujian tidak boleh kosong");
        }
        if (mataPelajaran == null || mataPelajaran.isBlank()) {
            throw new IllegalArgumentException("Mata pelajaran tidak boleh kosong");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token tidak boleh kosong");
        }
        if (durasiMenit < DURASI_MIN_MENIT || durasiMenit > DURASI_MAKS_MENIT) {
            throw new IllegalArgumentException("Durasi harus " + DURASI_MIN_MENIT + "-" + DURASI_MAKS_MENIT + " menit");
        }
        if (kkm < 0 || kkm > 100) {
            throw new IllegalArgumentException("KKM harus di antara 0 dan 100");
        }
        if (daftarSoal == null || daftarSoal.isEmpty()) {
            throw new IllegalArgumentException("Ujian minimal memiliki 1 soal");
        }
        if (waktuMulai != null && waktuSelesai != null && waktuMulai.isAfter(waktuSelesai)) {
            throw new IllegalArgumentException("Waktu mulai tidak boleh setelah waktu selesai");
        }

        this.kodeUjian = kodeUjian.trim().toUpperCase();
        this.mataPelajaran = mataPelajaran.trim();
        this.token = token.trim().toUpperCase();
        this.durasiMenit = durasiMenit;
        this.kkm = kkm;
        this.daftarSoal = List.copyOf(daftarSoal);
        this.bolehUlang = bolehUlang;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;

        Set<Integer> idTerpakai = new HashSet<>();
        for (Soal soal : daftarSoal) {
            if (!idTerpakai.add(soal.getIdSoal())) {
                throw new IllegalArgumentException("ID soal duplikat: " + soal.getIdSoal());
            }
        }
    }

    public boolean validasiToken(String input) {
        return input != null && input.trim().toUpperCase().equals(token);
    }

    public String getKodeUjian() {
        return kodeUjian;
    }

    public String getMataPelajaran() {
        return mataPelajaran;
    }

    public int getDurasiMenit() {
        return durasiMenit;
    }

    public int getDurasiDetik() {
        return durasiMenit * 60;
    }

    public double getKkm() {
        return kkm;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public boolean hasJadwalAkses() {
        return waktuMulai != null || waktuSelesai != null;
    }

    public boolean isBelumMulai() {
        return isBelumMulai(LocalDateTime.now());
    }

    public boolean isBelumMulai(LocalDateTime sekarang) {
        return waktuMulai != null && sekarang.isBefore(waktuMulai);
    }

    public boolean isSudahBerakhir() {
        return isSudahBerakhir(LocalDateTime.now());
    }

    public boolean isSudahBerakhir(LocalDateTime sekarang) {
        return waktuSelesai != null && sekarang.isAfter(waktuSelesai);
    }

    public boolean isAksesDibuka() {
        return isAksesDibuka(LocalDateTime.now());
    }

    public boolean isAksesDibuka(LocalDateTime sekarang) {
        return !isBelumMulai(sekarang) && !isSudahBerakhir(sekarang);
    }

    public Ujian denganSoalDiacak() {
        List<Soal> acak = new ArrayList<>(daftarSoal);
        Collections.shuffle(acak);
        return new Ujian(kodeUjian, mataPelajaran, token, durasiMenit, kkm, acak, bolehUlang, waktuMulai, waktuSelesai);
    }

    public boolean isBolehUlang() {
        return bolehUlang;
    }

    public List<Soal> getDaftarSoal() {
        return Collections.unmodifiableList(daftarSoal);
    }

    public int getJumlahSoal() {
        return daftarSoal.size();
    }

    public double getTotalBobot() {
        return daftarSoal.stream().mapToDouble(Soal::getBobot).sum();
    }

    public Soal getSoalBerdasarkanIndeks(int indeks) {
        return daftarSoal.get(indeks);
    }

    @Override
    public String toString() {
        return kodeUjian + " - " + mataPelajaran;
    }
}
