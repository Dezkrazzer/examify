package com.examify.model;

import com.examify.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Satu paket ujian beserta bank soalnya (komposisi: Ujian memiliki Soal). */
public class Ujian {

    public static final short DURASI_MIN_MENIT = 1;
    public static final short DURASI_MAKS_MENIT = 180;

    private final String kodeUjian;
    private final String mataPelajaran;
    private final String token; // tanpa getter publik
    private final short durasiMenit; // 1-180 menit cukup ditampung short
    private final double kkm;
    private final List<Soal> daftarSoal;
    private final boolean bolehUlang;

    /** Ujian yang hanya boleh dikerjakan sekali. */
    public Ujian(String kodeUjian, String mataPelajaran, String token,
                 int durasiMenit, double kkm, List<Soal> daftarSoal) {
        this(kodeUjian, mataPelajaran, token, durasiMenit, kkm, daftarSoal, false);
    }

    public Ujian(String kodeUjian, String mataPelajaran, String token,
                 int durasiMenit, double kkm, List<Soal> daftarSoal, boolean bolehUlang) {
        if (StringUtil.isKosong(kodeUjian) || StringUtil.isKosong(mataPelajaran) || StringUtil.isKosong(token)) {
            throw new IllegalArgumentException("Kode, mata pelajaran, dan token wajib diisi");
        }
        if (durasiMenit < DURASI_MIN_MENIT || durasiMenit > DURASI_MAKS_MENIT) {
            throw new IllegalArgumentException("Durasi harus " + DURASI_MIN_MENIT + "-" + DURASI_MAKS_MENIT + " menit");
        }
        if (kkm < 0 || kkm > Gradable.NILAI_MAKSIMUM) {
            throw new IllegalArgumentException("KKM harus 0-100");
        }
        if (daftarSoal == null || daftarSoal.isEmpty()) {
            throw new IllegalArgumentException("Ujian minimal memiliki 1 soal");
        }
        pastikanIdUnik(daftarSoal);
        this.kodeUjian = kodeUjian.trim().toUpperCase();
        this.mataPelajaran = mataPelajaran.trim();
        this.token = token.trim().toUpperCase();
        this.durasiMenit = (short) durasiMenit; // aman: rentang sudah divalidasi
        this.kkm = kkm;
        this.daftarSoal = new ArrayList<>(daftarSoal);
        this.bolehUlang = bolehUlang;
    }

    private static void pastikanIdUnik(List<Soal> daftarSoal) {
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

    public Ujian denganSoalDiacak() {
        List<Soal> acak = new ArrayList<>(daftarSoal);
        Collections.shuffle(acak);
        return new Ujian(kodeUjian, mataPelajaran, token, durasiMenit, kkm, acak, bolehUlang);
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
