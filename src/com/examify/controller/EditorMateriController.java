package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.io.SlideMarkdownConverter;
import com.examify.model.Bab;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriTeks;
import com.examify.model.materi.DrafBlok;
import com.examify.model.materi.DrafSlide;
import com.examify.model.materi.JenisBlok;
import com.examify.service.KursusService;
import com.examify.utils.HtmlMarkdownUtil;
import com.examify.utils.StringUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
import com.examify.view.MarkdownView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class EditorMateriController {

    // ------------------------------------------------------------------ Navbar & Status Atas
    @FXML private Label lblBreadcrumb;
    @FXML private TextField tfJudulMateri;
    @FXML private ToggleButton tbModeEdit;
    @FXML private ToggleButton tbModePratinjau;

    // ------------------------------------------------------------------ Simplified Ribbon UI
    @FXML private TabPane ribbonTabPane;

    // Tab 1: Home (Clipboard, Font, Paragraf, Tipografi)
    @FXML private Button btnCopy;
    @FXML private Button btnPaste;
    @FXML private Button btnUndo;
    @FXML private Button btnBold;
    @FXML private Button btnItalic;
    @FXML private Button btnUnderline;
    @FXML private Button btnAlignLeft;
    @FXML private Button btnAlignCenter;
    @FXML private Button btnAlignRight;
    @FXML private Button btnBulletList;
    @FXML private ComboBox<String> cbTipografi;

    // Tab 2: Insert (Slide, Media, Lampiran)
    @FXML private Button btnPageBreak;
    @FXML private Button btnInsertImage;
    @FXML private Button btnEmbedVideo;
    @FXML private Button btnUploadDoc;

    // ------------------------------------------------------------------ Kolom Kiri: Slide Strip
    @FXML private Label lblJumlahSlideNav;
    @FXML private VBox vbSlideList;

    // ------------------------------------------------------------------ Kolom Tengah: Kanvas Tulis
    @FXML private VBox boxModeEdit;
    @FXML private TextField tfJudulSlide;
    @FXML private VBox vbKanvasBlok;

    // ------------------------------------------------------------------ Kolom Tengah: Mode Pratinjau
    @FXML private VBox boxModePratinjau;
    @FXML private HBox hbPratinjauSegmen;
    @FXML private VBox vbPratinjauKonten;
    @FXML private Label lblPratinjauNomor;
    @FXML private Button btnPrevSlide;
    @FXML private Button btnNextSlide;

    @FXML private Label lblStatSlide;
    @FXML private Label lblStatBlok;

    private final AppSession session = AppSession.getInstance();
    private final KursusService kursusService = session.getKursusService();

    private Kursus kursus;
    private Bab bab;
    private MateriTeks materiLama;

    private final List<DrafSlide> daftarSlide = new ArrayList<>();
    private int slideAktifIndex = 0;
    private boolean sedangMemuatForm = false;

    // Komponen teks yang sedang aktif/fokus di dalam kanvas editor
    private TextInputControl inputFokusAktif;
    private HTMLEditor htmlEditorFokusAktif;
    private DrafBlok blokFokusAktif;
    private IndexRange seleksiTerakhir = new IndexRange(0, 0);

    // Daftar callback untuk memastikan konten HTMLEditor tersinkron sebelum simpan / pindah slide
    private final List<Runnable> daftarSinkronisasi = new ArrayList<>();

    // ------------------------------------------------------------------ History Undo & Redo
    private record DrafSnapshot(List<DrafSlide> slides, int slideIndex) {}
    private final Deque<DrafSnapshot> undoStack = new ArrayDeque<>();
    private final Deque<DrafSnapshot> redoStack = new ArrayDeque<>();

    @FXML
    private void initialize() {
        kursus = session.getKursusAktif();
        bab = session.getBabAktifMateri();
        materiLama = session.getMateriTeksAktif();

        if (kursus == null || bab == null) {
            SceneRouter.pindah(Layar.KURSUS_SAYA);
            return;
        }

        ToggleGroup tgMode = new ToggleGroup();
        tbModeEdit.setToggleGroup(tgMode);
        tbModePratinjau.setToggleGroup(tgMode);
        tbModeEdit.setSelected(true);
        tgMode.selectedToggleProperty().addListener((obs, lama, baru) -> {
            if (baru == null) {
                tgMode.selectToggle(lama);
            }
        });

        lblBreadcrumb.setText(kursus.getNama() + " · " + bab.getJudul());
        if (materiLama != null) {
            tfJudulMateri.setText(materiLama.getJudul());
        }

        // Inisialisasi Opsi Tipografi pada Ribbon Home
        if (cbTipografi != null) {
            cbTipografi.getItems().setAll(
                    "Paragraf (Normal)",
                    "Heading 1 (H1)",
                    "Heading 2 (H2)",
                    "Catatan (Callout)",
                    "Blok Kode",
                    "Kutipan"
            );
            cbTipografi.setValue("Paragraf (Normal)");
        }

        tfJudulSlide.textProperty().addListener((obs, lama, baru) -> {
            if (!sedangMemuatForm && !daftarSlide.isEmpty() && slideAktifIndex < daftarSlide.size()) {
                daftarSlide.get(slideAktifIndex).setJudulSlide(baru);
                renderSlideThumbnails();
            }
        });

        // Pintasan keyboard global: Ctrl+Z (Undo), Ctrl+Y (Redo), Ctrl+B, Ctrl+I, Ctrl+U
        boxModeEdit.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isShortcutDown() || event.isControlDown()) {
                if (event.getCode() == KeyCode.Z) {
                    if (event.isShiftDown()) {
                        handleRedo();
                    } else {
                        handleUndo();
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.Y) {
                    handleRedo();
                    event.consume();
                } else if (event.getCode() == KeyCode.B) {
                    handleBold();
                    event.consume();
                } else if (event.getCode() == KeyCode.I) {
                    handleItalic();
                    event.consume();
                } else if (event.getCode() == KeyCode.U) {
                    handleUnderline();
                    event.consume();
                }
            }
        });

        muatDataAwal();
    }

    private void muatDataAwal() {
        daftarSlide.clear();
        if (materiLama != null) {
            try {
                String markdown = kursusService.bacaTeks(materiLama);
                daftarSlide.addAll(SlideMarkdownConverter.parse(markdown));
            } catch (IOException e) {
                DialogUtil.peringatan("Gagal membaca materi lama: " + e.getMessage());
            }
        }

        if (daftarSlide.isEmpty()) {
            DrafSlide s1 = new DrafSlide("Pengantar Materi");
            s1.tambahBlok(new DrafBlok(JenisBlok.HEADING_1, "Judul Pembelajaran"));
            s1.tambahBlok(new DrafBlok(JenisBlok.PARAGRAF, "Tuliskan materi pembelajaran Anda di sini secara visual."));
            daftarSlide.add(s1);
        }

        slideAktifIndex = 0;
        perbaruiTampilan();
    }

    private void perbaruiTampilan() {
        renderSlideThumbnails();
        renderKanvasSlide();
        perbaruiStatistik();
    }

    // ------------------------------------------------------------------ Manajemen Snapshot Undo / Redo

    private void simpanRiwayatUndo() {
        sinkronkanSemuaEditor();
        List<DrafSlide> salinan = new ArrayList<>();
        for (DrafSlide s : daftarSlide) {
            DrafSlide copyS = new DrafSlide(s.getJudulSlide());
            for (DrafBlok b : s.getDaftarBlok()) {
                copyS.tambahBlok(new DrafBlok(b.getJenis(), b.getIsi(), b.getEkstra()));
            }
            salinan.add(copyS);
        }
        undoStack.push(new DrafSnapshot(salinan, slideAktifIndex));
        if (undoStack.size() > 50) {
            undoStack.removeLast();
        }
        redoStack.clear();
    }

    private void simpanRiwayatRedo() {
        sinkronkanSemuaEditor();
        List<DrafSlide> sekarang = new ArrayList<>();
        for (DrafSlide s : daftarSlide) {
            DrafSlide copyS = new DrafSlide(s.getJudulSlide());
            for (DrafBlok b : s.getDaftarBlok()) {
                copyS.tambahBlok(new DrafBlok(b.getJenis(), b.getIsi(), b.getEkstra()));
            }
            sekarang.add(copyS);
        }
        redoStack.push(new DrafSnapshot(sekarang, slideAktifIndex));
    }

    // ------------------------------------------------------------------ Slide Strip (Kolom Kiri)

    private void renderSlideThumbnails() {
        sinkronkanSemuaEditor();
        vbSlideList.getChildren().clear();
        lblJumlahSlideNav.setText(daftarSlide.size() + " Slide");

        for (int i = 0; i < daftarSlide.size(); i++) {
            final int index = i;
            DrafSlide slide = daftarSlide.get(i);

            VBox kartu = new VBox(6);
            kartu.getStyleClass().add("slide-nav-kartu");
            if (i == slideAktifIndex) {
                kartu.getStyleClass().add("slide-nav-kartu-aktif");
            }

            HBox barisAtas = new HBox(8);
            barisAtas.setAlignment(Pos.CENTER_LEFT);

            Label badgeNomor = new Label(String.valueOf(i + 1));
            badgeNomor.getStyleClass().add("badge");
            badgeNomor.setStyle("-fx-font-weight: bold; -fx-min-width: 22; -fx-alignment: center;");

            String judul = slide.getJudulSlide().isEmpty() ? ("Slide " + (i + 1)) : slide.getJudulSlide();
            Label lblJudul = new Label(judul);
            lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px; -fx-text-fill: -teks;");
            lblJudul.setMaxWidth(130);
            HBox.setHgrow(lblJudul, Priority.ALWAYS);

            barisAtas.getChildren().addAll(badgeNomor, lblJudul);

            HBox barisBawah = new HBox(4);
            barisBawah.setAlignment(Pos.CENTER_LEFT);
            Label lblBlokCount = new Label(slide.getDaftarBlok().size() + " blok");
            lblBlokCount.getStyleClass().add("teks-kecil");

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Button btnUp = new Button("▲");
            btnUp.getStyleClass().add("blok-handle-btn");
            btnUp.setDisable(i == 0);
            btnUp.setFocusTraversable(false);
            btnUp.setOnAction(e -> {
                pindahSlide(index, index - 1);
                e.consume();
            });

            Button btnDown = new Button("▼");
            btnDown.getStyleClass().add("blok-handle-btn");
            btnDown.setDisable(i == daftarSlide.size() - 1);
            btnDown.setFocusTraversable(false);
            btnDown.setOnAction(e -> {
                pindahSlide(index, index + 1);
                e.consume();
            });

            Button btnHapus = new Button("✕");
            btnHapus.getStyleClass().add("blok-handle-btn");
            btnHapus.setStyle("-fx-text-fill: -bahaya;");
            btnHapus.setDisable(daftarSlide.size() <= 1);
            btnHapus.setFocusTraversable(false);
            btnHapus.setOnAction(e -> {
                hapusSlide(index);
                e.consume();
            });

            barisBawah.getChildren().addAll(lblBlokCount, sp, btnUp, btnDown, btnHapus);

            kartu.getChildren().addAll(barisAtas, barisBawah);
            kartu.setOnMouseClicked(e -> {
                sinkronkanSemuaEditor();
                slideAktifIndex = index;
                renderSlideThumbnails();
                renderKanvasSlide();
            });

            vbSlideList.getChildren().add(kartu);
        }
    }

    private void pindahSlide(int dari, int ke) {
        if (ke < 0 || ke >= daftarSlide.size()) return;
        sinkronkanSemuaEditor();
        simpanRiwayatUndo();
        DrafSlide s = daftarSlide.remove(dari);
        daftarSlide.add(ke, s);
        slideAktifIndex = ke;
        perbaruiTampilan();
    }

    private void hapusSlide(int index) {
        if (daftarSlide.size() <= 1) {
            DialogUtil.peringatan("Materi minimal harus memiliki 1 slide.");
            return;
        }
        if (DialogUtil.konfirmasi("Hapus Slide " + (index + 1) + "?")) {
            sinkronkanSemuaEditor();
            simpanRiwayatUndo();
            daftarSlide.remove(index);
            if (slideAktifIndex >= daftarSlide.size()) {
                slideAktifIndex = daftarSlide.size() - 1;
            }
            perbaruiTampilan();
        }
    }

    // ------------------------------------------------------------------ Kanvas Editor Blok (Kolom Tengah)

    private void renderKanvasSlide() {
        if (daftarSlide.isEmpty()) return;
        if (slideAktifIndex >= daftarSlide.size()) slideAktifIndex = 0;

        sinkronkanSemuaEditor();
        daftarSinkronisasi.clear();

        sedangMemuatForm = true;
        DrafSlide slide = daftarSlide.get(slideAktifIndex);
        tfJudulSlide.setText(slide.getJudulSlide());
        sedangMemuatForm = false;

        vbKanvasBlok.getChildren().clear();

        for (int i = 0; i < slide.getDaftarBlok().size(); i++) {
            DrafBlok blok = slide.getDaftarBlok().get(i);
            Node nodeBlok = buatNodeBlok(slide, blok, i);
            vbKanvasBlok.getChildren().add(nodeBlok);
        }
    }

    private Node buatNodeBlok(DrafSlide slide, DrafBlok blok, int index) {
        VBox kartu = new VBox(8);
        kartu.getStyleClass().add("blok-kartu");

        // Baris Header Blok: Jenis & Tombol Kontrol
        HBox barisHeader = new HBox(8);
        barisHeader.setAlignment(Pos.CENTER_LEFT);

        Label badgeTipe = new Label(blok.getJenis().getLabel());
        badgeTipe.getStyleClass().add("badge");
        badgeTipe.setStyle("-fx-font-size: 11px; -fx-padding: 2 8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNaik = new Button("▲");
        btnNaik.getStyleClass().add("blok-handle-btn");
        btnNaik.setDisable(index == 0);
        btnNaik.setFocusTraversable(false);
        btnNaik.setTooltip(new Tooltip("Pindah ke atas"));
        btnNaik.setOnAction(e -> {
            sinkronkanSemuaEditor();
            simpanRiwayatUndo();
            slide.getDaftarBlok().remove(index);
            slide.getDaftarBlok().add(index - 1, blok);
            renderKanvasSlide();
        });

        Button btnTurun = new Button("▼");
        btnTurun.getStyleClass().add("blok-handle-btn");
        btnTurun.setDisable(index == slide.getDaftarBlok().size() - 1);
        btnTurun.setFocusTraversable(false);
        btnTurun.setTooltip(new Tooltip("Pindah ke bawah"));
        btnTurun.setOnAction(e -> {
            sinkronkanSemuaEditor();
            simpanRiwayatUndo();
            slide.getDaftarBlok().remove(index);
            slide.getDaftarBlok().add(index + 1, blok);
            renderKanvasSlide();
        });

        Button btnHapus = new Button("✕");
        btnHapus.getStyleClass().add("blok-handle-btn");
        btnHapus.setStyle("-fx-text-fill: -bahaya;");
        btnHapus.setFocusTraversable(false);
        btnHapus.setTooltip(new Tooltip("Hapus blok ini"));
        btnHapus.setOnAction(e -> {
            sinkronkanSemuaEditor();
            simpanRiwayatUndo();
            slide.getDaftarBlok().remove(index);
            renderKanvasSlide();
            perbaruiStatistik();
        });

        barisHeader.getChildren().addAll(badgeTipe, spacer, btnNaik, btnTurun, btnHapus);

        // Body Editor Berdasarkan Jenis Blok
        Node editorNode = buatEditorBlok(blok);

        kartu.getChildren().addAll(barisHeader, editorNode);
        return kartu;
    }

    private Node buatEditorBlok(DrafBlok blok) {
        switch (blok.getJenis()) {
            case HEADING_1 -> {
                TextField tf = new TextField(blok.getIsi());
                tf.setPromptText("Tuliskan Judul Utama (Heading 1)...");
                Pos alignPos = "center".equalsIgnoreCase(blok.getEkstra()) ? Pos.CENTER
                        : "right".equalsIgnoreCase(blok.getEkstra()) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT;
                tf.setAlignment(alignPos);
                tf.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 8 10;");
                tf.textProperty().addListener((obs, lama, baru) -> blok.setIsi(baru));
                pasangListenerFokus(tf, blok);
                return tf;
            }
            case HEADING_2 -> {
                TextField tf = new TextField(blok.getIsi());
                tf.setPromptText("Tuliskan Subjudul Bagian (Heading 2)...");
                Pos alignPos = "center".equalsIgnoreCase(blok.getEkstra()) ? Pos.CENTER
                        : "right".equalsIgnoreCase(blok.getEkstra()) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT;
                tf.setAlignment(alignPos);
                tf.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-padding: 6 10;");
                tf.textProperty().addListener((obs, lama, baru) -> blok.setIsi(baru));
                pasangListenerFokus(tf, blok);
                return tf;
            }
            case CALLOUT -> {
                VBox box = new VBox(6);
                box.setStyle("-fx-background-color: rgba(26, 163, 232, 0.08); -fx-border-color: -aksen; "
                        + "-fx-border-width: 0 0 0 4; -fx-background-radius: 0 8 8 0; -fx-padding: 10 12;");

                HBox barisOpsi = new HBox(8);
                barisOpsi.setAlignment(Pos.CENTER_LEFT);
                Label lblTipe = new Label("Tipe Catatan:");
                lblTipe.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");

                ComboBox<String> cbTipe = new ComboBox<>(FXCollections.observableArrayList("NOTE", "TIPS", "IMPORTANT", "WARNING"));
                cbTipe.setValue(blok.getEkstra().isEmpty() ? "NOTE" : blok.getEkstra().toUpperCase());
                cbTipe.valueProperty().addListener((obs, lama, baru) -> blok.setEkstra(baru));
                barisOpsi.getChildren().addAll(lblTipe, cbTipe);

                HTMLEditor editor = buatHtmlEditor(blok, 110);
                box.getChildren().addAll(barisOpsi, editor);
                return box;
            }
            case GAMBAR -> {
                VBox box = new VBox(8);
                HBox barisPilih = new HBox(8);
                barisPilih.setAlignment(Pos.CENTER_LEFT);

                TextField tfPath = new TextField(blok.getIsi());
                tfPath.setPromptText("Path gambar lokal atau URL (contoh: media/diagram.png)");
                HBox.setHgrow(tfPath, Priority.ALWAYS);
                tfPath.textProperty().addListener((obs, lama, baru) -> blok.setIsi(baru));
                pasangListenerFokus(tfPath, blok);

                Button btnBrowse = new Button("Pilih File...");
                btnBrowse.getStyleClass().add("tombol-sekunder");
                btnBrowse.setOnAction(e -> {
                    FileChooser fc = new FileChooser();
                    fc.setTitle("Pilih Gambar");
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("File Gambar", "*.png", "*.jpg", "*.jpeg", "*.gif"));
                    File f = fc.showOpenDialog(btnBrowse.getScene().getWindow());
                    if (f != null) {
                        tfPath.setText(f.getAbsolutePath());
                    }
                });
                barisPilih.getChildren().addAll(tfPath, btnBrowse);

                TextField tfCaption = new TextField(blok.getEkstra());
                tfCaption.setPromptText("Keterangan gambar (caption)...");
                tfCaption.textProperty().addListener((obs, lama, baru) -> blok.setEkstra(baru));

                box.getChildren().addAll(barisPilih, tfCaption);
                return box;
            }
            case VIDEO -> {
                VBox box = new VBox(8);
                HBox barisUrl = new HBox(8);
                barisUrl.setAlignment(Pos.CENTER_LEFT);

                TextField tfUrl = new TextField(blok.getIsi());
                tfUrl.setPromptText("Tautan Video YouTube (contoh: https://www.youtube.com/watch?v=...)");
                HBox.setHgrow(tfUrl, Priority.ALWAYS);
                tfUrl.textProperty().addListener((obs, lama, baru) -> blok.setIsi(baru));
                pasangListenerFokus(tfUrl, blok);
                barisUrl.getChildren().add(tfUrl);

                TextField tfJudulVid = new TextField(blok.getEkstra());
                tfJudulVid.setPromptText("Judul atau instruksi video...");
                tfJudulVid.textProperty().addListener((obs, lama, baru) -> blok.setEkstra(baru));

                box.getChildren().addAll(barisUrl, tfJudulVid);
                return box;
            }
            case KUTIPAN -> {
                VBox box = new VBox(4);
                box.setStyle("-fx-background-color: rgba(26, 163, 232, 0.06); -fx-border-color: -aksen; "
                        + "-fx-border-width: 0 0 0 3; -fx-padding: 8 12; -fx-background-radius: 0 6 6 0;");
                HTMLEditor editor = buatHtmlEditor(blok, 100);
                box.getChildren().add(editor);
                return box;
            }
            case KODE -> {
                VBox box = new VBox(6);
                HBox barisBahasa = new HBox(8);
                barisBahasa.setAlignment(Pos.CENTER_LEFT);
                Label lblBahasa = new Label("Bahasa Pemrograman:");
                lblBahasa.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");
                TextField tfBahasa = new TextField(blok.getEkstra().isEmpty() ? "java" : blok.getEkstra());
                tfBahasa.setPrefWidth(120);
                tfBahasa.textProperty().addListener((obs, lama, baru) -> blok.setEkstra(baru));
                barisBahasa.getChildren().addAll(lblBahasa, tfBahasa);

                TextArea ta = new TextArea(blok.getIsi());
                ta.setPromptText("// Tuliskan kode program di sini...");
                ta.setPrefRowCount(5);
                ta.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 13px;");
                ta.textProperty().addListener((obs, lama, baru) -> blok.setIsi(baru));
                pasangListenerFokus(ta, blok);

                box.getChildren().addAll(barisBahasa, ta);
                return box;
            }
            case PARAGRAF -> {
                return buatHtmlEditor(blok, 150);
            }
        }
        return new Label();
    }

    /**
     * Membuat instansi HTMLEditor untuk blok teks (Paragraf/Callout/Kutipan),
     * menyembunyikan toolbar bawaan agar hanya Simplified Ribbon UI yang mengontrol pemformatan.
     */
    private HTMLEditor buatHtmlEditor(DrafBlok blok, double prefHeight) {
        HTMLEditor editor = new HTMLEditor();
        editor.setPrefHeight(prefHeight);
        editor.setMinHeight(80);
        editor.setMaxHeight(600);

        String htmlAwal = HtmlMarkdownUtil.markdownToHtml(blok.getIsi());
        editor.setHtmlText(htmlAwal);

        sembunyikanToolbarHtml(editor);

        daftarSinkronisasi.add(() -> sinkronkanBlokHtml(editor, blok));

        editor.focusedProperty().addListener((obs, lama, isFokus) -> {
            if (isFokus) {
                this.htmlEditorFokusAktif = editor;
                this.inputFokusAktif = null;
                this.blokFokusAktif = blok;
                if (cbTipografi != null && !sedangMemuatForm) {
                    sedangMemuatForm = true;
                    cbTipografi.setValue(namaTipografi(blok.getJenis()));
                    sedangMemuatForm = false;
                }
            } else {
                sinkronkanBlokHtml(editor, blok);
            }
        });

        Platform.runLater(() -> {
            sembunyikanToolbarHtml(editor);
            WebView wv = temukanWebView(editor);
            if (wv != null) {
                wv.focusedProperty().addListener((obs, lama, isFokus) -> {
                    if (isFokus) {
                        this.htmlEditorFokusAktif = editor;
                        this.inputFokusAktif = null;
                        this.blokFokusAktif = blok;
                        if (cbTipografi != null && !sedangMemuatForm) {
                            sedangMemuatForm = true;
                            cbTipografi.setValue(namaTipografi(blok.getJenis()));
                            sedangMemuatForm = false;
                        }
                    } else {
                        sinkronkanBlokHtml(editor, blok);
                    }
                });
                wv.setOnMouseClicked(e -> {
                    this.htmlEditorFokusAktif = editor;
                    this.inputFokusAktif = null;
                    this.blokFokusAktif = blok;
                    if (cbTipografi != null && !sedangMemuatForm) {
                        sedangMemuatForm = true;
                        cbTipografi.setValue(namaTipografi(blok.getJenis()));
                        sedangMemuatForm = false;
                    }
                });
                wv.setOnKeyReleased(e -> sinkronkanBlokHtml(editor, blok));
            }
        });

        return editor;
    }

    private void sembunyikanToolbarHtml(HTMLEditor editor) {
        if (editor == null) return;
        editor.lookupAll(".top-toolbar").forEach(n -> {
            n.setVisible(false);
            n.setManaged(false);
        });
        editor.lookupAll(".bottom-toolbar").forEach(n -> {
            n.setVisible(false);
            n.setManaged(false);
        });
    }

    private void sinkronkanBlokHtml(HTMLEditor editor, DrafBlok blok) {
        if (editor != null && blok != null) {
            String html = editor.getHtmlText();
            String md = HtmlMarkdownUtil.htmlToMarkdown(html);
            blok.setIsi(md);
            perbaruiStatistik();
        }
    }

    private void sinkronkanSemuaEditor() {
        for (Runnable r : daftarSinkronisasi) {
            try {
                r.run();
            } catch (Exception ignored) {
            }
        }
    }

    private void eksekusiPerintahHtml(HTMLEditor editor, String perintah, String nilai) {
        if (editor == null) return;
        WebView webView = temukanWebView(editor);
        if (webView != null) {
            String script = (nilai == null)
                    ? String.format("document.execCommand('%s', false, null);", perintah)
                    : String.format("document.execCommand('%s', false, '%s');", perintah, nilai);
            try {
                webView.getEngine().executeScript(script);
            } catch (Exception ignored) {
            }
        }
    }

    private WebView temukanWebView(HTMLEditor editor) {
        if (editor == null) return null;
        Node node = editor.lookup("WebView");
        if (node instanceof WebView wv) {
            return wv;
        }
        return cariWebViewRekursif(editor);
    }

    private WebView cariWebViewRekursif(Parent parent) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof WebView wv) {
                return wv;
            }
            if (child instanceof Parent p) {
                WebView hasil = cariWebViewRekursif(p);
                if (hasil != null) return hasil;
            }
        }
        return null;
    }

    private void pasangListenerFokus(TextInputControl input, DrafBlok blok) {
        input.focusedProperty().addListener((obs, lama, isFokus) -> {
            if (isFokus) {
                this.inputFokusAktif = input;
                this.htmlEditorFokusAktif = null;
                this.blokFokusAktif = blok;
                if (cbTipografi != null && !sedangMemuatForm) {
                    sedangMemuatForm = true;
                    cbTipografi.setValue(namaTipografi(blok.getJenis()));
                    sedangMemuatForm = false;
                }
            }
        });
        input.selectionProperty().addListener((obs, lama, baru) -> {
            if (baru != null && baru.getLength() > 0) {
                this.seleksiTerakhir = baru;
                this.inputFokusAktif = input;
                this.htmlEditorFokusAktif = null;
                this.blokFokusAktif = blok;
            }
        });
    }

    private String namaTipografi(JenisBlok jenis) {
        if (jenis == null) return "Paragraf (Normal)";
        return switch (jenis) {
            case HEADING_1 -> "Heading 1 (H1)";
            case HEADING_2 -> "Heading 2 (H2)";
            case CALLOUT -> "Catatan (Callout)";
            case KODE -> "Blok Kode";
            case KUTIPAN -> "Kutipan";
            default -> "Paragraf (Normal)";
        };
    }

    private void formatTeksTerpilih(String prefix, String suffix) {
        if (inputFokusAktif != null) {
            String fullText = inputFokusAktif.getText();
            if (fullText == null) fullText = "";

            IndexRange sel = inputFokusAktif.getSelection();
            int start = sel != null ? Math.min(sel.getStart(), sel.getEnd()) : 0;
            int end = sel != null ? Math.max(sel.getStart(), sel.getEnd()) : 0;

            if (start == end && seleksiTerakhir != null && seleksiTerakhir.getLength() > 0) {
                start = Math.min(seleksiTerakhir.getStart(), seleksiTerakhir.getEnd());
                end = Math.max(seleksiTerakhir.getStart(), seleksiTerakhir.getEnd());
            }

            simpanRiwayatUndo();

            if (start < end && end <= fullText.length()) {
                String terpilih = fullText.substring(start, end);

                if (terpilih.startsWith(prefix) && terpilih.endsWith(suffix) && terpilih.length() >= prefix.length() + suffix.length()) {
                    String unformatted = terpilih.substring(prefix.length(), terpilih.length() - suffix.length());
                    inputFokusAktif.replaceText(start, end, unformatted);
                    inputFokusAktif.selectRange(start, start + unformatted.length());
                    seleksiTerakhir = new IndexRange(start, start + unformatted.length());
                } else {
                    String formatted = prefix + terpilih + suffix;
                    inputFokusAktif.replaceText(start, end, formatted);
                    inputFokusAktif.selectRange(start, start + formatted.length());
                    seleksiTerakhir = new IndexRange(start, start + formatted.length());
                }
            } else {
                int caret = inputFokusAktif.getCaretPosition();
                inputFokusAktif.insertText(caret, prefix + suffix);
                inputFokusAktif.positionCaret(caret + prefix.length());
            }
            inputFokusAktif.requestFocus();
        } else if (blokFokusAktif != null) {
            simpanRiwayatUndo();
            String isi = blokFokusAktif.getIsi();
            blokFokusAktif.setIsi(prefix + isi + suffix);
            renderKanvasSlide();
        }
    }

    private void sisipBlok(DrafBlok blok) {
        if (daftarSlide.isEmpty()) return;
        sinkronkanSemuaEditor();
        simpanRiwayatUndo();
        DrafSlide slide = daftarSlide.get(slideAktifIndex);
        slide.tambahBlok(blok);
        renderKanvasSlide();
        perbaruiStatistik();
    }

    // ==================================================================
    // RIBBON TAB 1: HOME (FOKUS PADA PEMFORMATAN)
    // ==================================================================

    @FXML
    private void handleCopy() {
        if (htmlEditorFokusAktif != null) {
            eksekusiPerintahHtml(htmlEditorFokusAktif, "copy", null);
        } else if (inputFokusAktif != null && !inputFokusAktif.getSelectedText().isEmpty()) {
            ClipboardContent content = new ClipboardContent();
            content.putString(inputFokusAktif.getSelectedText());
            Clipboard.getSystemClipboard().setContent(content);
        } else if (blokFokusAktif != null && blokFokusAktif.getIsi() != null) {
            ClipboardContent content = new ClipboardContent();
            content.putString(blokFokusAktif.getIsi());
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    @FXML
    private void handlePaste() {
        if (htmlEditorFokusAktif != null) {
            eksekusiPerintahHtml(htmlEditorFokusAktif, "paste", null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
            return;
        }

        String teksClipboard = Clipboard.getSystemClipboard().getString();
        if (StringUtil.isKosong(teksClipboard)) return;

        simpanRiwayatUndo();
        if (inputFokusAktif != null) {
            inputFokusAktif.replaceSelection(teksClipboard);
        } else {
            sisipBlok(new DrafBlok(JenisBlok.PARAGRAF, teksClipboard));
        }
    }

    @FXML
    private void handleUndo() {
        if (htmlEditorFokusAktif != null) {
            eksekusiPerintahHtml(htmlEditorFokusAktif, "undo", null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
            return;
        }

        if (!undoStack.isEmpty()) {
            simpanRiwayatRedo();
            DrafSnapshot snapshot = undoStack.pop();
            daftarSlide.clear();
            daftarSlide.addAll(snapshot.slides());
            slideAktifIndex = Math.min(snapshot.slideIndex(), daftarSlide.size() - 1);
            perbaruiTampilan();
            return;
        }

        if (inputFokusAktif != null && inputFokusAktif.isUndoable()) {
            inputFokusAktif.undo();
        }
    }

    private void handleRedo() {
        if (htmlEditorFokusAktif != null) {
            eksekusiPerintahHtml(htmlEditorFokusAktif, "redo", null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
            return;
        }

        if (!redoStack.isEmpty()) {
            List<DrafSlide> sekarang = new ArrayList<>();
            for (DrafSlide s : daftarSlide) {
                DrafSlide copyS = new DrafSlide(s.getJudulSlide());
                for (DrafBlok b : s.getDaftarBlok()) {
                    copyS.tambahBlok(new DrafBlok(b.getJenis(), b.getIsi(), b.getEkstra()));
                }
                sekarang.add(copyS);
            }
            undoStack.push(new DrafSnapshot(sekarang, slideAktifIndex));

            DrafSnapshot snapshot = redoStack.pop();
            daftarSlide.clear();
            daftarSlide.addAll(snapshot.slides());
            slideAktifIndex = Math.min(snapshot.slideIndex(), daftarSlide.size() - 1);
            perbaruiTampilan();
            return;
        }

        if (inputFokusAktif != null && inputFokusAktif.isRedoable()) {
            inputFokusAktif.redo();
        }
    }

    @FXML
    private void handleBold() {
        if (htmlEditorFokusAktif != null) {
            simpanRiwayatUndo();
            eksekusiPerintahHtml(htmlEditorFokusAktif, "bold", null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
        } else if (inputFokusAktif != null) {
            formatTeksTerpilih("**", "**");
        }
    }

    @FXML
    private void handleItalic() {
        if (htmlEditorFokusAktif != null) {
            simpanRiwayatUndo();
            eksekusiPerintahHtml(htmlEditorFokusAktif, "italic", null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
        } else if (inputFokusAktif != null) {
            formatTeksTerpilih("*", "*");
        }
    }

    @FXML
    private void handleUnderline() {
        if (htmlEditorFokusAktif != null) {
            simpanRiwayatUndo();
            eksekusiPerintahHtml(htmlEditorFokusAktif, "underline", null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
        } else if (inputFokusAktif != null) {
            formatTeksTerpilih("<u>", "</u>");
        }
    }

    @FXML
    private void handleAlignLeft() {
        aturPerataanTeks("left");
    }

    @FXML
    private void handleAlignCenter() {
        aturPerataanTeks("center");
    }

    @FXML
    private void handleAlignRight() {
        aturPerataanTeks("right");
    }

    private void aturPerataanTeks(String alignment) {
        simpanRiwayatUndo();

        if (htmlEditorFokusAktif != null) {
            String cmd = switch (alignment) {
                case "center" -> "justifyCenter";
                case "right" -> "justifyRight";
                default -> "justifyLeft";
            };
            eksekusiPerintahHtml(htmlEditorFokusAktif, cmd, null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
            return;
        }

        if (inputFokusAktif instanceof TextField tf) {
            Pos pos = switch (alignment) {
                case "center" -> Pos.CENTER;
                case "right" -> Pos.CENTER_RIGHT;
                default -> Pos.CENTER_LEFT;
            };
            tf.setAlignment(pos);
            if (blokFokusAktif != null) {
                blokFokusAktif.setEkstra(alignment);
            }
            return;
        }

        if (blokFokusAktif != null && inputFokusAktif instanceof TextArea ta) {
            terapkanGayaPerataanNode(ta, alignment);
            if (blokFokusAktif != null) {
                blokFokusAktif.setEkstra(alignment);
            }
        }
    }

    private void terapkanGayaPerataanNode(TextInputControl input, String alignment) {
        if (input instanceof TextArea ta) {
            String alignStyle = switch (alignment) {
                case "center" -> "-fx-text-alignment: center;";
                case "right" -> "-fx-text-alignment: right;";
                default -> "-fx-text-alignment: left;";
            };
            ta.setStyle("-fx-font-size: 13.5px; " + alignStyle);
            Node t = ta.lookup(".text");
            if (t instanceof Text text) {
                text.setTextAlignment(switch (alignment) {
                    case "center" -> TextAlignment.CENTER;
                    case "right" -> TextAlignment.RIGHT;
                    default -> TextAlignment.LEFT;
                });
            }
        } else if (input instanceof TextField tf) {
            tf.setAlignment(switch (alignment) {
                case "center" -> Pos.CENTER;
                case "right" -> Pos.CENTER_RIGHT;
                default -> Pos.CENTER_LEFT;
            });
        }
    }

    @FXML
    private void handleBulletList() {
        if (htmlEditorFokusAktif != null) {
            simpanRiwayatUndo();
            eksekusiPerintahHtml(htmlEditorFokusAktif, "insertUnorderedList", null);
            sinkronkanBlokHtml(htmlEditorFokusAktif, blokFokusAktif);
            return;
        }

        simpanRiwayatUndo();
        if (inputFokusAktif != null) {
            String terpilih = inputFokusAktif.getSelectedText();
            if (terpilih.isEmpty()) {
                inputFokusAktif.insertText(inputFokusAktif.getCaretPosition(), "\n- ");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String baris : terpilih.split("\n")) {
                    sb.append("- ").append(baris).append("\n");
                }
                inputFokusAktif.replaceSelection(sb.toString().stripTrailing());
            }
            inputFokusAktif.requestFocus();
        } else {
            sisipBlok(new DrafBlok(JenisBlok.PARAGRAF, "- Poin pertama\n- Poin kedua"));
        }
    }

    @FXML
    private void handleTipografiChanged() {
        if (sedangMemuatForm || cbTipografi == null) return;
        String pilihan = cbTipografi.getValue();
        if (pilihan == null) return;

        JenisBlok jenisTarget = switch (pilihan) {
            case "Heading 1 (H1)" -> JenisBlok.HEADING_1;
            case "Heading 2 (H2)" -> JenisBlok.HEADING_2;
            case "Catatan (Callout)" -> JenisBlok.CALLOUT;
            case "Blok Kode" -> JenisBlok.KODE;
            case "Kutipan" -> JenisBlok.KUTIPAN;
            default -> JenisBlok.PARAGRAF;
        };

        sinkronkanSemuaEditor();

        if (blokFokusAktif != null && blokFokusAktif.getJenis() != jenisTarget) {
            simpanRiwayatUndo();
            blokFokusAktif.setJenis(jenisTarget);
            renderKanvasSlide();
        } else if (blokFokusAktif == null) {
            simpanRiwayatUndo();
            sisipBlok(new DrafBlok(jenisTarget, ""));
        }
    }

    // ==================================================================
    // RIBBON TAB 2: INSERT (FOKUS PADA PENAMBAHAN ELEMEN)
    // ==================================================================

    @FXML
    private void handlePageBreak() {
        handleTambahSlideBaru();
    }

    @FXML
    private void handleInsertImage() {
        sisipBlok(new DrafBlok(JenisBlok.GAMBAR, "", "Keterangan gambar"));
    }

    @FXML
    private void handleEmbedVideo() {
        sisipBlok(new DrafBlok(JenisBlok.VIDEO, "", "Video Pembelajaran"));
    }

    @FXML
    private void handleUploadDoc() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Unggah Dokumen Materi Pembelajaran (.pdf / .docx / .md / .txt)");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Semua Dokumen Didukung (*.pdf, *.docx, *.md, *.txt)", "*.pdf", "*.docx", "*.md", "*.txt"),
                new FileChooser.ExtensionFilter("Dokumen PDF (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("Dokumen Word (*.docx)", "*.docx"),
                new FileChooser.ExtensionFilter("Dokumen Teks / Markdown (*.txt, *.md)", "*.txt", "*.md"),
                new FileChooser.ExtensionFilter("Semua Berkas (*.*)", "*.*")
        );
        File file = fc.showOpenDialog(tfJudulMateri.getScene().getWindow());
        if (file != null) {
            String nama = file.getName().toLowerCase();
            if (nama.endsWith(".txt") || nama.endsWith(".md")) {
                try {
                    String konten = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    List<DrafSlide> hasilParse = SlideMarkdownConverter.parse(konten);
                    sinkronkanSemuaEditor();
                    simpanRiwayatUndo();
                    daftarSlide.clear();
                    daftarSlide.addAll(hasilParse);
                    slideAktifIndex = 0;
                    perbaruiTampilan();
                    DialogUtil.info("Berhasil mengimpor " + hasilParse.size() + " slide dari berkas: " + file.getName());
                } catch (IOException e) {
                    DialogUtil.peringatan("Gagal membaca berkas: " + e.getMessage());
                }
            } else {
                sisipBlok(new DrafBlok(JenisBlok.CALLOUT, "Lampiran berkas: " + file.getName() + " (" + file.getAbsolutePath() + ")", "INFO"));
                DialogUtil.info("Lampiran dokumen '" + file.getName() + "' berhasil ditambahkan ke kanvas slide.");
            }
        }
    }

    // ------------------------------------------------------------------ Pratinjau Interaktif Siswa

    @FXML
    private void handleModeEdit() {
        tbModeEdit.setSelected(true);
        boxModeEdit.setVisible(true);
        boxModeEdit.setManaged(true);
        boxModePratinjau.setVisible(false);
        boxModePratinjau.setManaged(false);
    }

    @FXML
    private void handleModePratinjau() {
        sinkronkanSemuaEditor();
        tbModePratinjau.setSelected(true);
        boxModeEdit.setVisible(false);
        boxModeEdit.setManaged(false);
        boxModePratinjau.setVisible(true);
        boxModePratinjau.setManaged(true);
        renderPratinjauSiswa();
    }

    private void renderPratinjauSiswa() {
        if (daftarSlide.isEmpty()) return;

        hbPratinjauSegmen.getChildren().clear();
        for (int i = 0; i < daftarSlide.size(); i++) {
            Region segmen = new Region();
            segmen.setStyle("-fx-background-radius: 4; -fx-min-height: 4px; -fx-max-height: 4px; "
                    + "-fx-background-color: " + (i <= slideAktifIndex ? "-aksen" : "-garis") + ";");
            HBox.setHgrow(segmen, Priority.ALWAYS);
            hbPratinjauSegmen.getChildren().add(segmen);
        }

        lblPratinjauNomor.setText("Halaman " + (slideAktifIndex + 1) + " dari " + daftarSlide.size());
        btnPrevSlide.setDisable(slideAktifIndex == 0);
        btnNextSlide.setText(slideAktifIndex == daftarSlide.size() - 1 ? "Selesai & Paham" : "Halaman Berikutnya");

        DrafSlide slide = daftarSlide.get(slideAktifIndex);
        String md = SlideMarkdownConverter.toMarkdown(List.of(slide));

        vbPratinjauKonten.getChildren().clear();
        Label lblSubjudul = new Label((slide.getJudulSlide().isEmpty() ? ("Bagian " + (slideAktifIndex + 1)) : slide.getJudulSlide()).toUpperCase());
        lblSubjudul.getStyleClass().add("label-kecil-kapital");
        vbPratinjauKonten.getChildren().addAll(lblSubjudul, MarkdownView.render(md));
    }

    @FXML
    private void handlePrevPratinjau() {
        if (slideAktifIndex > 0) {
            slideAktifIndex--;
            renderPratinjauSiswa();
        }
    }

    @FXML
    private void handleNextPratinjau() {
        if (slideAktifIndex < daftarSlide.size() - 1) {
            slideAktifIndex++;
            renderPratinjauSiswa();
        } else {
            DialogUtil.info("Ini adalah halaman slide terakhir. Simulasi selesai!");
        }
    }

    // ------------------------------------------------------------------ Aksi Slide & Berkas

    @FXML
    private void handleTambahSlideBaru() {
        sinkronkanSemuaEditor();
        simpanRiwayatUndo();
        DrafSlide slideBaru = new DrafSlide("Slide " + (daftarSlide.size() + 1));
        slideBaru.tambahBlok(new DrafBlok(JenisBlok.HEADING_2, "Bagian " + (daftarSlide.size() + 1)));
        slideBaru.tambahBlok(new DrafBlok(JenisBlok.PARAGRAF, "Tulis penjelasan untuk slide ini..."));
        daftarSlide.add(slideBaru);
        slideAktifIndex = daftarSlide.size() - 1;
        perbaruiTampilan();
    }

    private void perbaruiStatistik() {
        if (lblStatSlide != null) {
            lblStatSlide.setText(daftarSlide.size() + " Slide (Halaman)");
        }
        if (lblStatBlok != null) {
            int totalBlok = daftarSlide.stream().mapToInt(s -> s.getDaftarBlok().size()).sum();
            lblStatBlok.setText(totalBlok + " Blok Konten");
        }
    }

    // ------------------------------------------------------------------ Simpan & Navigasi

    @FXML
    private void handleSimpanMateri() {
        sinkronkanSemuaEditor();

        String judul = tfJudulMateri.getText().trim();
        if (StringUtil.isKosong(judul)) {
            DialogUtil.peringatan("Judul materi pembelajaran tidak boleh kosong!");
            return;
        }

        String markdown = SlideMarkdownConverter.toMarkdown(daftarSlide);

        try {
            if (materiLama != null) {
                kursusService.simpanTeksMateri(materiLama, markdown);
                MateriTeks mtUpdate = new MateriTeks(materiLama.getIdMateri(), judul, materiLama.getFile());
                List<Bab> daftarBab = new ArrayList<>();
                for (Bab b : kursus.getDaftarBab()) {
                    if (b.getNomor() == bab.getNomor()) {
                        List<Materi> daftarMateri = new ArrayList<>();
                        for (Materi m : b.getDaftarMateri()) {
                            if (m.getIdMateri().equals(materiLama.getIdMateri())) {
                                daftarMateri.add(mtUpdate);
                            } else {
                                daftarMateri.add(m);
                            }
                        }
                        daftarBab.add(new Bab(b.getNomor(), b.getJudul(), daftarMateri));
                    } else {
                        daftarBab.add(b);
                    }
                }
                Kursus kursusBaru = kursus.denganDaftarBab(daftarBab);
                kursusService.perbaruiKursus(kursusBaru);
                session.setKursusAktif(kursusBaru);
                DialogUtil.info("Materi '" + judul + "' berhasil diperbarui!");
            } else {
                MateriTeks mtBaru = kursusService.buatMateriTeksBaru(kursus, judul, markdown);
                List<Bab> daftarBab = new ArrayList<>();
                for (Bab b : kursus.getDaftarBab()) {
                    if (b.getNomor() == bab.getNomor()) {
                        List<Materi> daftarMateri = new ArrayList<>(b.getDaftarMateri());
                        daftarMateri.add(mtBaru);
                        daftarBab.add(new Bab(b.getNomor(), b.getJudul(), daftarMateri));
                    } else {
                        daftarBab.add(b);
                    }
                }
                Kursus kursusBaru = kursus.denganDaftarBab(daftarBab);
                kursusService.perbaruiKursus(kursusBaru);
                session.setKursusAktif(kursusBaru);
                DialogUtil.info("Materi baru '" + judul + "' berhasil disimpan ke " + bab.getJudul() + "!");
            }
            session.setBabAktifMateri(null);
            session.setMateriTeksAktif(null);
            SceneRouter.pindah(Layar.KURSUS_DETAIL);
        } catch (IOException e) {
            DialogUtil.peringatan("Gagal menyimpan materi: " + e.getMessage());
        }
    }

    @FXML
    private void handleKembali() {
        if (DialogUtil.konfirmasi("Kembali ke halaman kursus? Pastikan perubahan Anda sudah disimpan.")) {
            SceneRouter.pindah(Layar.KURSUS_DETAIL);
        }
    }
}
