package com.examify.model;

import java.util.Set;

public class SoalTrueFalse extends Soal {

    public static final String LABEL_BENAR = "BENAR";
    public static final String LABEL_SALAH = "SALAH";

    private static final Set<String> ALIAS_TRUE = Set.of("T", "TRUE", "B", LABEL_BENAR);
    private static final Set<String> ALIAS_FALSE = Set.of("F", "FALSE", "S", LABEL_SALAH);

    private final char kunciJawaban; // 'T' atau 'F'

    public SoalTrueFalse(int idSoal, String pernyataan, double bobot, char kunci) {
        super(idSoal, pernyataan, bobot, JenisSoal.TF);
        char kunciKapital = Character.toUpperCase(kunci);
        if (kunciKapital != 'T' && kunciKapital != 'F') {
            throw new IllegalArgumentException("Kunci TF harus T atau F");
        }
        this.kunciJawaban = kunciKapital;
    }

    public char getKunciRaw() {
        return kunciJawaban;
    }

    @Override
    public boolean evaluasiJawaban(String inputJawaban) {
        String jawaban = normalisasi(inputJawaban);
        if (ALIAS_TRUE.contains(jawaban)) {
            return kunciJawaban == 'T';
        }
        if (ALIAS_FALSE.contains(jawaban)) {
            return kunciJawaban == 'F';
        }
        return false;
    }

    @Override
    public String getPetunjukFormat() {
        return "Tentukan apakah pernyataan ini Benar atau Salah";
    }

    @Override
    public String bukaKunci(StatusUjian status) {
        pastikanSelesai(status);
        return kunciJawaban == 'T' ? LABEL_BENAR : LABEL_SALAH;
    }
}
