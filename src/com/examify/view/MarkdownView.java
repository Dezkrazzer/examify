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
        for (Potongan p : MarkdownParser.parseInline(isi)) {
            Text t = new Text(p.teks());
            t.getStyleClass().addAll("md-teks", "md-" + p.gaya().name().toLowerCase());
            flow.getChildren().add(t);
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
