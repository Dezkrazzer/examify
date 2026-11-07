package com.examify.view;

import com.examify.utils.MarkdownParser;
import com.examify.utils.MarkdownParser.Blok;
import com.examify.utils.MarkdownParser.Potongan;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/** Mengubah hasil {@link MarkdownParser} menjadi node JavaFX yang ditata lewat CSS (.md-*). */
public final class MarkdownView {

    private static final double JARAK_BLOK = 12;

    private MarkdownView() {
    }

    public static VBox render(String markdown) {
        VBox wadah = new VBox(JARAK_BLOK);
        wadah.getStyleClass().add("md");
        for (Blok blok : MarkdownParser.parse(markdown)) {
            wadah.getChildren().add(renderBlok(blok));
        }
        return wadah;
    }

    private static Node renderBlok(Blok blok) {
        return switch (blok.jenis()) {
            case HEADING -> teks(blok.isi(), "md-h" + blok.level());
            case PARAGRAF -> teks(blok.isi(), "md-p");
            case BUTIR -> butir("•", blok.isi());
            case NOMOR -> butir(blok.level() + ".", blok.isi());
            case KUTIPAN -> kutipan(blok.isi());
            case KODE -> kode(blok.isi());
            case GARIS -> garis();
        };
    }

    private static TextFlow teks(String isi, String kelas) {
        TextFlow flow = new TextFlow();
        flow.getStyleClass().add(kelas);

        String teksBersih = isi != null ? isi : "";
        if (isi != null) {
            String lower = isi.trim().toLowerCase();
            if (lower.startsWith("<center>") && lower.endsWith("</center>") && isi.trim().length() >= 17) {
                flow.setTextAlignment(TextAlignment.CENTER);
                teksBersih = isi.trim().substring(8, isi.trim().length() - 9).trim();
            } else if (lower.startsWith("<div align=\"right\">") && lower.endsWith("</div>") && isi.trim().length() >= 25) {
                flow.setTextAlignment(TextAlignment.RIGHT);
                teksBersih = isi.trim().substring(19, isi.trim().length() - 6).trim();
            } else if (lower.startsWith("<div align=\"center\">") && lower.endsWith("</div>") && isi.trim().length() >= 26) {
                flow.setTextAlignment(TextAlignment.CENTER);
                teksBersih = isi.trim().substring(20, isi.trim().length() - 6).trim();
            } else if (lower.startsWith("<p align=\"center\">") && lower.endsWith("</p>") && isi.trim().length() >= 22) {
                flow.setTextAlignment(TextAlignment.CENTER);
                teksBersih = isi.trim().substring(18, isi.trim().length() - 4).trim();
            } else if (lower.startsWith("<p align=\"right\">") && lower.endsWith("</p>") && isi.trim().length() >= 21) {
                flow.setTextAlignment(TextAlignment.RIGHT);
                teksBersih = isi.trim().substring(17, isi.trim().length() - 4).trim();
            }
        }

        double ukuranFont = switch (kelas) {
            case "md-h1" -> 27.0;
            case "md-h2" -> 19.0;
            case "md-h3" -> 16.0;
            default -> 14.5;
        };

        for (Potongan p : MarkdownParser.parseInline(teksBersih)) {
            String teksPotongan = p.teks();
            if (teksPotongan == null || teksPotongan.isEmpty()) continue;

            String warnaLatar = p.warnaLatar();
            String warnaTeks = p.warnaTeks();
            boolean hasBg = warnaLatar != null && !warnaLatar.isEmpty() && !warnaLatar.equalsIgnoreCase("transparent");

            if (hasBg) {
                // Teks dengan warna sorotan / stabilo (background-color)
                String[] tokens = teksPotongan.split("(?<=\\s)|(?=\\s)");
                for (String token : tokens) {
                    if (token.isBlank()) {
                        Text sp = new Text(token);
                        sp.getStyleClass().add("md-teks");
                        sp.setStyle("-fx-font-size: " + ukuranFont + "px;");
                        flow.getChildren().add(sp);
                    } else {
                        Label lbl = new Label(token);
                        lbl.getStyleClass().add("md-highlight");
                        StringBuilder style = new StringBuilder();
                        style.append("-fx-background-color: ").append(warnaLatar).append("; ");
                        style.append("-fx-background-radius: 3px; ");
                        style.append("-fx-padding: 1 3; ");
                        style.append("-fx-font-size: ").append(ukuranFont).append("px; ");
                        if (warnaTeks != null && !warnaTeks.isEmpty()) {
                            style.append("-fx-text-fill: ").append(warnaTeks).append("; ");
                        } else {
                            style.append("-fx-text-fill: #0f172a; ");
                        }
                        if (p.gaya() == MarkdownParser.GayaTeks.TEBAL) {
                            style.append("-fx-font-weight: bold; ");
                        } else if (p.gaya() == MarkdownParser.GayaTeks.MIRING) {
                            style.append("-fx-font-style: italic; ");
                        } else if (p.gaya() == MarkdownParser.GayaTeks.GARIS_BAWAH) {
                            style.append("-fx-underline: true; ");
                        } else if (p.gaya() == MarkdownParser.GayaTeks.KODE) {
                            style.append("-fx-font-family: 'Consolas', monospace; ");
                        }
                        lbl.setStyle(style.toString());
                        flow.getChildren().add(lbl);
                    }
                }
            } else {
                Text t = new Text(teksPotongan);
                t.getStyleClass().addAll("md-teks", "md-" + p.gaya().name().toLowerCase());
                if (warnaTeks != null && !warnaTeks.isEmpty()) {
                    t.setStyle("-fx-fill: " + warnaTeks + ";");
                }
                flow.getChildren().add(t);
            }
        }
        return flow;
    }

    private static Node butir(String penanda, String isi) {
        Label tanda = new Label(penanda);
        tanda.getStyleClass().add("md-penanda");
        tanda.setMinWidth(22);
        TextFlow flow = teks(isi, "md-p");
        HBox.setHgrow(flow, Priority.ALWAYS);
        HBox baris = new HBox(6, tanda, flow);
        baris.getStyleClass().add("md-butir");
        return baris;
    }

    private static Node kutipan(String isi) {
        HBox kotak = new HBox(teks(isi, "md-p"));
        kotak.getStyleClass().add("md-kutipan");
        return kotak;
    }

    private static Node kode(String isi) {
        Label label = new Label(isi);
        label.getStyleClass().add("md-kode-teks");
        label.setWrapText(false);
        VBox kotak = new VBox(label);
        kotak.getStyleClass().add("md-kode");
        kotak.setAlignment(Pos.TOP_LEFT);
        return kotak;
    }

    private static Node garis() {
        Region r = new Region();
        r.getStyleClass().add("md-garis");
        return r;
    }
}
