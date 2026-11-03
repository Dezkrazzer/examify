package com.examify.service;

import com.examify.io.ProgresBelajarReader;
import com.examify.io.ProgresBelajarWriter;
import com.examify.io.RekapNilaiWriter;
import com.examify.model.Bab;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriTautan;
import com.examify.model.MateriUjian;
import com.examify.model.Siswa;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgresBelajarServiceTest {

    @TempDir
    Path folder;

    private final Siswa siswa = new Siswa("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final Ujian ujian = new Ujian("PBO-01", "PBO", "PBO123", 10, 60, List.of(
            new SoalTrueFalse(1, "a", 10, 'T')));
    private final Materi bacaan = new MateriTautan("m1", "Bacaan", "https://dev.java/learn/");
    private final Materi kuis = new MateriUjian("m2", "Kuis", "PBO-01");
    private final Kursus kursus = new Kursus("PBO", "PBO", "Bu Rina", "", List.of(
            new Bab(1, "Bab 1", List.of(bacaan, kuis))));

    private Path fileProgres;
    private UjianService ujianService;
    private KursusService kursusService;
    private RekapService rekapService;

    @BeforeEach
    void siapkan() {
        fileProgres = folder.resolve("progres.txt");
        rekapService = new RekapService(new RekapNilaiWriter(folder.resolve("rekap.txt")));
        ujianService = new UjianService(List.of(ujian), rekapService);
        kursusService = new KursusService(List.of(kursus), ujianService);
    }

    private ProgresBelajarService service() throws Exception {
        return new ProgresBelajarService(new ProgresBelajarWriter(fileProgres),
                new ProgresBelajarReader().baca(fileProgres), kursusService, rekapService);
    }

    @Test
    void materiSelesaiDicatatSekaliDanBertahanDiSesiBaru() throws Exception {
        ProgresBelajarService progres = service();
        progres.tandaiSelesai(siswa, kursus, bacaan);
        progres.tandaiSelesai(siswa, kursus, bacaan);
        assertEquals(2, Files.readAllLines(fileProgres).size()); // header + 1 baris

        assertTrue(service().isSelesai(siswa, kursus, bacaan));
        assertEquals(0.5, service().getProgres(siswa, kursus));
    }

    @Test
    void materiUjianSelesaiSetelahUjianDikerjakan() throws Exception {
        ProgresBelajarService progres = service();
        ujianService.selesaikanPaksa(ujianService.mulaiUjian(siswa, ujian));
        assertTrue(progres.isSelesai(siswa, kursus, kuis));
        assertEquals(1, progres.getJumlahSelesai(siswa, kursus));
    }

    @Test
    void kursusDenganUjianTidakTerdaftarDitolak() {
        Kursus rusak = new Kursus("XX", "Rusak", "", "", List.of(
                new Bab(1, "Bab", List.of(new MateriUjian("u", "Ujian", "TIDAK-ADA")))));
        assertThrows(IllegalStateException.class, () -> new KursusService(List.of(rusak), ujianService));
    }
}
