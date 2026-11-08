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
import com.examify.view.PemilihTanggalWaktu;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

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

        Label badgeUlang = new Label(u.isBolehUlang() ? "Bisa Diulang" : "1x Kerjakan");
        badgeUlang.getStyleClass().add(u.isBolehUlang() ? "badge" : "badge-netral");
        badgeUlang.setStyle("-fx-font-size: 11px; -fx-padding: 2 8;");

        barisAtas.getChildren().addAll(lblKode, badgeUlang, spacer, btnToken);

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

    /**
     * Pembungkus kontrol pemilih tanggal dan waktu terpadu untuk jadwal akses ujian.
     */
    private static class KontrolWaktuAkses {
        final PemilihTanggalWaktu pemilih;

        KontrolWaktuAkses(LocalDateTime waktuAwal) {
            this.pemilih = new PemilihTanggalWaktu(waktuAwal, true, "Pilih tanggal & waktu (opsional)...");
            HBox.setHgrow(this.pemilih, Priority.ALWAYS);
        }

        PemilihTanggalWaktu buatTampilan() {
            return pemilih;
        }

        LocalDateTime getWaktu() {
            return pemilih.getWaktu();
        }
    }

    private static final String KARAKTER_TOKEN = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM_TOKEN = new SecureRandom();

    private static String hasilkanTokenAcak() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(KARAKTER_TOKEN.charAt(RANDOM_TOKEN.nextInt(KARAKTER_TOKEN.length())));
        }
        return sb.toString();
    }

    private void handleUbahPengaturan(Ujian u) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(SceneRouter.getStage());
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = new DialogPane() {
            @Override
            protected Node createButtonBar() {
                return null;
            }
        };
        pane.setBackground(Background.EMPTY);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getStylesheets().setAll(SceneRouter.getScene().getStylesheets());
        pane.getButtonTypes().add(ButtonType.CLOSE);
        dialog.setDialogPane(pane);

        dialog.setOnShown(e -> {
            if (pane.getScene() != null) {
                pane.getScene().setFill(Color.TRANSPARENT);
            }
        });

        VBox kartu = new VBox(16);
        kartu.getStyleClass().add("modal-kartu");
        kartu.setPrefWidth(520);
        kartu.setMaxWidth(540);
        kartu.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.25), 30, 0.12, 0, 8));

        // Header Modal tanpa ikon dekoratif
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);

        final double[] dragOffset = new double[2];
        barisHeader.setOnMousePressed(e -> {
            dragOffset[0] = dialog.getX() - e.getScreenX();
            dragOffset[1] = dialog.getY() - e.getScreenY();
        });
        barisHeader.setOnMouseDragged(e -> {
            dialog.setX(e.getScreenX() + dragOffset[0]);
            dialog.setY(e.getScreenY() + dragOffset[1]);
        });

        VBox vbJudul = new VBox(2);
        HBox hbJudulAtas = new HBox(8);
        hbJudulAtas.setAlignment(Pos.CENTER_LEFT);
        Label lblJudul = new Label("Pengaturan Ujian");
        lblJudul.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: -teks;");
        Label badgeKode = new Label(u.getKodeUjian());
        badgeKode.getStyleClass().add("badge");
        hbJudulAtas.getChildren().addAll(lblJudul, badgeKode);

        Label lblSub = new Label("Ubah durasi, KKM, token acak, dan jadwal akses ujian CBT");
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        vbJudul.getChildren().addAll(hbJudulAtas, lblSub);

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        Button btnTutup = new Button();
        btnTutup.setGraphic(new IkonView(Ikon.SILANG, 13));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setOnAction(e -> dialog.close());

        barisHeader.getChildren().addAll(vbJudul, spacerHeader, btnTutup);

        Separator sepAtas = new Separator();
        sepAtas.setStyle("-fx-opacity: 0.4;");

        // Form Fields
        // 1. Mata Pelajaran
        VBox vbMapel = new VBox(6);
        Label lblMapel = new Label("Mata Pelajaran");
        lblMapel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        TextField tfMapel = new TextField(u.getMataPelajaran());
        tfMapel.setPromptText("cth. Bahasa Indonesia");
        vbMapel.getChildren().addAll(lblMapel, tfMapel);

        // 2. Durasi & KKM (tanpa chip pintasan)
        HBox hbDurasiKkm = new HBox(14);

        VBox vbDurasi = new VBox(6);
        HBox.setHgrow(vbDurasi, Priority.ALWAYS);
        Label lblDurasi = new Label("Durasi Ujian (menit)");
        lblDurasi.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        TextField tfDurasi = new TextField(String.valueOf(u.getDurasiMenit()));
        tfDurasi.setPromptText("Menit");
        vbDurasi.getChildren().addAll(lblDurasi, tfDurasi);

        VBox vbKkm = new VBox(6);
        HBox.setHgrow(vbKkm, Priority.ALWAYS);
        Label lblKkm = new Label("KKM Kelulusan");
        lblKkm.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        String strKkm = u.getKkm() == (long) u.getKkm() ? String.format("%d", (long) u.getKkm())
                : String.valueOf(u.getKkm());
        TextField tfKkm = new TextField(strKkm);
        tfKkm.setPromptText("Nilai KKM");
        vbKkm.getChildren().addAll(lblKkm, tfKkm);

        hbDurasiKkm.getChildren().addAll(vbDurasi, vbKkm);

        // 3. Token Ujian
        VBox vbToken = new VBox(6);
        Label lblToken = new Label("Token Akses Ujian (6 Karakter)");
        lblToken.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        TextField tfToken = new TextField(u.getToken());
        tfToken.setPromptText("TOKEN");
        tfToken.setTextFormatter(new TextFormatter<String>(change -> {
            change.setText(change.getText().toUpperCase());
            String newText = change.getControlNewText();
            return (newText.length() <= 6) ? change : null;
        }));
        tfToken.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        HBox.setHgrow(tfToken, Priority.ALWAYS);

        Button btnAcak = new Button("Acak Token");
        btnAcak.getStyleClass().add("tombol-sekunder");
        btnAcak.setOnAction(e -> tfToken.setText(hasilkanTokenAcak()));

        HBox hbToken = new HBox(10, tfToken, btnAcak);
        hbToken.setAlignment(Pos.CENTER_LEFT);
        vbToken.getChildren().addAll(lblToken, hbToken);

        // 4. Jadwal Akses CBT (Terpadu Tanggal & Waktu tanpa ikon/chip)
        VBox boxJadwal = new VBox(10);
        boxJadwal.getStyleClass().add("modal-kotak-jadwal");

        HBox hbJadwalHead = new HBox(8);
        hbJadwalHead.setAlignment(Pos.CENTER_LEFT);
        Label lblJadwalJudul = new Label("Jadwal Akses CBT (Opsional)");
        lblJadwalJudul.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: -teks;");

        Region spJadwal = new Region();
        HBox.setHgrow(spJadwal, Priority.ALWAYS);
        Label lblKetJadwal = new Label("Kosongkan jika bebas akses");
        lblKetJadwal.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");
        hbJadwalHead.getChildren().addAll(lblJadwalJudul, spJadwal, lblKetJadwal);

        KontrolWaktuAkses kontrolMulai = new KontrolWaktuAkses(u.getWaktuMulai());
        KontrolWaktuAkses kontrolSelesai = new KontrolWaktuAkses(u.getWaktuSelesai());

        VBox barisMulai = new VBox(4);
        Label lblMulai = new Label("Waktu Buka");
        lblMulai.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: -teks-redup;");
        barisMulai.getChildren().addAll(lblMulai, kontrolMulai.buatTampilan());

        VBox barisSelesai = new VBox(4);
        Label lblSelesai = new Label("Waktu Tutup");
        lblSelesai.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: -teks-redup;");
        barisSelesai.getChildren().addAll(lblSelesai, kontrolSelesai.buatTampilan());

        boxJadwal.getChildren().addAll(hbJadwalHead, barisMulai, barisSelesai);

        // 5. Pengaturan Ulang Ujian (Default: Tidak)
        VBox vbUlang = new VBox(4);
        vbUlang.getStyleClass().add("modal-kotak-jadwal");
        CheckBox cbBolehUlang = new CheckBox("Izinkan Ujian Dikerjakan Ulang");
        cbBolehUlang.setSelected(u.isBolehUlang());
        cbBolehUlang.setStyle("-fx-font-weight: bold; -fx-text-fill: -teks;");
        Label lblKetUlang = new Label("Jika tidak dicentang (default), siswa hanya dapat mengerjakan ujian ini satu kali.");
        lblKetUlang.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");
        vbUlang.getChildren().addAll(cbBolehUlang, lblKetUlang);

        VBox formContainer = new VBox(14, vbMapel, hbDurasiKkm, vbToken, boxJadwal, vbUlang);

        Separator sepBawah = new Separator();
        sepBawah.setStyle("-fx-opacity: 0.4;");

        // Footer Action Buttons
        HBox barisFooter = new HBox(10);
        barisFooter.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setOnAction(e -> dialog.close());

        Button btnSimpan = new Button("Simpan Perubahan");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setOnAction(e -> {
            String mapelBaru = tfMapel.getText().trim();
            String tokenBaru = tfToken.getText().trim().toUpperCase();
            int durasiBaru;
            double kkmBaru;
            try {
                durasiBaru = Integer.parseInt(tfDurasi.getText().trim());
                kkmBaru = Double.parseDouble(tfKkm.getText().trim());
                if (durasiBaru <= 0 || kkmBaru < 0 || kkmBaru > 100) {
                    DialogUtil.peringatan("Durasi harus lebih dari 0 dan KKM antara 0 - 100.");
                    return;
                }
            } catch (NumberFormatException ex) {
                DialogUtil.peringatan("Durasi dan KKM harus berupa angka yang valid!");
                return;
            }

            if (StringUtil.isKosong(mapelBaru)) {
                DialogUtil.peringatan("Mata pelajaran tidak boleh kosong.");
                return;
            }

            if (tokenBaru.length() != 6) {
                DialogUtil.peringatan("Token ujian harus terdiri dari 6 karakter.");
                return;
            }

            LocalDateTime mulai = kontrolMulai.getWaktu();
            LocalDateTime selesai = kontrolSelesai.getWaktu();
            if (mulai != null && selesai != null && mulai.isAfter(selesai)) {
                DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup!");
                return;
            }

            try {
                boolean bolehUlangBaru = cbBolehUlang.isSelected();
                Ujian ujianBaru = new Ujian(u.getKodeUjian(), mapelBaru, tokenBaru, durasiBaru, kkmBaru,
                        u.getDaftarSoal(), bolehUlangBaru, mulai, selesai);
                ujianService.perbaruiUjian(ujianBaru);
                muatDaftarUjian();
                perbaruiStatistik();
                dialog.close();
                DialogUtil.info("Pengaturan ujian " + u.getKodeUjian() + " berhasil diperbarui.");
            } catch (IOException ex) {
                DialogUtil.peringatan("Gagal menyimpan perubahan: " + ex.getMessage());
            }
        });

        barisFooter.getChildren().addAll(btnBatal, btnSimpan);

        kartu.getChildren().addAll(barisHeader, sepAtas, formContainer, sepBawah, barisFooter);
        pane.setContent(kartu);

        dialog.showAndWait();
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
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(SceneRouter.getStage());
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = new DialogPane() {
            @Override
            protected Node createButtonBar() {
                return null;
            }
        };
        pane.setBackground(Background.EMPTY);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getStylesheets().setAll(SceneRouter.getScene().getStylesheets());
        pane.getButtonTypes().add(ButtonType.CLOSE);
        dialog.setDialogPane(pane);

        dialog.setOnShown(e -> {
            if (pane.getScene() != null) {
                pane.getScene().setFill(Color.TRANSPARENT);
            }
        });

        VBox kartu = new VBox(16);
        kartu.getStyleClass().add("modal-kartu");
        kartu.setPrefWidth(520);
        kartu.setMaxWidth(540);
        kartu.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.25), 30, 0.12, 0, 8));

        // Header Modal tanpa ikon dekoratif
        HBox barisHeader = new HBox(12);
        barisHeader.setAlignment(Pos.CENTER_LEFT);

        final double[] dragOffset = new double[2];
        barisHeader.setOnMousePressed(e -> {
            dragOffset[0] = dialog.getX() - e.getScreenX();
            dragOffset[1] = dialog.getY() - e.getScreenY();
        });
        barisHeader.setOnMouseDragged(e -> {
            dialog.setX(e.getScreenX() + dragOffset[0]);
            dialog.setY(e.getScreenY() + dragOffset[1]);
        });

        VBox vbJudul = new VBox(2);
        Label lblJudul = new Label("Buat Paket Ujian Baru");
        lblJudul.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: -teks;");
        Label lblSub = new Label("Tambahkan paket ujian CBT baru ke Bank Soal");
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        vbJudul.getChildren().addAll(lblJudul, lblSub);

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        Button btnTutup = new Button();
        btnTutup.setGraphic(new IkonView(Ikon.SILANG, 13));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setOnAction(e -> dialog.close());

        barisHeader.getChildren().addAll(vbJudul, spacerHeader, btnTutup);

        Separator sepAtas = new Separator();
        sepAtas.setStyle("-fx-opacity: 0.4;");

        // Form Fields
        // 1. Kode Ujian & Mata Pelajaran
        HBox hbKodeMapel = new HBox(14);

        VBox vbKode = new VBox(6);
        vbKode.setPrefWidth(160);
        Label lblKode = new Label("Kode Ujian");
        lblKode.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        String saranKode = "UJI-0" + (ujianService.getJumlahUjian() + 1);
        TextField tfKode = new TextField(saranKode);
        tfKode.setPromptText("cth. UJI-08");
        tfKode.setTextFormatter(new TextFormatter<String>(change -> {
            change.setText(change.getText().toUpperCase());
            String newText = change.getControlNewText();
            return (newText.length() <= 10) ? change : null;
        }));
        tfKode.setStyle("-fx-font-weight: bold;");
        vbKode.getChildren().addAll(lblKode, tfKode);

        VBox vbMapel = new VBox(6);
        HBox.setHgrow(vbMapel, Priority.ALWAYS);
        Label lblMapel = new Label("Mata Pelajaran");
        lblMapel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        TextField tfMapel = new TextField("");
        tfMapel.setPromptText("cth. Matematika, Fisika...");
        vbMapel.getChildren().addAll(lblMapel, tfMapel);

        hbKodeMapel.getChildren().addAll(vbKode, vbMapel);

        // 2. Durasi & KKM (tanpa chip pintasan)
        HBox hbDurasiKkm = new HBox(14);

        VBox vbDurasi = new VBox(6);
        HBox.setHgrow(vbDurasi, Priority.ALWAYS);
        Label lblDurasi = new Label("Durasi Ujian (menit)");
        lblDurasi.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        TextField tfDurasi = new TextField("30");
        tfDurasi.setPromptText("Menit");
        vbDurasi.getChildren().addAll(lblDurasi, tfDurasi);

        VBox vbKkm = new VBox(6);
        HBox.setHgrow(vbKkm, Priority.ALWAYS);
        Label lblKkm = new Label("KKM Kelulusan");
        lblKkm.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        TextField tfKkm = new TextField("75");
        tfKkm.setPromptText("Nilai KKM");
        vbKkm.getChildren().addAll(lblKkm, tfKkm);

        hbDurasiKkm.getChildren().addAll(vbDurasi, vbKkm);

        // 3. Token Ujian
        VBox vbToken = new VBox(6);
        Label lblToken = new Label("Token Akses Ujian (6 Karakter)");
        lblToken.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -teks-redup;");
        TextField tfToken = new TextField(hasilkanTokenAcak());
        tfToken.setPromptText("TOKEN");
        tfToken.setTextFormatter(new TextFormatter<String>(change -> {
            change.setText(change.getText().toUpperCase());
            String newText = change.getControlNewText();
            return (newText.length() <= 6) ? change : null;
        }));
        tfToken.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        HBox.setHgrow(tfToken, Priority.ALWAYS);

        Button btnAcak = new Button("Acak Token");
        btnAcak.getStyleClass().add("tombol-sekunder");
        btnAcak.setOnAction(e -> tfToken.setText(hasilkanTokenAcak()));

        HBox hbToken = new HBox(10, tfToken, btnAcak);
        hbToken.setAlignment(Pos.CENTER_LEFT);
        vbToken.getChildren().addAll(lblToken, hbToken);

        // 4. Jadwal Akses CBT (Terpadu Tanggal & Waktu tanpa ikon/chip)
        VBox boxJadwal = new VBox(10);
        boxJadwal.getStyleClass().add("modal-kotak-jadwal");

        HBox hbJadwalHead = new HBox(8);
        hbJadwalHead.setAlignment(Pos.CENTER_LEFT);
        Label lblJadwalJudul = new Label("Jadwal Akses CBT (Opsional)");
        lblJadwalJudul.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: -teks;");

        Region spJadwal = new Region();
        HBox.setHgrow(spJadwal, Priority.ALWAYS);
        Label lblKetJadwal = new Label("Kosongkan jika bebas akses");
        lblKetJadwal.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");
        hbJadwalHead.getChildren().addAll(lblJadwalJudul, spJadwal, lblKetJadwal);

        KontrolWaktuAkses kontrolMulai = new KontrolWaktuAkses(null);
        KontrolWaktuAkses kontrolSelesai = new KontrolWaktuAkses(null);

        VBox barisMulai = new VBox(4);
        Label lblMulai = new Label("Waktu Buka");
        lblMulai.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: -teks-redup;");
        barisMulai.getChildren().addAll(lblMulai, kontrolMulai.buatTampilan());

        VBox barisSelesai = new VBox(4);
        Label lblSelesai = new Label("Waktu Tutup");
        lblSelesai.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: -teks-redup;");
        barisSelesai.getChildren().addAll(lblSelesai, kontrolSelesai.buatTampilan());

        boxJadwal.getChildren().addAll(hbJadwalHead, barisMulai, barisSelesai);

        // 5. Pengaturan Ulang Ujian (Default: Tidak)
        VBox vbUlang = new VBox(4);
        vbUlang.getStyleClass().add("modal-kotak-jadwal");
        CheckBox cbBolehUlang = new CheckBox("Izinkan Ujian Dikerjakan Ulang");
        cbBolehUlang.setSelected(false);
        cbBolehUlang.setStyle("-fx-font-weight: bold; -fx-text-fill: -teks;");
        Label lblKetUlang = new Label("Bawaan: Tidak (Siswa hanya dapat mengerjakan ujian ini satu kali).");
        lblKetUlang.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup;");
        vbUlang.getChildren().addAll(cbBolehUlang, lblKetUlang);

        VBox formContainer = new VBox(14, hbKodeMapel, hbDurasiKkm, vbToken, boxJadwal, vbUlang);

        Separator sepBawah = new Separator();
        sepBawah.setStyle("-fx-opacity: 0.4;");

        // Footer Action Buttons
        HBox barisFooter = new HBox(10);
        barisFooter.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setOnAction(e -> dialog.close());

        Button btnLanjut = new Button("Buat & Rancang Soal");
        btnLanjut.getStyleClass().add("tombol-utama");
        btnLanjut.setOnAction(e -> {
            String kode = tfKode.getText().trim().toUpperCase();
            String mapel = tfMapel.getText().trim();
            String token = tfToken.getText().trim().toUpperCase();
            int durasi;
            double kkm;
            try {
                durasi = Integer.parseInt(tfDurasi.getText().trim());
                kkm = Double.parseDouble(tfKkm.getText().trim());
                if (durasi <= 0 || kkm < 0 || kkm > 100) {
                    DialogUtil.peringatan("Durasi harus lebih dari 0 dan KKM antara 0 - 100.");
                    return;
                }
            } catch (NumberFormatException ex) {
                DialogUtil.peringatan("Durasi dan KKM harus berupa angka yang valid!");
                return;
            }

            if (StringUtil.isKosong(kode) || StringUtil.isKosong(mapel) || token.length() != 6) {
                DialogUtil.peringatan("Kode, mata pelajaran wajib diisi dan token harus 6 karakter!");
                return;
            }

            if (ujianService.cariUjian(kode).isPresent()) {
                DialogUtil.peringatan("Kode ujian sudah terpakai: " + kode);
                return;
            }

            LocalDateTime mulai = kontrolMulai.getWaktu();
            LocalDateTime selesai = kontrolSelesai.getWaktu();
            if (mulai != null && selesai != null && mulai.isAfter(selesai)) {
                DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup!");
                return;
            }

            try {
                boolean bolehUlang = cbBolehUlang.isSelected();
                Ujian ujianBaru = new Ujian(kode, mapel, token, durasi, kkm, new ArrayList<>(), bolehUlang, mulai, selesai);
                ujianService.tambahUjian(ujianBaru);
                session.setKursusAktif(null);
                session.setUjianAktif(ujianBaru);
                dialog.close();
                SceneRouter.pindah(Layar.EDITOR_UJIAN);
            } catch (IOException ex) {
                DialogUtil.peringatan("Gagal membuat ujian baru: " + ex.getMessage());
            }
        });

        barisFooter.getChildren().addAll(btnBatal, btnLanjut);

        kartu.getChildren().addAll(barisHeader, sepAtas, formContainer, sepBawah, barisFooter);
        pane.setContent(kartu);

        dialog.showAndWait();
    }
}
