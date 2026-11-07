package com.examify.controller;

import com.examify.app.SceneRouter;
import com.examify.utils.AppConfig;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/** Dialog standar agar tampilan pesan konsisten di semua layar. */
final class DialogUtil {

    private DialogUtil() {
    }

    static void info(String pesan) {
        buat(Alert.AlertType.INFORMATION, pesan).showAndWait();
    }

    static void peringatan(String pesan) {
        buat(Alert.AlertType.WARNING, pesan).showAndWait();
    }

    static boolean konfirmasi(String pesan) {
        Alert alert = buat(Alert.AlertType.CONFIRMATION, pesan);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().filter(ButtonType.YES::equals).isPresent();
    }

    private static Alert buat(Alert.AlertType tipe, String pesan) {
        Alert alert = new Alert(tipe, pesan);
        alert.setTitle(AppConfig.NAMA_APLIKASI);
        alert.setHeaderText(null);
        alert.initOwner(SceneRouter.getStage());
        alert.getDialogPane().getStylesheets().setAll(SceneRouter.getScene().getStylesheets()); // ikut tema
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // cegah pesan panjang terpotong
        alert.getDialogPane().setPrefWidth(460);
        return alert;
    }
}
