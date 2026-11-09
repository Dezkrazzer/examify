package com.examify.security;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.geometry.Rectangle2D;
import javafx.stage.PopupWindow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Mode Aman ujian (STRICTLY_SAFE): layar penuh terkunci, jendela tidak bisa ditutup/diminimalkan,
 * pintasan sistem diblokir lewat {@link KeyboardBlocker}, salin-tempel dan menu klik kanan dimatikan,
 * serta setiap kali siswa meninggalkan jendela ujian dicatat sebagai pelanggaran.
 * Semua method harus dipanggil dari FX Application Thread.
 */
public final class SecureMode {

    /** Satu kejadian pelanggaran yang dilaporkan ke layar ujian. */
    public record Pelanggaran(int ke, String jenis, String keterangan, LocalDateTime waktu) {
    }

    public static final String JENIS_KELUAR_JENDELA = "KELUAR_JENDELA";
    public static final String JENIS_MINIMIZE = "MINIMIZE";

    private static final Duration JEDA_CEK_FOKUS = Duration.millis(300);
    private static final long JEDA_PELANGGARAN_MS = 1500; // satu kejadian tidak dihitung berkali-kali

    private final Stage stage;
    private final Scene scene;
    private final Consumer<Pelanggaran> pendengarPelanggaran;
    private final Consumer<String> pendengarDiblokir;
    private final KeyboardBlocker pemblokir = new KeyboardBlocker();
    private ForegroundGuard penjagaJendela;

    private final EventHandler<WindowEvent> tolakTutup = Event::consume;
    private final EventHandler<KeyEvent> saringTombol = this::saringTombol;
    private final EventHandler<ContextMenuEvent> tolakMenu = Event::consume;
    private final EventHandler<DragEvent> tolakSeret = Event::consume;
    private final ChangeListener<Boolean> pantauFokus = (obs, lama, fokus) -> saatFokusBerubah(fokus);
    private final ChangeListener<Boolean> pantauLayarPenuh = (obs, lama, penuh) -> {
        if (!penuh) {
            kunciLayarPenuh();
        }
    };
    private final ChangeListener<Boolean> pantauMinimize = (obs, lama, kecil) -> {
        if (kecil) {
            catat(JENIS_MINIMIZE, "Jendela ujian diminimalkan");
            rebutFokus();
        }
    };
    private final Timeline penjagaFokus = new Timeline(new KeyFrame(Duration.seconds(1), e -> jagaFokus()));

    private boolean aktif;
    private boolean hookAktif;
    private boolean diLuarJendela;
    private int jumlahPelanggaran;
    private long waktuPelanggaranTerakhir;

    public SecureMode(Stage stage, Consumer<Pelanggaran> pendengarPelanggaran, Consumer<String> pendengarDiblokir) {
        this.stage = Objects.requireNonNull(stage);
        this.scene = Objects.requireNonNull(stage.getScene());
        this.pendengarPelanggaran = pendengarPelanggaran;
        this.pendengarDiblokir = pendengarDiblokir;
        penjagaFokus.setCycleCount(Animation.INDEFINITE);
    }

    public void aktifkan() {
        if (aktif) {
            return;
        }
        aktif = true;
        Clipboard.getSystemClipboard().clear(); // teks yang disiapkan sebelum ujian tidak bisa ditempel
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setAlwaysOnTop(true);
        sesuaikanKeLayar();
        stage.setFullScreen(true);
        // beberapa driver/skala DPI melaporkan ukuran scene yang belum final saat masuk layar penuh
        javafx.application.Platform.runLater(() -> scene.getRoot().requestLayout());
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, tolakTutup);
        scene.addEventFilter(KeyEvent.ANY, saringTombol);
        scene.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, tolakMenu);
        scene.addEventFilter(DragEvent.ANY, tolakSeret);
        stage.focusedProperty().addListener(pantauFokus);
        stage.fullScreenProperty().addListener(pantauLayarPenuh);
        stage.iconifiedProperty().addListener(pantauMinimize);
        penjagaFokus.play();
        hookAktif = pemblokir.pasang();
        // swipe 3/4 jari touchpad tidak bisa diblokir; aplikasi lain yang sempat aktif langsung ditutup kembali
        penjagaJendela = new ForegroundGuard(stage.getTitle(), () -> javafx.application.Platform.runLater(() -> {
            if (aktif) {
                catat(JENIS_KELUAR_JENDELA, "Siswa berpindah ke aplikasi/desktop lain (gesture atau pintasan)");
                rebutFokus();
            }
        }));
        penjagaJendela.mulai();
        if (!hookAktif) {
            System.err.println("[ModeAman] Hook keyboard tidak aktif; pintasan sistem hanya dideteksi, tidak diblokir.");
        }
    }

    public void nonaktifkan() {
        if (!aktif) {
            return;
        }
        aktif = false;
        penjagaFokus.stop();
        pemblokir.lepas();
        if (penjagaJendela != null) {
            penjagaJendela.berhenti();
            penjagaJendela = null;
        }
        stage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, tolakTutup);
        scene.removeEventFilter(KeyEvent.ANY, saringTombol);
        scene.removeEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, tolakMenu);
        scene.removeEventFilter(DragEvent.ANY, tolakSeret);
        stage.focusedProperty().removeListener(pantauFokus);
        stage.fullScreenProperty().removeListener(pantauLayarPenuh);
        stage.iconifiedProperty().removeListener(pantauMinimize);
        stage.setAlwaysOnTop(false);
        stage.setFullScreen(false);
        stage.setMaximized(true);
        stage.setFullScreenExitKeyCombination(null); // kembali ke bawaan (Esc)
        stage.setFullScreenExitHint(null);
    }

    public boolean isAktif() {
        return aktif;
    }

    /** true bila Alt+Tab/tombol Windows benar-benar diblokir (bukan hanya dideteksi). */
    public boolean isHookAktif() {
        return hookAktif;
    }

    public int getJumlahPelanggaran() {
        return jumlahPelanggaran;
    }

    // ---------------------------------------------------------------- pintasan di dalam aplikasi

    private void saringTombol(KeyEvent e) {
        if (e.getEventType() == KeyEvent.KEY_TYPED) {
            return; // karakter biasa tetap boleh diketik
        }
        KeyCode kode = e.getCode();
        boolean pintasan = e.isShortcutDown();
        boolean salinTempel = (pintasan && (kode == KeyCode.C || kode == KeyCode.V || kode == KeyCode.X
                || kode == KeyCode.INSERT))
                || (e.isShiftDown() && (kode == KeyCode.INSERT || kode == KeyCode.DELETE))
                || kode == KeyCode.COPY || kode == KeyCode.PASTE || kode == KeyCode.CUT;
        boolean sistem = kode == KeyCode.F11 || kode == KeyCode.ESCAPE && e.isAltDown()
                || kode == KeyCode.F4 && e.isAltDown() || kode == KeyCode.TAB && e.isAltDown()
                || kode == KeyCode.WINDOWS || kode == KeyCode.CONTEXT_MENU || kode == KeyCode.PRINTSCREEN;
        if (!salinTempel && !sistem) {
            return;
        }
        e.consume();
        if (salinTempel && e.getEventType() == KeyEvent.KEY_PRESSED && pendengarDiblokir != null) {
            pendengarDiblokir.accept("Salin, potong, dan tempel dinonaktifkan selama Mode Aman.");
        }
    }

    // ---------------------------------------------------------------- jendela & fokus

    private void kunciLayarPenuh() {
        if (aktif && !stage.isFullScreen()) {
            javafx.application.Platform.runLater(() -> {
                if (aktif) {
                    stage.setFullScreen(true);
                }
            });
        }
    }

    /**
     * Menyamakan ukuran jendela dengan monitor sebelum masuk layar penuh. Tanpa ini, pada skala
     * DPI pecahan ukuran jendela lama bisa terbawa sehingga bagian bawah layar tidak tertutup scene.
     */
    private void sesuaikanKeLayar() {
        if (stage.isFullScreen()) {
            return;
        }
        var layar = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Rectangle2D batas = (layar.isEmpty() ? Screen.getPrimary() : layar.get(0)).getBounds();
        stage.setMaximized(false);
        stage.setX(batas.getMinX());
        stage.setY(batas.getMinY());
        stage.setWidth(batas.getWidth());
        stage.setHeight(batas.getHeight());
    }

    private void saatFokusBerubah(boolean fokus) {
        if (!aktif) {
            return;
        }
        if (fokus) {
            diLuarJendela = false;
            return;
        }
        // tunggu sebentar: dialog milik aplikasi sendiri juga membuat stage kehilangan fokus
        PauseTransition jeda = new PauseTransition(JEDA_CEK_FOKUS);
        jeda.setOnFinished(e -> {
            if (aktif && !stage.isFocused() && !jendelaAplikasiAktif() && !diLuarJendela) {
                diLuarJendela = true;
                catat(JENIS_KELUAR_JENDELA, "Siswa meninggalkan jendela ujian");
                rebutFokus();
            }
        });
        jeda.play();
    }

    private void jagaFokus() {
        if (aktif && !stage.isFocused() && !jendelaAplikasiAktif()) {
            rebutFokus();
        }
    }

    /** Dialog/peringatan milik Examify sendiri tidak dihitung sebagai keluar dari ujian. */
    private boolean jendelaAplikasiAktif() {
        for (Window w : Window.getWindows()) {
            if (w == stage || w instanceof PopupWindow || !w.isShowing()) {
                continue;
            }
            if (w.isFocused() || (w instanceof Stage s && s.getOwner() == stage)) {
                return true;
            }
        }
        return false;
    }

    private void rebutFokus() {
        if (!aktif) {
            return;
        }
        stage.setIconified(false);
        if (!stage.isFullScreen()) {
            stage.setFullScreen(true);
        }
        stage.toFront();
        stage.requestFocus();
    }

    private void catat(String jenis, String keterangan) {
        long sekarang = System.currentTimeMillis();
        if (sekarang - waktuPelanggaranTerakhir < JEDA_PELANGGARAN_MS) {
            return;
        }
        waktuPelanggaranTerakhir = sekarang;
        jumlahPelanggaran++;
        if (pendengarPelanggaran != null) {
            pendengarPelanggaran.accept(new Pelanggaran(jumlahPelanggaran, jenis, keterangan, LocalDateTime.now()));
        }
    }
}
