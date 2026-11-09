package com.examify.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Menyimpan teks sertifikat ke folder sertifikat; file lama dengan nama sama ditimpa. */
public class CertificateWriter {

    private final Path folder;

    public CertificateWriter(Path folder) {
        this.folder = folder;
    }

    public Path tulis(String namaFile, String isi) throws IOException {
        Files.createDirectories(folder);
        Path tujuan = folder.resolve(namaFile);
        return Files.writeString(tujuan, isi, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }
}
