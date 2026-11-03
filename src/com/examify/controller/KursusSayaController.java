package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.Kursus;
import com.examify.model.Siswa;
import com.examify.service.ProgresBelajarService;
import com.examify.view.AnimasiUtil;
import com.examify.view.KartuKursus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.List;
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
    @FXML private Label lblJumlahKursus;
    @FXML private Label lblMateriSelesai;
    @FXML private Label lblUjianDikerjakan;
    @FXML private TextField tfCari;
    @FXML private ComboBox<Filter> cbFilter;
    @FXML private ComboBox<Urutan> cbUrut;
    @FXML private FlowPane fpKursus;
    @FXML private Label lblKosong;

    private final AppSession session = AppSession.getInstance();
    private final ProgresBelajarService progresService = session.getProgresService();
    private Siswa siswa;

    @FXML
    private void initialize() {
        siswa = session.getSiswaAktif();
        lblSapaan.setText("Halo, " + siswa.getNama().split(" ")[0] + "!");
        AnimasiUtil.hitungNaik(lblJumlahKursus, session.getKursusService().getDaftarKursus().size(), DURASI_HITUNG);
        AnimasiUtil.hitungNaik(lblMateriSelesai, progresService.getTotalMateriSelesai(siswa), DURASI_HITUNG);
        AnimasiUtil.hitungNaik(lblUjianDikerjakan, session.getRekapService().getRiwayat(siswa).size(), DURASI_HITUNG);

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
        KartuKursus kartu = new KartuKursus(kursus, p, p > 0 ? "Lanjutkan" : "Mulai belajar");
        kartu.setOnBuka(() -> {
            session.setKursusAktif(kursus);
            session.setMateriFokus(null);
            SceneRouter.pindah(Layar.KURSUS_DETAIL);
        });
        return kartu;
    }
}
