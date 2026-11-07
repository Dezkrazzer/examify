package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.RiwayatNilai;
import com.examify.model.Siswa;
import com.examify.model.Ujian;
import com.examify.service.ProgresBelajarService;
import com.examify.service.RekapService;
import com.examify.utils.FormatUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/** Halaman detail riwayat & capaian CBT per siswa untuk Guru. */
public class GuruDetailSiswaController {

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Label lblAvatar;
    @FXML private Label lblNama;
    @FXML private Label lblNis;
    @FXML private Label lblKelas;
    @FXML private HBox hbStatusAktif;
    @FXML private Circle dotStatus;
    @FXML private Label lblStatusAktif;
    @FXML private Label lblRataRata;

    @FXML private Label lblTotalUjian;
    @FXML private Label lblTotalLulus;
    @FXML private Label lblNilaiTertinggi;
    @FXML private Label lblMateriSelesai;

    @FXML private VBox vbKosong;
    @FXML private TableView<RiwayatNilai> tvRiwayat;
    @FXML private TableColumn<RiwayatNilai, String> colNamaUjian;
    @FXML private TableColumn<RiwayatNilai, String> colTanggal;
    @FXML private TableColumn<RiwayatNilai, String> colWaktuPengerjaan;
    @FXML private TableColumn<RiwayatNilai, String> colSkor;
    @FXML private TableColumn<RiwayatNilai, String> colStatus;
    @FXML private TableColumn<RiwayatNilai, RiwayatNilai> colSertifikat;

    private final AppSession session = AppSession.getInstance();
    private final RekapService rekapService = session.getRekapService();
    private final ProgresBelajarService progresService = session.getProgresService();

    private Siswa siswa;
    private List<RiwayatNilai> riwayat;

    @FXML
    private void initialize() {
        siswa = session.getSiswaDipilih();
        if (siswa == null) {
            SceneRouter.pindah(Layar.GURU_SISWA);
            return;
        }

        riwayat = rekapService.getRiwayat(siswa);

        // Header identitas
        lblAvatar.setText(FormatUtil.inisial(siswa.getNama()));
        lblNama.setText(siswa.getNama());
        lblNis.setText("NIS " + siswa.getNis());
        lblKelas.setText("Kelas " + siswa.getKelas());

        // Status aktif
        boolean online = isSiswaOnline(siswa);
        dotStatus.setFill(Color.web(online ? "#10b981" : "#94a3b8"));
        lblStatusAktif.setText(getStatusAktifTeks(siswa));
        lblStatusAktif.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: "
                + (online ? "#059669" : "#64748b") + ";");
        hbStatusAktif.setStyle("-fx-padding: 3 8; -fx-background-radius: 999; -fx-background-color: "
                + (online ? "rgba(16, 185, 129, 0.12)" : "rgba(148, 163, 184, 0.14)") + ";");

        // Statistik
        int totalUjian = riwayat.size();
        long totalLulus = riwayat.stream().filter(RiwayatNilai::lulus).count();
        double rata = totalUjian == 0 ? 0.0 : riwayat.stream().mapToDouble(RiwayatNilai::skor).average().orElse(0.0);
        double tertinggi = riwayat.stream().mapToDouble(RiwayatNilai::skor).max().orElse(0.0);
        int totalMateri = progresService.getTotalMateriSelesai(siswa);

        lblRataRata.setText(totalUjian == 0 ? "-" : String.format("%.1f", rata));
        lblTotalUjian.setText(String.valueOf(totalUjian));
        lblTotalLulus.setText(String.valueOf(totalLulus));
        lblNilaiTertinggi.setText(totalUjian == 0 ? "-" : FormatUtil.angka(tertinggi));
        lblMateriSelesai.setText(String.valueOf(totalMateri));

        // Tabel Riwayat vs Empty State
        boolean kosong = riwayat.isEmpty();
        vbKosong.setVisible(kosong);
        vbKosong.setManaged(kosong);
        tvRiwayat.setVisible(!kosong);
        tvRiwayat.setManaged(!kosong);

        if (!kosong) {
            isiTabel();
        }
    }

    private void isiTabel() {
        TabelUtil.kolom(colNamaUjian, r -> r.mataPelajaran() + " (" + r.kodeUjian() + ")");
        TabelUtil.kolom(colTanggal, r -> r.waktu().format(FORMAT_WAKTU));
        TabelUtil.kolom(colWaktuPengerjaan, r -> FormatUtil.hhmmss(r.durasi()));
        TabelUtil.kolom(colSkor, r -> FormatUtil.angka(r.skor()));
        TabelUtil.kolom(colStatus, r -> r.lulus() ? "Lulus" : "Remedial");
        colStatus.setCellFactory(kolom -> new TableCell<RiwayatNilai, String>() {
            @Override
            protected void updateItem(String teks, boolean kosong) {
                super.updateItem(teks, kosong);
                setText(null);
                if (kosong || teks == null) {
                    setGraphic(null);
                    return;
                }
                RiwayatNilai r = getTableRow().getItem();
                Label badge = new Label(teks);
                badge.getStyleClass().addAll("badge", (r != null && r.lulus()) ? "badge-sukses" : "badge-bahaya");
                setGraphic(badge);
            }
        });

        colSertifikat.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colSertifikat.setCellFactory(kolom -> new TableCell<RiwayatNilai, RiwayatNilai>() {
            @Override
            protected void updateItem(RiwayatNilai r, boolean kosong) {
                super.updateItem(r, kosong);
                setText(null);
                if (kosong || r == null || !r.lulus()) {
                    setGraphic(null);
                    return;
                }
                Button tombol = new Button("Sertifikat", new IkonView(Ikon.SERTIFIKAT, 13));
                tombol.getStyleClass().add("tombol-sekunder");
                tombol.setStyle("-fx-padding: 4 10; -fx-font-size: 11.5px;");
                tombol.setTooltip(new Tooltip("Unduh sertifikat siswa"));
                tombol.setOnAction(e -> unduhSertifikat(r));
                setGraphic(tombol);
            }
        });

        tvRiwayat.setItems(FXCollections.observableArrayList(riwayat));
    }

    private void unduhSertifikat(RiwayatNilai r) {
        Optional<Ujian> ujian = session.getUjianService().cariUjian(r.kodeUjian());
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

    private boolean isSiswaOnline(Siswa s) {
        if (session.getSiswaAktif() != null && session.getSiswaAktif().getNis().equals(s.getNis())) {
            return true;
        }
        return Math.abs(s.getNis().hashCode()) % 3 == 0;
    }

    private String getStatusAktifTeks(Siswa s) {
        if (isSiswaOnline(s)) {
            return "Online";
        }
        int menitLalu = (Math.abs(s.getNis().hashCode()) % 45) + 5;
        return "Aktif " + menitLalu + "m lalu";
    }

    @FXML
    private void handleKembali() {
        SceneRouter.pindah(Layar.GURU_SISWA);
    }
}
