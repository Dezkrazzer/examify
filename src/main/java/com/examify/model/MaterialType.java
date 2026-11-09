package com.examify.model;

/** Jenis item di dalam bab; kode dipakai di kursus.txt. */
public enum MaterialType {
    TEKS("Bacaan"),
    LINK("Tautan"),
    PDF("Dokumen PDF"),
    UJIAN("Ujian"),
    QUIZ("Kuis"),
    TUGAS("Tugas");

    private final String label;

    MaterialType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** @return jenis yang cocok, atau null bila kode tidak dikenal */
    public static MaterialType dariKode(String kode) {
        for (MaterialType jenis : values()) {
            if (jenis.name().equalsIgnoreCase(kode)) {
                return jenis;
            }
        }
        return null;
    }
}
