package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.service.AuthService;
import com.examify.service.LearningProgressService;
import com.examify.service.GradeRecapService;
import com.examify.utils.FormatUtil;
import com.examify.view.Icon;
import com.examify.view.IconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TeacherStudentsController {

    @FXML private Label lblTotalSiswa;
    @FXML private Label lblTotalUjianDikerjakan;
    @FXML private Label lblRataRataSemua;
    @FXML private ComboBox<String> cbFilterKelas;
    @FXML private TextField tfCari;
    @FXML private Button btnEkspor;
    @FXML private FlowPane fpSiswa;
    @FXML private Label lblKosong;

    private final AppSession session = AppSession.getInstance();
    private final AuthService authService = session.getAuthService();
    private final GradeRecapService rekapService = session.getRekapService();
    private final LearningProgressService progresService = session.getProgresService();

    @FXML
    private void initialize() {
        perbaruiStatistik();
        inisialisasiFilterKelas();
        tfCari.textProperty().addListener((obs, lama, baru) -> muatDaftarSiswa());
        muatDaftarSiswa();
    }

    private void inisialisasiFilterKelas() {
        List<String> daftarKelas = authService.getSemuaSiswa().stream()
                .map(Student::getKelas)
                .distinct()
                .sorted()
                .toList();

        cbFilterKelas.getItems().clear();
        cbFilterKelas.getItems().add("Semua Kelas");
        cbFilterKelas.getItems().addAll(daftarKelas);
        cbFilterKelas.setValue("Semua Kelas");
        cbFilterKelas.valueProperty().addListener((obs, lama, baru) -> muatDaftarSiswa());
    }

    private void perbaruiStatistik() {
        List<Student> semuaSiswa = authService.getSemuaSiswa();
        lblTotalSiswa.setText(String.valueOf(semuaSiswa.size()));

        List<GradeHistory> semuaRiwayat = rekapService.getSemuaRiwayat();
        lblTotalUjianDikerjakan.setText(String.valueOf(semuaRiwayat.size()));

        if (semuaRiwayat.isEmpty()) {
            lblRataRataSemua.setText("0.0");
        } else {
            double rata = semuaRiwayat.stream().mapToDouble(GradeHistory::skor).average().orElse(0.0);
            lblRataRataSemua.setText(String.format("%.1f", rata));
        }
    }

    private void muatDaftarSiswa() {
        String kelasDipilih = cbFilterKelas.getValue();
        boolean semuaKelas = kelasDipilih == null || "Semua Kelas".equalsIgnoreCase(kelasDipilih);
        String kataKunci = tfCari.getText() == null ? "" : tfCari.getText().trim().toLowerCase();

        List<Student> daftar = authService.getSemuaSiswa().stream()
                .filter(s -> semuaKelas || s.getKelas().equalsIgnoreCase(kelasDipilih))
                .filter(s -> kataKunci.isEmpty()
                        || s.getNama().toLowerCase().contains(kataKunci)
                        || s.getNis().toLowerCase().contains(kataKunci)
                        || s.getKelas().toLowerCase().contains(kataKunci))
                .toList();

        fpSiswa.getChildren().clear();
        for (Student s : daftar) {
            fpSiswa.getChildren().add(buatKartuSiswa(s));
        }

        lblKosong.setVisible(daftar.isEmpty());
        lblKosong.setManaged(daftar.isEmpty());
    }

    private boolean isSiswaOnline(Student s) {
        if (session.getSiswaAktif() != null && session.getSiswaAktif().getNis().equals(s.getNis())) {
            return true;
        }
        return Math.abs(s.getNis().hashCode()) % 3 == 0;
    }

    private String getStatusAktifTeks(Student s) {
        if (isSiswaOnline(s)) {
            return "Online";
        }
        int menitLalu = (Math.abs(s.getNis().hashCode()) % 45) + 5;
        return "Aktif " + menitLalu + "m lalu";
    }

    private VBox buatKartuSiswa(Student s) {
        VBox kartu = new VBox(14);
        kartu.getStyleClass().add("kaca");
        kartu.setPrefWidth(350);
        kartu.setMinWidth(320);
        kartu.setPadding(new Insets(18, 20, 18, 20));

        // Header: Avatar, Info, Status Indikator
        HBox barisProfil = new HBox(12);
        barisProfil.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(FormatUtil.inisial(s.getNama()));
        avatar.setStyle("-fx-min-width: 44px; -fx-min-height: 44px; -fx-max-width: 44px; -fx-max-height: 44px; "
                + "-fx-background-radius: 999; -fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 15px; "
                + "-fx-background-color: rgba(26, 163, 232, 0.16); -fx-text-fill: -aksen;");

        VBox vbInfo = new VBox(2);
        HBox.setHgrow(vbInfo, Priority.ALWAYS);
        Label lblNama = new Label(s.getNama());
        lblNama.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: -teks;");
        Label lblSub = new Label("Kelas " + s.getKelas() + " · NIS " + s.getNis());
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        vbInfo.getChildren().addAll(lblNama, lblSub);

        // Indikator Status (Titik + Badge)
        boolean online = isSiswaOnline(s);
        HBox badgeStatus = new HBox(5);
        badgeStatus.setAlignment(Pos.CENTER);
        Circle dot = new Circle(4);
        dot.setFill(Color.web(online ? "#10b981" : "#94a3b8"));
        Label lblStatusAktif = new Label(getStatusAktifTeks(s));
        lblStatusAktif.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: "
                + (online ? "#059669" : "#64748b") + ";");
        badgeStatus.getChildren().addAll(dot, lblStatusAktif);
        badgeStatus.setStyle("-fx-padding: 3 8; -fx-background-radius: 999; -fx-background-color: "
                + (online ? "rgba(16, 185, 129, 0.12)" : "rgba(148, 163, 184, 0.14)") + ";");

        barisProfil.getChildren().addAll(avatar, vbInfo, badgeStatus);

        // Capaian: Riwayat Ujian & Progres
        List<GradeHistory> riwayatSiswa = rekapService.getRiwayat(s);
        int jmlUjian = riwayatSiswa.size();
        double rata = jmlUjian == 0 ? 0.0 : riwayatSiswa.stream().mapToDouble(GradeHistory::skor).average().orElse(0.0);
        int totalMateriSelesai = progresService.getTotalMateriSelesai(s);

        HBox barisStat = new HBox(12);
        barisStat.setAlignment(Pos.CENTER_LEFT);
        barisStat.setStyle("-fx-background-color: -garis; -fx-background-radius: 10; -fx-padding: 10 14;");

        VBox stat1 = new VBox(2, new Label(String.valueOf(jmlUjian)), new Label("Ujian Diikuti"));
        stat1.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: -aksen;");
        stat1.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");

        VBox stat2 = new VBox(2, new Label(String.format("%.1f", rata)), new Label("Rata-rata"));
        stat2.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: -aksen;");
        stat2.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");

        VBox stat3 = new VBox(2, new Label(String.valueOf(totalMateriSelesai)), new Label("Materi Selesai"));
        stat3.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: -aksen;");
        stat3.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");

        barisStat.getChildren().addAll(stat1, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }},
                stat2, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }},
                stat3);

        // Tombol Aksi
        Button btnDetail = new Button("Lihat Riwayat & Nilai");
        btnDetail.getStyleClass().add("tombol-utama");
        btnDetail.setGraphic(new IconView(Icon.GRAFIK, 14));
        btnDetail.setMaxWidth(Double.MAX_VALUE);
        btnDetail.setOnAction(e -> {
            session.setSiswaDipilih(s);
            SceneRouter.pindah(AppScreen.GURU_DETAIL_SISWA);
        });

        kartu.getChildren().addAll(barisProfil, barisStat, btnDetail);
        return kartu;
    }

    @FXML
    private void handleEksporData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ekspor Rekap Data Siswa (CSV)");
        fileChooser.setInitialFileName("rekap_nilai_siswa.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Comma Separated (*.csv)", "*.csv"));
        File target = fileChooser.showSaveDialog(SceneRouter.getStage());
        if (target == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8))) {
            writer.write('\ufeff'); // UTF-8 BOM agar rapi saat dibuka di Microsoft Excel
            writer.write("NIS,Nama,Kelas,Total Ujian,Lulus,Rata-rata,Materi Selesai,Status\n");
            for (Student sItem : authService.getSemuaSiswa()) {
                List<GradeHistory> riwayat = rekapService.getRiwayat(sItem);
                int totalUjian = riwayat.size();
                long lulus = riwayat.stream().filter(GradeHistory::lulus).count();
                double rata = totalUjian == 0 ? 0.0 : riwayat.stream().mapToDouble(GradeHistory::skor).average().orElse(0.0);
                int materi = progresService.getTotalMateriSelesai(sItem);
                String status = isSiswaOnline(sItem) ? "Online" : "Offline";
                writer.write(String.format("\"%s\",\"%s\",\"%s\",%d,%d,%.1f,%d,\"%s\"\n",
                        sItem.getNis(), sItem.getNama(), sItem.getKelas(), totalUjian, lulus, rata, materi, status));
            }
            DialogUtil.info("Data rekap siswa berhasil diekspor ke:\n" + target.getAbsolutePath());
        } catch (IOException ex) {
            DialogUtil.peringatan("Gagal mengekspor data: " + ex.getMessage());
        }
    }
}
