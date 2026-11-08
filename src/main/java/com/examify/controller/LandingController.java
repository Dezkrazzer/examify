package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.io.TimReader;
import com.examify.model.AnggotaTim;
import com.examify.model.Kursus;
import com.examify.service.KursusService;
import com.examify.utils.AppConfig;
import com.examify.view.AnimasiUtil;
import com.examify.view.GridTengah;
import com.examify.view.GulirHalus;
import com.examify.view.KartuAnggota;
import com.examify.view.KartuKursus;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;

public class LandingController {

    private static final int MAKS_KOLOM = 4;

    private static final Duration DURASI_HITUNG = Duration.millis(1400);

    @FXML private ScrollPane spKonten;
    @FXML private VBox vbKonten;
    @FXML private VBox vbFitur;
    @FXML private VBox vbKursus;
    @FXML private FlowPane fpKursus;
    @FXML private Label lblKosong;
    @FXML private VBox vbTim;
    @FXML private FlowPane fpTim;
    @FXML private TextField tfCari;
    @FXML private Button btnTema;
    @FXML private Label lblSemester;
    @FXML private Circle cTitikHidup;
    @FXML private Circle cCincin;
    @FXML private Node vbMockUjian;
    @FXML private Node vbKartuBaru;
    @FXML private Node vbKartuStat;
    @FXML private Label lblKursusUnggulan;
    @FXML private Label lblKursusUnggulanInfo;
    @FXML private Label lblMateriHero;

    private final AppSession session = AppSession.getInstance();
    private final KursusService kursusService = session.getKursusService();

    @FXML
    private void initialize() {
        TemaUtil.pasang(btnTema, false);
        lblSemester.setText(teksSemester(LocalDate.now()) + " sedang berlangsung");
        isiHero();
        GridTengah.terapkan(fpKursus, KartuKursus.LEBAR, MAKS_KOLOM);
        GridTengah.terapkan(fpTim, KartuAnggota.LEBAR, MAKS_KOLOM);
        tampilkanTim();
        tampilkanKursus(kursusService.cari(""));
        tfCari.textProperty().addListener((obs, lama, kata) -> tampilkanKursus(kursusService.cari(kata)));
        tfCari.setOnAction(e -> handleKeKursus());
        animasiDekorasi();
    }

    /** Juli-Desember = semester ganjil, Januari-Juni = semester genap. */
    static String teksSemester(LocalDate tanggal) {
        int tahun = tanggal.getYear();
        return tanggal.getMonthValue() >= 7
                ? "Semester Ganjil " + tahun + "/" + (tahun + 1)
                : "Semester Genap " + (tahun - 1) + "/" + tahun;
    }

    private void isiHero() {
        List<Kursus> semua = kursusService.getDaftarKursus();
        Kursus unggulan = semua.stream().max((a, b) -> Integer.compare(a.getJumlahMateri(), b.getJumlahMateri()))
                .orElseThrow();
        lblKursusUnggulan.setText(unggulan.getNama());
        lblKursusUnggulanInfo.setText(unggulan.getKode() + " - " + unggulan.getDaftarBab().size() + " bab");
        AnimasiUtil.hitungNaik(lblMateriHero, kursusService.getJumlahMateri(), DURASI_HITUNG);
    }


    private void tampilkanKursus(List<Kursus> daftar) {
        List<KartuKursus> kartu = daftar.stream().map(this::buatKartu).toList();
        fpKursus.getChildren().setAll(kartu);
        lblKosong.setVisible(daftar.isEmpty());
        lblKosong.setManaged(daftar.isEmpty());
        AnimasiUtil.berurutan(kartu, Duration.millis(70));
    }

    /** Data tim bersifat dekoratif: bila file rusak/kosong, bagian Tim cukup disembunyikan. */
    private void tampilkanTim() {
        List<AnggotaTim> daftar;
        try {
            daftar = new TimReader().baca(AppConfig.FILE_TIM);
        } catch (Exception e) {
            System.err.println("[Examify] gagal memuat " + AppConfig.FILE_TIM + ": " + e.getMessage());
            daftar = List.of();
        }
        List<KartuAnggota> kartu = daftar.stream()
                .map(anggota -> new KartuAnggota(anggota, AppConfig.DIR_DATA, session::bukaDokumen))
                .toList();
        fpTim.getChildren().setAll(kartu);
        vbTim.setVisible(!kartu.isEmpty());
        vbTim.setManaged(!kartu.isEmpty());
        AnimasiUtil.berurutan(kartu, Duration.millis(90));
    }

    private KartuKursus buatKartu(Kursus kursus) {
        KartuKursus kartu = new KartuKursus(kursus, -1, "Masuk untuk belajar");
        kartu.setOnBuka(this::handleMasuk);
        return kartu;
    }

    private void animasiDekorasi() {
        AnimasiUtil.melayang(vbKartuBaru, 14, Duration.seconds(3.2));
        AnimasiUtil.melayang(vbKartuStat, 12, Duration.seconds(2.6));
        AnimasiUtil.melayang(vbMockUjian, 6, Duration.seconds(4));
        RotateTransition putar = new RotateTransition(Duration.seconds(60), cCincin);
        putar.setByAngle(360);
        putar.setCycleCount(Animation.INDEFINITE);
        putar.play();
        FadeTransition kedip = new FadeTransition(Duration.millis(900), cTitikHidup);
        kedip.setFromValue(1);
        kedip.setToValue(0.25);
        kedip.setAutoReverse(true);
        kedip.setCycleCount(Animation.INDEFINITE);
        kedip.play();
        AnimasiUtil.berurutan(List.of(vbKartuBaru, vbMockUjian, vbKartuStat), Duration.millis(160));
    }

    private void gulirKe(Node bagian) {
        double tinggiKonten = vbKonten.getHeight() - spKonten.getViewportBounds().getHeight();
        if (tinggiKonten > 0) {
            GulirHalus.animasikan(spKonten, Math.min(1, bagian.getBoundsInParent().getMinY() / tinggiKonten));
        }
    }

    @FXML
    private void handleKeAtas() {
        GulirHalus.animasikan(spKonten, 0);
    }

    @FXML
    private void handleKeFitur() {
        gulirKe(vbFitur);
    }

    @FXML
    private void handleKeKursus() {
        gulirKe(vbKursus);
    }

    @FXML
    private void handleKeTim() {
        gulirKe(vbTim);
    }

    @FXML
    private void handleMasuk() {
        SceneRouter.pindah(Layar.LOGIN);
    }
}
