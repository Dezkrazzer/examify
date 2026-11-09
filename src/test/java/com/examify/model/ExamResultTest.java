package com.examify.model;

import com.examify.exception.EmptyAnswerException;
import com.examify.exception.TimeOutException;
import com.examify.utils.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExamResultTest {

    private final Student siswa = new Student("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final ControlledClock clock = new ControlledClock();
    private Exam ujian;
    private ExamResult hasil;

    @BeforeEach
    void siapkan() {
        ujian = new Exam("MTK-01", "Matematika", "MTK7X2", 30, 75, List.of(
                new MultipleChoiceQuestion(1, "12 x 3", 10, List.of("30", "36", "38"), 'B'),
                new TrueFalseQuestion(2, "2 prima genap", 20, 'T'),
                new ShortAnswerQuestion(3, "2^5", 30, Set.of("32")),
                new MultipleChoiceQuestion(4, "3x = 21", 40, List.of("5", "6", "7"), 'C')));
        hasil = new ExamResult(siswa, ujian, clock);
    }

    private void jawab(int idSoal, String jawaban) throws Exception {
        hasil.simpanJawaban(idSoal, jawaban);
    }

    @Test
    void tanpaJawabanSkorNolDanSemuaKosong() {
        assertEquals(0.0, hasil.hitungNilaiAkhir());
        assertEquals(4, hasil.getJumlahKosong());
        assertEquals("E", hasil.getPredikat());
    }

    @Test // TC-06
    void semuaBenarDenganBobotBerbedaSkor100() throws Exception {
        jawab(1, "b");
        jawab(2, "benar");
        jawab(3, "32");
        jawab(4, "C");
        assertEquals(100.0, hasil.hitungNilaiAkhir());
        assertEquals("A", hasil.getPredikat());
    }

    @Test
    void skorMengikutiBobotSoalYangBenar() throws Exception {
        jawab(1, "B");  // benar, bobot 10
        jawab(2, "F");  // salah
        jawab(4, "C");  // benar, bobot 40
        assertEquals(50.0, hasil.hitungNilaiAkhir());
        assertEquals(2, hasil.getJumlahBenar());
        assertEquals(1, hasil.getJumlahSalah());
        assertEquals(1, hasil.getJumlahKosong());
        assertFalse(hasil.isLulus());
    }

    @Test
    void skorDibulatkanDuaDesimal() throws Exception {
        Exam tigaSoal = new Exam("UJI-01", "Uji", "UJI123", 1, 60, List.of(
                new TrueFalseQuestion(1, "a", 1, 'T'),
                new TrueFalseQuestion(2, "b", 1, 'T'),
                new TrueFalseQuestion(3, "c", 1, 'T')));
        ExamResult h = new ExamResult(siswa, tigaSoal, clock);
        h.simpanJawaban(1, "T");
        assertEquals(33.33, h.hitungNilaiAkhir());
    }

    @Test // TC-05
    void jawabanKosongDitolak() {
        EmptyAnswerException e = assertThrows(EmptyAnswerException.class, () -> jawab(1, " "));
        assertEquals(1, e.getIdSoal());
        assertFalse(hasil.isTerjawab(1));
    }

    @Test // TC-07
    void simpanSetelahBatasWaktuMelemparTimeOut() {
        clock.maju(Duration.ofMinutes(30).plusSeconds(AppConfig.GRACE_PERIOD_DETIK + 1));
        TimeOutException e = assertThrows(TimeOutException.class, () -> jawab(1, "B"));
        assertEquals("MTK-01", e.getKodeUjian());
    }

    @Test
    void simpanMasihDiterimaDalamGracePeriod() throws Exception {
        clock.maju(Duration.ofMinutes(30).plusSeconds(AppConfig.GRACE_PERIOD_DETIK));
        jawab(1, "B");
        assertTrue(hasil.isTerjawab(1));
    }

    @Test // TC-08
    void finalisasiIdempoten() throws Exception {
        jawab(1, "B");
        assertTrue(hasil.finalisasi(SubmitMode.MANUAL));
        double skorPertama = hasil.getSkor();
        assertFalse(hasil.finalisasi(SubmitMode.AUTO_SUBMIT));
        assertEquals(skorPertama, hasil.getSkor());
        assertEquals(SubmitMode.MANUAL, hasil.getModeSubmit());
        assertEquals(ExamStatus.SELESAI, hasil.getStatus());
    }

    @Test
    void jawabanTidakDapatDiubahSetelahSelesai() {
        hasil.finalisasi(SubmitMode.MANUAL);
        assertThrows(TimeOutException.class, () -> jawab(1, "B"));
    }

    @Test
    void hapusJawabanMengembalikanStatusKosong() throws Exception {
        jawab(1, "B");
        hasil.hapusJawaban(1);
        assertFalse(hasil.isTerjawab(1));
        assertEquals(AnswerStatus.KOSONG, hasil.nilaiJawaban(ujian.getSoalBerdasarkanIndeks(0)));
    }

    @Test
    void rankingMengurutkanSkorMenurun() throws Exception {
        ExamResult tinggi = new ExamResult(siswa, ujian, clock);
        tinggi.simpanJawaban(4, "C");
        tinggi.finalisasi(SubmitMode.MANUAL);
        hasil.finalisasi(SubmitMode.MANUAL);
        assertTrue(tinggi.compareTo(hasil) < 0);
    }

    @Test
    void proporsiTerjawabBertipeFloat() throws Exception {
        jawab(1, "B");
        assertEquals(0.25f, hasil.getProporsiTerjawab());
    }

    @Test // TC-09
    void siswaDenganNisSamaAdalahSatuKeyHashMap() {
        Map<Student, Double> rekap = new HashMap<>();
        rekap.put(siswa, 70.0);
        rekap.put(new Student("2410001", "Andi (login ulang)", "XI-IPA-1", "1234"), 90.0);
        assertEquals(1, rekap.size());
        assertEquals(90.0, rekap.get(siswa));
    }

    @Test
    void tandaiSoalRaguRaguBisaDiToggle() {
        assertFalse(hasil.isDitandai(1));
        assertEquals(0, hasil.getJumlahDitandai());

        hasil.setDitandai(1, true);
        assertTrue(hasil.isDitandai(1));
        assertEquals(1, hasil.getJumlahDitandai());

        hasil.toggleTandai(1);
        assertFalse(hasil.isDitandai(1));
        assertEquals(0, hasil.getJumlahDitandai());

        hasil.toggleTandai(2);
        assertTrue(hasil.isDitandai(2));
        assertEquals(1, hasil.getJumlahDitandai());
    }

    @Test
    void soalDitandaiTidakBisaDiubahSetelahSelesai() {
        hasil.setDitandai(1, true);
        hasil.finalisasi(SubmitMode.MANUAL);
        hasil.setDitandai(1, false);
        assertTrue(hasil.isDitandai(1));
        hasil.toggleTandai(2);
        assertFalse(hasil.isDitandai(2));
    }
}
