package com.examify.model;

/** Jenis item di dalam bab; kode dipakai di kursus.txt. */
public enum JenisMateri {
    TEKS("Bacaan"),
    LINK("Tautan"),
    PDF("Dokumen PDF"),
    UJIAN("Ujian");

    private final String label;

    JenisMateri(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** @return jenis yang cocok, atau null bila kode tidak dikenal */
    public static JenisMateri dariKode(String kode) {
        for (JenisMateri jenis : values()) {
            if (jenis.name().equalsIgnoreCase(kode)) {
                return jenis;
            }
        }
        return null;
    }
}
