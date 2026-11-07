package com.examify.io;

import com.examify.model.Bab;
import com.examify.model.Kursus;
import com.examify.model.MateriTeks;
import com.examify.model.MateriUjian;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KursusWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void tulisDanBacaKembaliKursus() throws Exception {
        Path fileKursus = tempDir.resolve("kursus.txt");
        Path fileTeks = tempDir.resolve("materi1.md");
        Files.writeString(fileTeks, "# Konten");

        Bab bab = new Bab(1, "Bab 1", List.of(
                new MateriTeks("m1", "Pengenalan", fileTeks),
                new MateriUjian("u1", "Kuis Bab 1", "PBO-01")
        ));

        Kursus kursus = new Kursus("pbo", "Pemrograman Berorientasi Objek", "Dr. Rina", "Kursus Java OOP", List.of(bab));

        KursusWriter writer = new KursusWriter(tempDir);
        writer.tulis(fileKursus, List.of(kursus));

        KursusReader reader = new KursusReader(tempDir);
        List<Kursus> hasil = reader.baca(fileKursus);

        assertEquals(1, hasil.size());
        Kursus k = hasil.get(0);
        assertEquals("PBO", k.getKode());
        assertEquals("Pemrograman Berorientasi Objek", k.getNama());
        assertEquals(1, k.getDaftarBab().size());
        assertEquals(2, k.getDaftarBab().get(0).getDaftarMateri().size());
    }
}
