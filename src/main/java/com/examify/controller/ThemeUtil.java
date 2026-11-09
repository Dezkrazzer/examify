package com.examify.controller;

import com.examify.app.SceneRouter;
import com.examify.view.Icon;
import com.examify.view.IconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/** Menghubungkan tombol pengganti tema terang/gelap ke router. */
final class ThemeUtil {

    private static final String KUNCI_LISTENER = "examify.listenerTema";

    private ThemeUtil() {
    }

    /** @param denganTeks true untuk tombol sidebar yang menampilkan label */
    static void pasang(Button tombol, boolean denganTeks) {
        Runnable perbarui = () -> {
            boolean gelap = SceneRouter.temaGelapProperty().get();
            tombol.setGraphic(new IconView(gelap ? Icon.MATAHARI : Icon.BULAN, 18));
            if (denganTeks) {
                tombol.setText(gelap ? "Mode terang" : "Mode gelap");
            }
            tombol.setTooltip(new Tooltip(gelap ? "Ganti ke mode terang" : "Ganti ke mode gelap"));
        };
        perbarui.run();
        ChangeListener<Boolean> listener = (obs, lama, baru) -> perbarui.run();
        // Weak listener agar tombol halaman lama dapat dibersihkan GC; referensi kuat disimpan di tombol
        tombol.getProperties().put(KUNCI_LISTENER, listener);
        SceneRouter.temaGelapProperty().addListener(new WeakChangeListener<>(listener));
        tombol.setOnAction(e -> SceneRouter.gantiTema());
    }
}
