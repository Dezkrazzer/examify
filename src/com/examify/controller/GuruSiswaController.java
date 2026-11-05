package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.model.ModeSubmit;
import com.examify.model.RiwayatNilai;
import com.examify.model.Siswa;
import com.examify.service.AuthService;
import com.examify.service.ProgresBelajarService;
import com.examify.service.RekapService;
import com.examify.utils.FormatUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GuruSiswaController {

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML private Label lblTotalSiswa;
    @FXML private Label lblTotalUjianDikerjakan;
    @FXML private Label lblRataRataSemua;
    @FXML private TextField tfCari;
    @FXML private FlowPane fpSiswa;
    @FXML private Label lblKosong;

    private final AppSession session = AppSession.getInstance();
    private final AuthService authService = session.getAuthService();
    private final RekapService rekapService = session.getRekapService();
    private final ProgresBelajarService progresService = session.getProgresService();

    @FXML
    private void initialize() {
        perbaruiStatistik();
        tfCari.textProperty().addListener((obs, lama, baru) -> muatDaftarSiswa());
        muatDaftarSiswa();
    }

    private void perbaruiStatistik() {
        List<Siswa> semuaSiswa = authService.getSemuaSiswa();
        lblTotalSiswa.setText(String.valueOf(semuaSiswa.size()));

        List<RiwayatNilai> semuaRiwayat = rekapService.getSemuaRiwayat();
        lblTotalUjianDikerjakan.setText(String.valueOf(semuaRiwayat.size()));

        if (semuaRiwayat.isEmpty()) {
            lblRataRataSemua.setText("0.0");
        } else {
            double rata = semuaRiwayat.stream().mapToDouble(RiwayatNilai::skor).average().orElse(0.0);
            lblRataRataSemua.setText(String.format("%.1f", rata));
        }
    }

    private void muatDaftarSiswa() {
        String kataKunci = tfCari.getText() == null ? "" : tfCari.getText().trim().toLowerCase();
        List<Siswa> daftar = authService.getSemuaSiswa().stream()
                .filter(s -> kataKunci.isEmpty()
                        || s.getNama().toLowerCase().contains(kataKunci)
                        || s.getNis().toLowerCase().contains(kataKunci)
                        || s.getKelas().toLowerCase().contains(kataKunci))
                .toList();

        fpSiswa.getChildren().clear();
        for (Siswa s : daftar) {
            fpSiswa.getChildren().add(buatKartuSiswa(s));
        }

        lblKosong.setVisible(daftar.isEmpty());
        lblKosong.setManaged(daftar.isEmpty());
    }

    private VBox buatKartuSiswa(Siswa s) {
        VBox kartu = new VBox(14);
        kartu.getStyleClass().add("kaca");
        kartu.setPrefWidth(350);
        kartu.setMinWidth(320);
        kartu.setPadding(new Insets(18, 20, 18, 20));

        // Header: Avatar, Nama, Kelas
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

        barisProfil.getChildren().addAll(avatar, vbInfo);

        // Capaian: Riwayat Ujian & Progres
        List<RiwayatNilai> riwayatSiswa = rekapService.getRiwayat(s);
        int jmlUjian = riwayatSiswa.size();
        double rata = jmlUjian == 0 ? 0.0 : riwayatSiswa.stream().mapToDouble(RiwayatNilai::skor).average().orElse(0.0);
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
        btnDetail.setGraphic(new IkonView(Ikon.GRAFIK, 14));
        btnDetail.setMaxWidth(Double.MAX_VALUE);
        btnDetail.setOnAction(e -> tampilkanDetailSiswa(s, riwayatSiswa));

        kartu.getChildren().addAll(barisProfil, barisStat, btnDetail);
        return kartu;
    }

    private void tampilkanDetailSiswa(Siswa s, List<RiwayatNilai> riwayatSiswa) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Riwayat Ujian Siswa");
        dialog.setHeaderText("Riwayat Nilai: " + s.getNama() + " (" + s.getKelas() + " · NIS " + s.getNis() + ")");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox konten = new VBox(14);
        konten.setPadding(new Insets(10, 10, 10, 10));
        konten.setPrefWidth(520);

        if (riwayatSiswa.isEmpty()) {
            Label lblKosong = new Label("Siswa ini belum pernah mengerjakan ujian CBT.");
            lblKosong.setStyle("-fx-text-fill: -teks-redup; -fx-padding: 20 0; -fx-alignment: center;");
            konten.getChildren().add(lblKosong);
        } else {
            VBox daftarRiwayat = new VBox(8);
            for (RiwayatNilai r : riwayatSiswa) {
                HBox baris = new HBox(12);
                baris.setAlignment(Pos.CENTER_LEFT);
                baris.setStyle("-fx-padding: 10 14; -fx-background-color: -garis; -fx-background-radius: 10;");

                VBox vbUjian = new VBox(2);
                HBox.setHgrow(vbUjian, Priority.ALWAYS);
                Label lblMapel = new Label(r.mataPelajaran() + " (" + r.kodeUjian() + ")");
                lblMapel.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px; -fx-text-fill: -teks;");
                String modeStr = r.modeSubmit() == ModeSubmit.MANUAL ? "Manual" : "Otomatis";
                Label lblWaktu = new Label(r.waktu().format(FORMAT_WAKTU) + " · " + modeStr);
                lblWaktu.setStyle("-fx-font-size: 11.5px; -fx-text-fill: -teks-redup;");
                vbUjian.getChildren().addAll(lblMapel, lblWaktu);

                Label lblSkor = new Label(String.format("%.1f", r.skor()));
                lblSkor.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + (r.lulus() ? "-sukses" : "-bahaya") + ";");

                Label badgeStatus = new Label(r.lulus() ? "Lulus" : "Remedial");
                badgeStatus.getStyleClass().addAll("badge", r.lulus() ? "badge-sukses" : "badge-bahaya");

                baris.getChildren().addAll(vbUjian, lblSkor, badgeStatus);
                daftarRiwayat.getChildren().add(baris);
            }

            ScrollPane sp = new ScrollPane(daftarRiwayat);
            sp.setFitToWidth(true);
            sp.setMaxHeight(380);
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            konten.getChildren().add(sp);
        }

        dialog.getDialogPane().setContent(konten);
        dialog.showAndWait();
    }
}
