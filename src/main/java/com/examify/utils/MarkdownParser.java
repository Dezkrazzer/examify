package com.examify.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser Markdown sederhana (tanpa library) untuk materi bacaan.
 * Mendukung: heading #..###, paragraf, daftar "-" dan "1.", blok kode ```, kutipan ">",
 * garis "---", serta inline **tebal**, *miring*, <u>garis bawah</u>, dan `kode`.
 */
public final class MarkdownParser {

    public enum BlockType { HEADING, PARAGRAF, BUTIR, NOMOR, KODE, KUTIPAN, GARIS }

    public enum GayaTeks { BIASA, TEBAL, MIRING, KODE, GARIS_BAWAH }

    /** level dipakai untuk heading (1-3) dan nomor urut daftar bernomor. */
    public record Blok(BlockType jenis, String isi, int level) {
    }

    public record Potongan(String teks, GayaTeks gaya, String warnaTeks, String warnaLatar) {
        public Potongan(String teks, GayaTeks gaya) {
            this(teks, gaya, null, null);
        }
    }

    private static final String PAGAR_KODE = "```";
    private static final Pattern POLA_HEADING = Pattern.compile("^(#{1,3})\\s+(.*)$");
    private static final Pattern POLA_BUTIR = Pattern.compile("^[-*]\\s+(.*)$");
    private static final Pattern POLA_NOMOR = Pattern.compile("^(\\d+)\\.\\s+(.*)$");

    private static final Pattern POLA_INLINE = Pattern.compile(
            "(?is)<span\\s+style=[\"\']([^\"\']*)?[\"\']>(.*?)</span>"
            + "|<mark(?:\\s+style=[\"\']([^\"\']*)?[\"\'])?>(.*?)</mark>"
            + "|<font\\s+color=[\"\']([^\"\']*)?[\"\']>(.*?)</font>"
            + "|\\*\\*(.+?)\\*\\*"
            + "|`([^`]+)`"
            + "|<u>(.+?)</u>"
            + "|\\*(.+?)\\*"
    );

    private static final Pattern POLA_COLOR = Pattern.compile("(?i)(?:^|;)\\s*color:\\s*([^;]+)");
    private static final Pattern POLA_BG_COLOR = Pattern.compile("(?i)(?:^|;)\\s*background-color:\\s*([^;]+)");

    private MarkdownParser() {
    }

    public static List<Blok> parse(String markdown) {
        List<Blok> hasil = new ArrayList<>();
        StringBuilder paragraf = new StringBuilder();
        StringBuilder kode = null; // != null berarti sedang di dalam blok kode

        for (String baris : markdown.replace("\r", "").split("\n", -1)) {
            String isi = baris.trim();
            if (isi.startsWith(PAGAR_KODE)) {
                if (kode == null) {
                    tutupParagraf(hasil, paragraf);
                    kode = new StringBuilder();
                } else {
                    hasil.add(new Blok(BlockType.KODE, kode.toString().stripTrailing(), 0));
                    kode = null;
                }
                continue;
            }
            if (kode != null) {
                kode.append(baris).append('\n'); // indentasi kode dipertahankan
                continue;
            }
            Blok blok = blokSatuBaris(isi);
            if (blok != null || isi.isEmpty()) {
                tutupParagraf(hasil, paragraf);
                if (blok != null) {
                    hasil.add(blok);
                }
            } else {
                paragraf.append(paragraf.length() > 0 ? " " : "").append(isi);
            }
        }
        if (kode != null) {
            hasil.add(new Blok(BlockType.KODE, kode.toString().stripTrailing(), 0));
        }
        tutupParagraf(hasil, paragraf);
        return hasil;
    }

    /** @return blok untuk baris yang berdiri sendiri, atau null bila baris bagian dari paragraf */
    private static Blok blokSatuBaris(String isi) {
        if (isi.equals("---")) {
            return new Blok(BlockType.GARIS, "", 0);
        }
        Matcher m = POLA_HEADING.matcher(isi);
        if (m.matches()) {
            return new Blok(BlockType.HEADING, m.group(2), m.group(1).length());
        }
        m = POLA_BUTIR.matcher(isi);
        if (m.matches()) {
            return new Blok(BlockType.BUTIR, m.group(1), 0);
        }
        m = POLA_NOMOR.matcher(isi);
        if (m.matches()) {
            return new Blok(BlockType.NOMOR, m.group(2), Integer.parseInt(m.group(1)));
        }
        if (isi.startsWith(">")) {
            return new Blok(BlockType.KUTIPAN, isi.substring(1).trim(), 0);
        }
        return null;
    }

    private static void tutupParagraf(List<Blok> hasil, StringBuilder paragraf) {
        if (paragraf.length() > 0) {
            hasil.add(new Blok(BlockType.PARAGRAF, paragraf.toString(), 0));
            paragraf.setLength(0);
        }
    }

    /** Memecah satu baris teks menjadi potongan berdasarkan gaya inline dan atribut warna. */
    public static List<Potongan> parseInline(String teks) {
        return parseInlineInternal(teks, null, null);
    }

    private static List<Potongan> parseInlineInternal(String teks, String inheritedColor, String inheritedBg) {
        List<Potongan> hasil = new ArrayList<>();
        if (teks == null || teks.isEmpty()) {
            return hasil;
        }

        Matcher m = POLA_INLINE.matcher(teks);
        int posisi = 0;
        while (m.find()) {
            if (m.start() > posisi) {
                hasil.add(new Potongan(teks.substring(posisi, m.start()), GayaTeks.BIASA, inheritedColor, inheritedBg));
            }

            if (m.group(1) != null) {
                // <span style="...">...</span>
                String style = m.group(1).trim();
                String inner = m.group(2);

                String color = inheritedColor;
                Matcher mc = POLA_COLOR.matcher(style);
                if (mc.find()) {
                    color = mc.group(1).trim();
                }

                String bg = inheritedBg;
                Matcher mbg = POLA_BG_COLOR.matcher(style);
                if (mbg.find()) {
                    bg = mbg.group(1).trim();
                }

                GayaTeks gaya = GayaTeks.BIASA;
                String lowerStyle = style.toLowerCase();
                if (lowerStyle.contains("font-weight: bold") || lowerStyle.contains("font-weight: 700")) {
                    gaya = GayaTeks.TEBAL;
                } else if (lowerStyle.contains("font-style: italic")) {
                    gaya = GayaTeks.MIRING;
                } else if (lowerStyle.contains("text-decoration: underline")) {
                    gaya = GayaTeks.GARIS_BAWAH;
                }

                if (inner.contains("<") || inner.contains("*") || inner.contains("`")) {
                    List<Potongan> subs = parseInlineInternal(inner, color, bg);
                    hasil.addAll(subs);
                } else {
                    hasil.add(new Potongan(inner, gaya, color, bg));
                }
            } else if (m.group(4) != null) {
                // <mark...>...</mark>
                String style = m.group(3) != null ? m.group(3) : "";
                String inner = m.group(4);
                String bg = "#ffff00";
                Matcher mbg = POLA_BG_COLOR.matcher(style);
                if (mbg.find()) {
                    bg = mbg.group(1).trim();
                }

                if (inner.contains("<") || inner.contains("*") || inner.contains("`")) {
                    List<Potongan> subs = parseInlineInternal(inner, inheritedColor, bg);
                    hasil.addAll(subs);
                } else {
                    hasil.add(new Potongan(inner, GayaTeks.BIASA, inheritedColor, bg));
                }
            } else if (m.group(6) != null) {
                // <font color="...">...</font>
                String color = m.group(5).trim();
                String inner = m.group(6);
                if (inner.contains("<") || inner.contains("*") || inner.contains("`")) {
                    List<Potongan> subs = parseInlineInternal(inner, color, inheritedBg);
                    hasil.addAll(subs);
                } else {
                    hasil.add(new Potongan(inner, GayaTeks.BIASA, color, inheritedBg));
                }
            } else if (m.group(7) != null) {
                // **tebal**
                hasil.add(new Potongan(m.group(7), GayaTeks.TEBAL, inheritedColor, inheritedBg));
            } else if (m.group(8) != null) {
                // `kode`
                hasil.add(new Potongan(m.group(8), GayaTeks.KODE, inheritedColor, inheritedBg));
            } else if (m.group(9) != null) {
                // <u>garis bawah</u>
                hasil.add(new Potongan(m.group(9), GayaTeks.GARIS_BAWAH, inheritedColor, inheritedBg));
            } else if (m.group(10) != null) {
                // *miring*
                hasil.add(new Potongan(m.group(10), GayaTeks.MIRING, inheritedColor, inheritedBg));
            }

            posisi = m.end();
        }

        if (posisi < teks.length()) {
            hasil.add(new Potongan(teks.substring(posisi), GayaTeks.BIASA, inheritedColor, inheritedBg));
        }

        return hasil;
    }
}
