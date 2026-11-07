package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.HasilUjian;
import com.examify.model.ModeSubmit;
import com.examify.model.RiwayatNilai;
import com.examify.model.Soal;
import com.examify.model.StatusJawaban;
import com.examify.model.StatusUjian;
import com.examify.utils.FormatUtil;
import com.examify.view.AnimasiUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ResultController {

    @FXML private Node hbHasil;
    @FXML private Node vbPembahasan;
    @FXML private Label lblJudulUjian;
    @FXML private Label lblSkor;
    @FXML private Label lblPredikat;
    @FXML private Label lblStatus;
    @FXML private Label lblPesan;
    @FXML private Label lblBenar;
    @FXML private Label lblSalah;
    @FXML private Label lblKosong;
    @FXML private Label lblDurasi;
    @FXML private Label lblMode;
    @FXML private TableView<Soal> tvPembahasan;
    @FXML private TableColumn<Soal, String> colNo;
    @FXML private TableColumn<Soal, String> colPertanyaan;
    @FXML private TableColumn<Soal, String> colJawabanSiswa;
    @FXML private TableColumn<Soal, String> colKunci;
    @FXML private TableColumn<Soal, String> colHasil;
    @FXML private Button btnSertifikat;

    private final AppSession session = AppSession.getInstance();
    private HasilUjian hasil;

    @FXML
    private void initialize() {
        hasil = session.getHasilAktif();
        lblJudulUjian.setText(hasil.getUjian().getMataPelajaran() + " - " + hasil.getUjian().getKodeUjian());
        lblSkor.setText(FormatUtil.angka(hasil.getSkor()));
        lblPredikat.setText("Predikat " + hasil.getPredikat());
        tampilkanStatus();
        lblBenar.setText(String.valueOf(hasil.getJumlahBenar()));
        lblSalah.setText(String.valueOf(hasil.getJumlahSalah()));
        lblKosong.setText(String.valueOf(hasil.getJumlahKosong()));
        lblDurasi.setText(FormatUtil.hhmmss(hasil.getDurasiPengerjaan()));
        lblMode.setText(hasil.getModeSubmit() == ModeSubmit.MANUAL ? "Manual" : "Otomatis");
        siapkanPembahasan();
        btnSertifikat.setDisable(!hasil.isLulus());
        AnimasiUtil.berurutan(List.of(hbHasil, vbPembahasan), Duration.millis(140));
    }

    private void tampilkanStatus() {
        boolean lulus = hasil.isLulus();
        lblStatus.setText(lulus ? "Selamat, kamu lulus!" : "Belum lulus kali ini");
        lblStatus.getStyleClass().add(lulus ? "lulus" : "tidak-lulus");
        lblPesan.setText("KKM ujian ini " + FormatUtil.angka(hasil.getUjian().getKkm()) + ". "
                + (lulus ? "Sertifikat sudah bisa diunduh."
                : hasil.getUjian().isBolehUlang() ? "Ujian ini boleh dikerjakan ulang." : "Pelajari lagi materinya, ya."));
    }

    private void siapkanPembahasan() {
        List<Soal> daftar = hasil.getUjian().getDaftarSoal();
        TabelUtil.kolom(colNo, soal -> String.valueOf(daftar.indexOf(soal) + 1));
        TabelUtil.kolom(colPertanyaan, Soal::getPertanyaan);
        TabelUtil.kolom(colJawabanSiswa, soal -> hasil.getJawaban(soal.getIdSoal()).orElse("-"));
        TabelUtil.kolom(colKunci, soal -> soal.bukaKunci(StatusUjian.SELESAI));
        TabelUtil.kolom(colHasil, soal -> hasil.nilaiJawaban(soal).name());
        colHasil.setCellFactory(kolom -> new SelHasil());
        tvPembahasan.setItems(FXCollections.observableArrayList(daftar));
    }

    /** Hasil per soal ditampilkan sebagai badge hijau/merah/abu. */
    private static final class SelHasil extends TableCell<Soal, String> {
        @Override
        protected void updateItem(String status, boolean kosong) {
            super.updateItem(status, kosong);
            setText(null);
            if (kosong || status == null) {
                setGraphic(null);
                return;
            }
            Label badge = new Label(status.charAt(0) + status.substring(1).toLowerCase());
            badge.getStyleClass().add("badge");
            if (StatusJawaban.BENAR.name().equals(status)) {
                badge.getStyleClass().add("badge-sukses");
            } else if (StatusJawaban.SALAH.name().equals(status)) {
                badge.getStyleClass().add("badge-bahaya");
            } else {
                badge.getStyleClass().add("badge-netral");
            }
            setGraphic(badge);
        }
    }

    @FXML
    private void handleSertifikat() {
        try {
            Path file = session.getSertifikatService().simpan(RiwayatNilai.dari(hasil), hasil.getUjian());
            DialogUtil.info("Sertifikat berhasil disimpan:\n" + file.toAbsolutePath());
        } catch (IOException e) {
            DialogUtil.peringatan("Sertifikat gagal disimpan: " + e.getMessage());
        }
    }

    @FXML
    private void handleKursus() {
        session.resetUjian();
        SceneRouter.pindah(Layar.KURSUS_DETAIL);
    }

    @FXML
    private void handleProfil() {
        session.resetUjian();
        SceneRouter.pindah(Layar.PROFIL);
    }
}
