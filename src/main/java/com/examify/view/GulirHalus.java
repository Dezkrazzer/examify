package com.examify.view;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Smooth scrolling ala web untuk semua ScrollPane: roda mouse tidak lagi melompat,
 * melainkan beranimasi (ease-out) menuju posisi tujuan. Scroll beruntun diakumulasi
 * agar tetap terasa responsif.
 */
public final class GulirHalus {

    private static final Duration DURASI = Duration.millis(380);
    private static final double KELIPATAN_DELTA = 2.2; // jarak per notch roda, relatif delta bawaan
    private static final Interpolator EASE_OUT = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

    private static final Map<ScrollPane, Timeline> ANIMASI = new WeakHashMap<>();
    private static final Map<ScrollPane, Double> TUJUAN = new WeakHashMap<>();

    private GulirHalus() {
    }

    /** Dipasang sekali pada Scene; berlaku untuk halaman apa pun yang dimuat kemudian. */
    public static void pasang(Scene scene) {
        scene.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0 || e.isShiftDown() || e.isControlDown() || e.isInertia()) {
                return;
            }
            ScrollPane sp = cariScrollPane(e.getTarget());
            if (sp == null) {
                return;
            }
            double tinggiKonten = sp.getContent().getBoundsInLocal().getHeight();
            double tinggiViewport = sp.getViewportBounds().getHeight();
            double sisa = tinggiKonten - tinggiViewport;
            if (sisa <= 0) {
                return;
            }
            double rentang = sp.getVmax() - sp.getVmin();
            double asal = animasiBerjalan(sp) ? TUJUAN.getOrDefault(sp, sp.getVvalue()) : sp.getVvalue();
            double tujuan = asal - e.getDeltaY() * KELIPATAN_DELTA / sisa * rentang;
            animasikan(sp, tujuan);
            e.consume();
        });
    }

    /** Menggulir halus ke nilai vvalue tertentu (mis. tombol navigasi "Kursus"). */
    public static void animasikan(ScrollPane sp, double vvalueTujuan) {
        double tujuan = Math.max(sp.getVmin(), Math.min(sp.getVmax(), vvalueTujuan));
        Timeline lama = ANIMASI.get(sp);
        if (lama != null) {
            lama.stop();
        }
        TUJUAN.put(sp, tujuan);
        Timeline tl = new Timeline(new KeyFrame(DURASI, new KeyValue(sp.vvalueProperty(), tujuan, EASE_OUT)));
        ANIMASI.put(sp, tl);
        tl.play();
    }

    private static boolean animasiBerjalan(ScrollPane sp) {
        Timeline tl = ANIMASI.get(sp);
        return tl != null && tl.getStatus() == Animation.Status.RUNNING;
    }

    /**
     * ScrollPane terdekat milik halaman. Kontrol yang punya scroll sendiri (TextArea,
     * ListView, TableView, dll.) dibiarkan memakai perilaku bawaan.
     */
    private static ScrollPane cariScrollPane(Object target) {
        Node node = target instanceof Node n ? n : null;
        while (node != null) {
            if (node instanceof ScrollPane sp && sp.getStyleClass().contains("scroll-pane")
                    && !(sp.getParent() != null && dalamKontrolLain(sp.getParent()))) {
                return sp;
            }
            if (node instanceof TextInputControl) {
                return null;
            }
            if (node instanceof Control && !(node instanceof ScrollPane) && punyaScrollSendiri(node)) {
                return null;
            }
            node = node.getParent();
        }
        return null;
    }

    private static boolean punyaScrollSendiri(Node node) {
        String nama = node.getClass().getSimpleName();
        return nama.endsWith("ListView") || nama.endsWith("TableView") || nama.endsWith("TreeView")
                || nama.equals("WebView") || nama.equals("ComboBoxListViewSkin");
    }

    private static boolean dalamKontrolLain(Node node) {
        for (Node n = node; n != null; n = n.getParent()) {
            if (n instanceof TextInputControl) {
                return true;
            }
            if (n instanceof ScrollPane) {
                return false;
            }
        }
        return false;
    }
}
