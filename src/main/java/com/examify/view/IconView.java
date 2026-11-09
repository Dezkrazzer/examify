package com.examify.view;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/**
 * Node ikon yang dapat dipakai langsung di FXML:
 * {@code <IconView ikon="BUKU" ukuran="18"/>}. Warna mengikuti CSS (.ikon).
 */
public class IconView extends StackPane {

    private static final double GRID = 24;

    private final SVGPath svg = new SVGPath();
    private Icon ikon = Icon.BINTANG;
    private double ukuran = 18;

    public IconView() {
        getStyleClass().add("ikon-wadah");
        svg.getStyleClass().add("ikon");
        getChildren().add(svg);
        perbarui();
    }

    public IconView(Icon ikon, double ukuran) {
        this();
        setIkon(ikon);
        setUkuran(ukuran);
    }

    public Icon getIkon() {
        return ikon;
    }

    public void setIkon(Icon ikon) {
        this.ikon = ikon;
        perbarui();
    }

    public double getUkuran() {
        return ukuran;
    }

    public void setUkuran(double ukuran) {
        this.ukuran = ukuran;
        perbarui();
    }

    private void perbarui() {
        svg.setContent(ikon.getPath());
        double skala = ukuran / GRID;
        svg.setScaleX(skala);
        svg.setScaleY(skala);
        setMinSize(ukuran, ukuran);
        setPrefSize(ukuran, ukuran);
        setMaxSize(ukuran, ukuran);
    }
}
