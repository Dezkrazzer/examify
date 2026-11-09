package com.examify.view;

import com.examify.utils.FormatUtil;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.List;

/**
 * Kumpulan animasi kecil yang konsisten di seluruh aplikasi. CSS JavaFX 21 belum mendukung
 * transition, sehingga gerakan dibuat dengan javafx.animation.
 */
public final class AnimationUtil {

    private static final Duration CEPAT = Duration.millis(180);
    private static final Duration SEDANG = Duration.millis(420);
    private static final double JARAK_MASUK = 18;

    private AnimationUtil() {
    }

    /** Node muncul memudar sambil bergeser naik. */
    public static void masuk(Node node) {
        masuk(node, Duration.ZERO);
    }

    public static void masuk(Node node, Duration jeda) {
        node.setOpacity(0);
        node.setTranslateY(JARAK_MASUK);
        FadeTransition pudar = new FadeTransition(SEDANG, node);
        pudar.setToValue(1);
        TranslateTransition geser = new TranslateTransition(SEDANG, node);
        geser.setToY(0);
        geser.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition animasi = new ParallelTransition(pudar, geser);
        animasi.setDelay(jeda);
        animasi.play();
    }

    /** Beberapa node muncul bergantian (stagger), misalnya kartu kursus. */
    public static void berurutan(List<? extends Node> nodes, Duration selang) {
        for (int i = 0; i < nodes.size(); i++) {
            masuk(nodes.get(i), selang.multiply(i));
        }
    }

    /** Kartu sedikit terangkat dan membesar saat kursor di atasnya. */
    public static void angkatSaatHover(Node node) {
        ScaleTransition skala = new ScaleTransition(CEPAT, node);
        TranslateTransition geser = new TranslateTransition(CEPAT, node);
        node.hoverProperty().addListener((obs, lama, hover) -> {
            skala.stop();
            geser.stop();
            skala.setToX(hover ? 1.02 : 1);
            skala.setToY(hover ? 1.02 : 1);
            geser.setToY(hover ? -6 : 0);
            skala.play();
            geser.play();
        });
    }

    /** Angka pada label menghitung naik dari 0 ke target. */
    public static void hitungNaik(Label label, long target, Duration durasi) {
        SimpleIntegerProperty nilai = new SimpleIntegerProperty(0);
        nilai.addListener((obs, lama, baru) -> label.setText(FormatUtil.ribuan(baru.longValue())));
        label.setText("0");
        new Timeline(new KeyFrame(durasi, new KeyValue(nilai, (int) target, Interpolator.EASE_OUT))).play();
    }

    /** Gerakan naik-turun pelan tanpa henti untuk elemen dekoratif. */
    public static Animation melayang(Node node, double jarak, Duration periode) {
        TranslateTransition t = new TranslateTransition(periode, node);
        t.setByY(-jarak);
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        t.setInterpolator(Interpolator.EASE_BOTH);
        t.play();
        return t;
    }

    /** Getaran singkat ke kiri-kanan, misalnya saat PIN salah. */
    public static void getar(Node node) {
        TranslateTransition t = new TranslateTransition(Duration.millis(60), node);
        t.setFromX(0);
        t.setByX(10);
        t.setAutoReverse(true);
        t.setCycleCount(6);
        t.setOnFinished(e -> node.setTranslateX(0));
        t.play();
    }

    /** Denyut skala berulang, misalnya untuk timer yang hampir habis. */
    public static Animation denyut(Node node) {
        ScaleTransition t = new ScaleTransition(Duration.millis(500), node);
        t.setToX(1.08);
        t.setToY(1.08);
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        t.play();
        return t;
    }
}
