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
import com.examify.view.KartuKursus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
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

            AnimasiUtil.hitungNaik(lblJumlahKursus, session.getKursusService().getDaftarKursus().size(), DURASI_HITUNG);
            AnimasiUtil.hitungNaik(lblMateriSelesai, session.getKursusService().getJumlahMateriSemuaKursus(), DURASI_HITUNG);
            AnimasiUtil.hitungNaik(lblUjianDikerjakan, session.getUjianService().getJumlahUjian(), DURASI_HITUNG);
        } else {
            lblSapaan.setText("Halo, " + (siswa != null ? siswa.getNama().split(" ")[0] : "Siswa") + "!");
            lblSubjudul.setText("Lanjutkan belajar dari kursus yang sedang kamu ikuti.");
            lblTile1.setText("Kursus diikuti");
            lblTile2.setText("Materi selesai");
            lblTile3.setText("Ujian dikerjakan");
            btnAksiKhusus.setText("Masuk lewat Kode");

            AnimasiUtil.hitungNaik(lblJumlahKursus, session.getKursusService().getDaftarKursus().size(), DURASI_HITUNG);
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

    private KartuKursus buatKartu(Kursus kursus) {
        double p = progres(kursus);
        String labelTombol = session.isGuru() ? "Kelola Kursus" : (p > 0 ? "Lanjutkan" : "Mulai belajar");
        KartuKursus kartu = new KartuKursus(kursus, p, labelTombol);
        kartu.setOnBuka(() -> {
            session.setKursusAktif(kursus);
            session.setMateriFokus(null);
            SceneRouter.pindah(Layar.KURSUS_DETAIL);
        });
        return kartu;
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Masuk lewat Kode");
        dialog.setHeaderText("Masukkan kode kursus yang diberikan oleh Guru");
        dialog.setContentText("Kode Kursus:");

        Optional<String> hasil = dialog.showAndWait();
        hasil.ifPresent(kodeInput -> {
            String kode = kodeInput.trim();
            if (StringUtil.isKosong(kode)) return;
            Optional<Kursus> opt = session.getKursusService().cariKursus(kode);
            if (opt.isPresent()) {
                session.setKursusAktif(opt.get());
                session.setMateriFokus(null);
                SceneRouter.pindah(Layar.KURSUS_DETAIL);
            } else {
                DialogUtil.peringatan("Kursus dengan kode '" + kode + "' tidak ditemukan.");
            }
        });
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
                String kode = tfKode.getText().trim().toUpperCase();
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
