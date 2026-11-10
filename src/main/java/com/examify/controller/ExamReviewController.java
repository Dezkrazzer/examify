package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.GradeHistory;
import com.examify.model.Question;
import com.examify.model.ExamStatus;
import com.examify.model.Exam;
import com.examify.utils.FormatUtil;
import com.examify.view.Icon;
import com.examify.view.IconView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/** Halaman evaluasi dan pembahasan soal ujian CBT untuk Siswa. */
public class ExamReviewController {

    @FXML private Label lblMataPelajaran;
    @FXML private Label lblKodeUjian;
    @FXML private Label lblTanggal;
    @FXML private Label lblStatus;
    @FXML private Label lblSkor;

    @FXML private Label lblBenar;
    @FXML private Label lblSalah;
    @FXML private Label lblKosong;
    @FXML private Label lblDurasi;
    @FXML private Label lblKkm;

    @FXML private TableView<Question> tvPembahasan;
    @FXML private TableColumn<Question, String> colNo;
    @FXML private TableColumn<Question, String> colTipe;
    @FXML private TableColumn<Question, String> colPertanyaan;
    @FXML private TableColumn<Question, String> colKunci;
    @FXML private TableColumn<Question, String> colBobot;

    @FXML private Button btnKembali;
    @FXML private Button btnSertifikat;
    @FXML private VBox vbSoalKosong;

    private final AppSession session = AppSession.getInstance();
    private GradeHistory riwayat;
    private Optional<Exam> ujianOpt;

    @FXML
    private void initialize() {
        riwayat = session.getRiwayatFokus();
        if (riwayat == null) {
            SceneRouter.pindah(AppScreen.PROFIL);
            return;
        }

        ujianOpt = session.getUjianService().cariUjian(riwayat.kodeUjian());

        // Header identitas ujian
        lblMataPelajaran.setText(riwayat.mataPelajaran());
        lblKodeUjian.setText("Kode: " + riwayat.kodeUjian());
        lblTanggal.setText(FormatUtil.tanggalSingkat(riwayat.waktu()));
        lblStatus.setText(riwayat.lulus() ? "Lulus CBT" : "Perlu Remedial");
        lblStatus.getStyleClass().addAll("badge", riwayat.lulus() ? "badge-sukses" : "badge-bahaya");
        lblSkor.setText(FormatUtil.angka(riwayat.skor()));

        // Statistik detail
        lblBenar.setText(String.valueOf(riwayat.benar()));
        lblSalah.setText(String.valueOf(riwayat.salah()));
        lblKosong.setText(String.valueOf(riwayat.kosong()));
        lblDurasi.setText(FormatUtil.hhmmss(riwayat.durasi()));

        if (ujianOpt.isPresent()) {
            lblKkm.setText(FormatUtil.angka(ujianOpt.get().getKkm()));
            List<Question> daftarSoal = ujianOpt.get().getDaftarSoal();
            isiTabelPembahasan(daftarSoal);
        } else {
            lblKkm.setText("-");
            tvPembahasan.setVisible(false);
            tvPembahasan.setManaged(false);
            vbSoalKosong.setVisible(true);
            vbSoalKosong.setManaged(true);
        }

        if (btnSertifikat != null) {
            btnSertifikat.setVisible(riwayat.lulus());
            btnSertifikat.setManaged(riwayat.lulus());
            btnSertifikat.setOnAction(e -> handleSertifikat());
        }

        if (btnKembali != null) {
            boolean dariKursus = session.getLayarAsalPembahasan() == AppScreen.KURSUS_DETAIL && session.getKursusAktif() != null;
            btnKembali.setText(dariKursus ? "Kembali ke Kursus" : "Kembali ke Profil & Nilai");
        }
    }

    private void isiTabelPembahasan(List<Question> daftarSoal) {
        TableUtil.kolom(colNo, s -> String.valueOf(daftarSoal.indexOf(s) + 1));
        TableUtil.kolom(colTipe, s -> s.getJenis().getLabel());
        TableUtil.kolom(colPertanyaan, Question::getPertanyaan);
        TableUtil.kolom(colKunci, s -> s.bukaKunci(ExamStatus.SELESAI));
        TableUtil.kolom(colBobot, s -> FormatUtil.angka(s.getBobot()) + " Poin");
        TableUtil.barisBisaDibuka(tvPembahasan, colPertanyaan, colKunci);

        tvPembahasan.setItems(FXCollections.observableArrayList(daftarSoal));
    }

    @FXML
    private void handleSertifikat() {
        if (ujianOpt.isEmpty()) {
            DialogUtil.peringatan("Ujian sudah tidak terdaftar di bank soal.");
            return;
        }
        try {
            Path file = session.getSertifikatService().simpan(riwayat, ujianOpt.get());
            DialogUtil.info("Sertifikat berhasil disimpan:\n" + file.toAbsolutePath());
        } catch (IOException e) {
            DialogUtil.peringatan("Sertifikat gagal disimpan: " + e.getMessage());
        }
    }

    @FXML
    private void handleKembali() {
        boolean dariKursus = session.getLayarAsalPembahasan() == AppScreen.KURSUS_DETAIL && session.getKursusAktif() != null;
        session.setLayarAsalPembahasan(AppScreen.PROFIL);
        if (dariKursus) {
            SceneRouter.pindah(AppScreen.KURSUS_DETAIL);
        } else {
            SceneRouter.pindah(AppScreen.PROFIL);
        }
    }
}
