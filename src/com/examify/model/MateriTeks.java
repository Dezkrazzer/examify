package com.examify.model;

import java.nio.file.Path;

/** Bacaan berformat Markdown yang disimpan di file terpisah. */
public class MateriTeks extends Materi {

    private final Path file;

    public MateriTeks(String idMateri, String judul, Path file) {
        super(idMateri, judul, JenisMateri.TEKS);
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    @Override
    public String getKeterangan() {
        return "Bacaan";
    }
}
