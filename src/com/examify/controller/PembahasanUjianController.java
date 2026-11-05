package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.RiwayatNilai;
import com.examify.model.Soal;
import com.examify.model.StatusUjian;
import com.examify.model.Ujian;
import com.examify.utils.FormatUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
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
public class PembahasanUjianController {

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

    @FXML private TableView<Soal> tvPembahasan;
    @FXML private TableColumn<Soal, String> colNo;
    @FXML private TableColumn<Soal, String> colTipe;
    @FXML private TableColumn<Soal, String> colPertanyaan;
    @FXML private TableColumn<Soal, String> colKunci;
    @FXML private TableColumn<Soal, String> colBobot;

    @FXML private Button btnSertifikat;
    @FXML private VBox vbSoalKosong;

    private final AppSession session = AppSession.getInstance();
    private RiwayatNilai riwayat;
    private Optional<Ujian> ujianOpt;

    @FXML
    private void initialize() {
        riwayat = session.getRiwayatFokus();
        if (riwayat == null) {
            SceneRouter.pindah(Layar.PROFIL);
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
            List<Soal> daftarSoal = ujianOpt.get().getDaftarSoal();
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
    }

    private void isiTabelPembahasan(List<Soal> daftarSoal) {
        TabelUtil.kolom(colNo, s -> String.valueOf(daftarSoal.indexOf(s) + 1));
        TabelUtil.kolom(colTipe, s -> s.getJenis().getLabel());
        colTipe.setCellFactory(kolom -> new TableCell<Soal, String>() {
            @Override
            protected void updateItem(String teks, boolean kosong) {
                super.updateItem(teks, kosong);
                setText(null);
                if (kosong || teks == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(teks);
                badge.getStyleClass().addAll("badge", "badge-netral");
                setGraphic(badge);
            }
        });

        TabelUtil.kolom(colPertanyaan, Soal::getPertanyaan);
        TabelUtil.kolom(colKunci, s -> s.bukaKunci(StatusUjian.SELESAI));
        TabelUtil.kolom(colBobot, s -> FormatUtil.angka(s.getBobot()) + " Poin");

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
        SceneRouter.pindah(Layar.PROFIL);
    }
}
