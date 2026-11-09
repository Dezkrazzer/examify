package com.examify.model;

import java.nio.file.Path;

/** Dokumen PDF lokal yang dibuka dengan aplikasi PDF bawaan komputer. */
public class PdfMaterial extends Material {

    private final Path file;

    public PdfMaterial(String idMateri, String judul, Path file) {
        super(idMateri, judul, MaterialType.PDF);
        if (!file.getFileName().toString().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("File materi PDF harus berekstensi .pdf");
        }
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    @Override
    public String getKeterangan() {
        return "PDF - " + file.getFileName();
    }
}
