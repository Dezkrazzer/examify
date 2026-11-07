package com.examify.model;

import java.nio.file.Path;

/** Dokumen PDF lokal yang dibuka dengan aplikasi PDF bawaan komputer. */
public class MateriPdf extends Materi {

    private final Path file;

    public MateriPdf(String idMateri, String judul, Path file) {
        super(idMateri, judul, JenisMateri.PDF);
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
