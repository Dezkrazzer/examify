package com.examify.model;

import java.util.Set;
import java.util.stream.Collectors;

public class SoalIsian extends Soal {

    private static final String TANDA_BACA_AKHIR = "[.,;!?]+$";

    private final Set<String> kunciJawaban; // beberapa jawaban sah, sudah dinormalisasi

    public SoalIsian(int idSoal, String pertanyaan, double bobot, Set<String> kunci) {
        super(idSoal, pertanyaan, bobot, JenisSoal.IS);
        if (kunci == null || kunci.isEmpty()) {
            throw new IllegalArgumentException("Soal isian minimal memiliki 1 kunci");
        }
        this.kunciJawaban = kunci.stream()
                .map(this::normalisasiIsian)
                .collect(Collectors.toUnmodifiableSet());
        if (kunciJawaban.contains("")) {
            throw new IllegalArgumentException("Kunci isian tidak boleh kosong");
        }
    }

    @Override
    public boolean evaluasiJawaban(String inputJawaban) {
        return kunciJawaban.contains(normalisasiIsian(inputJawaban));
    }

    @Override
    public String getPetunjukFormat() {
        return "Ketik jawaban singkat (huruf besar/kecil tidak berpengaruh)";
    }

    @Override
    public String bukaKunci(StatusUjian status) {
        pastikanSelesai(status);
        return String.join(" / ", kunciJawaban.stream().sorted().toList());
    }

    private String normalisasiIsian(String input) {
        return normalisasi(input).replaceAll(TANDA_BACA_AKHIR, "").trim();
    }
}
