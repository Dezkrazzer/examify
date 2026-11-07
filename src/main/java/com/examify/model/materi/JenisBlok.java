package com.examify.model.materi;

public enum JenisBlok {
    PARAGRAF("Paragraf", "Teks biasa untuk penjelasan materi"),
    HEADING_1("Heading 1", "Judul utama materi"),
    HEADING_2("Heading 2", "Subjudul bagian atau topik"),
    CALLOUT("Catatan (Callout)", "Kotak sorot informasi, tips, atau poin penting"),
    GAMBAR("Gambar", "Sisipkan ilustrasi atau diagram dengan keterangan"),
    VIDEO("Video YouTube", "Sematkan tautan video pembelajaran"),
    KUTIPAN("Kutipan", "Kutipan teks atau kata mutiara rujukan"),
    KODE("Blok Kode", "Cuplikan kode sumber pemrograman atau formula");

    private final String label;
    private final String deskripsi;

    JenisBlok(String label, String deskripsi) {
        this.label = label;
        this.deskripsi = deskripsi;
    }

    public String getLabel() {
        return label;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
}
