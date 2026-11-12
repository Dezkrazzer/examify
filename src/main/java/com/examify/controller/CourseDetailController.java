package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.view.DateTimePicker;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.io.SlideMarkdownConverter;
import com.examify.model.Chapter;
import com.examify.model.Teacher;
import com.examify.model.ExamResult;
import com.examify.model.Course;
import com.examify.model.Material;
import com.examify.model.PdfMaterial;
import com.examify.model.QuizMaterial;
import com.examify.model.AssignmentMaterial;
import com.examify.model.AssignmentSubmission;
import javafx.stage.FileChooser;
import javafx.scene.control.CheckBox;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import com.examify.model.LinkMaterial;
import com.examify.model.TextMaterial;
import com.examify.model.ExamMaterial;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.Question;
import com.examify.model.MultipleChoiceQuestion;
import com.examify.model.Exam;
import com.examify.model.material.SlideDraft;
import com.examify.service.CourseService;
import com.examify.service.LearningProgressService;
import com.examify.utils.FormatUtil;
import com.examify.utils.StringUtil;
import com.examify.view.AnimationUtil;
import com.examify.view.Icon;
import com.examify.view.IconView;
import com.examify.view.MarkdownView;
import com.examify.view.CoursePattern;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Separator;
import java.security.SecureRandom;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Halaman satu kursus: daftar bab di kiri, isi bab / bacaan / info ujian di
 * kanan.
 */
public class CourseDetailController {

    @FXML
    private StackPane spPola;
    @FXML
    private Label lblKode;
    @FXML
    private Button btnUbahKode;
    @FXML
    private Label lblNama;
    @FXML
    private Label lblPengajar;
    @FXML
    private Label lblDeskripsi;
    @FXML
    private Label lblJudulProgres;
    @FXML
    private Label lblPersen;
    @FXML
    private Label lblRingkasProgres;
    @FXML
    private ProgressBar pbProgres;
    @FXML
    private VBox vbBab;
    @FXML
    private StackPane spKonten;
    @FXML
    private ToggleButton tabMateri;
    @FXML
    private ToggleButton tabOrang;
    @FXML
    private HBox boxTabMateri;
    @FXML
    private ScrollPane spTabOrang;
    @FXML
    private VBox vbTabOrang;
    private final AppSession session = AppSession.getInstance();
    private final CourseService kursusService = session.getKursusService();
    private final LearningProgressService progresService = session.getProgresService();
    private final Map<Chapter, ToggleButton> tombolBab = new LinkedHashMap<>();
    private final ToggleGroup grupBab = new ToggleGroup();
    private final ToggleGroup grupTab = new ToggleGroup();
    private Course kursus;
    private Student siswa;
    @FXML
    private void initialize() {
        kursus = session.getKursusAktif();
        siswa = session.getSiswaAktif();
        if (kursus == null) {
            SceneRouter.pindah(AppScreen.KURSUS_SAYA);
            return;
        }
        if (session.isGuru()) {
            Teacher guru = session.getGuruAktif();
            if (!session.getKursusService().isKursusMilikGuru(guru, kursus)) {
                DialogUtil.peringatan("Anda hanya dapat mengakses kursus yang Anda buat sendiri.");
                SceneRouter.pindah(AppScreen.KURSUS_SAYA);
                return;
            }
        }
        spPola.getChildren().add(CoursePattern.buat(kursus.getKode(), 84, 84, 20));
        lblKode.setText(kursus.getKode());
        lblNama.setText(kursus.getNama());
        lblPengajar.setText(kursus.getPengajar());
        lblDeskripsi.setText(kursus.getDeskripsi());
        if (btnUbahKode != null) {
            btnUbahKode.setVisible(session.isGuru());
            btnUbahKode.setManaged(session.isGuru());
        }
        tabMateri.setToggleGroup(grupTab);
        tabOrang.setToggleGroup(grupTab);
        tabMateri.setSelected(true);
        grupTab.selectedToggleProperty().addListener((obs, lama, baru) -> {
            if (baru == null) {
                grupTab.selectToggle(lama);
            }
        });
        buatDaftarBab();
        perbaruiProgres();
        Material fokus = session.getMateriFokus();
        if (fokus != null && babDari(fokus).isPresent()) {
            bukaMateri(fokus);
        } else {
            tampilkanBab(babPertamaBelumSelesai());
        }
    }

    @FXML
    private void handlePilihTabMateri() {
        tabMateri.setSelected(true);
        boxTabMateri.setVisible(true);
        boxTabMateri.setManaged(true);
        spTabOrang.setVisible(false);
        spTabOrang.setManaged(false);
    }

    @FXML
    private void handlePilihTabOrang() {
        tabOrang.setSelected(true);
        boxTabMateri.setVisible(false);
        boxTabMateri.setManaged(false);
        spTabOrang.setVisible(true);
        spTabOrang.setManaged(true);
        muatTabOrang();
    }

    // ------------------------------------------------------------------ daftar bab
    private void buatDaftarBab() {
        vbBab.getChildren().clear();
        tombolBab.clear();
        for (Chapter bab : kursus.getDaftarBab()) {
            ToggleButton tombol = new ToggleButton(bab.getJudul());
            tombol.getStyleClass().add("item-bab");
            tombol.setToggleGroup(grupBab);
            tombol.setMaxWidth(Double.MAX_VALUE);
            tombol.setWrapText(true);
            tombol.setAlignment(Pos.CENTER_LEFT);
            tombol.setOnAction(e -> tampilkanBab(bab));
            tombolBab.put(bab, tombol);
            if (session.isGuru()) {
                tombol.getStyleClass().add("item-bab-guru");
                StackPane barisBab = new StackPane();
                barisBab.setMaxWidth(Double.MAX_VALUE);
                Button btnMenu = new Button();
                btnMenu.getStyleClass().addAll("tombol-hantu", "tombol-kebab-bab");
                btnMenu.setGraphic(new IconView(Icon.TITIK_TIGA, 14));
                btnMenu.setTooltip(new Tooltip("Menu Bab"));
                btnMenu.setFocusTraversable(false);
                ContextMenu menu = new ContextMenu();
                menu.getStyleClass().add("menu-kaca");
                MenuItem itemUbah = new MenuItem("Ubah Judul Bab");
                itemUbah.setGraphic(new IconView(Icon.PENSIL, 14));
                itemUbah.setOnAction(e -> handleUbahJudulBab(bab));
                MenuItem itemHapus = new MenuItem("Hapus Bab");
                itemHapus.setGraphic(new IconView(Icon.SAMPAH, 14));
                itemHapus.getStyleClass().add("menu-item-bahaya");
                itemHapus.setOnAction(e -> handleHapusBab(bab));
                menu.getItems().addAll(itemUbah, itemHapus);
                btnMenu.setOnAction(e -> {
                    if (menu.isShowing()) {
                        menu.hide();
                    } else {
                        menu.show(btnMenu, Side.BOTTOM, -120, 4);
                    }
                    e.consume();
                });
                tombol.setContextMenu(menu);
                StackPane.setAlignment(btnMenu, Pos.CENTER_RIGHT);
                StackPane.setMargin(btnMenu, new Insets(0, 8, 0, 0));
                barisBab.getChildren().addAll(tombol, btnMenu);
                vbBab.getChildren().add(barisBab);
            } else {
                vbBab.getChildren().add(tombol);
            }
        }
        if (session.isGuru()) {
            VBox boxAksiGuru = new VBox(8);
            boxAksiGuru.setPadding(new Insets(8, 0, 0, 0));
            Button btnTambahBab = new Button("+ Tambah Bab");
            btnTambahBab.getStyleClass().addAll("tombol-sekunder");
            btnTambahBab.setMaxWidth(Double.MAX_VALUE);
            btnTambahBab.setOnAction(e -> handleTambahBab());
            Button btnTambahUjian = new Button("Tambah Ujian");
            btnTambahUjian.getStyleClass().addAll("tombol-utama");
            btnTambahUjian.setMaxWidth(Double.MAX_VALUE);
            btnTambahUjian.setOnAction(e -> handleTambahUjianLangsung());
            boxAksiGuru.getChildren().addAll(btnTambahBab, btnTambahUjian);
            vbBab.getChildren().add(boxAksiGuru);
        }
        perbaruiStatusBab();
    }

    private boolean babSelesai(Chapter bab) {
        if (siswa == null)
            return false;
        return bab.getDaftarMateri().stream().allMatch(m -> progresService.isSelesai(siswa, kursus, m));
    }

    private void perbaruiStatusBab() {
        tombolBab.forEach((bab, tombol) -> {
            Label nomor = new Label(String.valueOf(bab.getNomor()));
            nomor.getStyleClass().add("nomor-bab");
            if (babSelesai(bab)) {
                nomor.getStyleClass().add("nomor-bab-selesai");
                nomor.setText("✓");
            }
            tombol.setGraphic(nomor);
        });
    }

    private void perbaruiProgres() {
        if (session.isGuru()) {
            if (lblJudulProgres != null)
                lblJudulProgres.setText("Jumlah Bab");
            lblPersen.setText(kursus.getDaftarBab().size() + " Bab");
            lblRingkasProgres.setText(kursus.getJumlahMateriPembelajaran() + " materi · "
                    + kursus.getJumlahUjian() + " ujian terdaftar");
            pbProgres.setVisible(false);
            pbProgres.setManaged(false);
            return;
        }
        if (lblJudulProgres != null)
            lblJudulProgres.setText("Progres kamu");
        double progres = progresService.getProgres(siswa, kursus);
        lblPersen.setText(Math.round(progres * 100) + "%");
        lblRingkasProgres.setText(progresService.getJumlahSelesai(siswa, kursus) + " dari "
                + kursus.getJumlahMateri() + " materi selesai");
        pbProgres.setProgress(progres);
        pbProgres.setVisible(true);
        pbProgres.setManaged(true);
    }

    private Chapter babPertamaBelumSelesai() {
        return kursus.getDaftarBab().stream()
                .filter(bab -> !babSelesai(bab))
                .findFirst()
                .orElse(kursus.getDaftarBab().get(0));
    }

    private Optional<Chapter> babDari(Material materi) {
        return kursus.getDaftarBab().stream()
                .filter(bab -> bab.getDaftarMateri().contains(materi))
                .findFirst();
    }

    private void tampilkanKonten(Node konten) {
        spKonten.getChildren().setAll(konten);
        AnimationUtil.masuk(konten);
    }

    // ------------------------------------------------------------------ isi bab
    private void tampilkanBab(Chapter bab) {
        if (tombolBab.containsKey(bab)) {
            tombolBab.get(bab).setSelected(true);
        }
        VBox isi = panel();
        Label label = new Label("BAB " + bab.getNomor());
        label.getStyleClass().add("label-kecil-kapital");
        Label judul = new Label(bab.getJudul());
        judul.getStyleClass().add("judul-halaman");
        Label info = new Label(bab.getDaftarMateri().size() + " item - klik untuk membuka");
        info.getStyleClass().add("teks-redup");
        isi.getChildren().addAll(label, judul, info);
        if (session.isGuru()) {
            HBox aksiBab = new HBox(10);
            aksiBab.setAlignment(Pos.CENTER_LEFT);
            aksiBab.setPadding(new Insets(6, 0, 8, 0));
            Button btnTambahMateri = new Button("+ Tambah Materi");
            btnTambahMateri.getStyleClass().add("tombol-utama");
            btnTambahMateri.setOnAction(e -> bukaEditorMateri(bab, null));
            Button btnTambahKuis = new Button("+ Buat Kuis Bab");
            btnTambahKuis.getStyleClass().add("tombol-sekunder");
            btnTambahKuis.setGraphic(new IconView(Icon.BINTANG, 14));
            btnTambahKuis.setOnAction(e -> handleBuatKuisBab(bab));
            Button btnTambahTugas = new Button("+ Tambah Tugas");
            btnTambahTugas.getStyleClass().add("tombol-sekunder");
            btnTambahTugas.setGraphic(new IconView(Icon.DOKUMEN, 14));
            btnTambahTugas.setOnAction(e -> handleTambahTugas(bab));
            aksiBab.getChildren().addAll(btnTambahMateri, btnTambahKuis, btnTambahTugas);
            isi.getChildren().add(aksiBab);
        }
        if (bab.getDaftarMateri().isEmpty()) {
            VBox boxKosong = new VBox(8);
            boxKosong.setAlignment(Pos.CENTER);
            boxKosong.setStyle("-fx-padding: 36 20; -fx-background-color: -garis; -fx-background-radius: 12;");
            Label lblKsg = new Label("Belum ada materi atau ujian di bab ini");
            lblKsg.getStyleClass().add("judul-kartu");
            Label lblPnjk = new Label(
                    "Klik '+ Tambah Materi' di atas untuk menulis bacaan baru atau tombol 'Tambah Ujian' di panel kiri.");
            lblPnjk.getStyleClass().add("teks-kecil");
            boxKosong.getChildren().addAll(lblKsg, lblPnjk);
            isi.getChildren().add(boxKosong);
        } else {
            List<Node> baris = bab.getDaftarMateri().stream().map(this::barisMateri).toList();
            isi.getChildren().addAll(baris);
            AnimationUtil.berurutan(baris, Duration.millis(50));
        }
        tampilkanKonten(gulir(isi));
    }

    private Node barisMateri(Material materi) {
        VBox teks = new VBox(2, label(materi.getJudul(), "judul-kartu"), label(materi.getKeterangan(), "teks-kecil"));
        HBox.setHgrow(teks, Priority.ALWAYS);
        Node status;
        if (session.isGuru()) {
            HBox aksi = new HBox(8);
            aksi.setAlignment(Pos.CENTER_RIGHT);
            if (materi instanceof TextMaterial mt) {
                Button btnEditTeks = new Button("Edit Teks");
                btnEditTeks.getStyleClass().addAll("tombol-sekunder");
                btnEditTeks.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");
                btnEditTeks.setOnAction(e -> {
                    e.consume();
                    bukaEditorMateri(babDari(mt).orElse(kursus.getDaftarBab().get(0)), mt);
                });
                aksi.getChildren().add(btnEditTeks);
            }
            if (materi instanceof ExamMaterial mu) {
                Button btnEditSoal = new Button("Edit Soal");
                btnEditSoal.getStyleClass().addAll("tombol-sekunder");
                btnEditSoal.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");
                btnEditSoal.setOnAction(e -> {
                    e.consume();
                    Exam u = kursusService.ujianDari(mu);
                    session.setUjianAktif(u);
                    session.setMateriFokus(mu);
                    SceneRouter.pindah(AppScreen.EDITOR_UJIAN);
                });
                aksi.getChildren().add(btnEditSoal);
            }
            if (materi instanceof QuizMaterial mq) {
                Button btnEditKuis = new Button("Edit Kuis");
                btnEditKuis.getStyleClass().addAll("tombol-sekunder");
                btnEditKuis.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");
                btnEditKuis.setOnAction(e -> {
                    e.consume();
                    Exam u = kursusService.ujianDari(mq);
                    session.setUjianAktif(u);
                    session.setMateriFokus(mq);
                    SceneRouter.pindah(AppScreen.EDITOR_UJIAN);
                });
                aksi.getChildren().add(btnEditKuis);
            }
            if (materi instanceof AssignmentMaterial mt) {
                Button btnEditTugas = new Button("Edit Tugas");
                btnEditTugas.getStyleClass().addAll("tombol-sekunder");
                btnEditTugas.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");
                btnEditTugas.setOnAction(e -> {
                    e.consume();
                    handleEditTugas(babDari(mt).orElse(kursus.getDaftarBab().get(0)), mt);
                });
                aksi.getChildren().add(btnEditTugas);
            }
            Button btnHapusMateri = new Button("✕");
            btnHapusMateri.getStyleClass().add("tombol-hantu");
            btnHapusMateri.setStyle("-fx-padding: 4 8; -fx-font-size: 12px; -fx-text-fill: -teks-redup;");
            btnHapusMateri.setOnAction(e -> {
                e.consume();
                handleHapusMateri(materi);
            });
            aksi.getChildren().addAll(btnHapusMateri, new IconView(Icon.PANAH_KANAN, 16));
            status = aksi;
        } else if (siswa != null && materi instanceof AssignmentMaterial mt && !mt.isBolehAkses(siswa)) {
            Label badgeTerkunci = label("Khusus " + mt.getTargetKelas(), "badge");
            badgeTerkunci.setStyle("-fx-background-color: -garis; -fx-text-fill: -teks-redup;");
            status = badgeTerkunci;
        } else if (siswa != null && (progresService.isSelesai(siswa, kursus, materi)
                || (materi instanceof QuizMaterial mq
                        && session.getRekapService().sudahMengerjakan(siswa, kursusService.ujianDari(mq)))
                || (materi instanceof AssignmentMaterial mt
                        && session.getTugasService().cariPengumpulan(mt.getIdMateri(), siswa.getNis()).isPresent()))) {
            Label selesai = label("Selesai", "badge");
            selesai.getStyleClass().add("badge-sukses");
            status = selesai;
        } else {
            status = new IconView(Icon.PANAH_KANAN, 18);
        }
        HBox baris = new HBox(14, ikonMateri(materi, 20), teks, status);
        baris.setAlignment(Pos.CENTER_LEFT);
        baris.getStyleClass().add("item-materi");
        baris.setOnMouseClicked(e -> bukaMateri(materi));
        return baris;
    }

    private void handleHapusMateri(Material materi) {
        if (!DialogUtil.konfirmasi("Hapus materi/ujian '" + materi.getJudul() + "' dari bab ini?")) {
            return;
        }
        Optional<Chapter> babOpt = babDari(materi);
        if (babOpt.isEmpty())
            return;
        Chapter bab = babOpt.get();
        List<Chapter> daftarBab = new ArrayList<>();
        for (Chapter b : kursus.getDaftarBab()) {
            if (b.getNomor() == bab.getNomor()) {
                List<Material> sisa = new ArrayList<>(b.getDaftarMateri());
                sisa.remove(materi);
                daftarBab.add(new Chapter(b.getNomor(), b.getJudul(), sisa));
            } else {
                daftarBab.add(b);
            }
        }
        simpanKursus(kursus.denganDaftarBab(daftarBab));
        tampilkanBab(kursus.getDaftarBab().get(bab.getNomor() - 1));
    }

    private void bukaMateri(Material materi) {
        switch (materi) {
            case TextMaterial t -> tampilkanTeks(t);
            case ExamMaterial u -> tampilkanUjian(u);
            case QuizMaterial q -> tampilkanQuiz(q);
            case AssignmentMaterial g -> tampilkanTugas(g);
            case LinkMaterial l -> {
                session.bukaDokumen(l.getAlamat().toString());
                tandai(l);
            }
            case PdfMaterial p -> {
                session.bukaDokumen(p.getFile().toUri().toString());
                tandai(p);
            }
            default -> {
            }
        }
    }

    private void tandai(Material materi) {
        if (siswa != null) {
            try {
                progresService.tandaiSelesai(siswa, kursus, materi);
                perbaruiStatusBab();
                perbaruiProgres();
            } catch (IOException e) {
                DialogUtil.peringatan("Gagal mencatat progres belajar: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ tampilan
    // materi
    private void tampilkanTeks(TextMaterial materi) {
        tampilkanTeksSlide(materi, 0);
    }

    private void tampilkanTeksSlide(TextMaterial materi, int slideIndex) {
        String markdown;
        try {
            markdown = kursusService.bacaTeks(materi);
        } catch (IOException e) {
            DialogUtil.peringatan("Materi tidak dapat dibaca: " + e.getMessage());
            return;
        }
        tandai(materi); // bacaan dianggap selesai saat dibuka
        List<SlideDraft> daftarSlide = SlideMarkdownConverter.parse(markdown);
        if (daftarSlide == null || daftarSlide.isEmpty()) {
            daftarSlide = List.of(new SlideDraft(materi.getJudul()));
        }
        int totalSlide = daftarSlide.size();
        int indeksAktif = Math.max(0, Math.min(slideIndex, totalSlide - 1));
        SlideDraft slideAktif = daftarSlide.get(indeksAktif);
        VBox isi = panel();
        HBox barisAtas = new HBox(12, tombolKembaliKeBab(materi));
        barisAtas.setAlignment(Pos.CENTER_LEFT);
        if (totalSlide > 1) {
            Label lblSlideInfo = new Label("Halaman " + (indeksAktif + 1) + " dari " + totalSlide);
            lblSlideInfo.getStyleClass().addAll("badge", "badge-netral");
            barisAtas.getChildren().add(lblSlideInfo);
        }
        if (session.isGuru()) {
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Button btnEdit = new Button("Edit Isi Materi");
            btnEdit.setGraphic(new IconView(Icon.DOKUMEN, 14));
            btnEdit.getStyleClass().add("tombol-sekunder");
            btnEdit.setOnAction(e -> bukaEditorMateri(babDari(materi).orElse(kursus.getDaftarBab().get(0)), materi));
            barisAtas.getChildren().addAll(sp, btnEdit);
        }
        String labelKategori = materi.getKeterangan().toUpperCase();
        if (totalSlide > 1 && slideAktif.getJudulSlide() != null && !slideAktif.getJudulSlide().trim().isEmpty()) {
            labelKategori += " • " + slideAktif.getJudulSlide().trim().toUpperCase();
        }
        String slideMarkdown = (totalSlide > 1)
                ? SlideMarkdownConverter.toMarkdown(slideAktif)
                : markdown;
        VBox boxSlideKonten = new VBox(12);
        boxSlideKonten.getChildren().add(MarkdownView.render(slideMarkdown));
        HBox barisBawah = new HBox(12);
        barisBawah.setAlignment(Pos.CENTER_LEFT);
        barisBawah.setPadding(new Insets(20, 0, 0, 0));
        if (totalSlide > 1) {
            Button btnPrev = new Button("Halaman Sebelumnya");
            btnPrev.setGraphic(new IconView(Icon.PANAH_KIRI, 14));
            btnPrev.getStyleClass().add("tombol-sekunder");
            btnPrev.setDisable(indeksAktif == 0);
            btnPrev.setOnAction(e -> tampilkanTeksSlide(materi, indeksAktif - 1));
            Button btnNext = new Button("Halaman Berikutnya");
            btnNext.setGraphic(new IconView(Icon.PANAH_KANAN, 14));
            btnNext.setContentDisplay(ContentDisplay.RIGHT);
            btnNext.getStyleClass().add("tombol-utama");
            btnNext.setOnAction(e -> tampilkanTeksSlide(materi, indeksAktif + 1));
            HBox dots = new HBox(6);
            dots.setAlignment(Pos.CENTER);
            for (int i = 0; i < totalSlide; i++) {
                final int targetIdx = i;
                Button dot = new Button();
                dot.setPrefSize(i == indeksAktif ? 20 : 8, 8);
                dot.setMaxSize(i == indeksAktif ? 20 : 8, 8);
                dot.setMinSize(i == indeksAktif ? 20 : 8, 8);
                dot.setStyle(i == indeksAktif
                        ? "-fx-background-color: -aksen; -fx-background-radius: 4;"
                        : "-fx-background-color: -garis; -fx-background-radius: 4; -fx-cursor: hand;");
                dot.setOnAction(e -> tampilkanTeksSlide(materi, targetIdx));
                dots.getChildren().add(dot);
            }
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            barisBawah.getChildren().addAll(btnPrev, dots, spacer);
            if (indeksAktif < totalSlide - 1) {
                barisBawah.getChildren().add(btnNext);
            } else {
                barisBawah.getChildren().add(navigasiBerikutnya(materi));
            }
        } else {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            barisBawah.getChildren().addAll(spacer, navigasiBerikutnya(materi));
        }
        isi.getChildren().addAll(barisAtas, label(labelKategori, "label-kecil-kapital"), boxSlideKonten, barisBawah);
        tampilkanKonten(gulir(isi));
    }

    private Node navigasiBerikutnya(Material materi) {
        List<Material> semua = kursus.getSemuaMateri();
        int indeks = semua.indexOf(materi);
        HBox baris = new HBox();
        baris.setAlignment(Pos.CENTER_RIGHT);
        baris.setPadding(new Insets(16, 0, 0, 0));
        if (indeks + 1 < semua.size()) {
            Material berikut = semua.get(indeks + 1);
            Button tombol = tombol("Berikutnya: " + berikut.getJudul(), Icon.PANAH_KANAN, "tombol-utama");
            tombol.setContentDisplay(ContentDisplay.RIGHT);
            tombol.setOnAction(e -> bukaMateri(berikut));
            baris.getChildren().add(tombol);
        }
        return baris;
    }

    private void tampilkanUjian(ExamMaterial materi) {
        Exam ujian = kursusService.ujianDari(materi);
        VBox isi = panel();
        HBox kepala = new HBox(16, ikonMateri(materi, 22),
                new VBox(2, label("UJIAN", "label-kecil-kapital"), label(materi.getJudul(), "judul-halaman"),
                        label(ujian.getMataPelajaran() + " - " + ujian.getKodeUjian(), "teks-redup")));
        kepala.setAlignment(Pos.CENTER_LEFT);
        HBox info;
        if (session.isGuru()) {
            info = new HBox(14,
                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),
                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),
                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),
                    kotakInfo("Token", ujian.getToken()));
        } else {
            int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);
            info = new HBox(14,
                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),
                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),
                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),
                    kotakInfo("Percobaan", String.valueOf(percobaan)));
        }
        Label aturan = label(ujian.isBolehUlang()
                ? "Ujian ini boleh dikerjakan ulang. Nilai terbaik yang dipakai."
                : "Ujian ini hanya dapat dikerjakan satu kali.", "teks-redup");
        aturan.setGraphic(new IconView(ujian.isBolehUlang() ? Icon.CENTANG : Icon.GEMBOK, 16));
        if (session.isGuru()) {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info, aturan);
        } else {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info);
        }
        if (ujian.hasJadwalAkses()) {
            HBox jadwalBox = new HBox(10);
            jadwalBox.setAlignment(Pos.CENTER_LEFT);
            jadwalBox.getStyleClass().addAll("kaca");
            jadwalBox.setStyle("-fx-padding: 8 14; -fx-background-radius: 8;");
            Label lblJadwal = new Label("Waktu Akses: "
                    + FormatUtil.formatRentangWaktuAkses(ujian.getWaktuMulai(), ujian.getWaktuSelesai()));
            lblJadwal.setGraphic(new IconView(Icon.JAM, 14));
            lblJadwal.getStyleClass().add("teks-kecil");
            jadwalBox.getChildren().add(lblJadwal);
            isi.getChildren().add(jadwalBox);
        }
        if (session.isGuru()) {
            Button editUjian = tombol("Edit Soal Ujian", Icon.DOKUMEN, "tombol-utama");
            editUjian.getStyleClass().add("tombol-besar");
            editUjian.setOnAction(e -> {
                session.setUjianAktif(ujian);
                session.setMateriFokus(materi);
                SceneRouter.pindah(AppScreen.EDITOR_UJIAN);
            });
            isi.getChildren().add(editUjian);
        } else {
            Optional<GradeHistory> terbaik = session.getRekapService().getNilaiTerbaik(siswa, ujian);
            boolean bolehMulai = session.getUjianService().bolehMulai(siswa, ujian);
            boolean aksesDibuka = ujian.isAksesDibuka();
            int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);
            terbaik.ifPresent(r -> isi.getChildren().add(kartuNilaiTerbaik(r)));
            if (!aksesDibuka) {
                Label pesanAkses = label(session.getUjianService().getPesanStatusAkses(ujian), "badge");
                pesanAkses.getStyleClass().add("badge-peringatan");
                isi.getChildren().add(pesanAkses);
            }
            String teksTombol;
            if (!aksesDibuka) {
                teksTombol = ujian.isBelumMulai() ? "Akses Belum Dibuka" : "Waktu Akses Berakhir";
            } else if (!bolehMulai) {
                teksTombol = "Sudah dikerjakan";
            } else if (percobaan > 0) {
                teksTombol = "Kerjakan ulang";
            } else {
                teksTombol = "Mulai ujian";
            }
            Button mulai = tombol(teksTombol, bolehMulai ? Icon.MAIN : Icon.GEMBOK, "tombol-utama");
            mulai.getStyleClass().add("tombol-besar");
            mulai.setDisable(!bolehMulai);
            mulai.setOnAction(e -> {
                session.setUjianAktif(ujian);
                session.setMateriFokus(materi);
                SceneRouter.pindah(AppScreen.TOKEN_GATE);
            });
            isi.getChildren().add(mulai);
        }
        tampilkanKonten(gulir(isi));
    }

    private void tampilkanQuiz(QuizMaterial materi) {
        Exam ujian = kursusService.ujianDari(materi);
        VBox isi = panel();
        HBox kepala = new HBox(16, ikonMateri(materi, 22),
                new VBox(2, label("KUIS BAB", "label-kecil-kapital"), label(materi.getJudul(), "judul-halaman"),
                        label(ujian.getMataPelajaran() + " - " + ujian.getKodeUjian(), "teks-redup")));
        kepala.setAlignment(Pos.CENTER_LEFT);
        HBox info;
        if (session.isGuru()) {
            info = new HBox(14,
                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),
                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),
                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),
                    kotakInfo("Kode Kuis", ujian.getKodeUjian()));
        } else {
            int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);
            info = new HBox(14,
                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),
                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),
                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),
                    kotakInfo("Status", percobaan > 0 ? "Sudah Dikerjakan" : "Belum Dikerjakan"));
        }
        Label aturan = label(
                "Kuis bab ini hanya dapat dikerjakan satu kali. Setelah selesai, Anda dapat melihat evaluasi dan pembahasan soal.",
                "teks-redup");
        aturan.setGraphic(new IconView(Icon.BINTANG, 16));
        if (session.isGuru()) {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info, aturan);
        } else {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info);
        }
        if (ujian.hasJadwalAkses()) {
            HBox jadwalBox = new HBox(10);
            jadwalBox.setAlignment(Pos.CENTER_LEFT);
            jadwalBox.getStyleClass().addAll("kaca");
            jadwalBox.setStyle("-fx-padding: 8 14; -fx-background-radius: 8;");
            Label lblJadwal = new Label("Waktu Akses: "
                    + FormatUtil.formatRentangWaktuAkses(ujian.getWaktuMulai(), ujian.getWaktuSelesai()));
            lblJadwal.setGraphic(new IconView(Icon.JAM, 14));
            lblJadwal.getStyleClass().add("teks-kecil");
            jadwalBox.getChildren().add(lblJadwal);
            isi.getChildren().add(jadwalBox);
        }
        if (session.isGuru()) {
            Button editUjian = tombol("Edit Soal Kuis", Icon.DOKUMEN, "tombol-utama");
            editUjian.getStyleClass().add("tombol-besar");
            editUjian.setOnAction(e -> {
                session.setUjianAktif(ujian);
                session.setMateriFokus(materi);
                SceneRouter.pindah(AppScreen.EDITOR_UJIAN);
            });
            isi.getChildren().add(editUjian);
        } else {
            boolean sudahMengerjakan = session.getRekapService().sudahMengerjakan(siswa, ujian);
            Optional<GradeHistory> riwayatOpt = session.getRekapService().getNilaiTerbaik(siswa, ujian);
            if (sudahMengerjakan && riwayatOpt.isPresent()) {
                GradeHistory r = riwayatOpt.get();
                isi.getChildren().add(kartuNilaiTerbaik(r));
                Label pesanSelesai = label(
                        "Kuis telah diselesaikan. Anda dapat melihat kembali evaluasi dan pembahasan jawaban kuis di bawah ini.",
                        "teks-redup");
                pesanSelesai.setWrapText(true);
                pesanSelesai.setGraphic(new IconView(Icon.CENTANG, 16));
                Button btnPembahasan = tombol("Lihat Pembahasan Kuis", Icon.MATA, "tombol-utama");
                btnPembahasan.getStyleClass().add("tombol-besar");
                btnPembahasan.setOnAction(e -> {
                    session.setRiwayatFokus(r);
                    session.setUjianAktif(ujian);
                    session.setMateriFokus(materi);
                    session.setLayarAsalPembahasan(AppScreen.KURSUS_DETAIL);
                    SceneRouter.pindah(AppScreen.PEMBAHASAN_UJIAN);
                });
                isi.getChildren().addAll(pesanSelesai, btnPembahasan);
            } else {
                boolean aksesDibuka = ujian.isAksesDibuka();
                if (!aksesDibuka) {
                    Label pesanAkses = label(session.getUjianService().getPesanStatusAkses(ujian), "badge");
                    pesanAkses.getStyleClass().add("badge-peringatan");
                    isi.getChildren().add(pesanAkses);
                }
                Button mulai = tombol(
                        !aksesDibuka ? (ujian.isBelumMulai() ? "Akses Belum Dibuka" : "Waktu Akses Berakhir")
                                : "Mulai Kuis",
                        aksesDibuka ? Icon.MAIN : Icon.GEMBOK, "tombol-utama");
                mulai.getStyleClass().add("tombol-besar");
                mulai.setDisable(!aksesDibuka);
                mulai.setOnAction(e -> {
                    session.setUjianAktif(ujian);
                    session.setMateriFokus(materi);
                    ExamResult hasilUjian = session.getUjianService().mulaiUjian(siswa, ujian);
                    session.setHasilAktif(hasilUjian);
                    SceneRouter.pindah(AppScreen.CBT_ROOM);
                });
                isi.getChildren().add(mulai);
            }
        }
        tampilkanKonten(gulir(isi));
    }

    private Node kartuNilaiTerbaik(GradeHistory r) {
        Label status = label(r.lulus() ? "LULUS" : "TIDAK LULUS", "badge");
        status.getStyleClass().add(r.lulus() ? "badge-sukses" : "badge-bahaya");
        HBox kartu = new HBox(14, new IconView(Icon.PIALA, 22),
                new VBox(2, label("Nilai terbaik", "teks-kecil"),
                        label(FormatUtil.angka(r.skor()) + "  (predikat " + r.predikat() + ")", "judul-kartu")),
                status);
        kartu.setAlignment(Pos.CENTER_LEFT);
        kartu.getStyleClass().add("kartu-ujian-info");
        return kartu;
    }

    // ------------------------------------------------------------------ aksi guru
    // untuk bab
    private void handleTambahBab() {
        Dialog<String> dialog = DialogUtil.buatDialogTransparan();
        DialogPane pane = dialog.getDialogPane();
        VBox kartu = DialogUtil.buatKartuModal(430, 450);
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);
        DialogUtil.pasangDragModal(dialog, barisHeader);
        VBox boxJudul = new VBox(2);
        Label lblJudul = new Label("Tambah Bab Baru");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label lblSub = new Label("Masukkan judul bab baru untuk kursus " + kursus.getNama());
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(lblJudul, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);
        Button btnTutup = new Button();
        btnTutup.setGraphic(new IconView(Icon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
        });
        barisHeader.getChildren().addAll(boxJudul, btnTutup);
        VBox boxInput = new VBox(6);
        Label lblField = new Label("Judul Bab");
        lblField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextField tfJudul = new TextField("Bab " + (kursus.getDaftarBab().size() + 1));
        tfJudul.setPromptText("Contoh: Bab 1 - Kata Baku");
        boxInput.getChildren().addAll(lblField, tfJudul);
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);
        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
        });
        Button btnSimpan = new Button("Simpan Bab");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setDefaultButton(true);
        btnSimpan.setOnAction(e -> {
            String teks = tfJudul.getText() != null ? tfJudul.getText().trim() : "";
            if (!StringUtil.isKosong(teks)) {
                dialog.setResult(teks);
                dialog.close();
            }
        });
        barisTombol.getChildren().addAll(btnBatal, btnSimpan);
        kartu.getChildren().addAll(barisHeader, new Separator(), boxInput, new Separator(), barisTombol);
        pane.setContent(kartu);
        Platform.runLater(() -> {
            tfJudul.requestFocus();
            tfJudul.selectAll();
        });
        dialog.showAndWait().ifPresent(judul -> {
            if (StringUtil.isKosong(judul)) return;
            List<Chapter> daftar = new ArrayList<>(kursus.getDaftarBab());
            Chapter babBaru = new Chapter(daftar.size() + 1, judul.trim(), List.of());
            daftar.add(babBaru);
            simpanKursus(kursus.denganDaftarBab(daftar));
            tampilkanBab(babBaru);
        });
    }

    private Chapter babAktifSaatIni() {
        for (Map.Entry<Chapter, ToggleButton> e : tombolBab.entrySet()) {
            if (e.getValue().isSelected()) {
                return e.getKey();
            }
        }
        return null;
    }

    private void handleUbahJudulBab(Chapter bab) {
        Dialog<String> dialog = DialogUtil.buatDialogTransparan();
        DialogPane pane = dialog.getDialogPane();
        VBox kartu = DialogUtil.buatKartuModal(430, 450);
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);
        DialogUtil.pasangDragModal(dialog, barisHeader);
        VBox boxJudul = new VBox(2);
        Label lblJudul = new Label("Ubah Judul Bab " + bab.getNomor());
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label lblSub = new Label("Perbarui judul bab pada kursus " + kursus.getNama());
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(lblJudul, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);
        Button btnTutup = new Button();
        btnTutup.setGraphic(new IconView(Icon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
        });
        barisHeader.getChildren().addAll(boxJudul, btnTutup);
        VBox boxInput = new VBox(6);
        Label lblField = new Label("Judul Bab");
        lblField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextField tfJudul = new TextField(bab.getJudul());
        boxInput.getChildren().addAll(lblField, tfJudul);
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);
        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
        });
        Button btnSimpan = new Button("Simpan Perubahan");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setDefaultButton(true);
        btnSimpan.setOnAction(e -> {
            String teks = tfJudul.getText() != null ? tfJudul.getText().trim() : "";
            if (!StringUtil.isKosong(teks)) {
                dialog.setResult(teks);
                dialog.close();
            }
        });
        barisTombol.getChildren().addAll(btnBatal, btnSimpan);
        kartu.getChildren().addAll(barisHeader, new Separator(), boxInput, new Separator(), barisTombol);
        pane.setContent(kartu);
        Platform.runLater(() -> {
            tfJudul.requestFocus();
            tfJudul.selectAll();
        });
        dialog.showAndWait().ifPresent(judulBaru -> {
            if (StringUtil.isKosong(judulBaru)) return;
            List<Chapter> daftar = new ArrayList<>();
            Chapter babTerpilih = null;
            for (Chapter b : kursus.getDaftarBab()) {
                if (b.getNomor() == bab.getNomor()) {
                    babTerpilih = new Chapter(b.getNomor(), judulBaru.trim(), b.getDaftarMateri());
                    daftar.add(babTerpilih);
                } else {
                    daftar.add(b);
                }
            }
            simpanKursus(kursus.denganDaftarBab(daftar));
            if (babTerpilih != null) {
                tampilkanBab(babTerpilih);
            }
        });
    }

    private void handleHapusBab(Chapter bab) {
        if (kursus.getDaftarBab().size() <= 1) {
            DialogUtil.peringatan("Kursus minimal harus memiliki 1 bab.");
            return;
        }
        if (DialogUtil.konfirmasi("Hapus bab '" + bab.getJudul() + "' beserta semua materi di dalamnya?")) {
            Chapter babAktif = babAktifSaatIni();
            List<Chapter> sisa = new ArrayList<>();
            int no = 1;
            int nomorBabAktifBaru = 1;
            for (Chapter b : kursus.getDaftarBab()) {
                if (b.getNomor() != bab.getNomor()) {
                    int noSekarang = no++;
                    sisa.add(new Chapter(noSekarang, b.getJudul(), b.getDaftarMateri()));
                    if (babAktif != null && b.getNomor() == babAktif.getNomor()) {
                        nomorBabAktifBaru = noSekarang;
                    }
                }
            }
            simpanKursus(kursus.denganDaftarBab(sisa));
            int idx = Math.max(0, Math.min(nomorBabAktifBaru - 1, sisa.size() - 1));
            tampilkanBab(kursus.getDaftarBab().get(idx));
        }
    }

    private void handleTambahUjianLangsung() {
        if (kursus.getDaftarBab().isEmpty()) {
            DialogUtil.peringatan("Tambahkan bab terlebih dahulu sebelum menambah ujian.");
            return;
        }
        Chapter babTerpilih = babAktifSaatIni();
        if (babTerpilih == null) {
            babTerpilih = kursus.getDaftarBab().get(0);
        }
        handleTambahUjian(babTerpilih);
    }

    private void handleTambahUjian(Chapter babDefault) {
        Dialog<Boolean> dialog = DialogUtil.buatDialogTransparan();
        DialogPane pane = dialog.getDialogPane();
        VBox kartu = DialogUtil.buatKartuModal(520, 540);
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);
        DialogUtil.pasangDragModal(dialog, barisHeader);
        VBox boxJudul = new VBox(2);
        HBox barisJudulBadge = new HBox(8);
        barisJudulBadge.setAlignment(Pos.CENTER_LEFT);
        Label lblJudulModal = new Label("Tambah Ujian");
        lblJudulModal.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label badgeBab = new Label("Bab " + babDefault.getNomor());
        badgeBab.getStyleClass().add("badge");
        barisJudulBadge.getChildren().addAll(lblJudulModal, badgeBab);
        Label lblSub = new Label("Tambahkan ujian CBT ke " + babDefault.getJudul());
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(barisJudulBadge, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);
        Button btnTutup = new Button();
        btnTutup.setGraphic(new IconView(Icon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });
        barisHeader.getChildren().addAll(boxJudul, btnTutup);
        RadioButton rbBaru = new RadioButton("Buat Ujian Baru & Langsung Rancang Soal");
        rbBaru.setStyle("-fx-font-weight: bold;");
        RadioButton rbPilih = new RadioButton("Pilih Ujian yang Sudah Ada di Bank Soal");
        rbPilih.setStyle("-fx-font-weight: bold;");
        ToggleGroup tg = new ToggleGroup();
        rbBaru.setToggleGroup(tg);
        rbPilih.setToggleGroup(tg);
        rbBaru.setSelected(true);
        String saranKode = kursus.getKode().toUpperCase() + "-0" + (session.getUjianService().getJumlahUjian() + 1);
        TextField tfKode = new TextField(saranKode);
        TextField tfJudul = new TextField("Ujian " + babDefault.getJudul());
        TextField tfDurasi = new TextField("30");
        TextField tfKkm = new TextField("75");
        String saranToken = hasilkanKodeKursusAcak();
        TextField tfToken = new TextField(saranToken);
        tfToken.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold;");
        HBox barisKodeJudul = new HBox(12);
        VBox vbKode = new VBox(5, new Label("Kode Ujian"), tfKode);
        vbKode.setPrefWidth(150);
        VBox vbJudulUjian = new VBox(5, new Label("Judul Ujian"), tfJudul);
        HBox.setHgrow(vbJudulUjian, Priority.ALWAYS);
        barisKodeJudul.getChildren().addAll(vbKode, vbJudulUjian);
        HBox barisDurasiKkmToken = new HBox(12);
        VBox vbDurasi = new VBox(5, new Label("Durasi (menit)"), tfDurasi);
        HBox.setHgrow(vbDurasi, Priority.ALWAYS);
        VBox vbKkm = new VBox(5, new Label("KKM"), tfKkm);
        HBox.setHgrow(vbKkm, Priority.ALWAYS);
        VBox vbToken = new VBox(5, new Label("Token (6 karakter)"), tfToken);
        HBox.setHgrow(vbToken, Priority.ALWAYS);
        barisDurasiKkmToken.getChildren().addAll(vbDurasi, vbKkm, vbToken);
        VBox boxJadwal = new VBox(8);
        boxJadwal.getStyleClass().add("modal-kotak-jadwal");
        Label lblJadwal = new Label("Jadwal Akses CBT (Opsional)");
        lblJadwal.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        DateTimePicker pemilihMulai = new DateTimePicker(null, true);
        DateTimePicker pemilihSelesai = new DateTimePicker(null, true);
        VBox vbMulai = new VBox(4, new Label("Waktu Buka"), pemilihMulai);
        VBox vbSelesai = new VBox(4, new Label("Waktu Tutup"), pemilihSelesai);
        boxJadwal.getChildren().addAll(lblJadwal, vbMulai, vbSelesai);
        CheckBox cbBolehUlang = new CheckBox("Izinkan siswa mengerjakan ulang ujian ini");
        cbBolehUlang.setSelected(false);
        VBox formBaru = new VBox(12, barisKodeJudul, barisDurasiKkmToken, boxJadwal, cbBolehUlang);
        VBox boxPilih = new VBox(8);
        ComboBox<String> cbUjian = new ComboBox<>();
        cbUjian.setMaxWidth(Double.MAX_VALUE);
        List<Exam> semuaUjian = session.getUjianService().getDaftarUjian();
        for (Exam u : semuaUjian) {
            cbUjian.getItems().add(u.getKodeUjian() + " - " + u.getMataPelajaran());
        }
        if (!cbUjian.getItems().isEmpty()) {
            cbUjian.getSelectionModel().select(0);
        }
        TextField tfJudulPilih = new TextField("Ujian " + babDefault.getJudul());
        boxPilih.getChildren().addAll(new Label("Judul di Kursus"), tfJudulPilih, new Label("Pilih Ujian CBT"), cbUjian);
        boxPilih.setVisible(false);
        boxPilih.setManaged(false);
        tg.selectedToggleProperty().addListener((obs, lama, terpilih) -> {
            boolean modeBaru = terpilih == rbBaru;
            formBaru.setVisible(modeBaru);
            formBaru.setManaged(modeBaru);
            boxPilih.setVisible(!modeBaru);
            boxPilih.setManaged(!modeBaru);
            if (dialog.getDialogPane().getScene() != null && dialog.getDialogPane().getScene().getWindow() != null) {
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);
        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });
        Button btnSimpan = new Button("Lanjutkan");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setDefaultButton(true);
        btnSimpan.setOnAction(e -> {
            Chapter babTarget = babDefault;
            if (rbBaru.isSelected()) {
                String kode = tfKode.getText().trim().toUpperCase();
                String jwb = tfJudul.getText().trim();
                String token = tfToken.getText().trim().toUpperCase();
                int durasi;
                double kkm;
                try {
                    durasi = Integer.parseInt(tfDurasi.getText().trim());
                    kkm = Double.parseDouble(tfKkm.getText().trim());
                } catch (NumberFormatException ex) {
                    DialogUtil.peringatan("Durasi dan KKM harus berupa angka");
                    return;
                }
                if (StringUtil.isKosong(kode) || StringUtil.isKosong(jwb) || StringUtil.isKosong(token)) {
                    DialogUtil.peringatan("Kode, Judul, dan Token wajib diisi");
                    return;
                }
                if (!kode.matches("^[A-Z]{2,5}-\\d{2}$")) {
                    DialogUtil.peringatan("Format Kode Ujian harus 2-5 huruf diikuti '-' dan 2 angka (contoh: " + saranKode + ")");
                    return;
                }
                if (token.length() != 6 || !token.matches("^[A-Z0-9]{6}$")) {
                    DialogUtil.peringatan("Token ujian harus tepat 6 huruf/angka");
                    return;
                }
                if (session.getUjianService().cariUjian(kode).isPresent()) {
                    DialogUtil.peringatan("Kode ujian sudah terpakai: " + kode);
                    return;
                }
                LocalDateTime mulai = pemilihMulai.getWaktu();
                LocalDateTime selesai = pemilihSelesai.getWaktu();
                if (mulai != null && selesai != null && mulai.isAfter(selesai)) {
                    DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup");
                    return;
                }
                try {
                    Question sampleSoal = new MultipleChoiceQuestion(1, "Pertanyaan nomor 1 untuk " + jwb + "...", 10.0,
                            List.of("Pilihan A", "Pilihan B", "Pilihan C", "Pilihan D"), 'A');
                    Exam ujianBaru = new Exam(kode, jwb, token, durasi, kkm, List.of(sampleSoal), cbBolehUlang.isSelected(), mulai, selesai);
                    session.getUjianService().tambahUjian(ujianBaru);
                    String idMateri = (kursus.getKode().toLowerCase() + "-u" + (System.currentTimeMillis() % 10000));
                    ExamMaterial mu = new ExamMaterial(idMateri, jwb, kode);
                    List<Chapter> daftarBab = new ArrayList<>();
                    for (Chapter b : kursus.getDaftarBab()) {
                        if (b.getNomor() == babTarget.getNomor()) {
                            List<Material> materiBaru = new ArrayList<>(b.getDaftarMateri());
                            materiBaru.add(mu);
                            daftarBab.add(new Chapter(b.getNomor(), b.getJudul(), materiBaru));
                        } else {
                            daftarBab.add(b);
                        }
                    }
                    simpanKursus(kursus.denganDaftarBab(daftarBab));
                    dialog.setResult(Boolean.TRUE);
                    dialog.close();
                    session.setUjianAktif(ujianBaru);
                    session.setMateriFokus(mu);
                    SceneRouter.pindah(AppScreen.EDITOR_UJIAN);
                } catch (Exception ex) {
                    DialogUtil.peringatan("Gagal membuat ujian baru: " + ex.getMessage());
                }
            } else {
                String jwb = tfJudulPilih.getText().trim();
                String terpilih = cbUjian.getValue();
                if (StringUtil.isKosong(jwb) || terpilih == null) return;
                String kodeUjian = terpilih.split(" - ")[0].trim();
                String idMateri = (kursus.getKode().toLowerCase() + "-u" + (System.currentTimeMillis() % 10000));
                ExamMaterial mu = new ExamMaterial(idMateri, jwb, kodeUjian);
                List<Chapter> daftarBab = new ArrayList<>();
                for (Chapter b : kursus.getDaftarBab()) {
                    if (b.getNomor() == babTarget.getNomor()) {
                        List<Material> materiBaru = new ArrayList<>(b.getDaftarMateri());
                        materiBaru.add(mu);
                        daftarBab.add(new Chapter(b.getNomor(), b.getJudul(), materiBaru));
                    } else {
                        daftarBab.add(b);
                    }
                }
                simpanKursus(kursus.denganDaftarBab(daftarBab));
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                tampilkanBab(kursus.getDaftarBab().get(babTarget.getNomor() - 1));
            }
        });
        barisTombol.getChildren().addAll(btnBatal, btnSimpan);
        VBox content = new VBox(12, rbBaru, formBaru, rbPilih, boxPilih);
        kartu.getChildren().addAll(barisHeader, new Separator(), content, new Separator(), barisTombol);
        pane.setContent(kartu);
        dialog.showAndWait();
    }

    private void handleBuatKuisBab(Chapter babTarget) {
        Dialog<Boolean> dialog = DialogUtil.buatDialogTransparan();
        DialogPane pane = dialog.getDialogPane();
        VBox kartu = DialogUtil.buatKartuModal(520, 540);
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);
        DialogUtil.pasangDragModal(dialog, barisHeader);
        VBox boxJudul = new VBox(2);
        HBox barisJudulBadge = new HBox(8);
        barisJudulBadge.setAlignment(Pos.CENTER_LEFT);
        Label lblJudulModal = new Label("Buat Kuis Bab");
        lblJudulModal.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label badgeBab = new Label("Bab " + babTarget.getNomor());
        badgeBab.getStyleClass().add("badge");
        barisJudulBadge.getChildren().addAll(lblJudulModal, badgeBab);
        Label lblSub = new Label("Buat kuis pemahaman baru di " + babTarget.getJudul());
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(barisJudulBadge, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);
        Button btnTutup = new Button();
        btnTutup.setGraphic(new IconView(Icon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });
        barisHeader.getChildren().addAll(boxJudul, btnTutup);
        int counter = 1;
        String kodeKuis = "QZ-" + String.format("%02d", counter);
        while (session.getUjianService().cariUjian(kodeKuis).isPresent()) {
            counter++;
            kodeKuis = "QZ-" + String.format("%02d", counter);
        }
        TextField tfKode = new TextField(kodeKuis);
        TextField tfJudul = new TextField("Kuis " + babTarget.getJudul());
        TextField tfDurasi = new TextField("15");
        TextField tfKkm = new TextField("70");
        String saranToken = ("QZ" + (int) (Math.random() * 8999 + 1000)).toUpperCase();
        TextField tfToken = new TextField(saranToken);
        tfToken.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold;");
        HBox barisKodeJudul = new HBox(12);
        VBox vbKode = new VBox(5, new Label("Kode Kuis"), tfKode);
        vbKode.setPrefWidth(150);
        VBox vbJudulKuis = new VBox(5, new Label("Judul Kuis"), tfJudul);
        HBox.setHgrow(vbJudulKuis, Priority.ALWAYS);
        barisKodeJudul.getChildren().addAll(vbKode, vbJudulKuis);
        HBox barisDurasiKkmToken = new HBox(12);
        VBox vbDurasi = new VBox(5, new Label("Durasi (menit)"), tfDurasi);
        HBox.setHgrow(vbDurasi, Priority.ALWAYS);
        VBox vbKkm = new VBox(5, new Label("KKM"), tfKkm);
        HBox.setHgrow(vbKkm, Priority.ALWAYS);
        VBox vbToken = new VBox(5, new Label("Token Akses"), tfToken);
        HBox.setHgrow(vbToken, Priority.ALWAYS);
        barisDurasiKkmToken.getChildren().addAll(vbDurasi, vbKkm, vbToken);
        VBox boxJadwal = new VBox(8);
        boxJadwal.getStyleClass().add("modal-kotak-jadwal");
        Label lblJadwal = new Label("Jadwal Akses Kuis (Opsional)");
        lblJadwal.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        DateTimePicker pemilihMulai = new DateTimePicker(null, true);
        DateTimePicker pemilihSelesai = new DateTimePicker(null, true);
        VBox vbMulai = new VBox(4, new Label("Waktu Buka"), pemilihMulai);
        VBox vbSelesai = new VBox(4, new Label("Waktu Tutup"), pemilihSelesai);
        boxJadwal.getChildren().addAll(lblJadwal, vbMulai, vbSelesai);
        CheckBox cbKuisBolehUlang = new CheckBox("Izinkan kuis dikerjakan ulang");
        cbKuisBolehUlang.setSelected(false);
        VBox formBox = new VBox(12, barisKodeJudul, barisDurasiKkmToken, boxJadwal, cbKuisBolehUlang);
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);
        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });
        Button btnSimpan = new Button("Buat & Rancang Soal Kuis");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setDefaultButton(true);
        btnSimpan.setOnAction(e -> {
            String kode = tfKode.getText().trim().toUpperCase();
            String judul = tfJudul.getText().trim();
            String token = tfToken.getText().trim().toUpperCase();
            int durasi;
            double kkm;
            try {
                durasi = Integer.parseInt(tfDurasi.getText().trim());
                kkm = Double.parseDouble(tfKkm.getText().trim());
            } catch (NumberFormatException ex) {
                DialogUtil.peringatan("Durasi dan KKM harus berupa angka");
                return;
            }
            if (StringUtil.isKosong(kode) || StringUtil.isKosong(judul) || StringUtil.isKosong(token)) {
                DialogUtil.peringatan("Kode, Judul, dan Token wajib diisi");
                return;
            }
            if (!kode.matches("^[A-Z]{2,5}-\\d{2}$")) {
                DialogUtil.peringatan("Format Kode Kuis harus 2-5 huruf diikuti '-' dan 2 angka (contoh: QZ-01)");
                return;
            }
            if (token.length() != 6 || !token.matches("^[A-Z0-9]{6}$")) {
                DialogUtil.peringatan("Token kuis harus tepat 6 huruf/angka");
                return;
            }
            if (session.getUjianService().cariUjian(kode).isPresent()) {
                DialogUtil.peringatan("Kode kuis sudah terpakai: " + kode);
                return;
            }
            LocalDateTime mulai = pemilihMulai.getWaktu();
            LocalDateTime selesai = pemilihSelesai.getWaktu();
            if (mulai != null && selesai != null && mulai.isAfter(selesai)) {
                DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup");
                return;
            }
            try {
                Question sampleSoal = new MultipleChoiceQuestion(1, "Pertanyaan nomor 1 untuk " + judul + "...", 10.0,
                        List.of("Pilihan A", "Pilihan B", "Pilihan C", "Pilihan D"), 'A');
                Exam ujianKuis = new Exam(kode, judul, token, durasi, kkm, List.of(sampleSoal), cbKuisBolehUlang.isSelected(), mulai, selesai);
                session.getUjianService().tambahUjian(ujianKuis);
                String idMateri = (kursus.getKode().toLowerCase() + "-qz" + (System.currentTimeMillis() % 10000));
                QuizMaterial mq = new QuizMaterial(idMateri, judul, kode);
                List<Chapter> daftarBab = new ArrayList<>();
                for (Chapter b : kursus.getDaftarBab()) {
                    if (b.getNomor() == babTarget.getNomor()) {
                        List<Material> materiBaru = new ArrayList<>(b.getDaftarMateri());
                        materiBaru.add(mq);
                        daftarBab.add(new Chapter(b.getNomor(), b.getJudul(), materiBaru));
                    } else {
                        daftarBab.add(b);
                    }
                }
                simpanKursus(kursus.denganDaftarBab(daftarBab));
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                session.setUjianAktif(ujianKuis);
                session.setMateriFokus(mq);
                SceneRouter.pindah(AppScreen.EDITOR_UJIAN);
            } catch (Exception ex) {
                DialogUtil.peringatan("Gagal membuat kuis baru: " + ex.getMessage());
            }
        });
        barisTombol.getChildren().addAll(btnBatal, btnSimpan);
        kartu.getChildren().addAll(barisHeader, new Separator(), formBox, new Separator(), barisTombol);
        pane.setContent(kartu);
        dialog.showAndWait();
    }

    private void bukaEditorMateri(Chapter bab, TextMaterial materiLama) {
        session.setKursusAktif(kursus);
        session.setBabAktifMateri(bab);
        session.setMateriTeksAktif(materiLama);
        SceneRouter.pindah(AppScreen.EDITOR_MATERI);
    }

    private void simpanKursus(Course baru) {
        this.kursus = baru;
        try {
            kursusService.perbaruiKursus(baru);
        } catch (IOException e) {
            DialogUtil.peringatan("Gagal menyimpan perubahan kursus: " + e.getMessage());
        }
        session.setKursusAktif(baru);
        buatDaftarBab();
        perbaruiProgres();
    }

    // ------------------------------------------------------------------ helpers UI
    private Node tombolKembaliKeBab(Material materi) {
        Chapter bab = babDari(materi).orElseThrow();
        Button tombol = tombol(bab.getJudul(), Icon.PANAH_KIRI, "tombol-hantu");
        tombol.setOnAction(e -> tampilkanBab(bab));
        return tombol;
    }

    private static VBox panel() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(28, 36, 32, 36));
        return box;
    }

    private static ScrollPane gulir(Node isi) {
        ScrollPane sp = new ScrollPane(isi);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private static Label label(String teks, String... styleClasses) {
        Label label = new Label(teks);
        label.getStyleClass().addAll(styleClasses);
        label.setWrapText(true);
        return label;
    }

    private static Button tombol(String teks, Icon ikon, String styleClass) {
        Button b = new Button(teks);
        b.getStyleClass().add(styleClass);
        if (ikon != null) {
            b.setGraphic(new IconView(ikon, 16));
        }
        return b;
    }

    private static VBox kotakInfo(String judul, String nilai) {
        VBox box = new VBox(2, label(judul, "teks-kecil"), label(nilai, "judul-kartu"));
        box.getStyleClass().add("kartu-mini");
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private static Node ikonMateri(Material materi, double ukuran) {
        Icon ikon = switch (materi.getJenis()) {
            case TEKS -> Icon.DOKUMEN;
            case LINK -> Icon.TAUTAN;
            case PDF -> Icon.PDF;
            case UJIAN -> Icon.UJIAN;
            case QUIZ -> Icon.BINTANG;
            case TUGAS -> Icon.DOKUMEN;
        };
        return new IconView(ikon, ukuran);
    }

    // ------------------------------------------------------------------ tab orang
    // (Google Classroom style)
    private void muatTabOrang() {
        vbTabOrang.getChildren().clear();
        vbTabOrang.setSpacing(24);
        vbTabOrang.setPadding(new Insets(24, 32, 32, 32));
        // 1. Banner Info Kode Kursus
        HBox bannerKode = new HBox(14);
        bannerKode.setAlignment(Pos.CENTER_LEFT);
        bannerKode.setStyle("-fx-background-color: -garis; -fx-background-radius: 14; -fx-padding: 12 18;");
        VBox vbInfoKode = new VBox(2);
        HBox.setHgrow(vbInfoKode, Priority.ALWAYS);
        Label lblInfoTeks = new Label("Kode Kursus untuk Bergabung:");
        lblInfoTeks.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        HBox barisKode = new HBox(8);
        barisKode.setAlignment(Pos.CENTER_LEFT);
        Label lblNilaiKode = new Label(kursus.getKode());
        lblNilaiKode.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -aksen; -fx-font-family: 'Consolas', monospace;");
        Label lblPetunjuk = new Label("· Bagikan kode ini agar siswa dapat masuk ke kelas");
        lblPetunjuk.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        barisKode.getChildren().addAll(lblNilaiKode, lblPetunjuk);
        vbInfoKode.getChildren().addAll(lblInfoTeks, barisKode);
        Button btnSalin = new Button("Salin Kode");
        btnSalin.getStyleClass().add("tombol-sekunder");
        btnSalin.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(kursus.getKode());
            Clipboard.getSystemClipboard().setContent(content);
            btnSalin.setText("Tersalin!");
        });
        bannerKode.getChildren().addAll(vbInfoKode, btnSalin);
        vbTabOrang.getChildren().add(bannerKode);
        // 2. Seksi PENGGAJAR (Guru)
        VBox boxPengajar = new VBox(12);
        HBox headerPengajar = new HBox(10);
        headerPengajar.setAlignment(Pos.CENTER_LEFT);
        Label lblJudulPengajar = new Label("Pengajar");
        lblJudulPengajar.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -aksen;");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label lblHitungPengajar = new Label("1 pengajar");
        lblHitungPengajar.setStyle("-fx-font-size: 13px; -fx-text-fill: -teks-redup; -fx-font-weight: bold;");
        headerPengajar.getChildren().addAll(lblJudulPengajar, spacer1, lblHitungPengajar);
        Region garisBawahPengajar = new Region();
        garisBawahPengajar.setStyle("-fx-background-color: -aksen; -fx-min-height: 2px; -fx-max-height: 2px;");
        VBox daftarItemPengajar = new VBox(8);
        // Baris Pengajar Utama
        HBox barisGuru = new HBox(14);
        barisGuru.setAlignment(Pos.CENTER_LEFT);
        barisGuru.setStyle("-fx-padding: 10 14; -fx-background-radius: 12;");
        String namaGuru = kursus.getPengajar();
        String inisialGuru = hitungInisial(namaGuru);
        Label avatarGuru = new Label(inisialGuru);
        avatarGuru.getStyleClass().add("avatar");
        avatarGuru.setStyle(
                "-fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px; -fx-font-size: 15px;");
        VBox vbTeksGuru = new VBox(2);
        HBox.setHgrow(vbTeksGuru, Priority.ALWAYS);
        Label lblNamaGuru = new Label(namaGuru);
        lblNamaGuru.setStyle("-fx-font-size: 14.5px; -fx-font-weight: bold; -fx-text-fill: -teks;");
        Optional<Teacher> optGuru = session.getAuthService().getSemuaGuru().stream()
                .filter(g -> g.getNama().equalsIgnoreCase(namaGuru) || namaGuru.contains(g.getNama())
                        || g.getNama().contains(namaGuru))
                .findFirst();
        String subGuru = optGuru.map(g -> "Guru Pengampu • NIP " + g.getNip()).orElse("Guru Pengampu Kursus");
        Label lblSubGuru = new Label(subGuru);
        lblSubGuru.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        vbTeksGuru.getChildren().addAll(lblNamaGuru, lblSubGuru);
        Label badgeGuru = new Label("Pengajar Utama");
        badgeGuru.getStyleClass().addAll("badge", "badge-sukses");
        barisGuru.getChildren().addAll(avatarGuru, vbTeksGuru, badgeGuru);
        daftarItemPengajar.getChildren().add(barisGuru);
        boxPengajar.getChildren().addAll(headerPengajar, garisBawahPengajar, daftarItemPengajar);
        vbTabOrang.getChildren().add(boxPengajar);
        // 3. Seksi SISWA / TEMAN SEKELAS
        VBox boxSiswa = new VBox(12);
        HBox headerSiswa = new HBox(10);
        headerSiswa.setAlignment(Pos.CENTER_LEFT);
        String judulSeksiSiswa = session.isGuru() ? "Siswa" : "Teman Sekelas";
        Label lblJudulSiswa = new Label(judulSeksiSiswa);
        lblJudulSiswa.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -aksen;");
        List<Student> semuaSiswa = session.getAuthService().getSemuaSiswa().stream()
                .filter(s -> session.getPendaftaranService().isTerdaftar(s, kursus))
                .toList();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        Label lblHitungSiswa = new Label(semuaSiswa.size() + " siswa");
        lblHitungSiswa.setStyle("-fx-font-size: 13px; -fx-text-fill: -teks-redup; -fx-font-weight: bold;");
        headerSiswa.getChildren().addAll(lblJudulSiswa, spacer2, lblHitungSiswa);
        Region garisBawahSiswa = new Region();
        garisBawahSiswa.setStyle("-fx-background-color: -aksen; -fx-min-height: 2px; -fx-max-height: 2px;");
        VBox daftarItemSiswa = new VBox(4);
        if (semuaSiswa.isEmpty()) {
            Label lblKosong = new Label("Belum ada siswa yang bergabung.");
            lblKosong.setStyle("-fx-text-fill: -teks-redup; -fx-padding: 16 0;");
            daftarItemSiswa.getChildren().add(lblKosong);
        } else {
            for (Student s : semuaSiswa) {
                HBox barisS = new HBox(14);
                barisS.setAlignment(Pos.CENTER_LEFT);
                barisS.setStyle(
                        "-fx-padding: 10 14; -fx-background-radius: 10; -fx-border-color: transparent transparent -garis transparent; -fx-border-width: 0 0 1 0;");
                String inisialS = hitungInisial(s.getNama());
                Label avatarS = new Label(inisialS);
                avatarS.setStyle(
                        "-fx-min-width: 38px; -fx-min-height: 38px; -fx-max-width: 38px; -fx-max-height: 38px; "
                                + "-fx-background-radius: 999; -fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 13px; "
                                + "-fx-background-color: rgba(26, 163, 232, 0.16); -fx-text-fill: -aksen;");
                VBox vbTeksS = new VBox(2);
                HBox.setHgrow(vbTeksS, Priority.ALWAYS);
                Label lblNamaS = new Label(s.getNama());
                lblNamaS.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: -teks;");
                Label lblSubS = new Label("Kelas " + s.getKelas() + " • NIS " + s.getNis());
                lblSubS.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
                vbTeksS.getChildren().addAll(lblNamaS, lblSubS);
                if (session.isSiswa() && s.equals(session.getSiswaAktif())) {
                    Label badgeKamu = new Label("Kamu");
                    badgeKamu.getStyleClass().addAll("badge", "badge-kaca");
                    barisS.getChildren().addAll(avatarS, vbTeksS, badgeKamu);
                } else if (session.isGuru()) {
                    double p = progresService.getProgres(s, kursus);
                    int jmlSelesai = progresService.getJumlahSelesai(s, kursus);
                    int totalMateri = kursus.getJumlahMateri();
                    Label badgeProgres = new Label();
                    if (p >= 1.0) {
                        badgeProgres.setText("Selesai (100%)");
                        badgeProgres.getStyleClass().addAll("badge", "badge-sukses");
                    } else if (p > 0) {
                        badgeProgres.setText(jmlSelesai + "/" + totalMateri + " materi (" + (int) (p * 100) + "%)");
                        badgeProgres.getStyleClass().addAll("badge");
                    } else {
                        badgeProgres.setText("Belum mulai");
                        badgeProgres.getStyleClass().addAll("badge", "badge-netral");
                    }
                    barisS.getChildren().addAll(avatarS, vbTeksS, badgeProgres);
                } else {
                    barisS.getChildren().addAll(avatarS, vbTeksS);
                }
                daftarItemSiswa.getChildren().add(barisS);
            }
        }
        boxSiswa.getChildren().addAll(headerSiswa, garisBawahSiswa, daftarItemSiswa);
        vbTabOrang.getChildren().add(boxSiswa);
    }

    private String hitungInisial(String nama) {
        if (nama == null || nama.trim().isEmpty())
            return "?";
        String[] kata = nama.trim().split("\\s+");
        if (kata.length == 1) {
            return kata[0].substring(0, Math.min(2, kata[0].length())).toUpperCase();
        }
        return ("" + kata[0].charAt(0) + kata[1].charAt(0)).toUpperCase();
    }

    @FXML
    private void handleUbahKode() {
        Dialog<Boolean> dialog = DialogUtil.buatDialogTransparan();
        DialogPane pane = dialog.getDialogPane();
        VBox kartu = DialogUtil.buatKartuModal(450, 470);
        // Header Modal (Bisa di-drag)
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);
        DialogUtil.pasangDragModal(dialog, barisHeader);
        StackPane kotakIkon = new StackPane(new IconView(Icon.PENSIL, 18));
        kotakIkon.getStyleClass().add("kotak-ikon-stat");
        kotakIkon.setMinSize(42, 42);
        kotakIkon.setMaxSize(42, 42);
        VBox boxJudul = new VBox(2);
        HBox barisJudulBadge = new HBox(8);
        barisJudulBadge.setAlignment(Pos.CENTER_LEFT);
        Label lblJudul = new Label("Ubah Kode Kursus");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label badgeKodeLama = new Label(kursus.getKode());
        badgeKodeLama.getStyleClass().add("badge");
        barisJudulBadge.getChildren().addAll(lblJudul, badgeKodeLama);
        Label lblSub = new Label("Perbarui kode akses kursus '" + kursus.getNama() + "'.");
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(barisJudulBadge, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);
        Button btnTutup = new Button();
        btnTutup.setGraphic(new IconView(Icon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setTooltip(new Tooltip("Tutup (ESC)"));
        btnTutup.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });
        barisHeader.getChildren().addAll(boxJudul, btnTutup);
        // Info Kode Saat Ini + Tombol Salin
        HBox kotakKodeSaatIni = new HBox(12);
        kotakKodeSaatIni.getStyleClass().add("modal-kotak-jadwal");
        kotakKodeSaatIni.setAlignment(Pos.CENTER_LEFT);
        Node polaThumb = CoursePattern.buat(kursus.getKode(), 40, 40, 10);
        VBox infoSaatIni = new VBox(2);
        Label lblKetSaatIni = new Label("Kode Akses Saat Ini");
        lblKetSaatIni.getStyleClass().add("teks-redup");
        lblKetSaatIni.setStyle("-fx-font-size: 11.5px;");
        Label lblNilaiSaatIni = new Label(kursus.getKode());
        lblNilaiSaatIni.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold; -fx-font-size: 15px;");
        infoSaatIni.getChildren().addAll(lblKetSaatIni, lblNilaiSaatIni);
        HBox.setHgrow(infoSaatIni, Priority.ALWAYS);
        Button btnSalin = new Button("Salin");
        btnSalin.getStyleClass().add("tombol-sekunder");
        btnSalin.setTooltip(new Tooltip("Salin kode akses saat ini"));
        btnSalin.setOnAction(e -> {
            ClipboardContent cc = new ClipboardContent();
            cc.putString(kursus.getKode());
            Clipboard.getSystemClipboard().setContent(cc);
            btnSalin.setText("Tersalin!");
        });
        kotakKodeSaatIni.getChildren().addAll(polaThumb, infoSaatIni, btnSalin);
        // Input Kode Baru
        VBox boxInput = new VBox(6);
        Label lblField = new Label("Kode Baru");
        lblField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextField tfKodeBaru = new TextField(kursus.getKode());
        tfKodeBaru.setPromptText("Masukkan kode akses baru...");
        tfKodeBaru.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold; -fx-font-size: 14px;");
        HBox.setHgrow(tfKodeBaru, Priority.ALWAYS);
        Button btnAcak = new Button("Acak");
        btnAcak.getStyleClass().add("tombol-sekunder");
        btnAcak.setTooltip(new Tooltip("Buat kode acak 6 karakter"));
        btnAcak.setOnAction(e -> tfKodeBaru.setText(hasilkanKodeKursusAcak()));
        HBox barisInput = new HBox(8, tfKodeBaru, btnAcak);
        barisInput.setAlignment(Pos.CENTER_LEFT);
        Label lblCatatan = new Label("Huruf besar dan kecil berpengaruh (case-sensitive). Siswa baru wajib memasukkan kode ini.");
        lblCatatan.getStyleClass().add("teks-redup");
        lblCatatan.setStyle("-fx-font-size: 11.5px;");
        lblCatatan.setWrapText(true);
        Label lblGalat = new Label();
        lblGalat.setWrapText(true);
        lblGalat.setMaxWidth(Double.MAX_VALUE);
        lblGalat.setStyle("-fx-text-fill: -bahaya; -fx-background-color: rgba(239, 68, 68, 0.1); "
                + "-fx-background-radius: 10; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblGalat.setVisible(false);
        lblGalat.setManaged(false);
        tfKodeBaru.textProperty().addListener((obs, lama, baru) -> {
            if (lblGalat.isVisible()) {
                lblGalat.setVisible(false);
                lblGalat.setManaged(false);
            }
        });
        boxInput.getChildren().addAll(lblField, barisInput, lblCatatan, lblGalat);
        // Footer Tombol
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);
        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });
        Button btnSimpan = new Button("Simpan Kode");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setDefaultButton(true);
        btnSimpan.setOnAction(e -> {
            String kodeBaru = tfKodeBaru.getText() != null ? tfKodeBaru.getText().trim() : "";
            if (kodeBaru.isEmpty()) {
                lblGalat.setText("Kode kursus tidak boleh kosong.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKodeBaru.requestFocus();
                return;
            }
            if (kodeBaru.equals(kursus.getKode())) {
                dialog.setResult(Boolean.FALSE);
                dialog.close();
                return;
            }
            try {
                String kodeLama = kursus.getKode();
                Course baru = kursusService.ubahKodeKursus(kursus, kodeBaru);
                session.getPendaftaranService().perbaruiKodeKursus(kodeLama, baru.getKode());
                this.kursus = baru;
                session.setKursusAktif(baru);
                lblKode.setText(baru.getKode());
                spPola.getChildren().setAll(CoursePattern.buat(baru.getKode(), 84, 84, 20));
                muatTabOrang();
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                DialogUtil.info("Kode kursus berhasil diubah menjadi: " + baru.getKode());
            } catch (Exception ex) {
                lblGalat.setText(ex.getMessage() != null ? ex.getMessage() : "Gagal mengubah kode kursus.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKodeBaru.requestFocus();
                tfKodeBaru.selectAll();
            }
        });
        barisTombol.getChildren().addAll(btnBatal, btnSimpan);
        kartu.getChildren().addAll(barisHeader, new Separator(), kotakKodeSaatIni, boxInput, new Separator(), barisTombol);
        pane.setContent(kartu);
        Platform.runLater(() -> {
            tfKodeBaru.requestFocus();
            tfKodeBaru.selectAll();
        });
        dialog.showAndWait();
    }

    private static final String KARAKTER_KODE_KURSUS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM_KODE_KURSUS = new SecureRandom();
    private static String hasilkanKodeKursusAcak() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(KARAKTER_KODE_KURSUS.charAt(RANDOM_KODE_KURSUS.nextInt(KARAKTER_KODE_KURSUS.length())));
        }
        return sb.toString();
    }

    @FXML
    private void handleKembali() {
        SceneRouter.pindah(AppScreen.KURSUS_SAYA);
    }

    // ------------------------------------------------------------------ tugas
    private void handleTambahTugas(Chapter bab) {
        handleEditTugas(bab, null);
    }

    private void handleEditTugas(Chapter bab, AssignmentMaterial tugasEdit) {
        Dialog<AssignmentMaterial> dialog = DialogUtil.buatDialogTransparan();
        DialogPane pane = dialog.getDialogPane();
        VBox kartu = DialogUtil.buatKartuModal(520, 540);
        // 1. Header Modal tanpa ikon dekoratif
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);
        DialogUtil.pasangDragModal(dialog, barisHeader);
        VBox boxJudul = new VBox(2);
        HBox barisJudulBadge = new HBox(8);
        barisJudulBadge.setAlignment(Pos.CENTER_LEFT);
        Label lblJudulModal = new Label(tugasEdit == null ? "Tambah Tugas Baru" : "Edit Tugas");
        lblJudulModal.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label badgeBab = new Label("Bab " + bab.getNomor());
        badgeBab.getStyleClass().add("badge");
        barisJudulBadge.getChildren().addAll(lblJudulModal, badgeBab);
        Label lblSub = new Label(tugasEdit == null
                ? ("Buat penugasan siswa untuk " + bab.getJudul() + " · " + kursus.getNama())
                : ("Perbarui detail tugas pada " + bab.getJudul()));
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(barisJudulBadge, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);
        Button btnTutup = new Button();
        btnTutup.setGraphic(new IconView(Icon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setTooltip(new Tooltip("Tutup (ESC)"));
        btnTutup.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
        });
        barisHeader.getChildren().addAll(boxJudul, btnTutup);
        // 2. Judul Tugas & Target Kelas (tanpa chip pintasan)
        VBox boxJudulTugas = new VBox(6);
        Label lblJudulField = new Label("Judul Tugas");
        lblJudulField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextField tfJudul = new TextField(tugasEdit != null ? tugasEdit.getJudul() : "");
        tfJudul.setPromptText("Contoh: Tugas 1 - Analisis Kata Baku dalam Artikel");
        boxJudulTugas.getChildren().addAll(lblJudulField, tfJudul);
        VBox boxTargetKelas = new VBox(6);
        HBox headerKelas = new HBox(8);
        headerKelas.setAlignment(Pos.CENTER_LEFT);
        Label lblKelasField = new Label("Target Kelas");
        lblKelasField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        Label lblInfoKelas = new Label("· Pilih kelas yang dapat mengakses dan mengumpulkan tugas");
        lblInfoKelas.getStyleClass().add("teks-redup");
        lblInfoKelas.setStyle("-fx-font-size: 11.5px;");
        headerKelas.getChildren().addAll(lblKelasField, lblInfoKelas);
        ComboBox<String> cbKelas = new ComboBox<>();
        cbKelas.getItems().add("Semua Kelas");
        List<String> daftarKelasSiswa = session.getAuthService().getSemuaSiswa().stream()
                .map(Student::getKelas)
                .filter(k -> k != null && !k.isBlank())
                .distinct()
                .sorted()
                .toList();
        cbKelas.getItems().addAll(daftarKelasSiswa);
        cbKelas.setEditable(true);
        cbKelas.setMaxWidth(Double.MAX_VALUE);
        cbKelas.setValue(tugasEdit != null ? tugasEdit.getTargetKelas() : "Semua Kelas");
        boxTargetKelas.getChildren().addAll(headerKelas, cbKelas);
        // 3. Batas Waktu Pengumpulan (Deadline) terpadu dalam satu pemilih Tanggal & Waktu tanpa ikon/chip
        VBox kotakDeadline = new VBox(8);
        kotakDeadline.getStyleClass().add("modal-kotak-jadwal");
        Label lblDeadlineJudul = new Label("Batas Waktu Pengumpulan (Deadline)");
        lblDeadlineJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        LocalDateTime deadlineAwal = (tugasEdit != null && tugasEdit.getDeadline() != null)
                ? tugasEdit.getDeadline()
                : LocalDateTime.of(LocalDate.now().plusDays(7), LocalTime.of(23, 59));
        DateTimePicker pemilihDeadline = new DateTimePicker(deadlineAwal, false);
        kotakDeadline.getChildren().addAll(lblDeadlineJudul, pemilihDeadline);
        // 4. Deskripsi / Instruksi Tugas
        VBox boxDeskripsi = new VBox(6);
        Label lblDeskripsiField = new Label("Deskripsi & Petunjuk Pengerjaan");
        lblDeskripsiField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextArea taDeskripsi = new TextArea(tugasEdit != null ? tugasEdit.getDeskripsi() : "");
        taDeskripsi.setPromptText("Tuliskan petunjuk pengerjaan tugas, format berkas yang diminta (.pdf/.docx), atau kriteria penilaian...");
        taDeskripsi.setPrefRowCount(4);
        taDeskripsi.setWrapText(true);
        boxDeskripsi.getChildren().addAll(lblDeskripsiField, taDeskripsi);
        // Pesan Validasi Inline
        Label lblGalat = new Label();
        lblGalat.setWrapText(true);
        lblGalat.setMaxWidth(Double.MAX_VALUE);
        lblGalat.setStyle("-fx-text-fill: -bahaya; -fx-background-color: rgba(239, 68, 68, 0.1); "
                + "-fx-background-radius: 10; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblGalat.setVisible(false);
        lblGalat.setManaged(false);
        tfJudul.textProperty().addListener((obs, lama, baru) -> {
            if (lblGalat.isVisible()) {
                lblGalat.setVisible(false);
                lblGalat.setManaged(false);
            }
        });
        // 5. Footer Tombol Aksi
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);
        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
        });
        Button btnSimpan = new Button(tugasEdit == null ? "Simpan Tugas" : "Simpan Perubahan");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setOnAction(e -> {
            String judul = tfJudul.getText() != null ? tfJudul.getText().trim() : "";
            if (judul.isEmpty()) {
                lblGalat.setText("Judul tugas wajib diisi terlebih dahulu.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfJudul.requestFocus();
                return;
            }
            LocalDateTime deadline = pemilihDeadline.getWaktu() != null
                    ? pemilihDeadline.getWaktu()
                    : LocalDateTime.of(LocalDate.now().plusDays(7), LocalTime.of(23, 59));
            String targetKelas = cbKelas.getEditor() != null && !cbKelas.getEditor().getText().isBlank()
                    ? cbKelas.getEditor().getText().trim()
                    : cbKelas.getValue();
            if (targetKelas == null || targetKelas.isBlank()) {
                targetKelas = "Semua Kelas";
            }
            String deskripsi = taDeskripsi.getText() != null ? taDeskripsi.getText().trim() : "";
            String id = tugasEdit != null ? tugasEdit.getIdMateri() : "tgs-" + (System.currentTimeMillis() % 100000);
            dialog.setResult(new AssignmentMaterial(id, judul, deadline, targetKelas, deskripsi));
            dialog.close();
        });
        barisTombol.getChildren().addAll(btnBatal, btnSimpan);
        kartu.getChildren().addAll(
                barisHeader,
                new Separator(),
                boxJudulTugas,
                boxTargetKelas,
                kotakDeadline,
                boxDeskripsi,
                lblGalat,
                new Separator(),
                barisTombol
        );
        pane.setContent(kartu);
        Platform.runLater(tfJudul::requestFocus);
        Optional<AssignmentMaterial> hasil = dialog.showAndWait();
        hasil.ifPresent(tugasBaru -> {
            List<Chapter> daftarBabBaru = new ArrayList<>();
            for (Chapter b : kursus.getDaftarBab()) {
                if (b.getNomor() == bab.getNomor()) {
                    List<Material> materiBaru = new ArrayList<>(b.getDaftarMateri());
                    if (tugasEdit != null) {
                        int idx = materiBaru.indexOf(tugasEdit);
                        if (idx >= 0) {
                            materiBaru.set(idx, tugasBaru);
                        } else {
                            materiBaru.add(tugasBaru);
                        }
                    } else {
                        materiBaru.add(tugasBaru);
                    }
                    daftarBabBaru.add(new Chapter(b.getNomor(), b.getJudul(), materiBaru));
                } else {
                    daftarBabBaru.add(b);
                }
            }
            simpanKursus(kursus.denganDaftarBab(daftarBabBaru));
            tampilkanBab(kursus.getDaftarBab().get(bab.getNomor() - 1));
        });
    }

    private void tampilkanTugas(AssignmentMaterial tugas) {
        VBox isi = panel();
        Label label = new Label("TUGAS · " + tugas.getTargetKelas());
        label.getStyleClass().add("label-kecil-kapital");
        Label judul = new Label(tugas.getJudul());
        judul.getStyleClass().add("judul-halaman");
        judul.setWrapText(true);
        HBox barisBadge = new HBox(8);
        barisBadge.setAlignment(Pos.CENTER_LEFT);
        Label badgeKelas = label("Target: " + tugas.getTargetKelas(), "badge");
        badgeKelas.setStyle("-fx-background-color: -garis; -fx-text-fill: -teks;");
        Label badgeDeadline;
        if (tugas.isLewatDeadline()) {
            badgeDeadline = label("Batas Waktu Berakhir: " + tugas.getDeadlineFormatted(), "badge", "badge-bahaya");
        } else {
            badgeDeadline = label("Batas Waktu: " + tugas.getDeadlineFormatted(), "badge", "badge-sukses");
        }
        barisBadge.getChildren().addAll(badgeKelas, badgeDeadline);
        isi.getChildren().addAll(label, judul, barisBadge);
        // Deskripsi tugas
        VBox boxDeskripsi = new VBox(8);
        boxDeskripsi.setStyle("-fx-padding: 18; -fx-background-color: -garis; -fx-background-radius: 12;");
        Label lblJudulDeskripsi = new Label("Deskripsi / Petunjuk Tugas");
        lblJudulDeskripsi.getStyleClass().add("judul-kartu");
        Label lblIsiDeskripsi = new Label(tugas.getDeskripsi().isEmpty() ? "Tidak ada petunjuk tambahan dari guru." : tugas.getDeskripsi());
        lblIsiDeskripsi.setWrapText(true);
        lblIsiDeskripsi.getStyleClass().add("teks-redup");
        boxDeskripsi.getChildren().addAll(lblJudulDeskripsi, lblIsiDeskripsi);
        isi.getChildren().add(boxDeskripsi);
        // Tampilan SISWA
        if (session.isSiswa()) {
            if (!tugas.isBolehAkses(siswa)) {
                // Akses ditolak karena bukan kelasnya
                VBox boxDibatasi = new VBox(12);
                boxDibatasi.setAlignment(Pos.CENTER);
                boxDibatasi.setStyle("-fx-padding: 36 20; -fx-background-color: rgba(220, 53, 69, 0.08); -fx-background-radius: 14; -fx-border-color: rgba(220, 53, 69, 0.2); -fx-border-radius: 14;");
                Label ikonGembok = new Label();
                ikonGembok.setGraphic(new IconView(Icon.GEMBOK, 40));
                Label lblAksesDibatasi = new Label("Tugas Dibatasi");
                lblAksesDibatasi.getStyleClass().add("judul-kartu");
                Label lblKet = new Label("Tugas ini hanya dapat diakses oleh kelas: " + tugas.getTargetKelas()
                        + ".\nKelas Anda saat ini adalah: " + (siswa != null ? siswa.getKelas() : "-"));
                lblKet.setStyle("-fx-text-fill: -teks-redup; -fx-text-alignment: center;");
                boxDibatasi.getChildren().addAll(ikonGembok, lblAksesDibatasi, lblKet);
                isi.getChildren().add(boxDibatasi);
            } else {
                // Siswa berhak mengakses
                Optional<AssignmentSubmission> optPengumpulan = session.getTugasService().cariPengumpulan(tugas.getIdMateri(), siswa.getNis());
                VBox boxPengumpulan = new VBox(14);
                boxPengumpulan.setStyle("-fx-padding: 20; -fx-background-color: -latar-kartu; -fx-background-radius: 14; -fx-border-color: -garis; -fx-border-radius: 14;");
                if (optPengumpulan.isPresent()) {
                    AssignmentSubmission p = optPengumpulan.get();
                    tandai(tugas); // Tandai progres selesai
                    Label lblStatus = new Label("Sudah Dikumpulkan");
                    lblStatus.getStyleClass().addAll("badge", "badge-sukses");
                    Label lblWaktu = new Label("Waktu Pengumpulan: " + p.getWaktuKumpulFormatted());
                    lblWaktu.getStyleClass().add("teks-redup");
                    HBox barisBerkas = new HBox(10);
                    barisBerkas.setAlignment(Pos.CENTER_LEFT);
                    barisBerkas.setStyle("-fx-padding: 10 14; -fx-background-color: -garis; -fx-background-radius: 10;");
                    barisBerkas.getChildren().addAll(new IconView(Icon.DOKUMEN, 20), new Label(p.getNamaBerkas()));
                    HBox aksiSiswa = new HBox(10);
                    aksiSiswa.setAlignment(Pos.CENTER_LEFT);
                    Button btnBukaBerkas = new Button("Buka Berkas Saya");
                    btnBukaBerkas.getStyleClass().add("tombol-sekunder");
                    btnBukaBerkas.setGraphic(new IconView(Icon.MATA, 14));
                    btnBukaBerkas.setOnAction(e -> {
                        File f = new File(p.getPathBerkas());
                        if (f.exists()) {
                            session.bukaDokumen(f.toURI().toString());
                        } else {
                            DialogUtil.peringatan("Berkas tidak ditemukan pada direktori penyimpanan.");
                        }
                    });
                    aksiSiswa.getChildren().add(btnBukaBerkas);
                    if (!tugas.isLewatDeadline()) {
                        Button btnKumpulUlang = new Button("Kumpulkan Ulang");
                        btnKumpulUlang.getStyleClass().add("tombol-hantu");
                        btnKumpulUlang.setOnAction(e -> tampilkanFormUpload(boxPengumpulan, tugas, true));
                        aksiSiswa.getChildren().add(btnKumpulUlang);
                    }
                    boxPengumpulan.getChildren().addAll(lblStatus, lblWaktu, barisBerkas);
                    if (!p.getCatatan().isBlank()) {
                        Label lblCat = new Label("Catatan Anda: " + p.getCatatan());
                        lblCat.getStyleClass().add("teks-kecil");
                        boxPengumpulan.getChildren().add(lblCat);
                    }
                    boxPengumpulan.getChildren().add(aksiSiswa);
                } else {
                    if (tugas.isLewatDeadline()) {
                        Label lblTerlambat = new Label("Batas Waktu Pengumpulan Telah Berakhir");
                        lblTerlambat.getStyleClass().addAll("badge", "badge-bahaya");
                        Label lblTutup = new Label("Pengumpulan tugas ini sudah ditutup oleh pengajar.");
                        lblTutup.getStyleClass().add("teks-redup");
                        boxPengumpulan.getChildren().addAll(lblTerlambat, lblTutup);
                    } else {
                        tampilkanFormUpload(boxPengumpulan, tugas, false);
                    }
                }
                isi.getChildren().add(boxPengumpulan);
            }
        }
        // Tampilan GURU
        if (session.isGuru()) {
            HBox barisAksiGuru = new HBox(10);
            barisAksiGuru.setAlignment(Pos.CENTER_LEFT);
            Button btnEditTugas = new Button("Edit Tugas");
            btnEditTugas.getStyleClass().add("tombol-sekunder");
            btnEditTugas.setGraphic(new IconView(Icon.PENSIL, 14));
            btnEditTugas.setOnAction(e -> handleEditTugas(babDari(tugas).orElse(kursus.getDaftarBab().get(0)), tugas));
            Button btnHapusTugas = new Button("Hapus Tugas");
            btnHapusTugas.getStyleClass().addAll("tombol-sekunder", "tombol-bahaya");
            btnHapusTugas.setGraphic(new IconView(Icon.SAMPAH, 14));
            btnHapusTugas.setOnAction(e -> handleHapusMateri(tugas));
            barisAksiGuru.getChildren().addAll(btnEditTugas, btnHapusTugas);
            isi.getChildren().add(barisAksiGuru);
            // Daftar Pengumpulan Siswa
            VBox boxSubmissions = new VBox(12);
            boxSubmissions.setStyle("-fx-padding: 20; -fx-background-color: -latar-kartu; -fx-background-radius: 14; -fx-border-color: -garis; -fx-border-radius: 14;");
            List<AssignmentSubmission> daftarPengumpulan = session.getTugasService().getDaftarPengumpulan(tugas.getIdMateri());
            Label lblJudulSub = new Label("Daftar Pengumpulan Siswa (" + daftarPengumpulan.size() + " terkumpul)");
            lblJudulSub.getStyleClass().add("judul-kartu");
            boxSubmissions.getChildren().add(lblJudulSub);
            if (daftarPengumpulan.isEmpty()) {
                VBox boxKosong = new VBox(8);
                boxKosong.setAlignment(Pos.CENTER);
                boxKosong.setStyle("-fx-padding: 24 16;");
                Label lblIconKosong = new Label();
                lblIconKosong.setGraphic(new IconView(Icon.FOLDER_KOSONG, 32));
                Label lblTeksKosong = new Label("Belum ada siswa yang mengumpulkan tugas ini.");
                lblTeksKosong.getStyleClass().add("teks-redup");
                boxKosong.getChildren().addAll(lblIconKosong, lblTeksKosong);
                boxSubmissions.getChildren().add(boxKosong);
            } else {
                VBox tabel = new VBox(6);
                for (AssignmentSubmission p : daftarPengumpulan) {
                    HBox barisS = new HBox(12);
                    barisS.setAlignment(Pos.CENTER_LEFT);
                    barisS.setStyle("-fx-padding: 10 14; -fx-background-color: -garis; -fx-background-radius: 10;");
                    Label avatar = new Label(hitungInisial(p.getNamaSiswa()));
                    avatar.getStyleClass().add("avatar");
                    avatar.setStyle("-fx-min-width: 34px; -fx-min-height: 34px; -fx-max-width: 34px; -fx-max-height: 34px; -fx-font-size: 12px;");
                    VBox infoSiswa = new VBox(2);
                    HBox.setHgrow(infoSiswa, Priority.ALWAYS);
                    Label lblNama = new Label(p.getNamaSiswa());
                    lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px;");
                    String ket = "Kelas " + p.getKelasSiswa() + " · NIS " + p.getNis() + " · Dikumpulkan: " + p.getWaktuKumpulFormatted();
                    Label lblKet = new Label(ket);
                    lblKet.getStyleClass().add("teks-kecil");
                    infoSiswa.getChildren().addAll(lblNama, lblKet);
                    if (!p.getCatatan().isBlank()) {
                        Label lblCat = new Label("Catatan: " + p.getCatatan());
                        lblCat.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup; -fx-font-style: italic;");
                        infoSiswa.getChildren().add(lblCat);
                    }
                    Label namaBerkas = new Label(p.getNamaBerkas());
                    namaBerkas.setStyle("-fx-font-size: 12px; -fx-text-fill: -aksen;");
                    namaBerkas.setGraphic(new IconView(Icon.DOKUMEN, 14));
                    Button btnBuka = new Button("Buka Berkas");
                    btnBuka.getStyleClass().add("tombol-sekunder");
                    btnBuka.setGraphic(new IconView(Icon.UNDUH, 14));
                    btnBuka.setOnAction(e -> {
                        File f = new File(p.getPathBerkas());
                        if (f.exists()) {
                            session.bukaDokumen(f.toURI().toString());
                        } else {
                            DialogUtil.peringatan("Berkas tidak ditemukan pada direktori penyimpanan.");
                        }
                    });
                    barisS.getChildren().addAll(avatar, infoSiswa, namaBerkas, btnBuka);
                    tabel.getChildren().add(barisS);
                }
                boxSubmissions.getChildren().add(tabel);
            }
            isi.getChildren().add(boxSubmissions);
        }
        tampilkanKonten(gulir(isi));
    }

    private void tampilkanFormUpload(VBox container, AssignmentMaterial tugas, boolean kumpulUlang) {
        container.getChildren().clear();
        Label lblJudulUpload = new Label(kumpulUlang ? "Kumpulkan Ulang Berkas Tugas" : "Form Pengumpulan Tugas");
        lblJudulUpload.getStyleClass().add("judul-kartu");
        Label lblPetunjuk = new Label("Pilih berkas tugas Anda (mendukung format .pdf, .docx, .doc, .xlsx, .xls, .pptx, .ppt, .txt, .zip, dll).");
        lblPetunjuk.getStyleClass().add("teks-kecil");
        File[] berkasDipilih = new File[1];
        HBox barisPilih = new HBox(12);
        barisPilih.setAlignment(Pos.CENTER_LEFT);
        Button btnPilih = new Button("Pilih Berkas");
        btnPilih.getStyleClass().add("tombol-sekunder");
        btnPilih.setGraphic(new IconView(Icon.UNGGAH, 16));
        Label lblNamaFile = new Label("Belum ada berkas yang dipilih");
        lblNamaFile.getStyleClass().add("teks-redup");
        btnPilih.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Pilih Berkas Tugas");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Semua Dokumen yang Didukung (*.pdf, *.docx, *.xlsx, *.pptx, *.txt, *.zip)",
                            "*.pdf", "*.docx", "*.doc", "*.xlsx", "*.xls", "*.pptx", "*.ppt", "*.txt", "*.zip", "*.rar", "*.csv"),
                    new FileChooser.ExtensionFilter("Dokumen PDF (*.pdf)", "*.pdf"),
                    new FileChooser.ExtensionFilter("Dokumen Microsoft Word (*.docx, *.doc)", "*.docx", "*.doc"),
                    new FileChooser.ExtensionFilter("Dokumen Microsoft Excel (*.xlsx, *.xls)", "*.xlsx", "*.xls"),
                    new FileChooser.ExtensionFilter("Dokumen Microsoft PowerPoint (*.pptx, *.ppt)", "*.pptx", "*.ppt"),
                    new FileChooser.ExtensionFilter("Semua Berkas (*.*)", "*.*")
            );
            File f = chooser.showOpenDialog(container.getScene().getWindow());
            if (f != null) {
                berkasDipilih[0] = f;
                long kb = f.length() / 1024;
                lblNamaFile.setText(f.getName() + " (" + (kb > 1024 ? (kb / 1024) + " MB" : kb + " KB") + ")");
                lblNamaFile.setStyle("-fx-text-fill: -teks; -fx-font-weight: bold;");
            }
        });
        barisPilih.getChildren().addAll(btnPilih, lblNamaFile);
        TextField tfCatatan = new TextField();
        tfCatatan.setPromptText("Catatan opsional untuk pengajar...");
        HBox barisAksi = new HBox(10);
        barisAksi.setAlignment(Pos.CENTER_LEFT);
        Button btnKirim = new Button("Kumpulkan Tugas");
        btnKirim.getStyleClass().add("tombol-utama");
        btnKirim.setOnAction(e -> {
            if (berkasDipilih[0] == null) {
                DialogUtil.peringatan("Silakan pilih berkas tugas terlebih dahulu sebelum mengumpulkan.");
                return;
            }
            try {
                session.getTugasService().simpanPengumpulan(tugas.getIdMateri(), siswa, berkasDipilih[0], tfCatatan.getText());
                tandai(tugas);
                DialogUtil.info("Tugas berhasil dikumpulkan!");
                tampilkanTugas(tugas);
            } catch (Exception ex) {
                DialogUtil.peringatan("Gagal mengumpulkan tugas: " + ex.getMessage());
            }
        });
        barisAksi.getChildren().add(btnKirim);
        if (kumpulUlang) {
            Button btnBatal = new Button("Batal");
            btnBatal.getStyleClass().add("tombol-hantu");
            btnBatal.setOnAction(e -> tampilkanTugas(tugas));
            barisAksi.getChildren().add(btnBatal);
        }
        container.getChildren().addAll(lblJudulUpload, lblPetunjuk, barisPilih, tfCatatan, barisAksi);
    }
}
