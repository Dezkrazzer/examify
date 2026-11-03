package com.examify.model;

import java.util.List;

public class SoalPilihanGanda extends Soal {

    private static final byte MIN_OPSI = 2;
    private static final byte MAKS_OPSI = 5; // huruf A-E

    private final List<String> opsi;
    private final char kunciJawaban; // encapsulated: tanpa getter

    public SoalPilihanGanda(int idSoal, String pertanyaan, double bobot, List<String> opsi, char kunci) {
        super(idSoal, pertanyaan, bobot, JenisSoal.PG);
        if (opsi == null || opsi.size() < MIN_OPSI || opsi.size() > MAKS_OPSI) {
            throw new IllegalArgumentException("Jumlah opsi harus " + MIN_OPSI + "-" + MAKS_OPSI);
        }
        char hurufTerakhir = hurufOpsi(opsi.size() - 1);
        char kunciKapital = Character.toUpperCase(kunci);
        if (kunciKapital < 'A' || kunciKapital > hurufTerakhir) {
            throw new IllegalArgumentException("Kunci harus A-" + hurufTerakhir);
        }
        this.opsi = List.copyOf(opsi);
        this.kunciJawaban = kunciKapital;
    }

    public static char hurufOpsi(int indeks) {
        return (char) ('A' + indeks);
    }

    @Override
    public boolean evaluasiJawaban(String inputJawaban) {
        String jawaban = normalisasi(inputJawaban);
        return jawaban.length() == 1 && jawaban.charAt(0) == kunciJawaban;
    }

    @Override
    public String getPetunjukFormat() {
        return "Pilih salah satu opsi A-" + hurufOpsi(opsi.size() - 1);
    }

    @Override
    public String bukaKunci(StatusUjian status) {
        pastikanSelesai(status);
        return String.valueOf(kunciJawaban);
    }

    /** @return daftar opsi yang tidak dapat diubah */
    public List<String> getOpsi() {
        return opsi;
    }

    public String getLabelOpsi(int indeks) {
        return hurufOpsi(indeks) + ". " + opsi.get(indeks);
    }
}
