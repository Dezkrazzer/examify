package com.examify.app;

import com.examify.utils.AppConfig;
import com.examify.view.AnimationUtil;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import com.examify.view.SmoothScroll;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;

/**
 * Satu-satunya jalur perpindahan layar. Data antar-layar dibawa lewat {@link AppSession}.
 * Layar dalam shell dimuat ke area konten shell, sehingga sidebar tidak ikut dimuat ulang.
 */
public final class SceneRouter {

    private static final String SHELL_FXML = AppScreen.FOLDER_FXML + "shell.fxml";
    private static final String THEME_CSS = "/com/examify/css/theme.css";
    private static final String LOGO = "/com/examify/img/examifylogo.png";
    private static final String DARK_CSS = "/com/examify/css/dark.css";
    private static final Color LATAR_TERANG = Color.web("#eef4fb");
    private static final Color LATAR_GELAP = Color.web("#0a1120");
    private static final double LEBAR_AWAL = 1240;
    private static final double TINGGI_AWAL = 800;

    private static final ObjectProperty<AppScreen> layarAktif = new SimpleObjectProperty<>();
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
        // Tema awal mengikuti Windows, dan ikut berubah saat pengguna mengganti mode Windows
        var preferensiOs = Platform.getPreferences();
        temaGelap.set(preferensiOs.getColorScheme() == ColorScheme.DARK);
        terapkanTema(temaGelap.get());
        preferensiOs.colorSchemeProperty().addListener((obs, lama, skema) -> temaGelap.set(skema == ColorScheme.DARK));
        // Setiap dialog/jendela baru otomatis memakai tema aplikasi (title bar + stylesheet)
        Window.getWindows().addListener((ListChangeListener<Window>) ubah -> {
            while (ubah.next()) {
                ubah.getAddedSubList().forEach(SceneRouter::terapkanTemaKeJendela);
            }
        });
        SmoothScroll.pasang(scene);
        stage.setScene(scene);
        stage.setTitle(AppConfig.NAMA_APLIKASI);
        stage.getIcons().add(new Image(sumber(LOGO).toExternalForm()));
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
    }

    public static void pindah(AppScreen layar) {
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
        AnimationUtil.masuk(halaman);
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

    public static ReadOnlyObjectProperty<AppScreen> layarAktifProperty() {
        return layarAktif;
    }

    public static BooleanProperty temaGelapProperty() {
        return temaGelap;
    }

    public static void gantiTema() {
        temaGelap.set(!temaGelap.get());
    }

    private static void terapkanTema(boolean gelap) {
        terapkanTemaKeScene(scene, gelap);
        Window.getWindows().forEach(SceneRouter::terapkanTemaKeJendela);
    }

    /**
     * Menyamakan warna bingkai jendela (title bar Windows) dan stylesheet dengan tema aplikasi.
     * Tanpa ini, title bar mengikuti mode Windows sehingga bisa gelap padahal isi aplikasi terang.
     */
    private static void terapkanTemaKeJendela(Window jendela) {
        if (jendela instanceof PopupWindow || jendela.getScene() == null) {
            return; // popup (menu, tooltip) sudah mewarisi stylesheet dari scene pemiliknya
        }
        terapkanTemaKeScene(jendela.getScene(), temaGelap.get());
    }

    private static void terapkanTemaKeScene(Scene target, boolean gelap) {
        target.getPreferences().setColorScheme(gelap ? ColorScheme.DARK : ColorScheme.LIGHT);
        // Fill scene = warna latar tema. Saat layar penuh dengan skala DPI pecahan (125%/150%),
        // ukuran root bisa lebih kecil 1-2 px dari layar; sisanya tidak lagi tampil sebagai strip putih.
        target.setFill(gelap ? LATAR_GELAP : LATAR_TERANG);
        String theme = sumber(THEME_CSS).toExternalForm();
        String dark = sumber(DARK_CSS).toExternalForm();
        if (!target.getStylesheets().contains(theme)) {
            target.getStylesheets().add(0, theme);
        }
        target.getStylesheets().remove(dark);
        if (gelap) {
            target.getStylesheets().add(dark);
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
