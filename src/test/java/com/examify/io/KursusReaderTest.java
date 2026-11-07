package com.examify.io;

import com.examify.exception.FormatDataException;
import com.examify.model.Kursus;
import com.examify.model.MateriPdf;
import com.examify.model.MateriTautan;
import com.examify.model.MateriTeks;
import com.examify.model.MateriUjian;
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

class KursusReaderTest {

    @TempDir
    Path folder;
    private KursusReader reader;

    @BeforeEach
    void siapkan() throws Exception {
        Files.writeString(folder.resolve("bab1.md"), "# Halo");
        Files.writeString(folder.resolve("slide.pdf"), "%PDF-1.4");
        reader = new KursusReader(folder);
    }

    private List<Kursus> baca(String isi) throws Exception {
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
        Kursus kursus = baca(VALID).get(0);
        assertEquals("PBO", kursus.getKode());
        assertEquals(2, kursus.getDaftarBab().size());
        assertEquals(2, kursus.getDaftarBab().get(1).getNomor());
        assertEquals(4, kursus.getJumlahMateri());
        assertInstanceOf(MateriTeks.class, kursus.getSemuaMateri().get(0));
        assertInstanceOf(MateriTautan.class, kursus.getSemuaMateri().get(1));
        assertInstanceOf(MateriPdf.class, kursus.getSemuaMateri().get(2));
        MateriUjian uts = assertInstanceOf(MateriUjian.class, kursus.getSemuaMateri().get(3));
        assertEquals("PBO-01", uts.getKodeUjian());
    }

    @Test
    void keteranganMateriBerbedaPerJenis() throws Exception {
        Kursus kursus = baca(VALID).get(0);
        assertEquals("Tautan - dev.java", kursus.getSemuaMateri().get(1).getKeterangan());
        assertEquals("Ujian - PBO-01", kursus.getSemuaMateri().get(3).getKeterangan());
    }

    @Test
    void fileMateriTidakAdaDitolakDenganNomorBaris() {
        String isi = VALID.replace("bab1.md", "hilang.md");
        FormatDataException e = assertThrows(FormatDataException.class, () -> baca(isi));
        assertEquals(7, e.getNomorBaris());
    }

    @Test
    void tautanTanpaHttpDitolak() {
        assertThrows(FormatDataException.class, () -> baca(VALID.replace("https://dev.java/learn/", "dev.java")));
    }

    @Test
    void jenisMateriTidakDikenalDitolak() {
        assertThrows(FormatDataException.class, () -> baca(VALID.replace("PDF|p3", "AUDIO|p3")));
    }

    @Test
    void idMateriDuplikatDitolak() {
        assertThrows(FormatDataException.class, () -> baca(VALID.replace("PDF|p3", "PDF|p1")));
    }

    @Test
    void blokTanpaEndDitolak() {
        assertThrows(FormatDataException.class, () -> baca(VALID.replace("[END]", "")));
    }

    @Test
    void pencarianKursusTanpaPeduliKapital() throws Exception {
        Kursus kursus = baca(VALID).get(0);
        assertTrue(kursus.cocokDengan("berorientasi"));
        assertTrue(kursus.cocokDengan(" pbo "));
    }

    @Test
    void fileKursusContohTerbacaLengkap() throws Exception {
        List<Kursus> daftar = new KursusReader(AppConfig.DIR_DATA).baca(AppConfig.FILE_KURSUS);
        assertTrue(daftar.size() >= 3);
    }
}
