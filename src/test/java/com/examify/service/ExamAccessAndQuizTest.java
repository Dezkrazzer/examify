package com.examify.service;

import com.examify.io.QuestionBankReader;
import com.examify.io.QuestionBankWriter;
import com.examify.io.CourseReader;
import com.examify.io.CourseWriter;
import com.examify.io.GradeRecapWriter;
import com.examify.model.Chapter;
import com.examify.model.MaterialType;
import com.examify.model.Course;
import com.examify.model.QuizMaterial;
import com.examify.model.Student;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import com.examify.model.ControlledClock;
import com.examify.utils.FormatUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExamAccessAndQuizTest {

    @TempDir
    Path folder;

    private final Student siswa = new Student("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final ControlledClock clock = new ControlledClock();
    private Path fileRekap;
    private GradeRecapService rekapService;

    @BeforeEach
    void setup() {
        fileRekap = folder.resolve("rekap.txt");
        rekapService = new GradeRecapService(new GradeRecapWriter(fileRekap));
    }

    @Test
    void ujiJadwalAksesUjian() {
        LocalDateTime tMulai = LocalDateTime.of(2026, 11, 10, 8, 0);
        LocalDateTime tSelesai = LocalDateTime.of(2026, 11, 10, 12, 0);

        Exam ujian = new Exam("QZ-01", "Kuis Bab 1", "ABC123", 30, 75.0,
                List.of(new TrueFalseQuestion(1, "Soal 1", 10.0, 'T')), false, tMulai, tSelesai);

        assertTrue(ujian.hasJadwalAkses());
        assertEquals(tMulai, ujian.getWaktuMulai());
        assertEquals(tSelesai, ujian.getWaktuSelesai());

        // Sebelum jadwal
        LocalDateTime sebelum = LocalDateTime.of(2026, 11, 10, 7, 59);
        assertTrue(ujian.isBelumMulai(sebelum));
        assertFalse(ujian.isAksesDibuka(sebelum));
        assertFalse(ujian.isSudahBerakhir(sebelum));

        // Selama jadwal
        LocalDateTime saatBuka = LocalDateTime.of(2026, 11, 10, 9, 30);
        assertFalse(ujian.isBelumMulai(saatBuka));
        assertTrue(ujian.isAksesDibuka(saatBuka));
        assertFalse(ujian.isSudahBerakhir(saatBuka));

        // Setelah jadwal
        LocalDateTime setelah = LocalDateTime.of(2026, 11, 10, 12, 1);
        assertFalse(ujian.isBelumMulai(setelah));
        assertFalse(ujian.isAksesDibuka(setelah));
        assertTrue(ujian.isSudahBerakhir(setelah));

        // Validasi waktu mulai tidak boleh sesudah selesai
        assertThrows(IllegalArgumentException.class, () ->
                new Exam("QZ-02", "Kuis Error", "ABC123", 30, 75.0,
                        List.of(new TrueFalseQuestion(1, "Soal 1", 10.0, 'T')), false, tSelesai, tMulai));
    }

    @Test
    void ujiUjianServiceBolehMulaiJadwalAkses() {
        LocalDateTime tMulai = LocalDateTime.of(2026, 11, 10, 8, 0);
        LocalDateTime tSelesai = LocalDateTime.of(2026, 11, 10, 12, 0);

        Exam ujian = new Exam("QZ-01", "Kuis Bab 1", "ABC123", 30, 75.0,
                List.of(new TrueFalseQuestion(1, "Soal 1", 10.0, 'T')), false, tMulai, tSelesai);

        ExamService service = new ExamService(List.of(ujian), rekapService, clock);

        // Sebelum jadwal: tidak boleh mulai
        clock.setWaktu(tMulai.minusMinutes(10));
        assertFalse(service.bolehMulai(siswa, ujian));
        assertTrue(service.getPesanStatusAkses(ujian).contains("belum dibuka"));

        // Saat jadwal: boleh mulai
        clock.setWaktu(tMulai.plusMinutes(10));
        assertTrue(service.bolehMulai(siswa, ujian));

        // Setelah jadwal: tidak boleh mulai
        clock.setWaktu(tSelesai.plusMinutes(5));
        assertFalse(service.bolehMulai(siswa, ujian));
        assertTrue(service.getPesanStatusAkses(ujian).contains("berakhir"));
    }

    @Test
    void ujiFormatUtilWaktuAkses() {
        LocalDateTime dt = LocalDateTime.of(2026, 11, 10, 14, 30);
        assertEquals("2026-11-10 14:30", FormatUtil.formatInputAkses(dt));
        assertEquals(dt, FormatUtil.parseInputAkses("2026-11-10 14:30"));
        assertNull(FormatUtil.parseInputAkses("   "));
        assertNull(FormatUtil.parseInputAkses(null));

        String labelRentang = FormatUtil.formatRentangWaktuAkses(dt, dt.plusHours(2));
        assertNotNull(labelRentang);
        assertTrue(labelRentang.contains("10 Nov 2026"));
    }

    @Test
    void ujiBankSoalParserDanWriterAkses() throws Exception {
        LocalDateTime tMulai = LocalDateTime.of(2026, 11, 10, 8, 0);
        LocalDateTime tSelesai = LocalDateTime.of(2026, 11, 10, 12, 0);

        Exam ujian = new Exam("QZ-10", "Kuis Algoritma", "QZ1099", 45, 80.0,
                List.of(new TrueFalseQuestion(1, "Java itu OOP?", 10.0, 'T')), false, tMulai, tSelesai);

        Path fileUjian = folder.resolve("bank_soal.txt");
        QuestionBankWriter writer = new QuestionBankWriter();
        writer.tulis(fileUjian, List.of(ujian));

        String isi = Files.readString(fileUjian);
        assertTrue(isi.contains("AKSES_MULAI=2026-11-10 08:00"));
        assertTrue(isi.contains("AKSES_SELESAI=2026-11-10 12:00"));

        QuestionBankReader reader = new QuestionBankReader();
        List<Exam> hasil = reader.baca(fileUjian);
        assertEquals(1, hasil.size());
        Exam dibaca = hasil.get(0);
        assertEquals("QZ-10", dibaca.getKodeUjian());
        assertEquals(tMulai, dibaca.getWaktuMulai());
        assertEquals(tSelesai, dibaca.getWaktuSelesai());
    }

    @Test
    void ujiKursusReaderDanWriterMateriQuiz() throws Exception {
        QuizMaterial quiz = new QuizMaterial("QZ01", "Kuis Pemrograman Dasar", "QZ-01");
        assertEquals(MaterialType.QUIZ, quiz.getJenis());
        assertEquals("QZ-01", quiz.getKodeUjian());

        Chapter bab = new Chapter(1, "Pendahuluan", List.of(quiz));
        Course kursus = new Course("K-01", "Belajar Java", "Budi", "Deskripsi", List.of(bab));

        Path fileKursus = folder.resolve("kursus.txt");
        CourseWriter writer = new CourseWriter(folder);
        writer.tulis(fileKursus, List.of(kursus));

        String teks = Files.readString(fileKursus);
        assertTrue(teks.contains("QUIZ|QZ01|Kuis Pemrograman Dasar|QZ-01"));

        CourseReader reader = new CourseReader(folder);
        List<Course> hasil = reader.baca(fileKursus);
        assertEquals(1, hasil.size());
        assertEquals(1, hasil.get(0).getDaftarBab().size());
        assertEquals(1, hasil.get(0).getDaftarBab().get(0).getDaftarMateri().size());

        var m = hasil.get(0).getDaftarBab().get(0).getDaftarMateri().get(0);
        assertInstanceOf(QuizMaterial.class, m);
        QuizMaterial mq = (QuizMaterial) m;
        assertEquals("QZ-01", mq.getKodeUjian());
        assertEquals("Kuis Pemrograman Dasar", mq.getJudul());
    }
}
