package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.Bab;
import com.examify.model.Kursus;
import com.examify.model.Siswa;
import com.examify.service.ProgresBelajarService;
import com.examify.utils.StringUtil;
import com.examify.view.AnimasiUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
import com.examify.view.KartuKursus;
import com.examify.view.PolaKursus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class KursusSayaController {

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

    private final AppSession session = AppSession.getInstance();
    private final ProgresBelajarService progresService = session.getProgresService();
    private Siswa siswa;

    @FXML
    private void initialize() {
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
            AnimasiUtil.hitungNaik(lblJumlahKursus, jmlDiikuti, DURASI_HITUNG);
            AnimasiUtil.hitungNaik(lblMateriSelesai, session.getKursusService().getJumlahMateriSemuaKursus(), DURASI_HITUNG);
            AnimasiUtil.hitungNaik(lblUjianDikerjakan, session.getUjianService().getJumlahUjian(), DURASI_HITUNG);
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
            AnimasiUtil.hitungNaik(lblJumlahKursus, jmlDiikuti, DURASI_HITUNG);
            AnimasiUtil.hitungNaik(lblMateriSelesai, progresService.getTotalMateriSelesai(siswa), DURASI_HITUNG);
            AnimasiUtil.hitungNaik(lblUjianDikerjakan, session.getRekapService().getRiwayat(siswa).size(), DURASI_HITUNG);
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

    private double progres(Kursus kursus) {
        if (siswa == null) {
            return 0.0;
        }
        return progresService.getProgres(siswa, kursus);
    }

    private void tampilkan() {
        Filter filter = cbFilter.getValue();
        List<Kursus> daftar = session.getKursusService().cari(tfCari.getText()).stream()
                .filter(kursus -> filter.cocok.test(progres(kursus)))
                .sorted(pembanding(cbUrut.getValue()))
                .toList();
        List<KartuKursus> kartu = daftar.stream().map(this::buatKartu).toList();
        fpKursus.getChildren().setAll(kartu);
        lblKosong.setVisible(daftar.isEmpty());
        lblKosong.setManaged(daftar.isEmpty());
        AnimasiUtil.berurutan(kartu, Duration.millis(60));
    }

    private Comparator<Kursus> pembanding(Urutan urutan) {
        Comparator<Kursus> menurutProgres = Comparator.comparingDouble(this::progres);
        return switch (urutan) {
            case NAMA -> Comparator.comparing(Kursus::getNama);
            case PROGRES_TERTINGGI -> menurutProgres.reversed();
            case PROGRES_TERENDAH -> menurutProgres;
        };
    }

    private boolean isSiswaTerdaftar(Kursus kursus) {
        if (siswa == null) return true;
        return session.getPendaftaranService().isTerdaftar(siswa, kursus) || progres(kursus) > 0;
    }

    private KartuKursus buatKartu(Kursus kursus) {
        double p = progres(kursus);
        boolean terdaftar = isSiswaTerdaftar(kursus);
        String labelTombol = session.isGuru() ? "Kelola Kursus"
                : (terdaftar ? (p > 0 ? "Lanjutkan" : "Buka Kursus") : "Masuk Kursus");
        double progresKartu = (session.isGuru() || terdaftar) ? p : -1.0;
        KartuKursus kartu = new KartuKursus(kursus, progresKartu, labelTombol);
        kartu.setOnBuka(() -> masukKursus(kursus));
        return kartu;
    }

    private void masukKursus(Kursus kursus) {
        if (session.isGuru() || isSiswaTerdaftar(kursus)) {
            session.setKursusAktif(kursus);
            session.setMateriFokus(null);
            SceneRouter.pindah(Layar.KURSUS_DETAIL);
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

        // Header Modal (Bisa di-drag)
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

        StackPane kotakIkon = new StackPane(new IkonView(Ikon.GEMBOK, 20));
        kotakIkon.getStyleClass().add("kotak-ikon-stat");
        kotakIkon.setMinSize(42, 42);
        kotakIkon.setMaxSize(42, 42);

        VBox boxJudul = new VBox(2);
        Label lblJudul = new Label("Masukkan Kode Kursus");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label lblSub = new Label("Kursus ini memerlukan kode akses dari pengajar.");
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(lblJudul, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);

        Button btnTutup = new Button();
        btnTutup.setGraphic(new IkonView(Ikon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setTooltip(new Tooltip("Tutup (ESC)"));
        btnTutup.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });

        barisHeader.getChildren().addAll(kotakIkon, boxJudul, btnTutup);

        // Info Kursus Tujuan
        HBox kotakKursus = new HBox(12);
        kotakKursus.getStyleClass().add("modal-kotak-jadwal");
        kotakKursus.setAlignment(Pos.CENTER_LEFT);

        Node polaThumb = PolaKursus.buat(kursus.getKode(), 44, 44, 12);

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
                SceneRouter.pindah(Layar.KURSUS_DETAIL);
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

        // Header Modal (Bisa di-drag)
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

        StackPane kotakIkon = new StackPane(new IkonView(Ikon.GEMBOK, 20));
        kotakIkon.getStyleClass().add("kotak-ikon-stat");
        kotakIkon.setMinSize(42, 42);
        kotakIkon.setMaxSize(42, 42);

        VBox boxJudul = new VBox(2);
        Label lblJudul = new Label("Masuk lewat Kode");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label lblSub = new Label("Masukkan kode kursus yang diberikan oleh pengajar.");
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(lblJudul, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);

        Button btnTutup = new Button();
        btnTutup.setGraphic(new IkonView(Ikon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setTooltip(new Tooltip("Tutup (ESC)"));
        btnTutup.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });

        barisHeader.getChildren().addAll(kotakIkon, boxJudul, btnTutup);

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
            Optional<Kursus> opt = session.getKursusService().cariKursus(kode);
            if (opt.isPresent()) {
                Kursus k = opt.get();
                if (siswa != null) {
                    try {
                        session.getPendaftaranService().daftarkan(siswa, k);
                    } catch (IOException ignored) {}
                }
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                session.setKursusAktif(k);
                session.setMateriFokus(null);
                SceneRouter.pindah(Layar.KURSUS_DETAIL);
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
        Dialog<Kursus> dialog = new Dialog<>();
        dialog.setTitle("Buat Kursus Baru");
        dialog.setHeaderText("Lengkapi informasi kursus baru yang ingin Anda buat:");

        ButtonType btnSimpan = new ButtonType("Buat Kursus", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSimpan, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfKode = new TextField();
        tfKode.setPromptText("Contoh: WEB");
        TextField tfNama = new TextField();
        tfNama.setPromptText("Contoh: Pemrograman Web");
        TextField tfPengajar = new TextField(session.getNamaPengguna());
        TextField tfDeskripsi = new TextField();
        tfDeskripsi.setPromptText("Deskripsi singkat kursus");

        grid.add(new Label("Kode Kursus:"), 0, 0);
        grid.add(tfKode, 1, 0);
        grid.add(new Label("Nama Kursus:"), 0, 1);
        grid.add(tfNama, 1, 1);
        grid.add(new Label("Pengajar:"), 0, 2);
        grid.add(tfPengajar, 1, 2);
        grid.add(new Label("Deskripsi:"), 0, 3);
        grid.add(tfDeskripsi, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSimpan) {
                String kode = tfKode.getText().trim();
                String nama = tfNama.getText().trim();
                String pengajar = tfPengajar.getText().trim();
                String deskripsi = tfDeskripsi.getText().trim();

                if (StringUtil.isKosong(kode) || StringUtil.isKosong(nama)) {
                    DialogUtil.peringatan("Kode dan nama kursus wajib diisi!");
                    return null;
                }
                Bab babAwal = new Bab(1, "Bab 1: Pendahuluan", List.of());
                return new Kursus(kode, nama, pengajar, deskripsi, List.of(babAwal));
            }
            return null;
        });

        Optional<Kursus> result = dialog.showAndWait();
        result.ifPresent(kursusBaru -> {
            try {
                session.getKursusService().tambahKursus(kursusBaru);
                DialogUtil.info("Kursus " + kursusBaru.getKode() + " berhasil dibuat!");
                session.setKursusAktif(kursusBaru);
                session.setMateriFokus(null);
                SceneRouter.pindah(Layar.KURSUS_DETAIL);
            } catch (IllegalArgumentException | IOException e) {
                DialogUtil.peringatan("Gagal membuat kursus: " + e.getMessage());
            }
        });
    }
}
