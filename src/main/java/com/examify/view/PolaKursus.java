package com.examify.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Random;

/**
 * Header bermotif untuk kartu kursus. Warna dan pola diturunkan dari kode kursus,
 * sehingga setiap kursus selalu tampil dengan motif yang sama dan khas.
 */
public final class PolaKursus {

    private static final int JUMLAH_LINGKARAN = 7;

    private PolaKursus() {
    }

    /** Warna dasar kursus (hue dari hash kode) agar konsisten di semua halaman. */
    public static Color warnaDasar(String kodeKursus) {
        double hue = Math.floorMod(kodeKursus.hashCode(), 360);
        return Color.hsb(hue, 0.55, 0.92);
    }

    public static Pane buat(String kodeKursus, double lebar, double tinggi, double radiusSudut) {
        Color dasar = warnaDasar(kodeKursus);
        Pane pane = new Pane();
        pane.setPrefSize(lebar, tinggi);
        pane.setMinHeight(tinggi);
        pane.setMaxHeight(tinggi);

        Rectangle latar = new Rectangle();
        latar.widthProperty().bind(pane.widthProperty());
        latar.setHeight(tinggi);
        latar.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, dasar), new Stop(1, dasar.deriveColor(40, 1, 0.85, 1))));
        pane.getChildren().add(latar);

        Random acak = new Random(kodeKursus.hashCode()); // seed tetap -> pola tetap
        for (int i = 0; i < JUMLAH_LINGKARAN; i++) {
            Circle c = new Circle(acak.nextDouble() * lebar, acak.nextDouble() * tinggi,
                    20 + acak.nextDouble() * tinggi * 0.6);
            c.setFill(Color.TRANSPARENT);
            c.setStroke(Color.color(1, 1, 1, 0.18 + acak.nextDouble() * 0.15));
            c.setStrokeWidth(6 + acak.nextDouble() * 10);
            pane.getChildren().add(c);
        }

        Rectangle klip = new Rectangle();
        klip.widthProperty().bind(pane.widthProperty());
        klip.setHeight(tinggi);
        klip.setArcWidth(radiusSudut * 2);
        klip.setArcHeight(radiusSudut * 2);
        pane.setClip(klip);
        return pane;
    }
}
