package com.examify.service;

import com.examify.app.AppScreen;
import com.examify.io.CourseReader;
import com.examify.io.CourseWriter;
import com.examify.io.LearningProgressWriter;
import com.examify.io.GradeRecapWriter;
import com.examify.model.CalendarAgenda;
import com.examify.model.CalendarAgenda.JenisAgenda;
import com.examify.model.Chapter;
import com.examify.model.Teacher;
import com.examify.model.ExamResult;
import com.examify.model.Course;
import com.examify.model.QuizMaterial;
import com.examify.model.AssignmentMaterial;
import com.examify.model.ExamMaterial;
import com.examify.model.SubmitMode;
import com.examify.model.Student;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import com.examify.model.ControlledClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarServiceTest {

    @TempDir
    Path tempDir;

    private Student siswaA;
    private Teacher guruSri;
    private GradeRecapService rekapService;
    private ExamService ujianService;
    private CourseService kursusService;
    private LearningProgressService progresService;
    private CourseEnrollmentService pendaftaranService;
    private AssignmentService tugasService;
    private CalendarService kalenderService;

    private Course kursusBin;
    private Course kursusSastraSri;
    private Course kursusMtk;
    private Course kursusBelumDiikuti;
    private Exam ujianBin;
    private Exam kuisMtkMultiHari;

    @BeforeEach
    void setUp() {
        siswaA = new Student("25001", "Raka Pratama", "XI-IPA-1", "1234");
        guruSri = new Teacher("1988001", "Bu Sri", "1234", "Bahasa Indonesia");

        ControlledClock clock = new ControlledClock();
        clock.setWaktu(LocalDateTime.of(2026, 11, 10, 9, 0));

        rekapService = new GradeRecapService(new GradeRecapWriter(tempDir.resolve("rekap.txt")));

        // Ujian satu hari: 10 Nov 2026 pukul 08:00 - 11:00
        ujianBin = new Exam(
                "UJ-BIN1",
                "Bahasa Indonesia",
                "TOKBIN",
                60,
                75.0,
                List.of(new TrueFalseQuestion(1, "Kalimat baku tepat?", 10.0, 'T')),
                false,
                LocalDateTime.of(2026, 11, 10, 8, 0),
                LocalDateTime.of(2026, 11, 10, 11, 0)
        );

        // Kuis multi-hari: 12 Nov 2026 08:00 s.d. 14 Nov 2026 23:59
        kuisMtkMultiHari = new Exam(
                "QZ-MTK1",
                "Matematika",
                "TOKMTK",
                30,
                70.0,
                List.of(new TrueFalseQuestion(1, "2 + 2 = 4?", 10.0, 'T')),
                true,
                LocalDateTime.of(2026, 11, 12, 8, 0),
                LocalDateTime.of(2026, 11, 14, 23, 59)
        );

        // Ujian tanpa jadwal (tidak boleh muncul sebagai event bertanggal di kalender)
        Exam ujianTanpaJadwal = new Exam(
                "UJ-FIS1",
                "Fisika",
                "TOKFIS",
                45,
                75.0,
                List.of(new TrueFalseQuestion(1, "F = m * a?", 10.0, 'T'))
        );

        ujianService = new ExamService(List.of(ujianBin, kuisMtkMultiHari, ujianTanpaJadwal), rekapService, clock);

        AssignmentMaterial tugasBinKelasA = new AssignmentMaterial(
                "tgs-bin-1",
                "Esai Kata Baku",
                LocalDateTime.of(2026, 11, 10, 23, 59),
                "XI-IPA-1",
                "Tulis esai 500 kata"
        );
        AssignmentMaterial tugasBinKelasLain = new AssignmentMaterial(
                "tgs-bin-2",
                "Tugas Khusus XI-IPA-2",
                LocalDateTime.of(2026, 11, 11, 23, 59),
                "XI-IPA-2",
                "Khusus kelas sebelah"
        );
        ExamMaterial materiUjianBin = new ExamMaterial("uj-bin-1", "UTS Bahasa Indonesia", "UJ-BIN1");

        kursusBin = new Course("BIN", "Bahasa Indonesia", "Bu Sri", "Kursus BIN",
                List.of(new Chapter(1, "Bab 1: Kata Baku", List.of(tugasBinKelasA, tugasBinKelasLain, materiUjianBin))));

        // Kursus kedua yang dibuat guruSri (memakai nipPembuat walau label pengajar dikustomisasi)
        AssignmentMaterial tugasSastra = new AssignmentMaterial(
                "tgs-sas-1",
                "Resensi Novel",
                LocalDateTime.of(2026, 12, 5, 20, 0),
                "Semua Kelas",
                "Buat resensi novel Nusantara"
        );
        kursusSastraSri = new Course("SAS", "Sastra Nusantara", "Tim Bahasa (Sri)", "1988001", "Kursus Sastra",
                List.of(new Chapter(1, "Bab 1: Prosa", List.of(tugasSastra))));

        QuizMaterial materiKuisMtk = new QuizMaterial("qz-mtk-1", "Kuis Aljabar", "QZ-MTK1");
        AssignmentMaterial tugasMtkDesember = new AssignmentMaterial(
                "tgs-mtk-1",
                "Latihan Matriks",
                LocalDateTime.of(2026, 12, 3, 17, 0),
                "Semua Kelas",
                "Kerjakan soal matriks"
        );
        kursusMtk = new Course("MTK", "Matematika", "Pak Budi", "Kursus MTK",
                List.of(new Chapter(1, "Bab 1: Aljabar", List.of(materiKuisMtk, tugasMtkDesember))));

        AssignmentMaterial tugasFisika = new AssignmentMaterial(
                "tgs-fis-1",
                "Laporan Praktikum",
                LocalDateTime.of(2026, 11, 15, 23, 59),
                "Semua Kelas",
                "Praktikum gerak lurus"
        );
        ExamMaterial materiUjianFisika = new ExamMaterial("uj-fis-1", "Ujian Harian Fisika", "UJ-FIS1");
        kursusBelumDiikuti = new Course("FIS", "Fisika Dasar", "Bu Ani", "Kursus FIS",
                List.of(new Chapter(1, "Bab 1: Mekanika", List.of(tugasFisika, materiUjianFisika))));

        kursusService = new CourseService(
                List.of(kursusBin, kursusSastraSri, kursusMtk, kursusBelumDiikuti),
                ujianService
        );
        progresService = new LearningProgressService(
                new LearningProgressWriter(tempDir.resolve("progres.txt")),
                List.of(), kursusService, rekapService, clock);
        pendaftaranService = new CourseEnrollmentService(tempDir.resolve("pendaftaran.txt"), progresService);
        tugasService = new AssignmentService(tempDir.resolve("pengumpulan.txt"), tempDir.resolve("berkas"));

        kalenderService = new CalendarService(
                kursusService,
                ujianService,
                pendaftaranService,
                progresService,
                tugasService,
                rekapService
        );
    }

    @Test
    void resourceFxmlKalenderTerdaftarDalamShell() {
        assertTrue(AppScreen.KALENDER.isDalamShell());
        assertNotNull(AppScreen.class.getResource(AppScreen.KALENDER.getFxmlPath()),
                "File kalender.fxml harus tersedia di classpath");
    }

    @Test
    void kalenderKosongSebelumSiswaMasukKursus() {
        assertTrue(kalenderService.getKursusDiikuti(siswaA).isEmpty());
        assertTrue(kalenderService.getSemuaAgenda(siswaA, null).isEmpty());
        assertTrue(kalenderService.filterBulan(siswaA, YearMonth.of(2026, 11), null).isEmpty());
        assertTrue(kalenderService.filterHari(siswaA, LocalDate.of(2026, 11, 10), null).isEmpty());
        assertTrue(kalenderService.filterMendatang(siswaA, LocalDateTime.of(2026, 11, 10, 9, 0), null).isEmpty());
    }

    @Test
    void sinkronisasiHanyaDariKursusDiikutiDanSesuaiTargetKelas() throws Exception {
        pendaftaranService.daftarkan(siswaA, kursusBin);
        pendaftaranService.daftarkan(siswaA, kursusMtk);

        List<Course> diikuti = kalenderService.getKursusDiikuti(siswaA);
        assertEquals(2, diikuti.size());

        List<CalendarAgenda> semua = kalenderService.getSemuaAgenda(siswaA, null);
        // BIN: 1 ujian satu-hari + 1 tugas XI-IPA-1 = 2 agenda
        // MTK: 1 kuis multi-hari (2 agenda) + 1 tugas Desember = 3 agenda
        assertEquals(5, semua.size());
        assertTrue(semua.stream().noneMatch(a -> a.kursus().getKode().equals("FIS")));
        assertTrue(semua.stream().noneMatch(a -> a.kursus().getKode().equals("SAS")));
        assertTrue(semua.stream().noneMatch(a -> a.judul().contains("XI-IPA-2")));
    }

    @Test
    void filterBulanHariUpcomingDanFilterKursusBerfungsiAkurat() throws Exception {
        pendaftaranService.daftarkan(siswaA, kursusBin);
        pendaftaranService.daftarkan(siswaA, kursusMtk);

        List<CalendarAgenda> novSemua = kalenderService.filterBulan(siswaA, YearMonth.of(2026, 11), null);
        assertEquals(4, novSemua.size());

        List<CalendarAgenda> desSemua = kalenderService.filterBulan(siswaA, YearMonth.of(2026, 12), null);
        assertEquals(1, desSemua.size());
        assertEquals("Latihan Matriks", desSemua.get(0).judul());

        List<CalendarAgenda> novHanyaBin = kalenderService.filterBulan(siswaA, YearMonth.of(2026, 11), "BIN");
        assertEquals(2, novHanyaBin.size());
        assertTrue(novHanyaBin.stream().allMatch(a -> a.kursus().getKode().equals("BIN")));

        List<CalendarAgenda> hari10Nov = kalenderService.filterHari(siswaA, LocalDate.of(2026, 11, 10), null);
        assertEquals(2, hari10Nov.size());
        assertEquals(JenisAgenda.UJIAN, hari10Nov.get(0).jenis());
        assertEquals(JenisAgenda.TUGAS, hari10Nov.get(1).jenis());

        LocalDateTime pukulSembilanTigaPuluh = LocalDateTime.of(2026, 11, 10, 9, 30);
        List<CalendarAgenda> upcomingPagi = kalenderService.filterMendatang(siswaA, pukulSembilanTigaPuluh, null);
        assertEquals(5, upcomingPagi.size());
        assertTrue(upcomingPagi.get(0).isSedangBerlangsung(pukulSembilanTigaPuluh));

        LocalDateTime pukulDuaBelasSiang = LocalDateTime.of(2026, 11, 10, 12, 0);
        List<CalendarAgenda> upcomingSiang = kalenderService.filterMendatang(siswaA, pukulDuaBelasSiang, null);
        assertEquals(4, upcomingSiang.size());
        assertEquals("Esai Kata Baku", upcomingSiang.get(0).judul());
    }

    @Test
    void statusSelesaiTersinkronDenganPengumpulanTugasDanRekapUjian() throws Exception {
        pendaftaranService.daftarkan(siswaA, kursusBin);

        List<CalendarAgenda> awal = kalenderService.filterHari(siswaA, LocalDate.of(2026, 11, 10), "BIN");
        assertEquals(2, awal.size());
        assertFalse(awal.get(0).selesai());
        assertFalse(awal.get(1).selesai());

        File fileTugas = tempDir.resolve("esai.pdf").toFile();
        Files.writeString(fileTugas.toPath(), "Isi esai kata baku");
        tugasService.simpanPengumpulan("tgs-bin-1", siswaA, fileTugas, "Sudah dikumpulkan");

        ExamResult hasil = ujianService.mulaiUjian(siswaA, ujianBin);
        ujianService.selesaikan(hasil, SubmitMode.MANUAL);

        List<CalendarAgenda> setelahSelesai = kalenderService.filterHari(siswaA, LocalDate.of(2026, 11, 10), "BIN");
        assertEquals(2, setelahSelesai.size());
        assertTrue(setelahSelesai.get(0).selesai());
        assertEquals("Sudah Dikerjakan", setelahSelesai.get(0).getLabelStatus(LocalDateTime.of(2026, 11, 10, 10, 0)));
        assertTrue(setelahSelesai.get(1).selesai());
        assertEquals("Sudah Dikumpulkan", setelahSelesai.get(1).getLabelStatus(LocalDateTime.of(2026, 11, 10, 10, 0)));
    }

    @Test
    void kalenderGuruMenampilkanHanyaKursusYangDibuatGuruBesertaStatistikPartisipasi() throws Exception {
        // Guru Sri memiliki kursusBin (cocok nama pengajar) dan kursusSastraSri (cocok NIP pembuat)
        List<Course> kursusSri = kalenderService.getKursusDibuatGuru(guruSri);
        assertEquals(2, kursusSri.size());
        assertTrue(kursusSri.stream().anyMatch(k -> k.getKode().equals("BIN")));
        assertTrue(kursusSri.stream().anyMatch(k -> k.getKode().equals("SAS")));
        assertTrue(kursusSri.stream().noneMatch(k -> k.getKode().equals("MTK")));

        // Untuk guru, seluruh tugas pada kursusnya tampil (termasuk XI-IPA-1 dan XI-IPA-2)
        // BIN: 1 ujian + 2 tugas = 3 agenda; SAS: 1 tugas = 1 agenda -> Total = 4 agenda
        List<CalendarAgenda> semuaGuru = kalenderService.getSemuaAgendaGuru(guruSri, null);
        assertEquals(4, semuaGuru.size());

        // Filter kursus khusus SAS pada kalender Guru
        List<CalendarAgenda> hanyaSas = kalenderService.filterBulanGuru(guruSri, YearMonth.of(2026, 12), "SAS");
        assertEquals(1, hanyaSas.size());
        assertEquals("Resensi Novel", hanyaSas.get(0).judul());
        assertEquals("Kelola Tugas", hanyaSas.get(0).jenis().getTeksTombol(true));

        // Cek statistik partisipasi pada kalender guru setelah siswa mengumpulkan tugas & ujian
        File fileTugas = tempDir.resolve("esai_siswa.pdf").toFile();
        Files.writeString(fileTugas.toPath(), "Jawaban tugas");
        tugasService.simpanPengumpulan("tgs-bin-1", siswaA, fileTugas, "");

        ExamResult hasil = ujianService.mulaiUjian(siswaA, ujianBin);
        ujianService.selesaikan(hasil, SubmitMode.MANUAL);

        List<CalendarAgenda> hari10Guru = kalenderService.filterHariGuru(guruSri, LocalDate.of(2026, 11, 10), "BIN");
        assertEquals(2, hari10Guru.size());
        assertEquals("Sedang Dibuka · 1 peserta",
                hari10Guru.get(0).getLabelStatus(LocalDateTime.of(2026, 11, 10, 9, 30)));
        assertEquals("1 dikumpulkan",
                hari10Guru.get(1).getLabelStatus(LocalDateTime.of(2026, 11, 10, 9, 30)));
    }

    @Test
    void nipPembuatKursusTersimpanDanTerbacaKembali() throws Exception {
        Path fileKursus = tempDir.resolve("kursus_guru.txt");
        CourseWriter writer = new CourseWriter(tempDir);
        writer.tulis(fileKursus, List.of(kursusSastraSri));

        CourseReader reader = new CourseReader(tempDir);
        List<Course> hasil = reader.baca(fileKursus);
        assertEquals(1, hasil.size());
        assertEquals("1988001", hasil.get(0).getNipPembuat());
        assertTrue(kalenderService.isKursusMilikGuru(guruSri, hasil.get(0)));
    }

    @Test
    void guruHanyaDapatMengaksesKursusYangDibuatSendiri() {
        assertTrue(kursusService.isKursusMilikGuru(guruSri, kursusBin));
        assertTrue(kursusService.isKursusMilikGuru(guruSri, kursusSastraSri));
        assertFalse(kursusService.isKursusMilikGuru(guruSri, kursusMtk));
        assertFalse(kursusService.isKursusMilikGuru(guruSri, kursusBelumDiikuti));

        List<Course> dibuatGuru = kursusService.getKursusDibuatGuru(guruSri);
        assertEquals(2, dibuatGuru.size());
        assertTrue(dibuatGuru.stream().anyMatch(k -> k.getKode().equals("BIN")));
        assertTrue(dibuatGuru.stream().anyMatch(k -> k.getKode().equals("SAS")));
        assertTrue(dibuatGuru.stream().noneMatch(k -> k.getKode().equals("MTK")));

        Teacher guruLain = new Teacher("9999", "Pak Joko", "1234", "Fisika");
        assertTrue(kursusService.getKursusDibuatGuru(guruLain).isEmpty());
        assertFalse(kursusService.isKursusMilikGuru(guruLain, kursusBin));

        List<Course> cariSastra = kursusService.cariUntukGuru(guruSri, "Sastra");
        assertEquals(1, cariSastra.size());
        assertEquals("SAS", cariSastra.get(0).getKode());
    }
}
