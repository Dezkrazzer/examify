package com.examify.view;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/**
 * Node ikon yang dapat dipakai langsung di FXML:
 * {@code <IkonView ikon="BUKU" ukuran="18"/>}. Warna mengikuti CSS (.ikon).
 */
public class IkonView extends StackPane {

    private static final double GRID = 24;

    private final SVGPath svg = new SVGPath();
    private Ikon ikon = Ikon.BINTANG;
    private double ukuran = 18;

    public IkonView() {
        getStyleClass().add("ikon-wadah");
        svg.getStyleClass().add("ikon");
        getChildren().add(svg);
        perbarui();
    }

    public IkonView(Ikon ikon, double ukuran) {
        this();
        setIkon(ikon);
        setUkuran(ukuran);
    }

    public Ikon getIkon() {
        return ikon;
    }

    public void setIkon(Ikon ikon) {
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
