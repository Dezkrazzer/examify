package com.examify.io;

import com.examify.model.Chapter;
import com.examify.model.Course;
import com.examify.model.TextMaterial;
import com.examify.model.ExamMaterial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void tulisDanBacaKembaliKursus() throws Exception {
        Path fileKursus = tempDir.resolve("kursus.txt");
        Path fileTeks = tempDir.resolve("materi1.md");
        Files.writeString(fileTeks, "# Konten");

        Chapter bab = new Chapter(1, "Bab 1", List.of(
                new TextMaterial("m1", "Pengenalan", fileTeks),
                new ExamMaterial("u1", "Kuis Bab 1", "PBO-01")
        ));

        Course kursus = new Course("pbo", "Pemrograman Berorientasi Objek", "Dr. Rina", "Kursus Java OOP", List.of(bab));

        CourseWriter writer = new CourseWriter(tempDir);
        writer.tulis(fileKursus, List.of(kursus));

        CourseReader reader = new CourseReader(tempDir);
        List<Course> hasil = reader.baca(fileKursus);

        assertEquals(1, hasil.size());
        Course k = hasil.get(0);
        assertEquals("PBO", k.getKode());
        assertEquals("Pemrograman Berorientasi Objek", k.getNama());
        assertEquals(1, k.getDaftarBab().size());
        assertEquals(2, k.getDaftarBab().get(0).getDaftarMateri().size());
    }
}
