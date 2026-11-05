package com.examify.io;

import com.examify.model.materi.DrafBlok;
import com.examify.model.materi.DrafSlide;
import com.examify.model.materi.JenisBlok;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlideMarkdownConverterTest {

    @Test
    void parseMarkdownSederhana() {
        String md = "# Judul Materi\n\nIni adalah paragraf penjelasan.\n\n## Sub Bab 1\n\nPoin penting.";
        List<DrafSlide> slides = SlideMarkdownConverter.parse(md);

        assertEquals(1, slides.size());
        DrafSlide slide1 = slides.get(0);
        assertEquals("Judul Materi", slide1.getJudulSlide());
        assertEquals(4, slide1.getDaftarBlok().size());

        assertEquals(JenisBlok.HEADING_1, slide1.getDaftarBlok().get(0).getJenis());
        assertEquals("Judul Materi", slide1.getDaftarBlok().get(0).getIsi());

        assertEquals(JenisBlok.PARAGRAF, slide1.getDaftarBlok().get(1).getJenis());
        assertEquals("Ini adalah paragraf penjelasan.", slide1.getDaftarBlok().get(1).getIsi());

        assertEquals(JenisBlok.HEADING_2, slide1.getDaftarBlok().get(2).getJenis());
        assertEquals(JenisBlok.PARAGRAF, slide1.getDaftarBlok().get(3).getJenis());
    }

    @Test
    void parseMarkdownDenganSlideBreakDanMedia() {
        String md = """
                # Halaman Pertama
                
                Pengantar materi.
                
                <!-- slide: Bagian Kedua -->
                
                ## Konsep Lanjutan
                
                > [!NOTE]
                > Ini adalah kotak pengingat.
                
                ![Diagram Arsitektur](media/diagram.png)
                
                <!-- youtube: https://youtu.be/contoh | Video Penjelasan -->
                """;

        List<DrafSlide> slides = SlideMarkdownConverter.parse(md);
        assertEquals(2, slides.size());

        DrafSlide slide1 = slides.get(0);
        assertEquals("Halaman Pertama", slide1.getJudulSlide());
        assertEquals(2, slide1.getDaftarBlok().size());

        DrafSlide slide2 = slides.get(1);
        assertEquals("Bagian Kedua", slide2.getJudulSlide());
        assertEquals(4, slide2.getDaftarBlok().size());
        assertEquals(JenisBlok.HEADING_2, slide2.getDaftarBlok().get(0).getJenis());
        assertEquals(JenisBlok.CALLOUT, slide2.getDaftarBlok().get(1).getJenis());
        assertEquals(JenisBlok.GAMBAR, slide2.getDaftarBlok().get(2).getJenis());
        assertEquals(JenisBlok.VIDEO, slide2.getDaftarBlok().get(3).getJenis());

        // Test konversi balik ke Markdown
        String mdHasil = SlideMarkdownConverter.toMarkdown(slides);
        assertTrue(mdHasil.contains("<!-- slide: Bagian Kedua -->"));
        assertTrue(mdHasil.contains("> [!NOTE]"));
        assertTrue(mdHasil.contains("![Diagram Arsitektur](media/diagram.png)"));
        assertTrue(mdHasil.contains("<!-- youtube: https://youtu.be/contoh | Video Penjelasan -->"));
    }
}
