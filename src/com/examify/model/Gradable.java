package com.examify.model;

/** Kontrak untuk objek yang dapat dinilai dengan skala 0-100. */
public interface Gradable {

    double NILAI_MAKSIMUM = 100.0;

    double hitungNilaiAkhir();

    default String getPredikat() {
        double nilai = hitungNilaiAkhir();
        if (nilai >= 90) return "A";
        if (nilai >= 80) return "B";
        if (nilai >= 70) return "C";
        if (nilai >= 60) return "D";
        return "E";
    }
}
