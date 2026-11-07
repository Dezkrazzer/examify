package com.examify.model;

/** Jenis soal beserta kode di file dan jumlah field wajib per baris. */
public enum JenisSoal {
    PG("PG", 6, "Pilihan ganda"),
    IS("IS", 5, "Isian singkat"),
    TF("TF", 5, "Benar / Salah");

    private final String kode;
    private final int jumlahField;
    private final String label;

    JenisSoal(String kode, int jumlahField, String label) {
        this.kode = kode;
        this.jumlahField = jumlahField;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getKode() {
        return kode;
    }

    public int getJumlahField() {
        return jumlahField;
    }

    /** @return jenis yang cocok, atau null bila kode tidak dikenal */
    public static JenisSoal dariKode(String kode) {
        for (JenisSoal jenis : values()) {
            if (jenis.kode.equalsIgnoreCase(kode)) {
                return jenis;
            }
        }
        return null;
    }
}
