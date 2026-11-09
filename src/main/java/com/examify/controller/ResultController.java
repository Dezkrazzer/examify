package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.ExamResult;
import com.examify.model.SubmitMode;
import com.examify.model.GradeHistory;
import com.examify.model.Question;
import com.examify.model.AnswerStatus;
import com.examify.model.ExamStatus;
import com.examify.utils.FormatUtil;
import com.examify.view.AnimationUtil;
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
    @FXML private TableView<Question> tvPembahasan;
    @FXML private TableColumn<Question, String> colNo;
    @FXML private TableColumn<Question, String> colPertanyaan;
    @FXML private TableColumn<Question, String> colJawabanSiswa;
    @FXML private TableColumn<Question, String> colKunci;
    @FXML private TableColumn<Question, String> colHasil;
    @FXML private Button btnSertifikat;

    private final AppSession session = AppSession.getInstance();
    private ExamResult hasil;

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
        lblMode.setText(hasil.getModeSubmit() == SubmitMode.MANUAL ? "Manual" : "Otomatis");
        siapkanPembahasan();
        btnSertifikat.setDisable(!hasil.isLulus());
        AnimationUtil.berurutan(List.of(hbHasil, vbPembahasan), Duration.millis(140));
    }

    private void tampilkanStatus() {
        boolean lulus = hasil.isLulus();
        lblStatus.setText(lulus ? "Selamat, kamu lulus!" : "Belum lulus kali ini");
        lblStatus.getStyleClass().add(lulus ? "lulus" : "tidak-lulus");
        lblPesan.setText("KKM ujian ini " + FormatUtil.angka(hasil.getUjian().getKkm()) + ". "
                + (lulus ? "Sertifikat sudah bisa diunduh." : "Pelajari lagi materinya, ya."));
    }

    private void siapkanPembahasan() {
        List<Question> daftar = hasil.getUjian().getDaftarSoal();
        TableUtil.kolom(colNo, soal -> String.valueOf(daftar.indexOf(soal) + 1));
        TableUtil.kolom(colPertanyaan, Question::getPertanyaan);
        TableUtil.kolom(colJawabanSiswa, soal -> hasil.getJawaban(soal.getIdSoal()).orElse("-"));
        TableUtil.kolom(colKunci, soal -> soal.bukaKunci(ExamStatus.SELESAI));
        TableUtil.kolom(colHasil, soal -> hasil.nilaiJawaban(soal).name());
        colHasil.setCellFactory(kolom -> new SelHasil());
        TableUtil.barisBisaDibuka(tvPembahasan, colPertanyaan, colJawabanSiswa, colKunci);
        tvPembahasan.setItems(FXCollections.observableArrayList(daftar));
    }

    /** Hasil per soal ditampilkan sebagai badge hijau/merah/abu. */
    private static final class SelHasil extends TableCell<Question, String> {
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
            if (AnswerStatus.BENAR.name().equals(status)) {
                badge.getStyleClass().add("badge-sukses");
            } else if (AnswerStatus.SALAH.name().equals(status)) {
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
            Path file = session.getSertifikatService().simpan(GradeHistory.dari(hasil), hasil.getUjian());
            DialogUtil.info("Sertifikat berhasil disimpan:\n" + file.toAbsolutePath());
        } catch (IOException e) {
            DialogUtil.peringatan("Sertifikat gagal disimpan: " + e.getMessage());
        }
    }

    @FXML
    private void handleKursus() {
        session.resetUjian();
        SceneRouter.pindah(AppScreen.KURSUS_DETAIL);
    }

    @FXML
    private void handleProfil() {
        session.resetUjian();
        SceneRouter.pindah(AppScreen.PROFIL);
    }
}
