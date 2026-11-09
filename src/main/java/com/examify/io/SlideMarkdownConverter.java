package com.examify.io;

import com.examify.model.material.BlockDraft;
import com.examify.model.material.SlideDraft;
import com.examify.model.material.BlockType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Konverter dua arah antara berkas teks Markdown dengan struktur DrafSlide dan DrafBlok.
 * Mendukung pembagian slide berbasis komentar {@code <!-- slide: Judul -->} atau pemisah {@code ---}.
 */
public final class SlideMarkdownConverter {

    private static final Pattern POLA_SLIDE = Pattern.compile("(?m)^<!--\\s*slide(?:\\s*:\\s*(.*?))?\\s*-->$|^---$");
    private static final Pattern POLA_GAMBAR = Pattern.compile("^!\\[(.*?)\\]\\((.*?)\\)$");
    private static final Pattern POLA_YOUTUBE = Pattern.compile("^<!--\\s*youtube:\\s*(.*?)(?:\\s*\\|\\s*(.*?))?\\s*-->$");
    private static final Pattern POLA_CALLOUT_AWAL = Pattern.compile("^>\\s*\\[!(NOTE|TIPS|TIP|INFO|IMPORTANT|WARNING|CAUTION)\\]\\s*$", Pattern.CASE_INSENSITIVE);

    private SlideMarkdownConverter() {
    }

    /** Mem-parsing teks Markdown menjadi daftar DrafSlide beserta blok-blok kontennya. */
    public static List<SlideDraft> parse(String markdown) {
        List<SlideDraft> daftarSlide = new ArrayList<>();
        if (markdown == null || markdown.trim().isEmpty()) {
            SlideDraft s = new SlideDraft("Slide 1");
            s.tambahBlok(new BlockDraft(BlockType.PARAGRAF, ""));
            daftarSlide.add(s);
            return daftarSlide;
        }

        String teksBersih = markdown.replace("\r\n", "\n").replace('\r', '\n');
        Matcher matcher = POLA_SLIDE.matcher(teksBersih);

        List<Integer> splitStarts = new ArrayList<>();
        List<Integer> splitEnds = new ArrayList<>();
        List<String> slideTitles = new ArrayList<>();

        while (matcher.find()) {
            splitStarts.add(matcher.start());
            splitEnds.add(matcher.end());
            String title = matcher.group(1);
            slideTitles.add(title != null ? title.trim() : "");
        }

        if (splitStarts.isEmpty()) {
            SlideDraft s = parseKontenSlide(teksBersih, "Slide 1");
            daftarSlide.add(s);
            return daftarSlide;
        }

        // Slide pertama (sebelum split pertama)
        String teksPertama = teksBersih.substring(0, splitStarts.get(0)).trim();
        if (!teksPertama.isEmpty() || splitStarts.get(0) > 0) {
            daftarSlide.add(parseKontenSlide(teksPertama, "Slide 1"));
        }

        // Slide berikutnya
        for (int i = 0; i < splitStarts.size(); i++) {
            int awal = splitEnds.get(i);
            int akhir = (i + 1 < splitStarts.size()) ? splitStarts.get(i + 1) : teksBersih.length();
            String isiSlide = teksBersih.substring(awal, akhir).trim();
            String judul = slideTitles.get(i);
            if (judul == null || judul.isEmpty()) {
                judul = "Slide " + (daftarSlide.size() + 1);
            }
            daftarSlide.add(parseKontenSlide(isiSlide, judul));
        }

        if (daftarSlide.isEmpty()) {
            SlideDraft s = new SlideDraft("Slide 1");
            s.tambahBlok(new BlockDraft(BlockType.PARAGRAF, ""));
            daftarSlide.add(s);
        }

        return daftarSlide;
    }

    private static SlideDraft parseKontenSlide(String teks, String judulDefault) {
        SlideDraft slide = new SlideDraft(judulDefault);
        if (teks == null || teks.trim().isEmpty()) {
            slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, ""));
            return slide;
        }

        String[] baris = teks.split("\n");
        StringBuilder bufferParagraf = new StringBuilder();

        for (int i = 0; i < baris.length; i++) {
            String b = baris[i];
            String trim = b.trim();

            if (trim.isEmpty()) {
                if (!bufferParagraf.isEmpty()) {
                    slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
                    bufferParagraf.setLength(0);
                }
                continue;
            }

            // Heading 1 (dengan dukungan perataan <center> atau <div align="right">)
            String trimH1 = trim;
            String alignH1 = "";
            if (trimH1.startsWith("<center># ") && trimH1.endsWith("</center>")) {
                trimH1 = "# " + trimH1.substring(10, trimH1.length() - 9).trim();
                alignH1 = "center";
            } else if (trimH1.startsWith("<div align=\"right\"># ") && trimH1.endsWith("</div>")) {
                trimH1 = "# " + trimH1.substring(21, trimH1.length() - 6).trim();
                alignH1 = "right";
            }

            if (trimH1.startsWith("# ") && !trimH1.startsWith("## ")) {
                if (!bufferParagraf.isEmpty()) {
                    slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
                    bufferParagraf.setLength(0);
                }
                String judulH1 = trimH1.substring(2).trim();
                if (judulH1.startsWith("<center>") && judulH1.endsWith("</center>") && judulH1.length() >= 17) {
                    alignH1 = "center";
                    judulH1 = judulH1.substring(8, judulH1.length() - 9).trim();
                } else if (judulH1.startsWith("<div align=\"right\">") && judulH1.endsWith("</div>") && judulH1.length() >= 25) {
                    alignH1 = "right";
                    judulH1 = judulH1.substring(19, judulH1.length() - 6).trim();
                }
                slide.tambahBlok(new BlockDraft(BlockType.HEADING_1, judulH1, alignH1));
                if ("Slide 1".equals(slide.getJudulSlide()) || slide.getJudulSlide().startsWith("Slide ")) {
                    slide.setJudulSlide(judulH1);
                }
                continue;
            }

            // Heading 2 (dengan dukungan perataan)
            String trimH2 = trim;
            String alignH2 = "";
            if (trimH2.startsWith("<center>## ") && trimH2.endsWith("</center>")) {
                trimH2 = "## " + trimH2.substring(11, trimH2.length() - 9).trim();
                alignH2 = "center";
            } else if (trimH2.startsWith("<div align=\"right\">## ") && trimH2.endsWith("</div>")) {
                trimH2 = "## " + trimH2.substring(22, trimH2.length() - 6).trim();
                alignH2 = "right";
            }

            if (trimH2.startsWith("## ")) {
                if (!bufferParagraf.isEmpty()) {
                    slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
                    bufferParagraf.setLength(0);
                }
                String judulH2 = trimH2.substring(3).trim();
                if (judulH2.startsWith("<center>") && judulH2.endsWith("</center>") && judulH2.length() >= 17) {
                    alignH2 = "center";
                    judulH2 = judulH2.substring(8, judulH2.length() - 9).trim();
                } else if (judulH2.startsWith("<div align=\"right\">") && judulH2.endsWith("</div>") && judulH2.length() >= 25) {
                    alignH2 = "right";
                    judulH2 = judulH2.substring(19, judulH2.length() - 6).trim();
                }
                slide.tambahBlok(new BlockDraft(BlockType.HEADING_2, judulH2, alignH2));
                continue;
            }

            // YouTube embed comment
            Matcher mYt = POLA_YOUTUBE.matcher(trim);
            if (mYt.matches()) {
                if (!bufferParagraf.isEmpty()) {
                    slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
                    bufferParagraf.setLength(0);
                }
                String url = mYt.group(1).trim();
                String label = mYt.group(2) != null ? mYt.group(2).trim() : "";
                slide.tambahBlok(new BlockDraft(BlockType.VIDEO, url, label));
                continue;
            }

            // Gambar Markdown: ![caption](url)
            Matcher mGb = POLA_GAMBAR.matcher(trim);
            if (mGb.matches()) {
                if (!bufferParagraf.isEmpty()) {
                    slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
                    bufferParagraf.setLength(0);
                }
                String caption = mGb.group(1).trim();
                String path = mGb.group(2).trim();
                slide.tambahBlok(new BlockDraft(BlockType.GAMBAR, path, caption));
                continue;
            }

            // Blok Kode: ```lang ... ```
            if (trim.startsWith("```")) {
                if (!bufferParagraf.isEmpty()) {
                    slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
                    bufferParagraf.setLength(0);
                }
                String bahasa = trim.substring(3).trim();
                StringBuilder sbKode = new StringBuilder();
                i++;
                while (i < baris.length && !baris[i].trim().startsWith("```")) {
                    sbKode.append(baris[i]).append("\n");
                    i++;
                }
                slide.tambahBlok(new BlockDraft(BlockType.KODE, sbKode.toString().stripTrailing(), bahasa));
                continue;
            }

            // Callout: > [!NOTE] atau Kutipan >
            if (trim.startsWith(">")) {
                if (!bufferParagraf.isEmpty()) {
                    slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
                    bufferParagraf.setLength(0);
                }

                Matcher mCallout = POLA_CALLOUT_AWAL.matcher(trim);
                if (mCallout.matches()) {
                    String jenisCallout = mCallout.group(1).toUpperCase();
                    StringBuilder sbCallout = new StringBuilder();
                    i++;
                    while (i < baris.length && baris[i].trim().startsWith(">")) {
                        String isiBaris = baris[i].trim().substring(1).trim();
                        sbCallout.append(isiBaris).append("\n");
                        i++;
                    }
                    i--; // kembalikan pointer 1 langkah
                    slide.tambahBlok(new BlockDraft(BlockType.CALLOUT, sbCallout.toString().trim(), jenisCallout));
                } else {
                    // Kutipan biasa
                    StringBuilder sbKutipan = new StringBuilder();
                    while (i < baris.length && baris[i].trim().startsWith(">")) {
                        sbKutipan.append(baris[i].trim().substring(1).trim()).append("\n");
                        i++;
                    }
                    i--;
                    slide.tambahBlok(new BlockDraft(BlockType.KUTIPAN, sbKutipan.toString().trim()));
                }
                continue;
            }

            // Baris paragraf biasa
            if (!bufferParagraf.isEmpty()) {
                bufferParagraf.append("\n");
            }
            bufferParagraf.append(b);
        }

        if (!bufferParagraf.isEmpty()) {
            slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, bufferParagraf.toString().trim()));
        }

        if (slide.getDaftarBlok().isEmpty()) {
            slide.tambahBlok(new BlockDraft(BlockType.PARAGRAF, ""));
        }

        return slide;
    }

    /** Men-serialisasi satu DrafSlide ke format Markdown standar. */
    public static String toMarkdown(SlideDraft slide) {
        if (slide == null) {
            return "";
        }
        return toMarkdown(List.of(slide));
    }

    /** Men-serialisasi daftar DrafSlide kembali ke format Markdown UTF-8 standar. */
    public static String toMarkdown(List<SlideDraft> daftarSlide) {
        if (daftarSlide == null || daftarSlide.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int sIdx = 0; sIdx < daftarSlide.size(); sIdx++) {
            SlideDraft slide = daftarSlide.get(sIdx);

            if (sIdx > 0) {
                String jdl = slide.getJudulSlide();
                if (jdl == null || jdl.isEmpty()) {
                    jdl = "Slide " + (sIdx + 1);
                }
                sb.append("\n<!-- slide: ").append(jdl).append(" -->\n\n");
            }

            for (BlockDraft blok : slide.getDaftarBlok()) {
                String isi = blok.getIsi() == null ? "" : blok.getIsi().trim();
                switch (blok.getJenis()) {
                    case HEADING_1 -> {
                        if ("center".equalsIgnoreCase(blok.getEkstra())) {
                            sb.append("<center># ").append(isi).append("</center>\n\n");
                        } else if ("right".equalsIgnoreCase(blok.getEkstra())) {
                            sb.append("<div align=\"right\"># ").append(isi).append("</div>\n\n");
                        } else {
                            sb.append("# ").append(isi).append("\n\n");
                        }
                    }
                    case HEADING_2 -> {
                        if ("center".equalsIgnoreCase(blok.getEkstra())) {
                            sb.append("<center>## ").append(isi).append("</center>\n\n");
                        } else if ("right".equalsIgnoreCase(blok.getEkstra())) {
                            sb.append("<div align=\"right\">## ").append(isi).append("</div>\n\n");
                        } else {
                            sb.append("## ").append(isi).append("\n\n");
                        }
                    }
                    case CALLOUT -> {
                        String jenis = blok.getEkstra().isEmpty() ? "NOTE" : blok.getEkstra().toUpperCase();
                        sb.append("> [!").append(jenis).append("]\n");
                        for (String baris : isi.split("\n")) {
                            sb.append("> ").append(baris).append("\n");
                        }
                        sb.append("\n");
                    }
                    case GAMBAR -> {
                        sb.append("![").append(blok.getEkstra()).append("](").append(isi).append(")\n\n");
                    }
                    case VIDEO -> {
                        sb.append("<!-- youtube: ").append(isi);
                        if (!blok.getEkstra().isEmpty()) {
                            sb.append(" | ").append(blok.getEkstra());
                        }
                        sb.append(" -->\n\n");
                    }
                    case KUTIPAN -> {
                        for (String baris : isi.split("\n")) {
                            sb.append("> ").append(baris).append("\n");
                        }
                        sb.append("\n");
                    }
                    case KODE -> {
                        sb.append("```").append(blok.getEkstra()).append("\n")
                                .append(isi).append("\n```\n\n");
                    }
                    case PARAGRAF -> {
                        if (!isi.isEmpty()) {
                            sb.append(isi).append("\n\n");
                        }
                    }
                }
            }
        }

        return sb.toString().stripTrailing() + "\n";
    }
}
