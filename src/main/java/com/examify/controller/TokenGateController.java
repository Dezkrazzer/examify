package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.exception.TokenTidakValidException;
import com.examify.model.Ujian;
import com.examify.service.UjianService;
import com.examify.utils.AppConfig;
import com.examify.utils.FormatUtil;
import com.examify.view.AnimasiUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.util.List;

public class TokenGateController {

    private static final int PANJANG_TOKEN = 6;

    @FXML private VBox vbKartu;
    @FXML private Label lblJudulUjian;
    @FXML private Label lblDurasi;
    @FXML private Label lblJumlahSoal;
    @FXML private Label lblKkm;
    @FXML private VBox vbAturan;
    @FXML private TextField tfToken;
    @FXML private Label lblSisaPercobaan;

    private final AppSession session = AppSession.getInstance();
    private final UjianService ujianService = session.getUjianService();
    private Ujian ujian;

    @FXML
    private void initialize() {
        ujian = session.getUjianAktif();
        lblJudulUjian.setText(ujian.getMataPelajaran() + " - " + ujian.getKodeUjian());
        lblDurasi.setText(ujian.getDurasiMenit() + " menit");
        lblJumlahSoal.setText(String.valueOf(ujian.getJumlahSoal()));
        lblKkm.setText(FormatUtil.angka(ujian.getKkm()));
        isiAturan();
        lblSisaPercobaan.setText("Maksimal " + AppConfig.MAKS_PERCOBAAN_TOKEN + " kali percobaan.");
        tfToken.setTextFormatter(new TextFormatter<String>(ubah -> {
            ubah.setText(ubah.getText().toUpperCase());
            return ubah.getControlNewText().length() <= PANJANG_TOKEN ? ubah : null;
        }));
    }

    private void isiAturan() {
        List<String> aturan = List.of(
                "Waktu mulai dihitung sejak token diterima.",
                "Jawaban tersimpan otomatis setiap kali dipilih atau diketik.",
                "Saat waktu habis, jawaban dikumpulkan otomatis.",
                ujian.isBolehUlang() ? "Ujian ini boleh dikerjakan ulang." : "Ujian ini hanya dapat dikerjakan satu kali.");
        for (String teks : aturan) {
            Label label = new Label(teks, new IkonView(Ikon.CENTANG, 14));
            label.getStyleClass().add("teks-redup");
            label.setWrapText(true);
            vbAturan.getChildren().add(label);
        }
        if (ujian.hasJadwalAkses()) {
            Label labelAkses = new Label("Waktu Akses: " + FormatUtil.formatRentangWaktuAkses(ujian.getWaktuMulai(), ujian.getWaktuSelesai()),
                    new IkonView(Ikon.JAM, 14));
            labelAkses.getStyleClass().add("teks-redup");
            labelAkses.setWrapText(true);
            vbAturan.getChildren().add(labelAkses);
        }
    }

    @FXML
    private void handleVerifikasi() {
        if (!ujian.isAksesDibuka()) {
            String pesan = ujianService.getPesanStatusAkses(ujian);
            DialogUtil.peringatan(pesan != null ? pesan : "Ujian saat ini tidak dapat diakses.");
            return;
        }

        try {
            ujianService.verifikasiToken(session.getSiswaAktif(), ujian, tfToken.getText());
            session.setHasilAktif(ujianService.mulaiUjian(session.getSiswaAktif(), ujian));
            SceneRouter.pindah(Layar.CBT_ROOM);
        } catch (TokenTidakValidException e) {
            tanganiTokenSalah(e);
        }
    }

    private void tanganiTokenSalah(TokenTidakValidException e) {
        AnimasiUtil.getar(vbKartu);
        if (e.getSisaPercobaan() > 0) {
            lblSisaPercobaan.setText(e.getMessage());
            lblSisaPercobaan.getStyleClass().setAll("label-error");
            tfToken.selectAll();
            tfToken.requestFocus();
            return;
        }
        DialogUtil.peringatan(e.getMessage());
        handleKembali();
    }

    @FXML
    private void handleKembali() {
        session.setUjianAktif(null);
        SceneRouter.pindah(Layar.KURSUS_DETAIL);
    }
}
