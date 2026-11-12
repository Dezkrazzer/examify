package com.examify.controller;

import com.examify.app.SceneRouter;
import com.examify.utils.AppConfig;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

/** Dialog standar agar tampilan pesan konsisten di semua layar. */
final class DialogUtil {

    private DialogUtil() {
    }

    static <T> Dialog<T> buatDialogTransparan() {
        Dialog<T> dialog = new Dialog<>();
        dialog.initOwner(SceneRouter.getStage());
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = new DialogPane() {
            @Override
            protected Node createButtonBar() {
                return null;
            }
        };
        pane.getStyleClass().add("dialog-transparan");
        pane.setBackground(Background.EMPTY);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getStylesheets().setAll(SceneRouter.getScene().getStylesheets());
        pane.getButtonTypes().add(ButtonType.CLOSE);
        pane.sceneProperty().addListener((obs, lama, sceneBaru) -> {
            if (sceneBaru != null) {
                sceneBaru.setFill(Color.TRANSPARENT);
            }
        });
        dialog.setDialogPane(pane);

        dialog.setOnShown(e -> {
            if (pane.getScene() != null) {
                pane.getScene().setFill(Color.TRANSPARENT);
            }
        });
        return dialog;
    }

    static VBox buatKartuModal(double prefWidth, double maxWidth) {
        VBox kartu = new VBox(16);
        kartu.getStyleClass().add("modal-kartu");
        kartu.setPrefWidth(prefWidth);
        kartu.setMaxWidth(maxWidth);
        // Radius & offsetY dipilih agar bayangan selesai luruh di dalam padding dialog (20/24/28).
        kartu.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.22), 18, 0.05, 0, 6));
        return kartu;
    }

    static void pasangDragModal(Dialog<?> dialog, Node handle) {
        final double[] dragOffset = new double[2];
        handle.setOnMousePressed(e -> {
            dragOffset[0] = dialog.getX() - e.getScreenX();
            dragOffset[1] = dialog.getY() - e.getScreenY();
        });
        handle.setOnMouseDragged(e -> {
            dialog.setX(e.getScreenX() + dragOffset[0]);
            dialog.setY(e.getScreenY() + dragOffset[1]);
        });
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
