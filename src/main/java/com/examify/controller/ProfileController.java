package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.Course;
import com.examify.model.SubmitMode;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.Exam;
import com.examify.service.LearningProgressService;
import com.examify.utils.FormatUtil;
import com.examify.view.AnimationUtil;
import com.examify.view.Icon;
import com.examify.view.IconView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Halaman profil: identitas, statistik nilai, grafik, progres kursus, dan riwayat ujian. */
public class ProfileController {

    private static final Duration DURASI_HITUNG = Duration.millis(1000);

    @FXML private Label lblAvatar;
    @FXML private Label lblNama;
    @FXML private Label lblNis;
    @FXML private Label lblKelas;
    @FXML private Label lblRataRata;
    @FXML private Label lblUjian;
    @FXML private Label lblLulus;
    @FXML private Label lblTertinggi;
    @FXML private Label lblMateri;
    @FXML private BarChart<String, Number> chartNilai;
    @FXML private Label lblGrafikKosong;
    @FXML private VBox vbProgresKursus;
    @FXML private TableView<GradeHistory> tvRiwayat;
    @FXML private TableColumn<GradeHistory, String> colTanggal;
    @FXML private TableColumn<GradeHistory, String> colUjian;
    @FXML private TableColumn<GradeHistory, String> colSkor;
    @FXML private TableColumn<GradeHistory, String> colPredikat;
    @FXML private TableColumn<GradeHistory, String> colStatus;
    @FXML private TableColumn<GradeHistory, String> colMode;
    @FXML private TableColumn<GradeHistory, String> colDurasi;
    @FXML private TableColumn<GradeHistory, GradeHistory> colPembahasan;
    @FXML private TableColumn<GradeHistory, GradeHistory> colSertifikat;

    private final AppSession session = AppSession.getInstance();
    private final LearningProgressService progresService = session.getProgresService();
    private Student siswa;
    private List<GradeHistory> riwayat;

    @FXML
    private void initialize() {
        siswa = session.getSiswaAktif();
        riwayat = session.getRekapService().getRiwayat(siswa);
        lblAvatar.setText(FormatUtil.inisial(siswa.getNama()));
        lblNama.setText(siswa.getNama());
        lblNis.setText("NIS " + siswa.getNis());
        lblKelas.setText("Kelas " + siswa.getKelas());
        isiStatistik();
        isiGrafik();
        isiProgresKursus();
        isiTabel();
    }

    private void isiStatistik() {
        double rataRata = riwayat.stream().mapToDouble(GradeHistory::skor).average().orElse(0);
        lblRataRata.setText(riwayat.isEmpty() ? "-" : FormatUtil.angka(rataRata));
        AnimationUtil.hitungNaik(lblUjian, riwayat.size(), DURASI_HITUNG);
        AnimationUtil.hitungNaik(lblLulus, riwayat.stream().filter(GradeHistory::lulus).count(), DURASI_HITUNG);
        lblTertinggi.setText(riwayat.stream().sorted().findFirst() // Comparable: skor tertinggi dulu
                .map(r -> FormatUtil.angka(r.skor())).orElse("-"));
        AnimationUtil.hitungNaik(lblMateri, progresService.getTotalMateriSelesai(siswa), DURASI_HITUNG);
    }

    /** Satu batang per ujian, nilainya diambil dari percobaan terbaik. */
    private void isiGrafik() {
        Map<String, Double> terbaik = new TreeMap<>();
        riwayat.forEach(r -> terbaik.merge(r.kodeUjian(), r.skor(), Math::max));
        XYChart.Series<String, Number> seri = new XYChart.Series<>();
        terbaik.forEach((kode, skor) -> seri.getData().add(new XYChart.Data<>(kode, skor)));
        chartNilai.getData().setAll(List.of(seri));
        boolean kosong = terbaik.isEmpty();
        chartNilai.setVisible(!kosong);
        chartNilai.setManaged(!kosong);
        lblGrafikKosong.setVisible(kosong);
        lblGrafikKosong.setManaged(kosong);
    }

    private void isiProgresKursus() {
        vbProgresKursus.getChildren().clear();
        List<Course> kursusDiikuti = session.getKursusService().getDaftarKursus().stream()
                .filter(k -> session.getPendaftaranService().isTerdaftar(siswa, k))
                .toList();

        if (kursusDiikuti.isEmpty()) {
            Label lblKosong = new Label("Belum ada kursus yang diikuti. Masukkan kode kursus pada menu Kursus Saya untuk mulai belajar.");
            lblKosong.getStyleClass().add("teks-redup");
            lblKosong.setWrapText(true);
            vbProgresKursus.getChildren().add(lblKosong);
            return;
        }

        for (Course kursus : kursusDiikuti) {
            double progres = progresService.getProgres(siswa, kursus);
            Label nama = new Label(kursus.getNama());
            nama.getStyleClass().add("judul-kartu");
            nama.setStyle("-fx-font-size: 13px;");
            HBox.setHgrow(nama, Priority.ALWAYS);
            nama.setMaxWidth(Double.MAX_VALUE);
            Label persen = new Label(Math.round(progres * 100) + "%");
            persen.getStyleClass().add("teks-kecil");
            ProgressBar bar = new ProgressBar(progres);
            bar.getStyleClass().add("progres-tipis");
            bar.setMaxWidth(Double.MAX_VALUE);
            vbProgresKursus.getChildren().add(new VBox(6, new HBox(8, nama, persen), bar));
        }
    }

    private void isiTabel() {
        TableUtil.kolom(colTanggal, r -> FormatUtil.tanggalSingkat(r.waktu()));
        TableUtil.kolom(colUjian, r -> r.kodeUjian() + " - " + r.mataPelajaran());
        TableUtil.kolom(colSkor, r -> FormatUtil.angka(r.skor()));
        TableUtil.kolom(colPredikat, GradeHistory::predikat);
        TableUtil.kolom(colMode, r -> r.modeSubmit() == SubmitMode.MANUAL ? "Manual" : "Otomatis");
        TableUtil.kolom(colDurasi, r -> FormatUtil.hhmmss(r.durasi()));
        TableUtil.kolom(colStatus, r -> r.lulus() ? "Lulus" : "Tidak lulus");
        colStatus.setCellFactory(kolom -> new SelStatus());

        colPembahasan.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colPembahasan.setCellFactory(kolom -> new SelPembahasan());

        colSertifikat.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colSertifikat.setCellFactory(kolom -> new SelSertifikat());
        tvRiwayat.setItems(FXCollections.observableArrayList(riwayat));
    }

    private void unduhSertifikat(GradeHistory r) {
        Optional<Exam> ujian = session.getUjianService().cariUjian(r.kodeUjian());
        if (ujian.isEmpty()) {
            DialogUtil.peringatan("Ujian " + r.kodeUjian() + " sudah tidak terdaftar di bank soal.");
            return;
        }
        try {
            Path file = session.getSertifikatService().simpan(r, ujian.get());
            DialogUtil.info("Sertifikat berhasil disimpan:\n" + file.toAbsolutePath());
        } catch (IOException e) {
            DialogUtil.peringatan("Sertifikat gagal disimpan: " + e.getMessage());
        }
    }

    private void bukaHalamanPembahasan(GradeHistory r) {
        session.setRiwayatFokus(r);
        session.setLayarAsalPembahasan(AppScreen.PROFIL);
        SceneRouter.pindah(AppScreen.PEMBAHASAN_UJIAN);
    }

    /** Status kelulusan ditampilkan sebagai badge berwarna. */
    private static final class SelStatus extends TableCell<GradeHistory, String> {
        @Override
        protected void updateItem(String teks, boolean kosong) {
            super.updateItem(teks, kosong);
            setText(null);
            if (kosong || teks == null) {
                setGraphic(null);
                return;
            }
            Label badge = new Label(teks);
            badge.getStyleClass().addAll("badge", getTableRow().getItem() != null && getTableRow().getItem().lulus()
                    ? "badge-sukses" : "badge-bahaya");
            setGraphic(badge);
        }
    }

    /** Tombol lihat pembahasan evaluasi ujian. */
    private final class SelPembahasan extends TableCell<GradeHistory, GradeHistory> {
        @Override
        protected void updateItem(GradeHistory r, boolean kosong) {
            super.updateItem(r, kosong);
            setText(null);
            if (kosong || r == null) {
                setGraphic(null);
                return;
            }
            Button tombol = new Button("Pembahasan", new IconView(Icon.MATA, 14));
            tombol.getStyleClass().add("tombol-sekunder");
            tombol.setStyle("-fx-padding: 5 12;");
            tombol.setTooltip(new Tooltip("Lihat evaluasi & pembahasan soal " + r.kodeUjian()));
            tombol.setOnAction(e -> bukaHalamanPembahasan(r));
            setGraphic(tombol);
        }
    }

    /** Tombol unduh hanya muncul pada baris yang lulus. */
    private final class SelSertifikat extends TableCell<GradeHistory, GradeHistory> {
        @Override
        protected void updateItem(GradeHistory r, boolean kosong) {
            super.updateItem(r, kosong);
            setText(null);
            if (kosong || r == null || !r.lulus()) {
                setGraphic(null);
                return;
            }
            Button tombol = new Button("Unduh", new IconView(Icon.SERTIFIKAT, 14));
            tombol.getStyleClass().add("tombol-sekunder");
            tombol.setStyle("-fx-padding: 5 12;");
            tombol.setTooltip(new Tooltip("Simpan e-sertifikat " + r.kodeUjian()));
            tombol.setOnAction(e -> unduhSertifikat(r));
            setGraphic(tombol);
        }
    }
}
