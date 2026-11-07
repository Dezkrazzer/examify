package com.examify.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Penulis file log append-only: satu objek menjadi satu baris, header ditulis sekali
 * saat file baru/kosong. Subclass cukup menentukan header dan cara serialisasi.
 */
public abstract class PenulisBaris<T> {

    private final Path file;

    protected PenulisBaris(Path file) {
        this.file = file;
    }

    protected abstract String getHeader();

    protected abstract String keBaris(T data);

    public synchronized void tulis(T data) throws IOException {
        Path folder = file.toAbsolutePath().getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }
        boolean perluHeader = Files.notExists(file) || Files.size(file) == 0;
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (perluHeader) {
                bw.write(getHeader());
                bw.newLine();
            }
            bw.write(keBaris(data));
            bw.newLine();
        }
    }
}
