package com.examify.service;

import com.examify.app.Layar;
import com.examify.io.KursusReader;
import com.examify.io.KursusWriter;
import com.examify.io.ProgresBelajarWriter;
import com.examify.io.RekapNilaiWriter;
import com.examify.model.AgendaKalender;
import com.examify.model.AgendaKalender.JenisAgenda;
import com.examify.model.Bab;
import com.examify.model.Guru;
import com.examify.model.HasilUjian;
import com.examify.model.Kursus;
import com.examify.model.MateriQuiz;
import com.examify.model.MateriTugas;
import com.examify.model.MateriUjian;
import com.examify.model.ModeSubmit;
import com.examify.model.Siswa;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import com.examify.model.WaktuTerkendali;
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

class KalenderServiceTest {

    @TempDir
    Path tempDir;

    private Siswa siswaA;
    private Guru guruSri;
    private RekapService rekapService;
    private UjianService ujianService;
    private KursusService kursusService;
    private ProgresBelajarService progresService;
    private PendaftaranKursusService pendaftaranService;
    private TugasService tugasService;
    private KalenderService kalenderService;

    private Kursus kursusBin;
    private Kursus kursusSastraSri;
    private Kursus kursusMtk;
    private Kursus kursusBelumDiikuti;
    private Ujian ujianBin;
    private Ujian kuisMtkMultiHari;

    @BeforeEach
    void setUp() {
        siswaA = new Siswa("25001", "Raka Pratama", "XI-IPA-1", "1234");
        guruSri = new Guru("1988001", "Bu Sri", "1234", "Bahasa Indonesia");

        WaktuTerkendali clock = new WaktuTerkendali();
        clock.setWaktu(LocalDateTime.of(2026, 11, 10, 9, 0));

        rekapService = new RekapService(new RekapNilaiWriter(tempDir.resolve("rekap.txt")));

        // Ujian satu hari: 10 Nov 2026 pukul 08:00 - 11:00
        ujianBin = new Ujian(
                "UJ-BIN1",
                "Bahasa Indonesia",
                "TOKBIN",
                60,
                75.0,
                List.of(new SoalTrueFalse(1, "Kalimat baku tepat?", 10.0, 'T')),
                false,
                LocalDateTime.of(2026, 11, 10, 8, 0),
                LocalDateTime.of(2026, 11, 10, 11, 0)
        );

        // Kuis multi-hari: 12 Nov 2026 08:00 s.d. 14 Nov 2026 23:59
        kuisMtkMultiHari = new Ujian(
                "QZ-MTK1",
                "Matematika",
                "TOKMTK",
                30,
                70.0,
                List.of(new SoalTrueFalse(1, "2 + 2 = 4?", 10.0, 'T')),
                true,
                LocalDateTime.of(2026, 11, 12, 8, 0),
                LocalDateTime.of(2026, 11, 14, 23, 59)
        );

        // Ujian tanpa jadwal (tidak boleh muncul sebagai event bertanggal di kalender)
        Ujian ujianTanpaJadwal = new Ujian(
                "UJ-FIS1",
                "Fisika",
                "TOKFIS",
                45,
                75.0,
                List.of(new SoalTrueFalse(1, "F = m * a?", 10.0, 'T'))
        );

        ujianService = new UjianService(List.of(ujianBin, kuisMtkMultiHari, ujianTanpaJadwal), rekapService, clock);

        MateriTugas tugasBinKelasA = new MateriTugas(
                "tgs-bin-1",
                "Esai Kata Baku",
                LocalDateTime.of(2026, 11, 10, 23, 59),
                "XI-IPA-1",
                "Tulis esai 500 kata"
        );
        MateriTugas tugasBinKelasLain = new MateriTugas(
                "tgs-bin-2",
                "Tugas Khusus XI-IPA-2",
                LocalDateTime.of(2026, 11, 11, 23, 59),
                "XI-IPA-2",
                "Khusus kelas sebelah"
        );
        MateriUjian materiUjianBin = new MateriUjian("uj-bin-1", "UTS Bahasa Indonesia", "UJ-BIN1");

        kursusBin = new Kursus("BIN", "Bahasa Indonesia", "Bu Sri", "Kursus BIN",
                List.of(new Bab(1, "Bab 1: Kata Baku", List.of(tugasBinKelasA, tugasBinKelasLain, materiUjianBin))));

        // Kursus kedua yang dibuat guruSri (memakai nipPembuat walau label pengajar dikustomisasi)
        MateriTugas tugasSastra = new MateriTugas(
                "tgs-sas-1",
                "Resensi Novel",
                LocalDateTime.of(2026, 12, 5, 20, 0),
                "Semua Kelas",
                "Buat resensi novel Nusantara"
        );
        kursusSastraSri = new Kursus("SAS", "Sastra Nusantara", "Tim Bahasa (Sri)", "1988001", "Kursus Sastra",
                List.of(new Bab(1, "Bab 1: Prosa", List.of(tugasSastra))));

        MateriQuiz materiKuisMtk = new MateriQuiz("qz-mtk-1", "Kuis Aljabar", "QZ-MTK1");
        MateriTugas tugasMtkDesember = new MateriTugas(
                "tgs-mtk-1",
                "Latihan Matriks",
                LocalDateTime.of(2026, 12, 3, 17, 0),
                "Semua Kelas",
                "Kerjakan soal matriks"
        );
        kursusMtk = new Kursus("MTK", "Matematika", "Pak Budi", "Kursus MTK",
                List.of(new Bab(1, "Bab 1: Aljabar", List.of(materiKuisMtk, tugasMtkDesember))));

        MateriTugas tugasFisika = new MateriTugas(
                "tgs-fis-1",
                "Laporan Praktikum",
                LocalDateTime.of(2026, 11, 15, 23, 59),
                "Semua Kelas",
                "Praktikum gerak lurus"
        );
        MateriUjian materiUjianFisika = new MateriUjian("uj-fis-1", "Ujian Harian Fisika", "UJ-FIS1");
        kursusBelumDiikuti = new Kursus("FIS", "Fisika Dasar", "Bu Ani", "Kursus FIS",
                List.of(new Bab(1, "Bab 1: Mekanika", List.of(tugasFisika, materiUjianFisika))));

        kursusService = new KursusService(
                List.of(kursusBin, kursusSastraSri, kursusMtk, kursusBelumDiikuti),
                ujianService
        );
        progresService = new ProgresBelajarService(
                new ProgresBelajarWriter(tempDir.resolve("progres.txt")),
                List.of(), kursusService, rekapService, clock);
        pendaftaranService = new PendaftaranKursusService(tempDir.resolve("pendaftaran.txt"), progresService);
        tugasService = new TugasService(tempDir.resolve("pengumpulan.txt"), tempDir.resolve("berkas"));

        kalenderService = new KalenderService(
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
        assertTrue(Layar.KALENDER.isDalamShell());
        assertNotNull(Layar.class.getResource(Layar.KALENDER.getFxmlPath()),
                "File kalender.fxml harus tersedia di classpath");
    }

    @Test
    void kalenderKosongSebelumSiswaMasukKursus() {
        assertTrue(kalenderService.getKursusDiikuti(siswaA).isEmpty());
        assertTrue(kalenderService.getSemuaAgenda(siswaA, null).isEmpty());
    }

    @Test
    void sinkronisasiHanyaDariKursusDiikutiDanSesuaiTargetKelas() throws Exception {
        pendaftaranService.daftarkan(siswaA, kursusBin);
        pendaftaranService.daftarkan(siswaA, kursusMtk);

        List<Kursus> diikuti = kalenderService.getKursusDiikuti(siswaA);
        assertEquals(2, diikuti.size());

        List<AgendaKalender> semua = kalenderService.getSemuaAgenda(siswaA, null);
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

        List<AgendaKalender> novSemua = kalenderService.filterBulan(siswaA, YearMonth.of(2026, 11), null);
        assertEquals(4, novSemua.size());

        List<AgendaKalender> desSemua = kalenderService.filterBulan(siswaA, YearMonth.of(2026, 12), null);
        assertEquals(1, desSemua.size());
        assertEquals("Latihan Matriks", desSemua.get(0).judul());

        List<AgendaKalender> novHanyaBin = kalenderService.filterBulan(siswaA, YearMonth.of(2026, 11), "BIN");
        assertEquals(2, novHanyaBin.size());
        assertTrue(novHanyaBin.stream().allMatch(a -> a.kursus().getKode().equals("BIN")));

        List<AgendaKalender> hari10Nov = kalenderService.filterHari(siswaA, LocalDate.of(2026, 11, 10), null);
        assertEquals(2, hari10Nov.size());
        assertEquals(JenisAgenda.UJIAN, hari10Nov.get(0).jenis());
        assertEquals(JenisAgenda.TUGAS, hari10Nov.get(1).jenis());

        LocalDateTime pukulSembilanTigaPuluh = LocalDateTime.of(2026, 11, 10, 9, 30);
        List<AgendaKalender> upcomingPagi = kalenderService.filterMendatang(siswaA, pukulSembilanTigaPuluh, null);
        assertEquals(5, upcomingPagi.size());
        assertTrue(upcomingPagi.get(0).isSedangBerlangsung(pukulSembilanTigaPuluh));

        LocalDateTime pukulDuaBelasSiang = LocalDateTime.of(2026, 11, 10, 12, 0);
        List<AgendaKalender> upcomingSiang = kalenderService.filterMendatang(siswaA, pukulDuaBelasSiang, null);
        assertEquals(4, upcomingSiang.size());
        assertEquals("Esai Kata Baku", upcomingSiang.get(0).judul());
    }

    @Test
    void statusSelesaiTersinkronDenganPengumpulanTugasDanRekapUjian() throws Exception {
        pendaftaranService.daftarkan(siswaA, kursusBin);

        List<AgendaKalender> awal = kalenderService.filterHari(siswaA, LocalDate.of(2026, 11, 10), "BIN");
        assertEquals(2, awal.size());
        assertFalse(awal.get(0).selesai());
        assertFalse(awal.get(1).selesai());

        File fileTugas = tempDir.resolve("esai.pdf").toFile();
        Files.writeString(fileTugas.toPath(), "Isi esai kata baku");
        tugasService.simpanPengumpulan("tgs-bin-1", siswaA, fileTugas, "Sudah dikumpulkan");

        HasilUjian hasil = ujianService.mulaiUjian(siswaA, ujianBin);
        ujianService.selesaikan(hasil, ModeSubmit.MANUAL);

        List<AgendaKalender> setelahSelesai = kalenderService.filterHari(siswaA, LocalDate.of(2026, 11, 10), "BIN");
        assertEquals(2, setelahSelesai.size());
        assertTrue(setelahSelesai.get(0).selesai());
        assertEquals("Sudah Dikerjakan", setelahSelesai.get(0).getLabelStatus(LocalDateTime.of(2026, 11, 10, 10, 0)));
        assertTrue(setelahSelesai.get(1).selesai());
        assertEquals("Sudah Dikumpulkan", setelahSelesai.get(1).getLabelStatus(LocalDateTime.of(2026, 11, 10, 10, 0)));
    }

    @Test
    void kalenderGuruMenampilkanHanyaKursusYangDibuatGuruBesertaStatistikPartisipasi() throws Exception {
        // Guru Sri memiliki kursusBin (cocok nama pengajar) dan kursusSastraSri (cocok NIP pembuat)
        List<Kursus> kursusSri = kalenderService.getKursusDibuatGuru(guruSri);
        assertEquals(2, kursusSri.size());
        assertTrue(kursusSri.stream().anyMatch(k -> k.getKode().equals("BIN")));
        assertTrue(kursusSri.stream().anyMatch(k -> k.getKode().equals("SAS")));
        assertTrue(kursusSri.stream().noneMatch(k -> k.getKode().equals("MTK")));

        // Untuk guru, seluruh tugas pada kursusnya tampil (termasuk XI-IPA-1 dan XI-IPA-2)
        // BIN: 1 ujian + 2 tugas = 3 agenda; SAS: 1 tugas = 1 agenda -> Total = 4 agenda
        List<AgendaKalender> semuaGuru = kalenderService.getSemuaAgendaGuru(guruSri, null);
        assertEquals(4, semuaGuru.size());

        // Filter kursus khusus SAS pada kalender Guru
        List<AgendaKalender> hanyaSas = kalenderService.filterBulanGuru(guruSri, YearMonth.of(2026, 12), "SAS");
        assertEquals(1, hanyaSas.size());
        assertEquals("Resensi Novel", hanyaSas.get(0).judul());
        assertEquals("Kelola Tugas", hanyaSas.get(0).jenis().getTeksTombol(true));

        // Cek statistik partisipasi pada kalender guru setelah siswa mengumpulkan tugas & ujian
        File fileTugas = tempDir.resolve("esai_siswa.pdf").toFile();
        Files.writeString(fileTugas.toPath(), "Jawaban tugas");
        tugasService.simpanPengumpulan("tgs-bin-1", siswaA, fileTugas, "");

        HasilUjian hasil = ujianService.mulaiUjian(siswaA, ujianBin);
        ujianService.selesaikan(hasil, ModeSubmit.MANUAL);

        List<AgendaKalender> hari10Guru = kalenderService.filterHariGuru(guruSri, LocalDate.of(2026, 11, 10), "BIN");
        assertEquals(2, hari10Guru.size());
        assertEquals("Sedang Dibuka · 1 peserta",
                hari10Guru.get(0).getLabelStatus(LocalDateTime.of(2026, 11, 10, 9, 30)));
        assertEquals("1 dikumpulkan",
                hari10Guru.get(1).getLabelStatus(LocalDateTime.of(2026, 11, 10, 9, 30)));
    }

    @Test
    void nipPembuatKursusTersimpanDanTerbacaKembali() throws Exception {
        Path fileKursus = tempDir.resolve("kursus_guru.txt");
        KursusWriter writer = new KursusWriter(tempDir);
        writer.tulis(fileKursus, List.of(kursusSastraSri));

        KursusReader reader = new KursusReader(tempDir);
        List<Kursus> hasil = reader.baca(fileKursus);
        assertEquals(1, hasil.size());
        assertEquals("1988001", hasil.get(0).getNipPembuat());
        assertTrue(kalenderService.isKursusMilikGuru(guruSri, hasil.get(0)));
    }
}
