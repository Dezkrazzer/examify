package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.SoalIsian;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import com.examify.service.UjianService;
import com.examify.utils.FormatUtil;
import com.examify.utils.StringUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankSoalController {

    @FXML
    private Label lblTotalUjian;
    @FXML
    private Label lblTotalSoal;
    @FXML
    private Label lblTotalPeserta;
    @FXML
    private TextField tfCari;
    @FXML
    private FlowPane fpUjian;
    @FXML
    private Label lblKosong;

    private final AppSession session = AppSession.getInstance();
    private final UjianService ujianService = session.getUjianService();

    @FXML
    private void initialize() {
        perbaruiStatistik();
        tfCari.textProperty().addListener((obs, lama, baru) -> muatDaftarUjian());
        muatDaftarUjian();
    }

    private void perbaruiStatistik() {
        lblTotalUjian.setText(String.valueOf(ujianService.getJumlahUjian()));
        int totalSoal = ujianService.getDaftarUjian().stream()
                .mapToInt(u -> u.getDaftarSoal().size())
                .sum();
        lblTotalSoal.setText(String.valueOf(totalSoal));
        lblTotalPeserta.setText(String.valueOf(session.getRekapService().getSemuaRiwayat().size()));
    }

    private void muatDaftarUjian() {
        String kataKunci = tfCari.getText() == null ? "" : tfCari.getText().trim().toLowerCase();
        List<Ujian> daftar = ujianService.getDaftarUjian().stream()
                .filter(u -> kataKunci.isEmpty()
                        || u.getKodeUjian().toLowerCase().contains(kataKunci)
                        || u.getMataPelajaran().toLowerCase().contains(kataKunci)
                        || u.getToken().toLowerCase().contains(kataKunci))
                .toList();

        fpUjian.getChildren().clear();
        for (Ujian u : daftar) {
            fpUjian.getChildren().add(buatKartuUjian(u));
        }

        lblKosong.setVisible(daftar.isEmpty());
        lblKosong.setManaged(daftar.isEmpty());
    }

    private VBox buatKartuUjian(Ujian u) {
        VBox kartu = new VBox(14);
        kartu.getStyleClass().add("kaca");
        kartu.setPrefWidth(350);
        kartu.setMinWidth(320);
        kartu.setPadding(new Insets(18, 20, 18, 20));

        // Header: Kode & Token
        HBox barisAtas = new HBox(8);
        barisAtas.setAlignment(Pos.CENTER_LEFT);

        Label lblKode = new Label(u.getKodeUjian());
        lblKode.getStyleClass().add("badge");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnToken = new Button("Token: " + u.getToken());
        btnToken.getStyleClass().add("badge-kaca");
        btnToken.setGraphic(new IkonView(Ikon.GEMBOK, 12));
        btnToken.setOnAction(e -> {
            ClipboardContent cc = new ClipboardContent();
            cc.putString(u.getToken());
            Clipboard.getSystemClipboard().setContent(cc);
            btnToken.setText("Tersalin!");
        });

        barisAtas.getChildren().addAll(lblKode, spacer, btnToken);

        // Judul Mata Pelajaran
        Label lblMapel = new Label(u.getMataPelajaran());
        lblMapel.getStyleClass().add("judul-kartu");
        lblMapel.setWrapText(true);

        // Info ringkas: Soal, Durasi, KKM
        HBox barisInfo = new HBox(12);
        barisInfo.setAlignment(Pos.CENTER_LEFT);

        Label lblJmlSoal = new Label(u.getDaftarSoal().size() + " Soal");
        lblJmlSoal.getStyleClass().add("teks-kecil");

        Label lblDurasi = new Label(u.getDurasiMenit() + " Menit");
        lblDurasi.getStyleClass().add("teks-kecil");

        String strKkm = u.getKkm() == (long) u.getKkm() ? String.format("%d", (long) u.getKkm())
                : String.valueOf(u.getKkm());
        Label lblKkm = new Label("KKM " + strKkm);
        lblKkm.getStyleClass().add("teks-kecil");

        barisInfo.getChildren().addAll(lblJmlSoal, new Label("·"), lblDurasi, new Label("·"), lblKkm);

        // Rincian jenis soal
        int pg = (int) u.getDaftarSoal().stream().filter(s -> s instanceof SoalPilihanGanda).count();
        int tf = (int) u.getDaftarSoal().stream().filter(s -> s instanceof SoalTrueFalse).count();
        int is = (int) u.getDaftarSoal().stream().filter(s -> s instanceof SoalIsian).count();
        Label lblKomposisi = new Label(pg + " PG  ·  " + tf + " B/S  ·  " + is + " Isian");
        lblKomposisi.setStyle("-fx-font-size: 11.5px; -fx-text-fill: -teks-redup;");

        // Action buttons
        HBox barisAksi = new HBox(8);
        barisAksi.setAlignment(Pos.CENTER_LEFT);

        Button btnEdit = new Button("Edit Soal");
        btnEdit.getStyleClass().add("tombol-utama");
        HBox.setHgrow(btnEdit, Priority.ALWAYS);
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.setOnAction(e -> {
            session.setKursusAktif(null);
            session.setUjianAktif(u);
            SceneRouter.pindah(Layar.EDITOR_UJIAN);
        });

        Button btnSetting = new Button("Pengaturan");
        btnSetting.getStyleClass().add("tombol-sekunder");
        btnSetting.setOnAction(e -> handleUbahPengaturan(u));

        Button btnHapus = new Button();
        btnHapus.getStyleClass().add("tombol-bahaya");
        btnHapus.setGraphic(new IkonView(Ikon.SAMPAH, 14));
        btnHapus.setTooltip(new Tooltip("Hapus Ujian"));
        btnHapus.setOnAction(e -> handleHapusUjian(u));

        barisAksi.getChildren().addAll(btnEdit, btnSetting, btnHapus);

        if (u.hasJadwalAkses()) {
            Label lblAkses = new Label(
                    "Akses: " + FormatUtil.formatRentangWaktuAkses(u.getWaktuMulai(), u.getWaktuSelesai()));
            lblAkses.getStyleClass().add("badge");
            kartu.getChildren().addAll(barisAtas, lblMapel, barisInfo, lblKomposisi, lblAkses, barisAksi);
        } else {
            kartu.getChildren().addAll(barisAtas, lblMapel, barisInfo, lblKomposisi, barisAksi);
        }
        return kartu;
    }

    private void handleUbahPengaturan(Ujian u) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Pengaturan Ujian");
        dialog.setHeaderText("Ubah Pengaturan: " + u.getKodeUjian());

        ButtonType btnSimpan = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSimpan, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 10, 10, 10));

        TextField tfMapel = new TextField(u.getMataPelajaran());
        TextField tfDurasi = new TextField(String.valueOf(u.getDurasiMenit()));
        String strKkm = u.getKkm() == (long) u.getKkm() ? String.format("%d", (long) u.getKkm())
                : String.valueOf(u.getKkm());
        TextField tfKkm = new TextField(strKkm);
        TextField tfToken = new TextField(u.getToken());
        TextField tfAksesMulai = new TextField(FormatUtil.formatInputAkses(u.getWaktuMulai()));
        tfAksesMulai.setPromptText("yyyy-MM-dd HH:mm (opsional)");
        TextField tfAksesSelesai = new TextField(FormatUtil.formatInputAkses(u.getWaktuSelesai()));
        tfAksesSelesai.setPromptText("yyyy-MM-dd HH:mm (opsional)");

        grid.add(new Label("Mata Pelajaran:"), 0, 0);
        grid.add(tfMapel, 1, 0);
        grid.add(new Label("Durasi (menit):"), 0, 1);
        grid.add(tfDurasi, 1, 1);
        grid.add(new Label("KKM:"), 0, 2);
        grid.add(tfKkm, 1, 2);
        grid.add(new Label("Token (6 karakter):"), 0, 3);
        grid.add(tfToken, 1, 3);
        grid.add(new Label("Waktu Buka:"), 0, 4);
        grid.add(tfAksesMulai, 1, 4);
        grid.add(new Label("Waktu Tutup:"), 0, 5);
        grid.add(tfAksesSelesai, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == btnSimpan) {
            String mapelBaru = tfMapel.getText().trim();
            String tokenBaru = tfToken.getText().trim().toUpperCase();
            int durasiBaru;
            double kkmBaru;
            try {
                durasiBaru = Integer.parseInt(tfDurasi.getText().trim());
                kkmBaru = Double.parseDouble(tfKkm.getText().trim());
            } catch (NumberFormatException ex) {
                DialogUtil.peringatan("Durasi dan KKM harus angka!");
                return;
            }

            if (StringUtil.isKosong(mapelBaru) || tokenBaru.length() != 6) {
                DialogUtil.peringatan("Mata pelajaran tidak boleh kosong dan token harus 6 karakter.");
                return;
            }

            LocalDateTime mulai = FormatUtil.parseInputAkses(tfAksesMulai.getText());
            LocalDateTime selesai = FormatUtil.parseInputAkses(tfAksesSelesai.getText());
            if (mulai != null && selesai != null && mulai.isAfter(selesai)) {
                DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup");
                return;
            }

            try {
                Ujian ujianBaru = new Ujian(u.getKodeUjian(), mapelBaru, tokenBaru, durasiBaru, kkmBaru,
                        u.getDaftarSoal(), u.isBolehUlang(), mulai, selesai);
                ujianService.perbaruiUjian(ujianBaru);
                muatDaftarUjian();
                perbaruiStatistik();
                DialogUtil.info("Pengaturan ujian " + u.getKodeUjian() + " berhasil diperbarui.");
            } catch (IOException ex) {
                DialogUtil.peringatan("Gagal menyimpan perubahan: " + ex.getMessage());
            }
        }
    }

    private void handleHapusUjian(Ujian u) {
        if (DialogUtil.konfirmasi(
                "Hapus paket ujian " + u.getKodeUjian() + " (" + u.getMataPelajaran() + ") dari Bank Soal?")) {
            try {
                ujianService.hapusUjian(u.getKodeUjian());
                muatDaftarUjian();
                perbaruiStatistik();
                DialogUtil.info("Paket ujian " + u.getKodeUjian() + " berhasil dihapus.");
            } catch (IOException ex) {
                DialogUtil.peringatan("Gagal menghapus ujian: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleTambahUjianBaru() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Buat Paket Ujian Baru");
        dialog.setHeaderText("Tambahkan paket ujian CBT baru ke Bank Soal");

        ButtonType btnLanjut = new ButtonType("Buat & Rancang Soal", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnLanjut, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 10, 10, 10));

        String saranKode = "UJI-0" + (ujianService.getJumlahUjian() + 1);
        TextField tfKode = new TextField(saranKode);
        TextField tfMapel = new TextField("");
        TextField tfDurasi = new TextField("30");
        TextField tfKkm = new TextField("75");
        String saranToken = ("CBT" + (int) (Math.random() * 899 + 100)).toUpperCase();
        TextField tfToken = new TextField(saranToken);
        TextField tfAksesMulai = new TextField("");
        tfAksesMulai.setPromptText("yyyy-MM-dd HH:mm (opsional)");
        TextField tfAksesSelesai = new TextField("");
        tfAksesSelesai.setPromptText("yyyy-MM-dd HH:mm (opsional)");

        grid.add(new Label("Kode Ujian:"), 0, 0);
        grid.add(tfKode, 1, 0);
        grid.add(new Label("Mata Pelajaran:"), 0, 1);
        grid.add(tfMapel, 1, 1);
        grid.add(new Label("Durasi (menit):"), 0, 2);
        grid.add(tfDurasi, 1, 2);
        grid.add(new Label("KKM:"), 0, 3);
        grid.add(tfKkm, 1, 3);
        grid.add(new Label("Token (6 karakter):"), 0, 4);
        grid.add(tfToken, 1, 4);
        grid.add(new Label("Waktu Buka:"), 0, 5);
        grid.add(tfAksesMulai, 1, 5);
        grid.add(new Label("Waktu Tutup:"), 0, 6);
        grid.add(tfAksesSelesai, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == btnLanjut) {
            String kode = tfKode.getText().trim().toUpperCase();
            String mapel = tfMapel.getText().trim();
            String token = tfToken.getText().trim().toUpperCase();
            int durasi;
            double kkm;
            try {
                durasi = Integer.parseInt(tfDurasi.getText().trim());
                kkm = Double.parseDouble(tfKkm.getText().trim());
            } catch (NumberFormatException e) {
                DialogUtil.peringatan("Durasi dan KKM harus berupa angka!");
                return;
            }

            if (StringUtil.isKosong(kode) || StringUtil.isKosong(mapel) || token.length() != 6) {
                DialogUtil.peringatan("Semua field wajib diisi dan token harus 6 karakter!");
                return;
            }

            if (ujianService.cariUjian(kode).isPresent()) {
                DialogUtil.peringatan("Kode ujian sudah terpakai: " + kode);
                return;
            }

            LocalDateTime mulai = FormatUtil.parseInputAkses(tfAksesMulai.getText());
            LocalDateTime selesai = FormatUtil.parseInputAkses(tfAksesSelesai.getText());
            if (mulai != null && selesai != null && mulai.isAfter(selesai)) {
                DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup");
                return;
            }

            try {
                Ujian ujianBaru = new Ujian(kode, mapel, token, durasi, kkm, new ArrayList<>(), false, mulai, selesai);
                ujianService.tambahUjian(ujianBaru);
                session.setKursusAktif(null);
                session.setUjianAktif(ujianBaru);
                SceneRouter.pindah(Layar.EDITOR_UJIAN);
            } catch (IOException e) {
                DialogUtil.peringatan("Gagal membuat ujian baru: " + e.getMessage());
            }
        }
    }
}
