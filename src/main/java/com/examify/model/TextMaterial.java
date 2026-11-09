package com.examify.model;

import java.nio.file.Path;

/** Bacaan berformat Markdown yang disimpan di file terpisah. */
public class TextMaterial extends Material {

    private final Path file;

    public TextMaterial(String idMateri, String judul, Path file) {
        super(idMateri, judul, MaterialType.TEKS);
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
