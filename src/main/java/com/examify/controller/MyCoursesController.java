package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.Chapter;
import com.examify.model.Course;
import com.examify.model.Student;
import com.examify.service.LearningProgressService;
import com.examify.utils.StringUtil;
import com.examify.view.AnimationUtil;
import com.examify.view.CenteredGrid;
import com.examify.view.Icon;
import com.examify.view.IconView;
import com.examify.view.CourseCard;
import com.examify.view.CoursePattern;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MyCoursesController {

    private static final int MAKS_KOLOM = 4;

    private static final Duration DURASI_HITUNG = Duration.millis(900);

    /** Pilihan filter berdasarkan progres; label ditampilkan lewat toString. */
    private enum Filter {
        SEMUA("Semua kursus", p -> true),
        BELUM("Belum dimulai", p -> p == 0),
        BERJALAN("Sedang berjalan", p -> p > 0 && p < 1),
        SELESAI("Selesai", p -> p >= 1);

        private final String label;
        private final Predicate<Double> cocok;

        Filter(String label, Predicate<Double> cocok) {
            this.label = label;
            this.cocok = cocok;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private enum Urutan {
        NAMA("Urutkan: nama"),
        PROGRES_TERTINGGI("Urutkan: progres tertinggi"),
        PROGRES_TERENDAH("Urutkan: progres terendah");

        private final String label;

        Urutan(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @FXML private Label lblSapaan;
    @FXML private Label lblSubjudul;
    @FXML private Label lblJumlahKursus;
    @FXML private Label lblMateriSelesai;
    @FXML private Label lblUjianDikerjakan;
    @FXML private Label lblTile1;
    @FXML private Label lblTile2;
    @FXML private Label lblTile3;
    @FXML private TextField tfCari;
    @FXML private ComboBox<Filter> cbFilter;
    @FXML private ComboBox<Urutan> cbUrut;
    @FXML private Button btnAksiKhusus;
    @FXML private FlowPane fpKursus;
    @FXML private Label lblKosong;
    @FXML private Label lblJudulKursus;
    @FXML private VBox boxKursusLain;
    @FXML private Label lblJudulLain;
    @FXML private Button btnLihatSemua;
    @FXML private FlowPane fpKursusLain;
    @FXML private Label lblKosongLain;

    /** Batas kartu "Available Courses" sebelum tombol Lihat Semua dipakai (2 baris). */
    private static final int BATAS_KURSUS_LAIN = MAKS_KOLOM * 2;
    private boolean lihatSemuaLain = false;

    private final AppSession session = AppSession.getInstance();
    private final LearningProgressService progresService = session.getProgresService();
    private Student siswa;

    @FXML
    private void initialize() {
        CenteredGrid.terapkan(fpKursus, CourseCard.LEBAR, MAKS_KOLOM);
        CenteredGrid.terapkan(fpKursusLain, CourseCard.LEBAR, MAKS_KOLOM);
        siswa = session.getSiswaAktif();
        if (session.isGuru()) {
            lblSapaan.setText("Halo, " + session.getNamaPengguna().split(" ")[0] + " (Guru)!");
            lblSubjudul.setText("Kelola kursus, bab materi, dan bank soal ujian Anda.");
            lblTile1.setText("Total Kursus");
            lblTile2.setText("Total Materi");
            lblTile3.setText("Total Ujian");
            btnAksiKhusus.setText("+ Buat Kursus Baru");

            int jmlDiikuti = (int) session.getKursusService().getDaftarKursus().stream()
                    .filter(this::isSiswaTerdaftar)
                    .count();
            AnimationUtil.hitungNaik(lblJumlahKursus, jmlDiikuti, DURASI_HITUNG);
            AnimationUtil.hitungNaik(lblMateriSelesai, session.getKursusService().getJumlahMateriSemuaKursus(), DURASI_HITUNG);
            AnimationUtil.hitungNaik(lblUjianDikerjakan, session.getUjianService().getJumlahUjian(), DURASI_HITUNG);
        } else {
            lblSapaan.setText("Halo, " + (siswa != null ? siswa.getNama().split(" ")[0] : "Siswa") + "!");
            lblSubjudul.setText("Lanjutkan belajar dari kursus yang sedang kamu ikuti.");
            lblTile1.setText("Kursus diikuti");
            lblTile2.setText("Materi selesai");
            lblTile3.setText("Ujian dikerjakan");
            btnAksiKhusus.setText("Masuk lewat Kode");

            int jmlDiikuti = (int) session.getKursusService().getDaftarKursus().stream()
                    .filter(this::isSiswaTerdaftar)
                    .count();
            AnimationUtil.hitungNaik(lblJumlahKursus, jmlDiikuti, DURASI_HITUNG);
            AnimationUtil.hitungNaik(lblMateriSelesai, progresService.getTotalMateriSelesai(siswa), DURASI_HITUNG);
            AnimationUtil.hitungNaik(lblUjianDikerjakan, session.getRekapService().getRiwayat(siswa).size(), DURASI_HITUNG);
        }

        cbFilter.setItems(FXCollections.observableArrayList(Filter.values()));
        cbFilter.getSelectionModel().select(Filter.SEMUA);
        cbUrut.setItems(FXCollections.observableArrayList(Urutan.values()));
        cbUrut.getSelectionModel().select(Urutan.NAMA);

        tfCari.textProperty().addListener((obs, lama, baru) -> tampilkan());
        cbFilter.valueProperty().addListener((obs, lama, baru) -> tampilkan());
        cbUrut.valueProperty().addListener((obs, lama, baru) -> tampilkan());
        tampilkan();
    }

    private double progres(Course kursus) {
        if (siswa == null) {
            return 0.0;
        }
        return progresService.getProgres(siswa, kursus);
    }

    private void tampilkan() {
        Filter filter = cbFilter.getValue();
        List<Course> daftar = session.getKursusService().cari(tfCari.getText()).stream()
                .filter(kursus -> filter.cocok.test(progres(kursus)))
                .sorted(pembanding(cbUrut.getValue()))
                .toList();
        if (session.isGuru() || siswa == null) {
            List<CourseCard> kartu = daftar.stream().map(this::buatKartu).toList();
            fpKursus.getChildren().setAll(kartu);
            tampilkanLabel(lblKosong, daftar.isEmpty());
            AnimationUtil.berurutan(kartu, Duration.millis(60));
            return;
        }

        boolean sedangMencari = !StringUtil.isKosong(tfCari.getText()) || filter != Filter.SEMUA;
        List<Course> diikuti = daftar.stream().filter(this::isSiswaTerdaftar).toList();
        List<Course> lain = daftar.stream().filter(k -> !isSiswaTerdaftar(k)).toList();

        // Bagian atas: My Course
        lblJudulKursus.setText("My Course (" + diikuti.size() + ")");
        List<CourseCard> kartuDiikuti = diikuti.stream().map(this::buatKartu).toList();
        fpKursus.getChildren().setAll(kartuDiikuti);
        lblKosong.setText(sedangMencari
                ? "Tidak ada kursus yang kamu ikuti yang cocok."
                : "Kamu belum mengikuti kursus apa pun. Masukkan kode kursus atau pilih kursus di bawah.");
        tampilkanLabel(lblKosong, diikuti.isEmpty());

        // Bagian bawah: kursus yang belum diikuti
        boxKursusLain.setVisible(true);
        boxKursusLain.setManaged(true);
        lblJudulLain.setText("Available Courses (" + lain.size() + ")");
        boolean perluBatas = lain.size() > BATAS_KURSUS_LAIN;
        List<Course> lainTampil = (perluBatas && !lihatSemuaLain) ? lain.subList(0, BATAS_KURSUS_LAIN) : lain;
        List<CourseCard> kartuLain = lainTampil.stream().map(this::buatKartu).toList();
        fpKursusLain.getChildren().setAll(kartuLain);
        btnLihatSemua.setText(lihatSemuaLain ? "Tampilkan Lebih Sedikit" : "Lihat Semua");
        btnLihatSemua.setVisible(perluBatas);
        btnLihatSemua.setManaged(perluBatas);
        lblKosongLain.setText(sedangMencari
                ? "Tidak ada kursus lain yang cocok."
                : "Kamu sudah mengikuti semua kursus yang tersedia.");
        tampilkanLabel(lblKosongLain, lain.isEmpty());

        AnimationUtil.berurutan(kartuDiikuti, Duration.millis(60));
        AnimationUtil.berurutan(kartuLain, Duration.millis(60));
    }

    private static void tampilkanLabel(Label label, boolean tampil) {
        label.setVisible(tampil);
        label.setManaged(tampil);
    }

    @FXML
    private void handleLihatSemua() {
        lihatSemuaLain = !lihatSemuaLain;
        tampilkan();
    }

    private Comparator<Course> pembanding(Urutan urutan) {
        Comparator<Course> menurutProgres = Comparator.comparingDouble(this::progres);
        return switch (urutan) {
            case NAMA -> Comparator.comparing(Course::getNama);
            case PROGRES_TERTINGGI -> menurutProgres.reversed();
            case PROGRES_TERENDAH -> menurutProgres;
        };
    }

    private boolean isSiswaTerdaftar(Course kursus) {
        if (siswa == null) return true;
        return session.getPendaftaranService().isTerdaftar(siswa, kursus) || progres(kursus) > 0;
    }

    private CourseCard buatKartu(Course kursus) {
        double p = progres(kursus);
        boolean terdaftar = isSiswaTerdaftar(kursus);
        String labelTombol = session.isGuru() ? "Kelola Kursus"
                : (terdaftar ? (p > 0 ? "Lanjutkan" : "Buka Kursus") : "Masuk Kursus");
        double progresKartu = (session.isGuru() || terdaftar) ? p : -1.0;
        CourseCard kartu = new CourseCard(kursus, progresKartu, labelTombol);
        kartu.setOnBuka(() -> masukKursus(kursus));
        return kartu;
    }

    private void masukKursus(Course kursus) {
        if (session.isGuru() || isSiswaTerdaftar(kursus)) {
            session.setKursusAktif(kursus);
            session.setMateriFokus(null);
            SceneRouter.pindah(AppScreen.KURSUS_DETAIL);
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(SceneRouter.getStage());
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = new DialogPane() {
            @Override
            protected Node createButtonBar() {
                return null;
            }
        };
        pane.setBackground(Background.EMPTY);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getStylesheets().setAll(SceneRouter.getScene().getStylesheets());
        pane.getButtonTypes().add(ButtonType.CLOSE);
        dialog.setDialogPane(pane);

        dialog.setOnShown(e -> {
            if (pane.getScene() != null) {
                pane.getScene().setFill(Color.TRANSPARENT);
            }
        });

        VBox kartu = new VBox(16);
        kartu.getStyleClass().add("modal-kartu");
        kartu.setPrefWidth(440);
        kartu.setMaxWidth(460);
        kartu.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.25), 30, 0.12, 0, 8));

        // Header Modal tanpa ikon dekoratif
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);

        final double[] dragOffset = new double[2];
        barisHeader.setOnMousePressed(e -> {
            dragOffset[0] = dialog.getX() - e.getScreenX();
            dragOffset[1] = dialog.getY() - e.getScreenY();
        });
        barisHeader.setOnMouseDragged(e -> {
            dialog.setX(e.getScreenX() + dragOffset[0]);
            dialog.setY(e.getScreenY() + dragOffset[1]);
        });

        VBox boxJudul = new VBox(2);
        Label lblJudul = new Label("Masukkan Kode Kursus");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label lblSub = new Label("Kursus ini memerlukan kode akses dari pengajar.");
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(lblJudul, lblSub);
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

        // Info Kursus Tujuan
        HBox kotakKursus = new HBox(12);
        kotakKursus.getStyleClass().add("modal-kotak-jadwal");
        kotakKursus.setAlignment(Pos.CENTER_LEFT);

        Node polaThumb = CoursePattern.buat(kursus.getKode(), 44, 44, 12);

        VBox infoKursus = new VBox(2);
        Label lblNamaKursus = new Label(kursus.getNama());
        lblNamaKursus.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblNamaKursus.setWrapText(true);
        Label lblPengajar = new Label("Pengajar: " + kursus.getPengajar());
        lblPengajar.getStyleClass().add("teks-redup");
        lblPengajar.setStyle("-fx-font-size: 12px;");
        infoKursus.getChildren().addAll(lblNamaKursus, lblPengajar);
        HBox.setHgrow(infoKursus, Priority.ALWAYS);

        Label badgeTerkunci = new Label("Terkunci");
        badgeTerkunci.getStyleClass().add("badge");

        kotakKursus.getChildren().addAll(polaThumb, infoKursus, badgeTerkunci);

        // Input Kode Kursus
        VBox boxInput = new VBox(6);
        Label lblField = new Label("Kode Kursus");
        lblField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");

        TextField tfKode = new TextField();
        tfKode.setPromptText("Ketik kode akses kursus...");
        tfKode.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblCatatan = new Label("Perhatikan huruf besar dan kecil (case-sensitive) saat memasukkan kode.");
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

        tfKode.textProperty().addListener((obs, lama, baru) -> {
            if (lblGalat.isVisible()) {
                lblGalat.setVisible(false);
                lblGalat.setManaged(false);
            }
        });

        boxInput.getChildren().addAll(lblField, tfKode, lblCatatan, lblGalat);

        // Footer Aksi
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });

        Button btnMasuk = new Button("Masuk Kursus");
        btnMasuk.getStyleClass().add("tombol-utama");
        btnMasuk.setDefaultButton(true);
        btnMasuk.setOnAction(e -> {
            String kode = tfKode.getText() != null ? tfKode.getText().trim() : "";
            if (StringUtil.isKosong(kode)) {
                lblGalat.setText("Kode kursus tidak boleh kosong.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKode.requestFocus();
                return;
            }
            if (kode.equals(kursus.getKode())) {
                try {
                    session.getPendaftaranService().daftarkan(siswa, kursus);
                } catch (IOException ignored) {}
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                session.setKursusAktif(kursus);
                session.setMateriFokus(null);
                SceneRouter.pindah(AppScreen.KURSUS_DETAIL);
            } else {
                lblGalat.setText("Kode kursus tidak sesuai. Perhatikan huruf besar dan kecil atau tanyakan kepada guru.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKode.requestFocus();
                tfKode.selectAll();
            }
        });

        barisTombol.getChildren().addAll(btnBatal, btnMasuk);

        kartu.getChildren().addAll(barisHeader, new Separator(), kotakKursus, boxInput, new Separator(), barisTombol);
        pane.setContent(kartu);

        Platform.runLater(tfKode::requestFocus);
        dialog.showAndWait();
    }

    @FXML
    private void handleAksiKhusus() {
        if (session.isGuru()) {
            tampilkanDialogBuatKursus();
        } else {
            tampilkanDialogMasukKode();
        }
    }

    private void tampilkanDialogMasukKode() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(SceneRouter.getStage());
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = new DialogPane() {
            @Override
            protected Node createButtonBar() {
                return null;
            }
        };
        pane.setBackground(Background.EMPTY);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getStylesheets().setAll(SceneRouter.getScene().getStylesheets());
        pane.getButtonTypes().add(ButtonType.CLOSE);
        dialog.setDialogPane(pane);

        dialog.setOnShown(e -> {
            if (pane.getScene() != null) {
                pane.getScene().setFill(Color.TRANSPARENT);
            }
        });

        VBox kartu = new VBox(16);
        kartu.getStyleClass().add("modal-kartu");
        kartu.setPrefWidth(430);
        kartu.setMaxWidth(450);
        kartu.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.25), 30, 0.12, 0, 8));

        // Header Modal tanpa ikon dekoratif
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);

        final double[] dragOffset = new double[2];
        barisHeader.setOnMousePressed(e -> {
            dragOffset[0] = dialog.getX() - e.getScreenX();
            dragOffset[1] = dialog.getY() - e.getScreenY();
        });
        barisHeader.setOnMouseDragged(e -> {
            dialog.setX(e.getScreenX() + dragOffset[0]);
            dialog.setY(e.getScreenY() + dragOffset[1]);
        });

        VBox boxJudul = new VBox(2);
        Label lblJudul = new Label("Masuk lewat Kode");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label lblSub = new Label("Masukkan kode kursus yang diberikan oleh pengajar.");
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(lblJudul, lblSub);
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

        VBox boxInput = new VBox(6);
        Label lblField = new Label("Kode Kursus");
        lblField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");

        TextField tfKode = new TextField();
        tfKode.setPromptText("Contoh: BIN, MTK, FIS...");
        tfKode.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblCatatan = new Label("Perhatikan huruf besar dan kecil (case-sensitive) saat memasukkan kode.");
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

        tfKode.textProperty().addListener((obs, lama, baru) -> {
            if (lblGalat.isVisible()) {
                lblGalat.setVisible(false);
                lblGalat.setManaged(false);
            }
        });

        boxInput.getChildren().addAll(lblField, tfKode, lblCatatan, lblGalat);

        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });

        Button btnMasuk = new Button("Cari & Masuk");
        btnMasuk.getStyleClass().add("tombol-utama");
        btnMasuk.setDefaultButton(true);
        btnMasuk.setOnAction(e -> {
            String kode = tfKode.getText() != null ? tfKode.getText().trim() : "";
            if (StringUtil.isKosong(kode)) {
                lblGalat.setText("Kode kursus tidak boleh kosong.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKode.requestFocus();
                return;
            }
            Optional<Course> opt = session.getKursusService().cariKursus(kode);
            if (opt.isPresent()) {
                Course k = opt.get();
                if (siswa != null) {
                    try {
                        session.getPendaftaranService().daftarkan(siswa, k);
                    } catch (IOException ignored) {}
                }
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                session.setKursusAktif(k);
                session.setMateriFokus(null);
                SceneRouter.pindah(AppScreen.KURSUS_DETAIL);
            } else {
                lblGalat.setText("Kursus dengan kode '" + kode + "' tidak ditemukan. Perhatikan huruf besar dan kecil.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKode.requestFocus();
                tfKode.selectAll();
            }
        });

        barisTombol.getChildren().addAll(btnBatal, btnMasuk);

        kartu.getChildren().addAll(barisHeader, new Separator(), boxInput, new Separator(), barisTombol);
        pane.setContent(kartu);

        Platform.runLater(tfKode::requestFocus);
        dialog.showAndWait();
    }

    private void tampilkanDialogBuatKursus() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(SceneRouter.getStage());
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = new DialogPane() {
            @Override
            protected Node createButtonBar() {
                return null;
            }
        };
        pane.setBackground(Background.EMPTY);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getStylesheets().setAll(SceneRouter.getScene().getStylesheets());
        pane.getButtonTypes().add(ButtonType.CLOSE);
        dialog.setDialogPane(pane);

        dialog.setOnShown(e -> {
            if (pane.getScene() != null) {
                pane.getScene().setFill(Color.TRANSPARENT);
            }
        });

        VBox kartu = new VBox(16);
        kartu.getStyleClass().add("modal-kartu");
        kartu.setPrefWidth(480);
        kartu.setMaxWidth(500);
        kartu.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.25), 30, 0.12, 0, 8));

        // Header Modal tanpa ikon dekoratif
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);

        final double[] dragOffset = new double[2];
        barisHeader.setOnMousePressed(e -> {
            dragOffset[0] = dialog.getX() - e.getScreenX();
            dragOffset[1] = dialog.getY() - e.getScreenY();
        });
        barisHeader.setOnMouseDragged(e -> {
            dialog.setX(e.getScreenX() + dragOffset[0]);
            dialog.setY(e.getScreenY() + dragOffset[1]);
        });

        VBox boxJudul = new VBox(2);
        Label lblJudul = new Label("Buat Kursus Baru");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label lblSub = new Label("Lengkapi informasi kursus baru yang ingin Anda buat.");
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(lblJudul, lblSub);
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

        HBox barisKodePengajar = new HBox(12);
        VBox boxKode = new VBox(6);
        boxKode.setPrefWidth(150);
        Label lblKode = new Label("Kode Kursus");
        lblKode.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextField tfKode = new TextField();
        tfKode.setPromptText("Contoh: WEB");
        tfKode.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold;");
        boxKode.getChildren().addAll(lblKode, tfKode);

        VBox boxPengajar = new VBox(6);
        HBox.setHgrow(boxPengajar, Priority.ALWAYS);
        Label lblPengajar = new Label("Pengajar");
        lblPengajar.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextField tfPengajar = new TextField(session.getNamaPengguna());
        boxPengajar.getChildren().addAll(lblPengajar, tfPengajar);

        barisKodePengajar.getChildren().addAll(boxKode, boxPengajar);

        VBox boxNama = new VBox(6);
        Label lblNama = new Label("Nama Kursus");
        lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextField tfNama = new TextField();
        tfNama.setPromptText("Contoh: Pemrograman Web");
        boxNama.getChildren().addAll(lblNama, tfNama);

        VBox boxDeskripsi = new VBox(6);
        Label lblDeskripsi = new Label("Deskripsi Singkat");
        lblDeskripsi.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");
        TextArea taDeskripsi = new TextArea();
        taDeskripsi.setPromptText("Deskripsi singkat kursus...");
        taDeskripsi.setPrefRowCount(3);
        taDeskripsi.setWrapText(true);
        boxDeskripsi.getChildren().addAll(lblDeskripsi, taDeskripsi);

        Label lblGalat = new Label();
        lblGalat.setWrapText(true);
        lblGalat.setMaxWidth(Double.MAX_VALUE);
        lblGalat.setStyle("-fx-text-fill: -bahaya; -fx-background-color: rgba(239, 68, 68, 0.1); "
                + "-fx-background-radius: 10; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblGalat.setVisible(false);
        lblGalat.setManaged(false);

        VBox formBox = new VBox(12, barisKodePengajar, boxNama, boxDeskripsi, lblGalat);

        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });

        Button btnSimpan = new Button("Buat Kursus");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setDefaultButton(true);
        btnSimpan.setOnAction(e -> {
            String kode = tfKode.getText() != null ? tfKode.getText().trim() : "";
            String nama = tfNama.getText() != null ? tfNama.getText().trim() : "";
            String pengajar = tfPengajar.getText() != null ? tfPengajar.getText().trim() : "";
            String deskripsi = taDeskripsi.getText() != null ? taDeskripsi.getText().trim() : "";

            if (StringUtil.isKosong(kode) || StringUtil.isKosong(nama)) {
                lblGalat.setText("Kode dan nama kursus wajib diisi.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                return;
            }

            Chapter babAwal = new Chapter(1, "Bab 1: Pendahuluan", List.of());
            String nipPembuat = session.isGuru() && session.getGuruAktif() != null
                    ? session.getGuruAktif().getNip() : "";
            Course kursusBaru = new Course(kode, nama, pengajar, nipPembuat, deskripsi, List.of(babAwal));
            try {
                session.getKursusService().tambahKursus(kursusBaru);
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                DialogUtil.info("Kursus " + kursusBaru.getKode() + " berhasil dibuat!");
                session.setKursusAktif(kursusBaru);
                session.setMateriFokus(null);
                SceneRouter.pindah(AppScreen.KURSUS_DETAIL);
            } catch (IllegalArgumentException | IOException ex) {
                lblGalat.setText("Gagal membuat kursus: " + ex.getMessage());
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
            }
        });

        barisTombol.getChildren().addAll(btnBatal, btnSimpan);

        kartu.getChildren().addAll(barisHeader, new Separator(), formBox, new Separator(), barisTombol);
        pane.setContent(kartu);

        Platform.runLater(tfKode::requestFocus);
        dialog.showAndWait();
    }
}
