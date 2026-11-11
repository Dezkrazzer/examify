package com.examify.io;

import com.examify.exception.DataFormatException;
import com.examify.model.Course;
import com.examify.model.PdfMaterial;
import com.examify.model.LinkMaterial;
import com.examify.model.TextMaterial;
import com.examify.model.ExamMaterial;
import com.examify.utils.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseReaderTest {

    @TempDir
    Path folder;
    private CourseReader reader;

    @BeforeEach
    void siapkan() throws Exception {
        Files.writeString(folder.resolve("bab1.md"), "# Halo");
        Files.writeString(folder.resolve("slide.pdf"), "%PDF-1.4");
        reader = new CourseReader(folder);
    }

    private List<Course> baca(String isi) throws Exception {
        return reader.baca(new BufferedReader(new StringReader(isi)));
    }

    private static final String VALID = """
            [KURSUS]
            KODE=pbo
            NAMA=Pemrograman Berorientasi Objek
            PENGAJAR=Bu Rina
            DESKRIPSI=Dasar OOP
            [BAB] Pengenalan
            TEKS|p1|Apa itu OOP|bab1.md
            LINK|p2|Dokumentasi|https://dev.java/learn/
            [BAB] Evaluasi
            PDF|p3|Slide|slide.pdf
            UJIAN|p4|UTS|pbo-01
            [END]
            """;

    @Test
    void kursusValidTerbacaDenganBabDanMateri() throws Exception {
        Course kursus = baca(VALID).get(0);
        assertEquals("PBO", kursus.getKode());
        assertEquals(2, kursus.getDaftarBab().size());
        assertEquals(2, kursus.getDaftarBab().get(1).getNomor());
        assertEquals(4, kursus.getJumlahMateri());
        assertInstanceOf(TextMaterial.class, kursus.getSemuaMateri().get(0));
        assertInstanceOf(LinkMaterial.class, kursus.getSemuaMateri().get(1));
        assertInstanceOf(PdfMaterial.class, kursus.getSemuaMateri().get(2));
        ExamMaterial uts = assertInstanceOf(ExamMaterial.class, kursus.getSemuaMateri().get(3));
        assertEquals("PBO-01", uts.getKodeUjian());
    }

    @Test
    void keteranganMateriBerbedaPerJenis() throws Exception {
        Course kursus = baca(VALID).get(0);
        assertEquals("Tautan - dev.java", kursus.getSemuaMateri().get(1).getKeterangan());
        assertEquals("Ujian - PBO-01", kursus.getSemuaMateri().get(3).getKeterangan());
    }

    @Test
    void fileMateriTidakAdaDitolakDenganNomorBaris() {
        String isi = VALID.replace("bab1.md", "hilang.md");
        DataFormatException e = assertThrows(DataFormatException.class, () -> baca(isi));
        assertEquals(7, e.getNomorBaris());
    }

    @Test
    void tautanTanpaHttpDitolak() {
        assertThrows(DataFormatException.class, () -> baca(VALID.replace("https://dev.java/learn/", "dev.java")));
    }

    @Test
    void jenisMateriTidakDikenalDitolak() {
        assertThrows(DataFormatException.class, () -> baca(VALID.replace("PDF|p3", "AUDIO|p3")));
    }

    @Test
    void idMateriDuplikatDitolak() {
        assertThrows(DataFormatException.class, () -> baca(VALID.replace("PDF|p3", "PDF|p1")));
    }

    @Test
    void blokTanpaEndDitolak() {
        assertThrows(DataFormatException.class, () -> baca(VALID.replace("[END]", "")));
    }

    @Test
    void pencarianKursusTanpaPeduliKapital() throws Exception {
        Course kursus = baca(VALID).get(0);
        assertTrue(kursus.cocokDengan("berorientasi"));
        assertTrue(kursus.cocokDengan(" pbo "));
    }

    @Test
    void fileKursusContohTerbacaLengkap() throws Exception {
        List<Course> daftar = new CourseReader(AppConfig.DIR_DATA).baca(AppConfig.FILE_KURSUS);
        assertEquals(23, daftar.size());
        var daftarUjian = new QuestionBankReader().baca(AppConfig.FILE_BANK_SOAL);
        var daftarGuru = new TeacherReader().baca(AppConfig.FILE_GURU);
        assertEquals(7, daftarGuru.size());
        var ujianService = new com.examify.service.ExamService(
                daftarUjian, new com.examify.service.GradeRecapService(null, List.of()), java.time.Clock.systemDefaultZone());
        var kursusService = new com.examify.service.CourseService(daftar, ujianService);

        for (Course k : daftar) {
            if (!"UJI".equals(k.getKode())) {
                assertEquals(16, k.getDaftarBab().size(), "Kursus " + k.getKode() + " harus memiliki 16 bab");
                assertEquals(14, k.getJumlahMateriPembelajaran(), "Kursus " + k.getKode() + " harus memiliki 14 materi");
                assertEquals(2, k.getJumlahUjian(), "Kursus " + k.getKode() + " harus memiliki 2 ujian (UTS dan UAS)");
            }
        }
        assertEquals(23, kursusService.getJumlahKursus());
    }
}
