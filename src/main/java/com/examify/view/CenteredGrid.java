package com.examify.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;

/**
 * Grid kartu berlebar tetap yang rata tengah dan dibatasi maksimal sejumlah kolom.
 * Padding kiri-kanan diatur dinamis sehingga FlowPane membungkus tepat di batas kolom.
 */
public final class CenteredGrid {

    private CenteredGrid() {
    }

    public static void terapkan(FlowPane grid, double lebarKartu, int maksKolom) {
        grid.setAlignment(Pos.TOP_CENTER);
        double lebarMaks = maksKolom * lebarKartu + (maksKolom - 1) * grid.getHgap();
        grid.widthProperty().addListener((obs, lama, lebar) -> {
            double sisi = Math.max(0, Math.floor((lebar.doubleValue() - lebarMaks) / 2) - 1);
            grid.setPadding(new Insets(0, sisi, 0, sisi));
        });
    }
}
