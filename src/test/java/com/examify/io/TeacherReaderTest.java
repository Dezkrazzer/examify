package com.examify.io;

import com.examify.model.Teacher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeacherReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void bacaFormatGuruValid() throws Exception {
        String isi = """
                # NIP|Nama Lengkap|PIN|Mata Pelajaran
                19850101|Dr. Rina Kartika, M.Kom.|1234|Pemrograman Java & OOP
                19820512|Budi Santoso, M.Pd.|1234|Bahasa Indonesia
                """;
        Path file = tempDir.resolve("guru_test.txt");
        Files.writeString(file, isi);
        TeacherReader reader = new TeacherReader();
        List<Teacher> daftar = reader.baca(file);

        assertEquals(2, daftar.size());
        assertEquals("19850101", daftar.get(0).getNip());
        assertEquals("Dr. Rina Kartika, M.Kom.", daftar.get(0).getNama());
        assertTrue(daftar.get(0).verifikasiPin("1234"));
        assertEquals("Pemrograman Java & OOP", daftar.get(0).getMataPelajaran());
    }

    @Test
    void bacaDariFile() throws Exception {
        Path file = tempDir.resolve("guru.txt");
        Files.writeString(file, "19800101|Guru Satu|5678|Matematika\n");
        TeacherReader reader = new TeacherReader();
        List<Teacher> daftar = reader.baca(file);

        assertEquals(1, daftar.size());
        assertEquals("Guru Satu", daftar.get(0).getNama());
    }
}
