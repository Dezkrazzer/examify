package com.examify.service;

import com.examify.io.LearningProgressReader;
import com.examify.io.LearningProgressWriter;
import com.examify.io.GradeRecapWriter;
import com.examify.model.Chapter;
import com.examify.model.Course;
import com.examify.model.Material;
import com.examify.model.LinkMaterial;
import com.examify.model.ExamMaterial;
import com.examify.model.Student;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LearningProgressServiceTest {

    @TempDir
    Path folder;

    private final Student siswa = new Student("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final Exam ujian = new Exam("PBO-01", "PBO", "PBO123", 10, 60, List.of(
            new TrueFalseQuestion(1, "a", 10, 'T')));
    private final Material bacaan = new LinkMaterial("m1", "Bacaan", "https://dev.java/learn/");
    private final Material kuis = new ExamMaterial("m2", "Kuis", "PBO-01");
    private final Course kursus = new Course("PBO", "PBO", "Bu Rina", "", List.of(
            new Chapter(1, "Bab 1", List.of(bacaan, kuis))));

    private Path fileProgres;
    private ExamService ujianService;
    private CourseService kursusService;
    private GradeRecapService rekapService;

    @BeforeEach
    void siapkan() {
        fileProgres = folder.resolve("progres.txt");
        rekapService = new GradeRecapService(new GradeRecapWriter(folder.resolve("rekap.txt")));
        ujianService = new ExamService(List.of(ujian), rekapService);
        kursusService = new CourseService(List.of(kursus), ujianService);
    }

    private LearningProgressService service() throws Exception {
        return new LearningProgressService(new LearningProgressWriter(fileProgres),
                new LearningProgressReader().baca(fileProgres), kursusService, rekapService);
    }

    @Test
    void materiSelesaiDicatatSekaliDanBertahanDiSesiBaru() throws Exception {
        LearningProgressService progres = service();
        progres.tandaiSelesai(siswa, kursus, bacaan);
        progres.tandaiSelesai(siswa, kursus, bacaan);
        assertEquals(2, Files.readAllLines(fileProgres).size()); // header + 1 baris

        assertTrue(service().isSelesai(siswa, kursus, bacaan));
        assertEquals(0.5, service().getProgres(siswa, kursus));
    }

    @Test
    void materiUjianSelesaiSetelahUjianDikerjakan() throws Exception {
        LearningProgressService progres = service();
        ujianService.selesaikanPaksa(ujianService.mulaiUjian(siswa, ujian));
        assertTrue(progres.isSelesai(siswa, kursus, kuis));
        assertEquals(1, progres.getJumlahSelesai(siswa, kursus));
    }

    @Test
    void kursusDenganUjianTidakTerdaftarDitolak() {
        Course rusak = new Course("XX", "Rusak", "", "", List.of(
                new Chapter(1, "Bab", List.of(new ExamMaterial("u", "Ujian", "TIDAK-ADA")))));
        assertThrows(IllegalStateException.class, () -> new CourseService(List.of(rusak), ujianService));
    }
}
