package com.examify.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoalEvaluasiTest {

    private final Soal pg = new SoalPilihanGanda(1, "12 x 3 = ...", 10, List.of("30", "36", "38", "42"), 'B');
    private final Soal tf = new SoalTrueFalse(2, "2 adalah bilangan prima genap.", 10, 'T');
    private final Soal isian = new SoalIsian(3, "Ibu kota Jawa Tengah?", 10, Set.of("Semarang", "kota semarang"));

    @ParameterizedTest // TC-01
    @ValueSource(strings = {"B", "b", " b ", "  B"})
    void pilihanGandaMenerimaHurufKunciTanpaPeduliKapitalDanSpasi(String input) {
        assertTrue(pg.evaluasiJawaban(input));
    }

    @ParameterizedTest // TC-02
    @ValueSource(strings = {"A", "E", "B. 36", "BB", ""})
    void pilihanGandaMenolakJawabanSelainKunci(String input) {
        assertFalse(pg.evaluasiJawaban(input));
    }

    @Test
    void pilihanGandaMenolakNull() {
        assertFalse(pg.evaluasiJawaban(null));
    }

    @ParameterizedTest // TC-03
    @ValueSource(strings = {"T", "true", "Benar", "b", " BENAR "})
    void trueFalseMenerimaAliasBenar(String input) {
        assertTrue(tf.evaluasiJawaban(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"F", "salah", "S", "ya", "tidak"})
    void trueFalseMenolakAliasSalahDanInputAsing(String input) {
        assertFalse(tf.evaluasiJawaban(input));
    }

    @ParameterizedTest // TC-04
    @ValueSource(strings = {"semarang.", " kota  semarang. ", "SEMARANG!"})
    void isianMengabaikanKapitalSpasiDanTandaBacaAkhir(String input) {
        assertTrue(isian.evaluasiJawaban(input));
    }

    @Test
    void isianMenolakJawabanLain() {
        assertFalse(isian.evaluasiJawaban("Surabaya"));
    }

    @Test
    void kunciTidakDapatDibukaSebelumUjianSelesai() {
        assertThrows(IllegalStateException.class, () -> pg.bukaKunci(StatusUjian.BERLANGSUNG));
        assertThrows(IllegalStateException.class, () -> tf.bukaKunci(StatusUjian.BELUM_MULAI));
    }

    @Test
    void kunciDapatDibukaSetelahSelesai() {
        assertEquals("B", pg.bukaKunci(StatusUjian.SELESAI));
        assertEquals("BENAR", tf.bukaKunci(StatusUjian.SELESAI));
        assertEquals("KOTA SEMARANG / SEMARANG", isian.bukaKunci(StatusUjian.SELESAI));
    }

    @Test
    void konstruktorMemvalidasiInvarian() {
        assertThrows(IllegalArgumentException.class,
                () -> new SoalPilihanGanda(1, "x", 10, List.of("a", "b"), 'C'));
        assertThrows(IllegalArgumentException.class,
                () -> new SoalPilihanGanda(1, "x", 10, List.of("a"), 'A'));
        assertThrows(IllegalArgumentException.class, () -> new SoalTrueFalse(1, "x", 10, 'Y'));
        assertThrows(IllegalArgumentException.class, () -> new SoalIsian(1, " ", 10, Set.of("a")));
        assertThrows(IllegalArgumentException.class, () -> new SoalIsian(1, "x", 0, Set.of("a")));
    }

    @Test
    void labelOpsiDiturunkanDariIndeks() {
        SoalPilihanGanda soal = (SoalPilihanGanda) pg;
        assertEquals("B. 36", soal.getLabelOpsi(1));
        assertEquals("Pilih salah satu opsi A-D", soal.getPetunjukFormat());
    }
}
