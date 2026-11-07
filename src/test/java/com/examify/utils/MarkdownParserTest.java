package com.examify.utils;

import com.examify.utils.MarkdownParser.Blok;
import com.examify.utils.MarkdownParser.GayaTeks;
import com.examify.utils.MarkdownParser.JenisBlok;
import com.examify.utils.MarkdownParser.Potongan;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkdownParserTest {

    @Test
    void blokDasarDikenali() {
        List<Blok> blok = MarkdownParser.parse("""
                # Judul
                Baris satu
                baris dua.

                - butir
                2. nomor
                > catatan
                ---
                """);
        assertEquals(List.of(
                new Blok(JenisBlok.HEADING, "Judul", 1),
                new Blok(JenisBlok.PARAGRAF, "Baris satu baris dua.", 0),
                new Blok(JenisBlok.BUTIR, "butir", 0),
                new Blok(JenisBlok.NOMOR, "nomor", 2),
                new Blok(JenisBlok.KUTIPAN, "catatan", 0),
                new Blok(JenisBlok.GARIS, "", 0)), blok);
    }

    @Test
    void blokKodeMempertahankanIndentasiDanTidakDiparse() {
        List<Blok> blok = MarkdownParser.parse("```java\nclass A {\n    # bukan heading\n}\n```");
        assertEquals(1, blok.size());
        assertEquals(new Blok(JenisBlok.KODE, "class A {\n    # bukan heading\n}", 0), blok.get(0));
    }

    @Test
    void inlineTebalMiringDanKode() {
        assertEquals(List.of(
                new Potongan("Pakai ", GayaTeks.BIASA),
                new Potongan("private", GayaTeks.KODE),
                new Potongan(" agar ", GayaTeks.BIASA),
                new Potongan("aman", GayaTeks.TEBAL),
                new Potongan(" dan ", GayaTeks.BIASA),
                new Potongan("rapi", GayaTeks.MIRING)),
                MarkdownParser.parseInline("Pakai `private` agar **aman** dan *rapi*"));
    }

    @Test
    void inlineGarisBawah() {
        assertEquals(List.of(
                new Potongan("Teks ", GayaTeks.BIASA),
                new Potongan("garis bawah", GayaTeks.GARIS_BAWAH),
                new Potongan(" dan ", GayaTeks.BIASA),
                new Potongan("tebal", GayaTeks.TEBAL)),
                MarkdownParser.parseInline("Teks <u>garis bawah</u> dan **tebal**"));
    }

    @Test
    void teksTanpaFormatMenjadiSatuPotongan() {
        assertEquals(List.of(new Potongan("biasa saja", GayaTeks.BIASA)), MarkdownParser.parseInline("biasa saja"));
    }

    @Test
    void inlineSpanHighlightDanFontColor() {
        String input = "<span style=\"background-color: rgb(0, 229, 255);\">Tuliskan materi pembelajaran</span>";
        List<Potongan> hasil = MarkdownParser.parseInline(input);
        assertEquals(1, hasil.size());
        assertEquals("Tuliskan materi pembelajaran", hasil.get(0).teks());
        assertEquals("rgb(0, 229, 255)", hasil.get(0).warnaLatar());
    }

    @Test
    void inlineSpanColorDanMark() {
        String input = "Ini <span style=\"color: #e53935;\">merah</span> dan <mark>kuning</mark>";
        List<Potongan> hasil = MarkdownParser.parseInline(input);
        assertEquals(4, hasil.size());
        assertEquals(new Potongan("Ini ", GayaTeks.BIASA), hasil.get(0));
        assertEquals(new Potongan("merah", GayaTeks.BIASA, "#e53935", null), hasil.get(1));
        assertEquals(new Potongan(" dan ", GayaTeks.BIASA), hasil.get(2));
        assertEquals(new Potongan("kuning", GayaTeks.BIASA, null, "#ffff00"), hasil.get(3));
    }
}
