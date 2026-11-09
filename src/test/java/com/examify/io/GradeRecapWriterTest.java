package com.examify.io;

import com.examify.model.ExamResult;
import com.examify.model.SubmitMode;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeRecapWriterTest {

    private final Student siswa = new Student("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final Exam ujian = new Exam("MTK-01", "Matematika", "MTK7X2", 30, 75, List.of(
            new TrueFalseQuestion(1, "a", 10, 'T'),
            new TrueFalseQuestion(2, "b", 10, 'F')));

    private GradeHistory riwayatSelesai() throws Exception {
        ExamResult hasil = new ExamResult(siswa, ujian);
        hasil.simpanJawaban(1, "T");
        hasil.finalisasi(SubmitMode.MANUAL);
        return GradeHistory.dari(hasil);
    }

    @Test // TC-12
    void headerDitulisSekaliLaluBarisDitambahkan(@TempDir Path folder) throws Exception {
        Path file = folder.resolve("rekap_nilai.txt");
        GradeRecapWriter writer = new GradeRecapWriter(file);
        writer.tulis(riwayatSelesai());
        writer.tulis(riwayatSelesai());

        List<String> baris = Files.readAllLines(file);
        assertEquals(3, baris.size());
        assertEquals(GradeRecapWriter.HEADER, baris.get(0));
    }

    @Test
    void formatBarisSesuaiDataContract() throws Exception {
        String[] field = new GradeRecapWriter(Path.of("x")).keBaris(riwayatSelesai()).split("\\|", -1);
        assertEquals(14, field.length);
        assertTrue(field[0].matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"));
        assertEquals("2410001", field[1]);
        assertEquals("MTK-01", field[4]);
        assertEquals("1|0|1", String.join("|", field[6], field[7], field[8]));
        assertEquals("50.00", field[9]); // titik desimal, bukan koma
        assertEquals("E", field[10]);
        assertEquals("TIDAK_LULUS", field[11]);
        assertEquals("MANUAL", field[12]);
        assertTrue(field[13].matches("\\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void riwayatYangDitulisDapatDibacaKembali(@TempDir Path folder) throws Exception {
        Path file = folder.resolve("rekap_nilai.txt");
        GradeHistory asli = riwayatSelesai();
        new GradeRecapWriter(file).tulis(asli);

        List<GradeHistory> terbaca = new GradeRecapReader().baca(file);
        assertEquals(1, terbaca.size());
        GradeHistory r = terbaca.get(0);
        assertEquals(asli.nis(), r.nis());
        assertEquals(asli.skor(), r.skor());
        assertEquals(asli.lulus(), r.lulus());
        assertEquals(asli.modeSubmit(), r.modeSubmit());
        assertEquals(asli.waktu().withNano(0), r.waktu());
    }

    @Test
    void fileRekapYangBelumAdaDianggapKosong(@TempDir Path folder) throws Exception {
        assertTrue(new GradeRecapReader().baca(folder.resolve("tidak-ada.txt")).isEmpty());
    }
}
