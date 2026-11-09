package com.examify.io;

import com.examify.exception.QuestionBankFormatException;
import com.examify.model.QuestionType;
import com.examify.model.Question;
import com.examify.model.MultipleChoiceQuestion;
import com.examify.model.Exam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionBankParserTest {

    private final QuestionBankParser parser = new QuestionBankParser();

    private static final String HEADER = """
            [UJIAN]
            KODE=MTK-01
            MAPEL=Matematika
            TOKEN=MTK7X2
            DURASI=30
            KKM=75
            [SOAL]
            """;

    private List<Exam> baca(String isi) throws Exception {
        return new QuestionBankReader().baca(new BufferedReader(new StringReader(isi)));
    }

    @Test
    void parseBarisPilihanGandaValid() throws Exception {
        Question soal = parser.parseBaris("PG | 1 | 12 x 3 = ... | 30;36;38;42 | b | 10", 1);
        MultipleChoiceQuestion pg = assertInstanceOf(MultipleChoiceQuestion.class, soal);
        assertEquals(4, pg.getOpsi().size());
        assertEquals(10.0, pg.getBobot());
        assertTrue(pg.evaluasiJawaban("B"));
    }

    @Test
    void parseBarisTrueFalseDanIsianValid() throws Exception {
        assertEquals(QuestionType.TF, parser.parseBaris("tf|2|Pernyataan.|F|5", 1).getJenis());
        Question isian = parser.parseBaris("IS|3|Ibu kota?|semarang;kota semarang|5", 1);
        assertTrue(isian.evaluasiJawaban("Kota Semarang"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PG|2|Soal|4x;5x;6x;3x|10",          // field kurang
            "PG|2|Soal|a;b;c|A|sepuluh",         // bobot bukan angka
            "PG|x|Soal|a;b;c|A|10",              // id bukan angka
            "PG|2|Soal|a;b;c|D|10",              // kunci di luar rentang opsi
            "PG|2|Soal|a|A|10",                  // opsi kurang dari 2
            "PG|2|Soal|a;b;c|AB|10",             // kunci lebih dari 1 huruf
            "XX|2|Soal|T|10",                    // jenis tidak dikenal
            "TF|2|Soal|Y|10",                    // kunci TF tidak valid
            "TF|2||T|10",                        // pertanyaan kosong
            "IS|2|Soal| ; |10",                  // tanpa kunci isian
            "IS|2|Soal|a|-5",                    // bobot negatif
            "TF|2|Soal|T|10|",                   // field berlebih (pipa di akhir)
    })
    void barisRusakMenghasilkanFormatBankSoalException(String baris) {
        QuestionBankFormatException e = assertThrows(QuestionBankFormatException.class, () -> parser.parseBaris(baris, 7));
        assertEquals(7, e.getNomorBaris());
        assertEquals(baris, e.getIsiBaris());
    }

    @Test
    void pesanErrorMenyebutJumlahField() {
        QuestionBankFormatException e = assertThrows(QuestionBankFormatException.class,
                () -> parser.parseBaris("PG|2|Soal|4x;5x|10", 17));
        assertTrue(e.getMessage().startsWith("Baris 17 tidak valid -> jumlah field PG harus 6, ditemukan 5"));
    }

    @Test // TC-10
    void readerMelaporkanNomorBarisYangTepat() {
        String isi = "# komentar\n\n" + HEADER   // baris 1-9
                + "PG|1|Soal satu|a;b|A|10\n"     // 10
                + "\n".repeat(6)                  // 11-16
                + "PG|2|Soal dua|a;b|10\n"        // 17
                + "[END]\n";
        QuestionBankFormatException e = assertThrows(QuestionBankFormatException.class, () -> baca(isi));
        assertEquals(17, e.getNomorBaris());
    }

    @Test
    void readerMenolakIdSoalDuplikat() {
        String isi = HEADER + "TF|1|a|T|10\nTF|1|b|F|10\n[END]\n";
        QuestionBankFormatException e = assertThrows(QuestionBankFormatException.class, () -> baca(isi));
        assertEquals(9, e.getNomorBaris());
    }

    @Test
    void readerMenolakBlokTanpaEnd() {
        assertThrows(QuestionBankFormatException.class, () -> baca(HEADER + "TF|1|a|T|10\n"));
    }

    @Test
    void readerMenolakHeaderTidakLengkap() {
        String isi = "[UJIAN]\nKODE=MTK-01\nMAPEL=Matematika\n[SOAL]\nTF|1|a|T|10\n[END]\n";
        QuestionBankFormatException e = assertThrows(QuestionBankFormatException.class, () -> baca(isi));
        assertEquals(4, e.getNomorBaris());
    }

    @Test
    void readerMenolakKodeUjianDuplikat() {
        String blok = HEADER + "TF|1|a|T|10\n[END]\n";
        assertThrows(QuestionBankFormatException.class, () -> baca(blok + blok));
    }

    @Test
    void readerMenolakBlokTanpaSoal() {
        assertThrows(QuestionBankFormatException.class, () -> baca(HEADER + "[END]\n"));
    }

    @Test
    void nilaiHeaderBolehMengandungSamaDengan() throws Exception {
        String isi = HEADER.replace("MAPEL=Matematika", "MAPEL=Aljabar a=b") + "TF|1|a|T|10\n[END]\n";
        assertEquals("Aljabar a=b", baca(isi).get(0).getMataPelajaran());
    }

    private static final String SATU_SOAL = "TF|1|a|T|10\n[END]\n";

    @Test
    void headerUlangOpsionalDenganBawaanFalse() throws Exception {
        assertFalse(baca(HEADER + SATU_SOAL).get(0).isBolehUlang());
        String ulang = HEADER.replace("KKM=75", "KKM=75\nULANG=true");
        assertTrue(baca(ulang + SATU_SOAL).get(0).isBolehUlang());
    }

    @Test
    void headerStrictlySafeOpsionalDenganBawaanFalse() throws Exception {
        assertFalse(baca(HEADER + SATU_SOAL).get(0).isModeAman());
        String aman = HEADER.replace("KKM=75", "KKM=75\nSTRICTLY_SAFE=true");
        assertTrue(baca(aman + SATU_SOAL).get(0).isModeAman());
    }

    @Test
    void headerStrictlySafeSelainTrueFalseDitolak() {
        String aman = HEADER.replace("KKM=75", "KKM=75\nSTRICTLY_SAFE=mungkin");
        assertThrows(QuestionBankFormatException.class, () -> baca(aman + SATU_SOAL));
    }

    @Test
    void headerUlangSelainTrueFalseDitolak() {
        String ulang = HEADER.replace("KKM=75", "KKM=75\nULANG=mungkin");
        assertThrows(QuestionBankFormatException.class, () -> baca(ulang + SATU_SOAL));
    }

    @Test // TC-11
    void tokenDibandingkanTanpaPedulikanKapital() throws Exception {
        Exam ujian = baca(HEADER + "TF|1|a|T|10\n[END]\n").get(0);
        assertTrue(ujian.validasiToken(" mtk7x2 "));
    }

    @Test
    void fileDataContohTerbacaLengkap() throws Exception {
        List<Exam> daftar = new QuestionBankReader().baca(Path.of("data", "bank_soal.txt"));
        assertTrue(daftar.size() >= 3);
        daftar.stream().limit(3).forEach(u -> assertEquals(10, u.getJumlahSoal(), u.getKodeUjian()));
    }
}
