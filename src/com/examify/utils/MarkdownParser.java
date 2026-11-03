package com.examify.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser Markdown sederhana (tanpa library) untuk materi bacaan.
 * Mendukung: heading #..###, paragraf, daftar "-" dan "1.", blok kode ```, kutipan ">",
 * garis "---", serta inline **tebal**, *miring*, dan `kode`.
 */
public final class MarkdownParser {

    public enum JenisBlok { HEADING, PARAGRAF, BUTIR, NOMOR, KODE, KUTIPAN, GARIS }

    public enum GayaTeks { BIASA, TEBAL, MIRING, KODE }

    /** level dipakai untuk heading (1-3) dan nomor urut daftar bernomor. */
    public record Blok(JenisBlok jenis, String isi, int level) {
    }

    public record Potongan(String teks, GayaTeks gaya) {
    }

    private static final String PAGAR_KODE = "```";
    private static final Pattern POLA_HEADING = Pattern.compile("^(#{1,3})\\s+(.*)$");
    private static final Pattern POLA_BUTIR = Pattern.compile("^[-*]\\s+(.*)$");
    private static final Pattern POLA_NOMOR = Pattern.compile("^(\\d+)\\.\\s+(.*)$");
    private static final Pattern POLA_INLINE = Pattern.compile("\\*\\*(.+?)\\*\\*|`([^`]+)`|\\*(.+?)\\*");

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
                    hasil.add(new Blok(JenisBlok.KODE, kode.toString().stripTrailing(), 0));
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
            hasil.add(new Blok(JenisBlok.KODE, kode.toString().stripTrailing(), 0));
        }
        tutupParagraf(hasil, paragraf);
        return hasil;
    }

    /** @return blok untuk baris yang berdiri sendiri, atau null bila baris bagian dari paragraf */
    private static Blok blokSatuBaris(String isi) {
        if (isi.equals("---")) {
            return new Blok(JenisBlok.GARIS, "", 0);
        }
        Matcher m = POLA_HEADING.matcher(isi);
        if (m.matches()) {
            return new Blok(JenisBlok.HEADING, m.group(2), m.group(1).length());
        }
        m = POLA_BUTIR.matcher(isi);
        if (m.matches()) {
            return new Blok(JenisBlok.BUTIR, m.group(1), 0);
        }
        m = POLA_NOMOR.matcher(isi);
        if (m.matches()) {
            return new Blok(JenisBlok.NOMOR, m.group(2), Integer.parseInt(m.group(1)));
        }
        if (isi.startsWith(">")) {
            return new Blok(JenisBlok.KUTIPAN, isi.substring(1).trim(), 0);
        }
        return null;
    }

    private static void tutupParagraf(List<Blok> hasil, StringBuilder paragraf) {
        if (paragraf.length() > 0) {
            hasil.add(new Blok(JenisBlok.PARAGRAF, paragraf.toString(), 0));
            paragraf.setLength(0);
        }
    }

    /** Memecah satu baris teks menjadi potongan berdasarkan gaya inline. */
    public static List<Potongan> parseInline(String teks) {
        List<Potongan> hasil = new ArrayList<>();
        Matcher m = POLA_INLINE.matcher(teks);
        int posisi = 0;
        while (m.find()) {
            if (m.start() > posisi) {
                hasil.add(new Potongan(teks.substring(posisi, m.start()), GayaTeks.BIASA));
            }
            if (m.group(1) != null) {
                hasil.add(new Potongan(m.group(1), GayaTeks.TEBAL));
            } else if (m.group(2) != null) {
                hasil.add(new Potongan(m.group(2), GayaTeks.KODE));
            } else {
                hasil.add(new Potongan(m.group(3), GayaTeks.MIRING));
            }
            posisi = m.end();
        }
        if (posisi < teks.length()) {
            hasil.add(new Potongan(teks.substring(posisi), GayaTeks.BIASA));
        }
        return hasil;
    }
}
