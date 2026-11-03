package com.examify.view;

import javafx.scene.CacheHint;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Latar dekoratif "glassmorphism": beberapa gumpalan warna besar yang di-blur dan
 * bergerak pelan. Kartu semi-transparan di atasnya terlihat seperti kaca buram.
 * Warna gumpalan diatur di CSS (.blob-1 .. .blob-3) sehingga ikut tema terang/gelap.
 */
public class LatarAnimasi extends Pane {

    private static final double[][] POSISI = { // {x relatif, y relatif, radius relatif terhadap lebar}
            {0.12, 0.18, 0.22},
            {0.85, 0.30, 0.20},
            {0.55, 0.92, 0.26},
    };

    public LatarAnimasi() {
        getStyleClass().add("latar-animasi");
        setMouseTransparent(true);
        setManaged(true);
        for (int i = 0; i < POSISI.length; i++) {
            getChildren().add(buatGumpalan(i));
        }
        widthProperty().addListener((obs, lama, baru) -> aturPosisi());
        heightProperty().addListener((obs, lama, baru) -> aturPosisi());
    }

    private Circle buatGumpalan(int indeks) {
        Circle c = new Circle();
        c.getStyleClass().addAll("blob", "blob-" + (indeks + 1));
        c.setEffect(new GaussianBlur(63));
        c.setCache(true);            // blur dihitung sekali, lalu hanya digeser
        c.setCacheHint(CacheHint.SPEED);
        AnimasiUtil.melayang(c, 40 + indeks * 15, Duration.seconds(7 + indeks * 2));
        return c;
    }

    private void aturPosisi() {
        double w = getWidth();
        double h = getHeight();
        for (int i = 0; i < POSISI.length; i++) {
            Circle c = (Circle) getChildren().get(i);
            c.setCenterX(w * POSISI[i][0]);
            c.setCenterY(h * POSISI[i][1]);
            c.setRadius(Math.max(120, w * POSISI[i][2]));
        }
    }
}
