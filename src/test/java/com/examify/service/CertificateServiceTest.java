package com.examify.service;

import com.examify.io.CertificateWriter;
import com.examify.model.ExamResult;
import com.examify.model.SubmitMode;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import com.examify.utils.AppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateServiceTest {

    @TempDir
    Path folder;

    private final Student siswa = new Student("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final Exam ujian = new Exam("MTK-01", "Matematika", "MTK7X2", 30, 75, List.of(
            new TrueFalseQuestion(1, "a", 10, 'T')));

    private GradeHistory riwayat(String jawaban) throws Exception {
        ExamResult hasil = new ExamResult(siswa, ujian);
        hasil.simpanJawaban(1, jawaban);
        hasil.finalisasi(SubmitMode.MANUAL);
        return GradeHistory.dari(hasil);
    }

    private CertificateService service() {
        return new CertificateService(new CertificateWriter(folder));
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
        GradeHistory gagal = riwayat("F");
        assertThrows(IllegalStateException.class, () -> service().simpan(gagal, ujian));
    }
}
