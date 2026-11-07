package com.examify.service;

import com.examify.io.BankSoalReader;
import com.examify.io.BankSoalWriter;
import com.examify.io.KursusReader;
import com.examify.io.KursusWriter;
import com.examify.io.RekapNilaiWriter;
import com.examify.model.Bab;
import com.examify.model.JenisMateri;
import com.examify.model.Kursus;
import com.examify.model.MateriQuiz;
import com.examify.model.Siswa;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import com.examify.model.WaktuTerkendali;
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

class UjianAksesDanQuizTest {

    @TempDir
    Path folder;

    private final Siswa siswa = new Siswa("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final WaktuTerkendali clock = new WaktuTerkendali();
    private Path fileRekap;
    private RekapService rekapService;

    @BeforeEach
    void setup() {
        fileRekap = folder.resolve("rekap.txt");
        rekapService = new RekapService(new RekapNilaiWriter(fileRekap));
    }

    @Test
    void ujiJadwalAksesUjian() {
        LocalDateTime tMulai = LocalDateTime.of(2026, 11, 10, 8, 0);
        LocalDateTime tSelesai = LocalDateTime.of(2026, 11, 10, 12, 0);

        Ujian ujian = new Ujian("QZ-01", "Kuis Bab 1", "ABC123", 30, 75.0,
                List.of(new SoalTrueFalse(1, "Soal 1", 10.0, 'T')), false, tMulai, tSelesai);

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
                new Ujian("QZ-02", "Kuis Error", "ABC123", 30, 75.0,
                        List.of(new SoalTrueFalse(1, "Soal 1", 10.0, 'T')), false, tSelesai, tMulai));
    }

    @Test
    void ujiUjianServiceBolehMulaiJadwalAkses() {
        LocalDateTime tMulai = LocalDateTime.of(2026, 11, 10, 8, 0);
        LocalDateTime tSelesai = LocalDateTime.of(2026, 11, 10, 12, 0);

        Ujian ujian = new Ujian("QZ-01", "Kuis Bab 1", "ABC123", 30, 75.0,
                List.of(new SoalTrueFalse(1, "Soal 1", 10.0, 'T')), false, tMulai, tSelesai);

        UjianService service = new UjianService(List.of(ujian), rekapService, clock);

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

        Ujian ujian = new Ujian("QZ-10", "Kuis Algoritma", "QZ1099", 45, 80.0,
                List.of(new SoalTrueFalse(1, "Java itu OOP?", 10.0, 'T')), false, tMulai, tSelesai);

        Path fileUjian = folder.resolve("bank_soal.txt");
        BankSoalWriter writer = new BankSoalWriter();
        writer.tulis(fileUjian, List.of(ujian));

        String isi = Files.readString(fileUjian);
        assertTrue(isi.contains("AKSES_MULAI=2026-11-10 08:00"));
        assertTrue(isi.contains("AKSES_SELESAI=2026-11-10 12:00"));

        BankSoalReader reader = new BankSoalReader();
        List<Ujian> hasil = reader.baca(fileUjian);
        assertEquals(1, hasil.size());
        Ujian dibaca = hasil.get(0);
        assertEquals("QZ-10", dibaca.getKodeUjian());
        assertEquals(tMulai, dibaca.getWaktuMulai());
        assertEquals(tSelesai, dibaca.getWaktuSelesai());
    }

    @Test
    void ujiKursusReaderDanWriterMateriQuiz() throws Exception {
        MateriQuiz quiz = new MateriQuiz("QZ01", "Kuis Pemrograman Dasar", "QZ-01");
        assertEquals(JenisMateri.QUIZ, quiz.getJenis());
        assertEquals("QZ-01", quiz.getKodeUjian());

        Bab bab = new Bab(1, "Pendahuluan", List.of(quiz));
        Kursus kursus = new Kursus("K-01", "Belajar Java", "Budi", "Deskripsi", List.of(bab));

        Path fileKursus = folder.resolve("kursus.txt");
        KursusWriter writer = new KursusWriter(folder);
        writer.tulis(fileKursus, List.of(kursus));

        String teks = Files.readString(fileKursus);
        assertTrue(teks.contains("QUIZ|QZ01|Kuis Pemrograman Dasar|QZ-01"));

        KursusReader reader = new KursusReader(folder);
        List<Kursus> hasil = reader.baca(fileKursus);
        assertEquals(1, hasil.size());
        assertEquals(1, hasil.get(0).getDaftarBab().size());
        assertEquals(1, hasil.get(0).getDaftarBab().get(0).getDaftarMateri().size());

        var m = hasil.get(0).getDaftarBab().get(0).getDaftarMateri().get(0);
        assertInstanceOf(MateriQuiz.class, m);
        MateriQuiz mq = (MateriQuiz) m;
        assertEquals("QZ-01", mq.getKodeUjian());
        assertEquals("Kuis Pemrograman Dasar", mq.getJudul());
    }
}
