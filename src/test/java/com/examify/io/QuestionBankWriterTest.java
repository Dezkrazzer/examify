package com.examify.io;

import com.examify.model.Question;
import com.examify.model.ShortAnswerQuestion;
import com.examify.model.MultipleChoiceQuestion;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionBankWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void tulisDanBacaKembaliBankSoal() throws Exception {
        Path fileBankSoal = tempDir.resolve("bank_soal.txt");

        List<Question> daftarSoal = List.of(
                new MultipleChoiceQuestion(1, "1 + 1 = ?", 10.0, List.of("1", "2", "3"), 'b'),
                new TrueFalseQuestion(2, "Bumi itu bulat", 10.0, 'T'),
                new ShortAnswerQuestion(3, "Ibukota Indonesia sekarang?", 10.0, Set.of("Nusantara", "IKN"))
        );

        Exam ujian = new Exam("TEST-01", "Ujian Uji Coba", "TOKEN9", 30, 75.0, daftarSoal);

        QuestionBankWriter writer = new QuestionBankWriter();
        writer.tulis(fileBankSoal, List.of(ujian));

        QuestionBankReader reader = new QuestionBankReader();
        List<Exam> hasil = reader.baca(fileBankSoal);

        assertEquals(1, hasil.size());
        Exam u = hasil.get(0);
        assertEquals("TEST-01", u.getKodeUjian());
        assertEquals("TOKEN9", u.getToken());
        assertEquals(3, u.getJumlahSoal());
        assertTrue(u.getDaftarSoal().get(0).evaluasiJawaban("B"));
        assertTrue(u.getDaftarSoal().get(1).evaluasiJawaban("TRUE"));
        assertTrue(u.getDaftarSoal().get(2).evaluasiJawaban("nusantara"));
    }

    @Test
    void modeAmanIkutTertulisDanTerbacaKembali() throws Exception {
        Path fileBankSoal = tempDir.resolve("bank_soal_aman.txt");
        Exam aman = new Exam("AMN-01", "Ujian Aman", "AMAN12", 10, 70.0,
                List.of(new TrueFalseQuestion(1, "Langit biru", 10.0, 'T'))).denganModeAman(true);

        new QuestionBankWriter().tulis(fileBankSoal, List.of(aman));

        assertTrue(new QuestionBankReader().baca(fileBankSoal).get(0).isModeAman());
    }
}
