package com.examify.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlMarkdownUtilTest {

    @Test
    void testHtmlToMarkdown_BoldItalicUnderline() {
        String html = "<html><body><p>Ini adalah <b>teks tebal</b> dan <i>teks miring</i> serta <u>garis bawah</u>.</p></body></html>";
        String md = HtmlMarkdownUtil.htmlToMarkdown(html);
        assertEquals("Ini adalah **teks tebal** dan *teks miring* serta <u>garis bawah</u>.", md);
    }

    @Test
    void testHtmlToMarkdown_CenterAlignment() {
        String html = "<html><body><p align=\"center\">Judul di Tengah</p></body></html>";
        String md = HtmlMarkdownUtil.htmlToMarkdown(html);
        assertEquals("<center>Judul di Tengah</center>", md);
    }

    @Test
    void testHtmlToMarkdown_RightAlignment() {
        String html = "<html><body><div align=\"right\">Teks di Kanan</div></body></html>";
        String md = HtmlMarkdownUtil.htmlToMarkdown(html);
        assertEquals("<div align=\"right\">Teks di Kanan</div>", md);
    }

    @Test
    void testHtmlToMarkdown_List() {
        String html = "<html><body><ul><li>Poin Satu</li><li>Poin Dua</li></ul></body></html>";
        String md = HtmlMarkdownUtil.htmlToMarkdown(html);
        assertEquals("- Poin Satu\n- Poin Dua", md);
    }

    @Test
    void testMarkdownToHtml_Basic() {
        String md = "Tuliskan **materi** dan *visual*.";
        String html = HtmlMarkdownUtil.markdownToHtml(md);
        assertTrue(html.contains("<b>materi</b>"));
        assertTrue(html.contains("<i>visual</i>"));
    }

    @Test
    void testHtmlToMarkdown_PreserveColorAndHighlight() {
        String html = "<p>Teks <span style=\"color: #e53935;\">merah</span> dan <span style=\"background-color: #ffff00;\">stabilo kuning</span> serta <mark style=\"background-color: #00ff00;\">hijau</mark>.</p>";
        String md = HtmlMarkdownUtil.htmlToMarkdown(html);
        assertTrue(md.contains("<span style=\"color: #e53935;\">merah</span>"));
        assertTrue(md.contains("<span style=\"background-color: #ffff00;\">stabilo kuning</span>"));
        assertTrue(md.contains("<mark style=\"background-color: #00ff00;\">hijau</mark>"));
    }

    @Test
    void testHtmlToMarkdown_FontTagColorConversion() {
        String html = "<p>Teks <font color=\"#1e88e5\">biru</font>.</p>";
        String md = HtmlMarkdownUtil.htmlToMarkdown(html);
        assertTrue(md.contains("<span style=\"color: #1e88e5;\">biru</span>"));
    }

    @Test
    void testMarkdownToHtml_Center() {
        String md = "<center>Judul Tengah</center>";
        String html = HtmlMarkdownUtil.markdownToHtml(md);
        assertTrue(html.contains("<p align=\"center\">Judul Tengah</p>"));
    }
}
