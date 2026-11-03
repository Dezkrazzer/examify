package com.examify.service;

import com.examify.io.SertifikatWriter;
import com.examify.model.HasilUjian;
import com.examify.model.ModeSubmit;
import com.examify.model.RiwayatNilai;
import com.examify.model.Siswa;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import com.examify.utils.AppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SertifikatServiceTest {

    @TempDir
    Path folder;

    private final Siswa siswa = new Siswa("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final Ujian ujian = new Ujian("MTK-01", "Matematika", "MTK7X2", 30, 75, List.of(
            new SoalTrueFalse(1, "a", 10, 'T')));

    private RiwayatNilai riwayat(String jawaban) throws Exception {
        HasilUjian hasil = new HasilUjian(siswa, ujian);
        hasil.simpanJawaban(1, jawaban);
        hasil.finalisasi(ModeSubmit.MANUAL);
        return RiwayatNilai.dari(hasil);
    }

    private SertifikatService service() {
        return new SertifikatService(new SertifikatWriter(folder));
    }

    @Test
    void setiapBarisSertifikatRataDenganLebarTetap() throws Exception {
        String teks = service().buatTeks(riwayat("T"), ujian);
        teks.lines().forEach(baris ->
                assertEquals(AppConfig.LEBAR_SERTIFIKAT + 2, baris.length(), baris));
        assertTrue(teks.contains("ANDI PRATAMA"));
        assertTrue(teks.contains("EXF/MTK-01/2410001/"));
        assertTrue(teks.contains("KKM 75.00"));
    }

    @Test
    void sertifikatDisimpanDenganPolaNamaFile() throws Exception {
        Path file = service().simpan(riwayat("T"), ujian);
        assertEquals("sertifikat_2410001_MTK-01.txt", file.getFileName().toString());
        assertTrue(Files.size(file) > 0);
    }

    @Test
    void sertifikatDitolakBilaTidakLulus() throws Exception {
        RiwayatNilai gagal = riwayat("F");
        assertThrows(IllegalStateException.class, () -> service().simpan(gagal, ujian));
    }
}
