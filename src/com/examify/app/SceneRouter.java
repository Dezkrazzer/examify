package com.examify.app;

import com.examify.utils.AppConfig;
import com.examify.view.AnimasiUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;

/**
 * Satu-satunya jalur perpindahan layar. Data antar-layar dibawa lewat {@link AppSession}.
 * Layar dalam shell dimuat ke area konten shell, sehingga sidebar tidak ikut dimuat ulang.
 */
public final class SceneRouter {

    private static final String SHELL_FXML = Layar.FOLDER_FXML + "shell.fxml";
    private static final String THEME_CSS = "/com/examify/css/theme.css";
    private static final String DARK_CSS = "/com/examify/css/dark.css";
    private static final double LEBAR_AWAL = 1240;
    private static final double TINGGI_AWAL = 800;

    private static final ObjectProperty<Layar> layarAktif = new SimpleObjectProperty<>();
    private static final BooleanProperty temaGelap = new SimpleBooleanProperty(false);

    private static Stage stage;
    private static Scene scene;
    private static Parent shell;
    private static StackPane kontenShell;

    private SceneRouter() {
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        scene = new Scene(new Pane(), LEBAR_AWAL, TINGGI_AWAL);
        scene.getStylesheets().add(sumber(THEME_CSS).toExternalForm());
        temaGelap.addListener((obs, lama, gelap) -> terapkanTema(gelap));
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
    }

    public static void pindah(Layar layar) {
        stage.setOnCloseRequest(null); // handler khusus layar lama tidak boleh terbawa
        Parent halaman = muat(layar.getFxmlPath());
        if (layar.isDalamShell()) {
            pastikanShellTampil();
            kontenShell.getChildren().setAll(halaman);
        } else {
            shell = null;
            scene.setRoot(halaman);
        }
        layarAktif.set(layar);
        stage.setTitle(AppConfig.NAMA_APLIKASI + " - " + layar.getJudul());
        AnimasiUtil.masuk(halaman);
    }

    private static void pastikanShellTampil() {
        if (shell == null || scene.getRoot() != shell) {
            shell = muat(SHELL_FXML); // ShellController memanggil pasangKontenShell()
            scene.setRoot(shell);
        }
    }

    /** Dipanggil ShellController agar router tahu tempat memuat halaman. */
    public static void pasangKontenShell(StackPane konten) {
        kontenShell = konten;
    }

    public static ReadOnlyObjectProperty<Layar> layarAktifProperty() {
        return layarAktif;
    }

    public static BooleanProperty temaGelapProperty() {
        return temaGelap;
    }

    public static void gantiTema() {
        temaGelap.set(!temaGelap.get());
    }

    private static void terapkanTema(boolean gelap) {
        String dark = sumber(DARK_CSS).toExternalForm();
        scene.getStylesheets().remove(dark);
        if (gelap) {
            scene.getStylesheets().add(dark);
        }
    }

    /** Stylesheet yang sama juga dipasang ke dialog agar ikut tema. */
    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return stage;
    }

    private static Parent muat(String path) {
        try {
            return FXMLLoader.load(sumber(path));
        } catch (IOException e) {
            throw new UncheckedIOException("Gagal memuat " + path, e);
        }
    }

    private static URL sumber(String path) {
        return Objects.requireNonNull(SceneRouter.class.getResource(path), "Resource tidak ditemukan: " + path);
    }
}
