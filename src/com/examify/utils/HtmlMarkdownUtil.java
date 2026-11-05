package com.examify.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitas konversi dua arah antara HTML yang dihasilkan editor visual (seperti HTMLEditor)
 * dan format teks Markdown standar Examify.
 * Menghilangkan/menyembunyikan sintaks mentah seperti **, *, <u>, <center> dari pandangan pengguna.
 */
public final class HtmlMarkdownUtil {

    private static final Pattern POLA_BODY = Pattern.compile("(?is)<body[^>]*>(.*?)</body>");
    private static final Pattern POLA_BOLD = Pattern.compile("(?is)<(?:b|strong)(?:\\s+[^>]*)?>(.*?)</(?:b|strong)>");
    private static final Pattern POLA_ITALIC = Pattern.compile("(?is)<(?:i|em)(?:\\s+[^>]*)?>(.*?)</(?:i|em)>");
    private static final Pattern POLA_UNDERLINE = Pattern.compile("(?is)<u(?:\\s+[^>]*)?>(.*?)</u>");
    private static final Pattern POLA_SPAN_STYLE = Pattern.compile("(?is)<span\\s+style=[\"']([^\"']*)[\"']>(.*?)</span>");
    private static final Pattern POLA_CENTER = Pattern.compile("(?is)<(?:center|div|p)(?:\\s+[^>]*)?(?:align=[\"']center[\"']|style=[\"'][^\"']*text-align:\\s*center[^\"']*[\"'])[^>]*>(.*?)</(?:center|div|p)>|<center>(.*?)</center>");
    private static final Pattern POLA_RIGHT = Pattern.compile("(?is)<(?:div|p)(?:\\s+[^>]*)?(?:align=[\"']right[\"']|style=[\"'][^\"']*text-align:\\s*right[^\"']*[\"'])[^>]*>(.*?)</(?:div|p)>");
    private static final Pattern POLA_LI = Pattern.compile("(?is)<li[^>]*>(.*?)</li>");
    private static final Pattern POLA_UL = Pattern.compile("(?is)<ul[^>]*>(.*?)</ul>");
    private static final Pattern POLA_H1 = Pattern.compile("(?is)<h1[^>]*>(.*?)</h1>");
    private static final Pattern POLA_H2 = Pattern.compile("(?is)<h2[^>]*>(.*?)</h2>");
    private static final Pattern POLA_H3 = Pattern.compile("(?is)<h3[^>]*>(.*?)</h3>");
    private static final Pattern POLA_P = Pattern.compile("(?is)<p[^>]*>(.*?)</p>");
    private static final Pattern POLA_BR = Pattern.compile("(?is)<br\\s*/?>");
    private static final Pattern POLA_TAG_SISA = Pattern.compile("<[^>]+>");

    private HtmlMarkdownUtil() {
    }

    /**
     * Mengubah HTML kaya dari visual editor menjadi sintaks Markdown bersih.
     */
    public static String htmlToMarkdown(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        String konten = html;
        Matcher mBody = POLA_BODY.matcher(konten);
        if (mBody.find()) {
            konten = mBody.group(1);
        }

        // Tangani span dengan style CSS (font-weight, font-style, text-decoration)
        Matcher mSpan = POLA_SPAN_STYLE.matcher(konten);
        StringBuilder sbSpan = new StringBuilder();
        while (mSpan.find()) {
            String style = mSpan.group(1).toLowerCase();
            String isi = mSpan.group(2);
            if (style.contains("font-weight: bold") || style.contains("font-weight: 700")) {
                isi = "<b>" + isi + "</b>";
            }
            if (style.contains("font-style: italic")) {
                isi = "<i>" + isi + "</i>";
            }
            if (style.contains("text-decoration: underline")) {
                isi = "<u>" + isi + "</u>";
            }
            mSpan.appendReplacement(sbSpan, Matcher.quoteReplacement(isi));
        }
        mSpan.appendTail(sbSpan);
        konten = sbSpan.toString();

        // Alignment: Ubah ke penanda sementara agar tidak terhapus pembersihan tag div/p
        konten = ubahCenterKePlaceholder(konten);
        konten = ubahRightKePlaceholder(konten);

        // Headings
        konten = POLA_H1.matcher(konten).replaceAll("# $1\n\n");
        konten = POLA_H2.matcher(konten).replaceAll("## $1\n\n");
        konten = POLA_H3.matcher(konten).replaceAll("### $1\n\n");

        // Format Teks Dasar
        konten = POLA_BOLD.matcher(konten).replaceAll("**$1**");
        konten = POLA_ITALIC.matcher(konten).replaceAll("*$1*");
        konten = POLA_UNDERLINE.matcher(konten).replaceAll("<u>$1</u>");

        // Daftar Butir (List)
        Matcher mUl = POLA_UL.matcher(konten);
        StringBuilder sbUl = new StringBuilder();
        while (mUl.find()) {
            String listContent = mUl.group(1);
            Matcher mLi = POLA_LI.matcher(listContent);
            StringBuilder sbLi = new StringBuilder();
            while (mLi.find()) {
                String item = bersihkanTagHtml(mLi.group(1)).trim();
                if (!item.isEmpty()) {
                    sbLi.append("- ").append(item).append("\n");
                }
            }
            mUl.appendReplacement(sbUl, Matcher.quoteReplacement(sbLi.toString() + "\n"));
        }
        mUl.appendTail(sbUl);
        konten = sbUl.toString();

        // Paragraf dan line breaks
        konten = POLA_BR.matcher(konten).replaceAll("\n");
        konten = POLA_P.matcher(konten).replaceAll("$1\n\n");
        konten = konten.replaceAll("(?is)<div[^>]*>(.*?)</div>", "$1\n");

        // Hapus sisa tag HTML lain yang tidak didukung
        konten = bersihkanTagLain(konten);

        // Kembalikan placeholder alignment ke sintaks Markdown/HTML final
        konten = konten.replace("___CENTER_START___", "<center>")
                .replace("___CENTER_END___", "</center>")
                .replace("___RIGHT_START___", "<div align=\"right\">")
                .replace("___RIGHT_END___", "</div>");

        // Entitas HTML
        konten = dekodeEntitasHtml(konten);

        // Bersihkan spasi dan baris kosong berlebih
        konten = konten.replaceAll("\n{3,}", "\n\n").trim();
        return konten;
    }

    private static String ubahCenterKePlaceholder(String html) {
        Matcher m = POLA_CENTER.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String isi = m.group(1) != null ? m.group(1) : m.group(2);
            if (isi != null) {
                m.appendReplacement(sb, Matcher.quoteReplacement("___CENTER_START___" + isi.trim() + "___CENTER_END___"));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String ubahRightKePlaceholder(String html) {
        Matcher m = POLA_RIGHT.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String isi = m.group(1);
            if (isi != null) {
                m.appendReplacement(sb, Matcher.quoteReplacement("___RIGHT_START___" + isi.trim() + "___RIGHT_END___"));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String bersihkanTagLain(String teks) {
        // Jangan bersihkan tag <u>, </u>, <center>, </center>, <div align="right">, </div>
        Matcher m = POLA_TAG_SISA.matcher(teks);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String tag = m.group(0).toLowerCase();
            if (tag.equals("<u>") || tag.equals("</u>")
                    || tag.equals("<center>") || tag.equals("</center>")
                    || tag.startsWith("<div align=\"right\"") || tag.equals("</div>")) {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
            } else {
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String bersihkanTagHtml(String teks) {
        if (teks == null) return "";
        return POLA_TAG_SISA.matcher(teks).replaceAll("");
    }

    public static String dekodeEntitasHtml(String teks) {
        if (teks == null) return "";
        return teks.replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }

    /**
     * Mengubah Markdown menjadi HTML siap pakai untuk dirender di visual editor WYSIWYG.
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return templateHtml("<p></p>");
        }

        String teks = markdown.replace("\r\n", "\n").replace('\r', '\n');

        StringBuilder sb = new StringBuilder();
        String[] baris = teks.split("\n");

        boolean inList = false;
        StringBuilder listBuffer = new StringBuilder();

        for (int i = 0; i < baris.length; i++) {
            String b = baris[i];
            String trim = b.trim();

            if (trim.startsWith("- ") || trim.startsWith("* ")) {
                if (!inList) {
                    inList = true;
                    listBuffer.setLength(0);
                }
                String isiItem = trim.substring(2).trim();
                listBuffer.append("<li>").append(formatInlineHtml(isiItem)).append("</li>");
                continue;
            } else if (inList) {
                sb.append("<ul>").append(listBuffer).append("</ul>");
                inList = false;
                listBuffer.setLength(0);
            }

            if (trim.isEmpty()) {
                continue;
            }

            if (trim.startsWith("# ") && !trim.startsWith("## ")) {
                sb.append("<h1>").append(formatInlineHtml(trim.substring(2).trim())).append("</h1>");
            } else if (trim.startsWith("## ")) {
                sb.append("<h2>").append(formatInlineHtml(trim.substring(3).trim())).append("</h2>");
            } else if (trim.startsWith("### ")) {
                sb.append("<h3>").append(formatInlineHtml(trim.substring(4).trim())).append("</h3>");
            } else if (trim.startsWith("<center>") && trim.endsWith("</center>") && trim.length() >= 17) {
                String dalam = trim.substring(8, trim.length() - 9).trim();
                sb.append("<p align=\"center\">").append(formatInlineHtml(dalam)).append("</p>");
            } else if (trim.startsWith("<div align=\"right\">") && trim.endsWith("</div>") && trim.length() >= 25) {
                String dalam = trim.substring(19, trim.length() - 6).trim();
                sb.append("<p align=\"right\">").append(formatInlineHtml(dalam)).append("</p>");
            } else {
                sb.append("<p>").append(formatInlineHtml(trim)).append("</p>");
            }
        }

        if (inList) {
            sb.append("<ul>").append(listBuffer).append("</ul>");
        }

        return templateHtml(sb.toString());
    }

    private static String formatInlineHtml(String inline) {
        if (inline == null) return "";
        String s = inline;
        // Bold: **teks**
        s = s.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        // Italic: *teks*
        s = s.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)(?<!\\*)\\*(?!\\*)", "<i>$1</i>");
        // Underline: <u>teks</u> -> sudah HTML
        return s;
    }

    private static String templateHtml(String bodyContent) {
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\">"
                + "<style>"
                + "body {"
                + "  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;"
                + "  font-size: 14.5px;"
                + "  color: #1e293b;"
                + "  background-color: transparent;"
                + "  margin: 6px 10px;"
                + "  line-height: 1.6;"
                + "  outline: none;"
                + "}"
                + "p { margin: 0 0 6px 0; }"
                + "h1 { font-size: 22px; font-weight: bold; margin: 0 0 10px 0; color: #0f172a; }"
                + "h2 { font-size: 17px; font-weight: bold; margin: 0 0 8px 0; color: #1e293b; }"
                + "ul { margin: 0 0 8px 20px; padding: 0; }"
                + "li { margin-bottom: 4px; }"
                + "b, strong { font-weight: bold; }"
                + "i, em { font-style: italic; }"
                + "u { text-decoration: underline; }"
                + "</style></head><body contenteditable=\"true\">"
                + bodyContent
                + "</body></html>";
    }
}
