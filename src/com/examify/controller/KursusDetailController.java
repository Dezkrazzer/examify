package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.Bab;
import com.examify.model.Guru;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriPdf;
import com.examify.model.MateriTautan;
import com.examify.model.MateriTeks;
import com.examify.model.MateriUjian;
import com.examify.model.RiwayatNilai;
import com.examify.model.Siswa;
import com.examify.model.Soal;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.Ujian;
import com.examify.service.KursusService;
import com.examify.service.ProgresBelajarService;
import com.examify.utils.FormatUtil;
import com.examify.utils.StringUtil;
import com.examify.view.AnimasiUtil;
import com.examify.view.Ikon;
import com.examify.view.IkonView;
import com.examify.view.MarkdownView;
import com.examify.view.PolaKursus;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Halaman satu kursus: daftar bab di kiri, isi bab / bacaan / info ujian di kanan. */
public class KursusDetailController {

    @FXML private StackPane spPola;
    @FXML private Label lblKode;
    @FXML private Label lblNama;
    @FXML private Label lblPengajar;
    @FXML private Label lblDeskripsi;
    @FXML private Label lblJudulProgres;
    @FXML private Label lblPersen;
    @FXML private Label lblRingkasProgres;
    @FXML private ProgressBar pbProgres;
    @FXML private VBox vbBab;
    @FXML private StackPane spKonten;
    @FXML private ToggleButton tabMateri;
    @FXML private ToggleButton tabOrang;
    @FXML private HBox boxTabMateri;
    @FXML private ScrollPane spTabOrang;
    @FXML private VBox vbTabOrang;

    private final AppSession session = AppSession.getInstance();
    private final KursusService kursusService = session.getKursusService();
    private final ProgresBelajarService progresService = session.getProgresService();
    private final Map<Bab, ToggleButton> tombolBab = new LinkedHashMap<>();
    private final ToggleGroup grupBab = new ToggleGroup();
    private final ToggleGroup grupTab = new ToggleGroup();
    private Kursus kursus;
    private Siswa siswa;

    @FXML
    private void initialize() {
        kursus = session.getKursusAktif();
        siswa = session.getSiswaAktif();
        spPola.getChildren().add(PolaKursus.buat(kursus.getKode(), 84, 84, 20));
        lblKode.setText(kursus.getKode());
        lblNama.setText(kursus.getNama());
        lblPengajar.setText(kursus.getPengajar());
        lblDeskripsi.setText(kursus.getDeskripsi());

        tabMateri.setToggleGroup(grupTab);
        tabOrang.setToggleGroup(grupTab);
        tabMateri.setSelected(true);
        grupTab.selectedToggleProperty().addListener((obs, lama, baru) -> {
            if (baru == null) {
                grupTab.selectToggle(lama);
            }
        });

        buatDaftarBab();
        perbaruiProgres();

        Materi fokus = session.getMateriFokus();
        if (fokus != null && babDari(fokus).isPresent()) {
            bukaMateri(fokus);
        } else {
            tampilkanBab(babPertamaBelumSelesai());
        }
    }

    @FXML
    private void handlePilihTabMateri() {
        tabMateri.setSelected(true);
        boxTabMateri.setVisible(true);
        boxTabMateri.setManaged(true);
        spTabOrang.setVisible(false);
        spTabOrang.setManaged(false);
    }

    @FXML
    private void handlePilihTabOrang() {
        tabOrang.setSelected(true);
        boxTabMateri.setVisible(false);
        boxTabMateri.setManaged(false);
        spTabOrang.setVisible(true);
        spTabOrang.setManaged(true);
        muatTabOrang();
    }

    // ------------------------------------------------------------------ daftar bab

    private void buatDaftarBab() {
        vbBab.getChildren().clear();
        tombolBab.clear();
        for (Bab bab : kursus.getDaftarBab()) {
            ToggleButton tombol = new ToggleButton(bab.getJudul());
            tombol.getStyleClass().add("item-bab");
            tombol.setToggleGroup(grupBab);
            tombol.setMaxWidth(Double.MAX_VALUE);
            tombol.setWrapText(true);
            tombol.setOnAction(e -> tampilkanBab(bab));
            tombolBab.put(bab, tombol);
            vbBab.getChildren().add(tombol);
        }
        if (session.isGuru()) {
            VBox boxAksiGuru = new VBox(8);
            boxAksiGuru.setPadding(new Insets(8, 0, 0, 0));

            Button btnTambahBab = new Button("+ Tambah Bab");
            btnTambahBab.getStyleClass().addAll("tombol-sekunder");
            btnTambahBab.setMaxWidth(Double.MAX_VALUE);
            btnTambahBab.setOnAction(e -> handleTambahBab());

            Button btnTambahUjian = new Button("Tambah Ujian");
            btnTambahUjian.getStyleClass().addAll("tombol-utama");
            btnTambahUjian.setMaxWidth(Double.MAX_VALUE);
            btnTambahUjian.setOnAction(e -> handleTambahUjianLangsung());

            boxAksiGuru.getChildren().addAll(btnTambahBab, btnTambahUjian);
            vbBab.getChildren().add(boxAksiGuru);
        }
        perbaruiStatusBab();
    }

    private boolean babSelesai(Bab bab) {
        if (siswa == null) return false;
        return bab.getDaftarMateri().stream().allMatch(m -> progresService.isSelesai(siswa, kursus, m));
    }

    private void perbaruiStatusBab() {
        tombolBab.forEach((bab, tombol) -> {
            Label nomor = new Label(String.valueOf(bab.getNomor()));
            nomor.getStyleClass().add("nomor-bab");
            if (babSelesai(bab)) {
                nomor.getStyleClass().add("nomor-bab-selesai");
                nomor.setText("✓");
            }
            tombol.setGraphic(nomor);
        });
    }

    private void perbaruiProgres() {
        if (session.isGuru()) {
            if (lblJudulProgres != null) lblJudulProgres.setText("Jumlah Bab");
            lblPersen.setText(kursus.getDaftarBab().size() + " Bab");
            lblRingkasProgres.setText(kursus.getJumlahMateri() + " materi terdaftar");
            pbProgres.setVisible(false);
            pbProgres.setManaged(false);
            return;
        }
        if (lblJudulProgres != null) lblJudulProgres.setText("Progres kamu");
        double progres = progresService.getProgres(siswa, kursus);
        lblPersen.setText(Math.round(progres * 100) + "%");
        lblRingkasProgres.setText(progresService.getJumlahSelesai(siswa, kursus) + " dari "
                + kursus.getJumlahMateri() + " materi selesai");
        pbProgres.setProgress(progres);
        pbProgres.setVisible(true);
        pbProgres.setManaged(true);
    }

    private Bab babPertamaBelumSelesai() {
        return kursus.getDaftarBab().stream()
                .filter(bab -> !babSelesai(bab))
                .findFirst()
                .orElse(kursus.getDaftarBab().get(0));
    }

    private Optional<Bab> babDari(Materi materi) {
        return kursus.getDaftarBab().stream()
                .filter(bab -> bab.getDaftarMateri().contains(materi))
                .findFirst();
    }

    private void tampilkanKonten(Node konten) {
        spKonten.getChildren().setAll(konten);
        AnimasiUtil.masuk(konten);
    }

    // ------------------------------------------------------------------ isi bab

    private void tampilkanBab(Bab bab) {
        if (tombolBab.containsKey(bab)) {
            tombolBab.get(bab).setSelected(true);
        }
        VBox isi = panel();
        Label label = new Label("BAB " + bab.getNomor());
        label.getStyleClass().add("label-kecil-kapital");
        Label judul = new Label(bab.getJudul());
        judul.getStyleClass().add("judul-halaman");
        Label info = new Label(bab.getDaftarMateri().size() + " item - klik untuk membuka");
        info.getStyleClass().add("teks-redup");
        isi.getChildren().addAll(label, judul, info);

        if (session.isGuru()) {
            HBox aksiBab = new HBox(10);
            aksiBab.setAlignment(Pos.CENTER_LEFT);
            aksiBab.setPadding(new Insets(6, 0, 8, 0));

            Button btnUbah = new Button("Ubah Judul Bab");
            btnUbah.getStyleClass().add("tombol-sekunder");
            btnUbah.setOnAction(e -> handleUbahJudulBab(bab));

            Button btnHapus = new Button("Hapus Bab");
            btnHapus.getStyleClass().add("tombol-hantu");
            btnHapus.setOnAction(e -> handleHapusBab(bab));

            Button btnTambahMateri = new Button("+ Tambah Materi (Tulis / Upload)");
            btnTambahMateri.getStyleClass().add("tombol-utama");
            btnTambahMateri.setOnAction(e -> bukaEditorMateri(bab, null));

            aksiBab.getChildren().addAll(btnUbah, btnHapus, btnTambahMateri);
            isi.getChildren().add(aksiBab);
        }

        if (bab.getDaftarMateri().isEmpty()) {
            VBox boxKosong = new VBox(8);
            boxKosong.setAlignment(Pos.CENTER);
            boxKosong.setStyle("-fx-padding: 36 20; -fx-background-color: -garis; -fx-background-radius: 12;");
            Label lblKsg = new Label("Belum ada materi atau ujian di bab ini");
            lblKsg.getStyleClass().add("judul-kartu");
            Label lblPnjk = new Label("Klik '+ Tambah Materi' di atas untuk menulis bacaan baru atau tombol 'Tambah Ujian' di panel kiri.");
            lblPnjk.getStyleClass().add("teks-kecil");
            boxKosong.getChildren().addAll(lblKsg, lblPnjk);
            isi.getChildren().add(boxKosong);
        } else {
            List<Node> baris = bab.getDaftarMateri().stream().map(this::barisMateri).toList();
            isi.getChildren().addAll(baris);
            AnimasiUtil.berurutan(baris, Duration.millis(50));
        }
        tampilkanKonten(gulir(isi));
    }

    private Node barisMateri(Materi materi) {
        VBox teks = new VBox(2, label(materi.getJudul(), "judul-kartu"), label(materi.getKeterangan(), "teks-kecil"));
        HBox.setHgrow(teks, Priority.ALWAYS);
        Node status;
        if (session.isGuru()) {
            HBox aksi = new HBox(8);
            aksi.setAlignment(Pos.CENTER_RIGHT);
            if (materi instanceof MateriTeks mt) {
                Button btnEditTeks = new Button("Edit Teks");
                btnEditTeks.getStyleClass().addAll("tombol-sekunder");
                btnEditTeks.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");
                btnEditTeks.setOnAction(e -> {
                    e.consume();
                    bukaEditorMateri(babDari(mt).orElse(kursus.getDaftarBab().get(0)), mt);
                });
                aksi.getChildren().add(btnEditTeks);
            }
            if (materi instanceof MateriUjian mu) {
                Button btnEditSoal = new Button("Edit Soal");
                btnEditSoal.getStyleClass().addAll("tombol-sekunder");
                btnEditSoal.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");
                btnEditSoal.setOnAction(e -> {
                    e.consume();
                    Ujian u = kursusService.ujianDari(mu);
                    session.setUjianAktif(u);
                    session.setMateriFokus(mu);
                    SceneRouter.pindah(Layar.EDITOR_UJIAN);
                });
                aksi.getChildren().add(btnEditSoal);
            }
            Button btnHapusMateri = new Button("✕");
            btnHapusMateri.getStyleClass().add("tombol-hantu");
            btnHapusMateri.setStyle("-fx-padding: 4 8; -fx-font-size: 12px; -fx-text-fill: -teks-redup;");
            btnHapusMateri.setOnAction(e -> {
                e.consume();
                handleHapusMateri(materi);
            });
            aksi.getChildren().addAll(btnHapusMateri, new IkonView(Ikon.PANAH_KANAN, 16));
            status = aksi;
        } else if (siswa != null && progresService.isSelesai(siswa, kursus, materi)) {
            Label selesai = label("Selesai", "badge");
            selesai.getStyleClass().add("badge-sukses");
            status = selesai;
        } else {
            status = new IkonView(Ikon.PANAH_KANAN, 18);
        }
        HBox baris = new HBox(14, ikonMateri(materi, 20), teks, status);
        baris.setAlignment(Pos.CENTER_LEFT);
        baris.getStyleClass().add("item-materi");
        baris.setOnMouseClicked(e -> bukaMateri(materi));
        return baris;
    }

    private void handleHapusMateri(Materi materi) {
        if (!DialogUtil.konfirmasi("Hapus materi/ujian '" + materi.getJudul() + "' dari bab ini?")) {
            return;
        }
        Optional<Bab> babOpt = babDari(materi);
        if (babOpt.isEmpty()) return;
        Bab bab = babOpt.get();
        List<Bab> daftarBab = new ArrayList<>();
        for (Bab b : kursus.getDaftarBab()) {
            if (b.getNomor() == bab.getNomor()) {
                List<Materi> sisa = new ArrayList<>(b.getDaftarMateri());
                sisa.remove(materi);
                daftarBab.add(new Bab(b.getNomor(), b.getJudul(), sisa));
            } else {
                daftarBab.add(b);
            }
        }
        simpanKursus(kursus.denganDaftarBab(daftarBab));
        tampilkanBab(kursus.getDaftarBab().get(bab.getNomor() - 1));
    }

    private void bukaMateri(Materi materi) {
        switch (materi) {
            case MateriTeks t -> tampilkanTeks(t);
            case MateriUjian u -> tampilkanUjian(u);
            case MateriTautan l -> {
                session.bukaDokumen(l.getAlamat().toString());
                tandai(l);
            }
            case MateriPdf p -> {
                session.bukaDokumen(p.getFile().toUri().toString());
                tandai(p);
            }
            default -> {
            }
        }
    }

    private void tandai(Materi materi) {
        if (siswa != null) {
            try {
                progresService.tandaiSelesai(siswa, kursus, materi);
                perbaruiStatusBab();
                perbaruiProgres();
            } catch (IOException e) {
                DialogUtil.peringatan("Gagal mencatat progres belajar: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ tampilan materi

    private void tampilkanTeks(MateriTeks materi) {
        String markdown;
        try {
            markdown = kursusService.bacaTeks(materi);
        } catch (IOException e) {
            DialogUtil.peringatan("Materi tidak dapat dibaca: " + e.getMessage());
            return;
        }
        tandai(materi); // bacaan dianggap selesai saat dibuka
        VBox isi = panel();

        HBox barisAtas = new HBox(12, tombolKembaliKeBab(materi));
        barisAtas.setAlignment(Pos.CENTER_LEFT);
        if (session.isGuru()) {
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Button btnEdit = new Button("✏️ Edit Isi Materi (WordPress Style)");
            btnEdit.getStyleClass().add("tombol-sekunder");
            btnEdit.setOnAction(e -> bukaEditorMateri(babDari(materi).orElse(kursus.getDaftarBab().get(0)), materi));
            barisAtas.getChildren().addAll(sp, btnEdit);
        }

        isi.getChildren().addAll(barisAtas, label(materi.getKeterangan().toUpperCase(), "label-kecil-kapital"),
                MarkdownView.render(markdown), new Region(), navigasiBerikutnya(materi));
        tampilkanKonten(gulir(isi));
    }

    private Node navigasiBerikutnya(Materi materi) {
        List<Materi> semua = kursus.getSemuaMateri();
        int indeks = semua.indexOf(materi);
        HBox baris = new HBox();
        baris.setAlignment(Pos.CENTER_RIGHT);
        baris.setPadding(new Insets(16, 0, 0, 0));
        if (indeks + 1 < semua.size()) {
            Materi berikut = semua.get(indeks + 1);
            Button tombol = tombol("Berikutnya: " + berikut.getJudul(), Ikon.PANAH_KANAN, "tombol-utama");
            tombol.setContentDisplay(ContentDisplay.RIGHT);
            tombol.setOnAction(e -> bukaMateri(berikut));
            baris.getChildren().add(tombol);
        }
        return baris;
    }

    private void tampilkanUjian(MateriUjian materi) {
        Ujian ujian = kursusService.ujianDari(materi);
        VBox isi = panel();
        HBox kepala = new HBox(16, ikonMateri(materi, 22),
                new VBox(2, label("UJIAN", "label-kecil-kapital"), label(materi.getJudul(), "judul-halaman"),
                        label(ujian.getMataPelajaran() + " - " + ujian.getKodeUjian(), "teks-redup")));
        kepala.setAlignment(Pos.CENTER_LEFT);

        HBox info;
        if (session.isGuru()) {
            info = new HBox(14,
                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),
                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),
                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),
                    kotakInfo("Token", ujian.getToken()));
        } else {
            int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);
            info = new HBox(14,
                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),
                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),
                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),
                    kotakInfo("Percobaan", String.valueOf(percobaan)));
        }

        Label aturan = label(ujian.isBolehUlang()
                ? "Ujian ini boleh dikerjakan ulang. Nilai terbaik yang dipakai."
                : "Ujian ini hanya dapat dikerjakan satu kali.", "teks-redup");
        aturan.setGraphic(new IkonView(ujian.isBolehUlang() ? Ikon.CENTANG : Ikon.GEMBOK, 16));

        isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info, aturan);

        if (session.isGuru()) {
            Button editUjian = tombol("Edit Soal Ujian", Ikon.DOKUMEN, "tombol-utama");
            editUjian.getStyleClass().add("tombol-besar");
            editUjian.setOnAction(e -> {
                session.setUjianAktif(ujian);
                session.setMateriFokus(materi);
                SceneRouter.pindah(Layar.EDITOR_UJIAN);
            });
            isi.getChildren().add(editUjian);
        } else {
            Optional<RiwayatNilai> terbaik = session.getRekapService().getNilaiTerbaik(siswa, ujian);
            boolean bolehMulai = session.getUjianService().bolehMulai(siswa, ujian);
            int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);
            terbaik.ifPresent(r -> isi.getChildren().add(kartuNilaiTerbaik(r)));

            Button mulai = tombol(!bolehMulai ? "Sudah dikerjakan" : percobaan > 0 ? "Kerjakan ulang" : "Mulai ujian",
                    bolehMulai ? Ikon.MAIN : Ikon.GEMBOK, "tombol-utama");
            mulai.getStyleClass().add("tombol-besar");
            mulai.setDisable(!bolehMulai);
            mulai.setOnAction(e -> {
                session.setUjianAktif(ujian);
                session.setMateriFokus(materi);
                SceneRouter.pindah(Layar.TOKEN_GATE);
            });
            isi.getChildren().add(mulai);
        }
        tampilkanKonten(gulir(isi));
    }

    private Node kartuNilaiTerbaik(RiwayatNilai r) {
        Label status = label(r.lulus() ? "LULUS" : "TIDAK LULUS", "badge");
        status.getStyleClass().add(r.lulus() ? "badge-sukses" : "badge-bahaya");
        HBox kartu = new HBox(14, new IkonView(Ikon.PIALA, 22),
                new VBox(2, label("Nilai terbaik", "teks-kecil"),
                        label(FormatUtil.angka(r.skor()) + "  (predikat " + r.predikat() + ")", "judul-kartu")),
                status);
        kartu.setAlignment(Pos.CENTER_LEFT);
        kartu.getStyleClass().add("kartu-ujian-info");
        return kartu;
    }

    // ------------------------------------------------------------------ aksi guru untuk bab

    private void handleTambahBab() {
        TextInputDialog dialog = new TextInputDialog("Bab " + (kursus.getDaftarBab().size() + 1));
        dialog.setTitle("Tambah Bab Baru");
        dialog.setHeaderText("Masukkan judul bab baru untuk kursus " + kursus.getKode());
        dialog.setContentText("Judul Bab:");
        dialog.showAndWait().ifPresent(judul -> {
            if (StringUtil.isKosong(judul)) return;
            List<Bab> daftar = new ArrayList<>(kursus.getDaftarBab());
            Bab babBaru = new Bab(daftar.size() + 1, judul.trim(), List.of());
            daftar.add(babBaru);
            simpanKursus(kursus.denganDaftarBab(daftar));
            tampilkanBab(babBaru);
        });
    }

    private void handleUbahJudulBab(Bab bab) {
        TextInputDialog dialog = new TextInputDialog(bab.getJudul());
        dialog.setTitle("Ubah Judul Bab");
        dialog.setHeaderText("Ubah judul bab " + bab.getNomor());
        dialog.setContentText("Judul Bab:");
        dialog.showAndWait().ifPresent(judulBaru -> {
            if (StringUtil.isKosong(judulBaru)) return;
            List<Bab> daftar = new ArrayList<>();
            for (Bab b : kursus.getDaftarBab()) {
                if (b.getNomor() == bab.getNomor()) {
                    daftar.add(new Bab(b.getNomor(), judulBaru.trim(), b.getDaftarMateri()));
                } else {
                    daftar.add(b);
                }
            }
            simpanKursus(kursus.denganDaftarBab(daftar));
            tampilkanBab(kursus.getDaftarBab().get(bab.getNomor() - 1));
        });
    }

    private void handleHapusBab(Bab bab) {
        if (kursus.getDaftarBab().size() <= 1) {
            DialogUtil.peringatan("Kursus minimal harus memiliki 1 bab.");
            return;
        }
        if (DialogUtil.konfirmasi("Hapus bab '" + bab.getJudul() + "' beserta semua materi di dalamnya?")) {
            List<Bab> sisa = new ArrayList<>();
            int no = 1;
            for (Bab b : kursus.getDaftarBab()) {
                if (b.getNomor() != bab.getNomor()) {
                    sisa.add(new Bab(no++, b.getJudul(), b.getDaftarMateri()));
                }
            }
            simpanKursus(kursus.denganDaftarBab(sisa));
            tampilkanBab(kursus.getDaftarBab().get(0));
        }
    }

    private void handleTambahUjianLangsung() {
        if (kursus.getDaftarBab().isEmpty()) {
            DialogUtil.peringatan("Tambahkan bab terlebih dahulu sebelum menambah ujian.");
            return;
        }

        Bab babTerpilih = null;
        for (Map.Entry<Bab, ToggleButton> e : tombolBab.entrySet()) {
            if (e.getValue().isSelected()) {
                babTerpilih = e.getKey();
                break;
            }
        }
        if (babTerpilih == null) {
            babTerpilih = kursus.getDaftarBab().get(0);
        }

        handleTambahUjian(babTerpilih);
    }

    private void handleTambahUjian(Bab babDefault) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tambah Ujian");
        dialog.setHeaderText("Tambah Ujian ke Bab: " + babDefault.getJudul());

        ButtonType btnLanjut = new ButtonType("Lanjutkan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnLanjut, ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(10, 10, 10, 10));

        RadioButton rbBaru = new RadioButton("Buat Ujian Baru & Langsung Rancang Soal");
        RadioButton rbPilih = new RadioButton("Pilih Ujian yang Sudah Ada di Bank Soal");
        ToggleGroup tg = new ToggleGroup();
        rbBaru.setToggleGroup(tg);
        rbPilih.setToggleGroup(tg);
        rbBaru.setSelected(true);

        GridPane gridBaru = new GridPane();
        gridBaru.setHgap(10);
        gridBaru.setVgap(8);

        String saranKode = kursus.getKode().toUpperCase() + "-0" + (session.getUjianService().getJumlahUjian() + 1);
        TextField tfKode = new TextField(saranKode);
        TextField tfJudul = new TextField("Ujian " + babDefault.getJudul());
        TextField tfDurasi = new TextField("30");
        TextField tfKkm = new TextField("75");
        String saranToken = (kursus.getKode() + "7X" + (int)(Math.random() * 9 + 1)).toUpperCase();
        TextField tfToken = new TextField(saranToken);

        gridBaru.add(new Label("Kode Ujian:"), 0, 0);
        gridBaru.add(tfKode, 1, 0);
        gridBaru.add(new Label("Judul Ujian:"), 0, 1);
        gridBaru.add(tfJudul, 1, 1);
        gridBaru.add(new Label("Durasi (menit):"), 0, 2);
        gridBaru.add(tfDurasi, 1, 2);
        gridBaru.add(new Label("KKM:"), 0, 3);
        gridBaru.add(tfKkm, 1, 3);
        gridBaru.add(new Label("Token (6 karakter):"), 0, 4);
        gridBaru.add(tfToken, 1, 4);

        VBox boxPilih = new VBox(6);
        ComboBox<String> cbUjian = new ComboBox<>();
        List<Ujian> semuaUjian = session.getUjianService().getDaftarUjian();
        for (Ujian u : semuaUjian) {
            cbUjian.getItems().add(u.getKodeUjian() + " - " + u.getMataPelajaran());
        }
        if (!cbUjian.getItems().isEmpty()) {
            cbUjian.getSelectionModel().select(0);
        }
        TextField tfJudulPilih = new TextField("Ujian " + babDefault.getJudul());
        boxPilih.getChildren().addAll(new Label("Judul di Kursus:"), tfJudulPilih, new Label("Pilih Ujian CBT:"), cbUjian);

        boxPilih.setVisible(false);
        boxPilih.setManaged(false);

        tg.selectedToggleProperty().addListener((obs, lama, terpilih) -> {
            boolean modeBaru = terpilih == rbBaru;
            gridBaru.setVisible(modeBaru);
            gridBaru.setManaged(modeBaru);
            boxPilih.setVisible(!modeBaru);
            boxPilih.setManaged(!modeBaru);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        content.getChildren().addAll(rbBaru, gridBaru, rbPilih, boxPilih);
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == btnLanjut) {
            Bab babTarget = babDefault;

            if (rbBaru.isSelected()) {
                String kode = tfKode.getText().trim().toUpperCase();
                String jwb = tfJudul.getText().trim();
                String token = tfToken.getText().trim().toUpperCase();
                int durasi;
                double kkm;
                try {
                    durasi = Integer.parseInt(tfDurasi.getText().trim());
                    kkm = Double.parseDouble(tfKkm.getText().trim());
                } catch (NumberFormatException e) {
                    DialogUtil.peringatan("Durasi dan KKM harus berupa angka");
                    return;
                }
                if (StringUtil.isKosong(kode) || StringUtil.isKosong(jwb) || StringUtil.isKosong(token)) {
                    DialogUtil.peringatan("Kode, Judul, dan Token wajib diisi");
                    return;
                }
                if (!kode.matches("^[A-Z]{2,5}-\\d{2}$")) {
                    DialogUtil.peringatan("Format Kode Ujian harus 2-5 huruf diikuti '-' dan 2 angka (contoh: " + saranKode + ")");
                    return;
                }
                if (token.length() != 6 || !token.matches("^[A-Z0-9]{6}$")) {
                    DialogUtil.peringatan("Token ujian harus tepat 6 huruf/angka");
                    return;
                }
                if (session.getUjianService().cariUjian(kode).isPresent()) {
                    DialogUtil.peringatan("Kode ujian sudah terpakai: " + kode);
                    return;
                }

                try {
                    Soal sampleSoal = new SoalPilihanGanda(1, "Pertanyaan nomor 1 untuk " + jwb + "...", 10.0,
                            List.of("Pilihan A", "Pilihan B", "Pilihan C", "Pilihan D"), 'A');
                    Ujian ujianBaru = new Ujian(kode, jwb, token, durasi, kkm, List.of(sampleSoal));
                    session.getUjianService().tambahUjian(ujianBaru);

                    String idMateri = (kursus.getKode().toLowerCase() + "-u" + (System.currentTimeMillis() % 10000));
                    MateriUjian mu = new MateriUjian(idMateri, jwb, kode);

                    List<Bab> daftarBab = new ArrayList<>();
                    for (Bab b : kursus.getDaftarBab()) {
                        if (b.getNomor() == babTarget.getNomor()) {
                            List<Materi> materiBaru = new ArrayList<>(b.getDaftarMateri());
                            materiBaru.add(mu);
                            daftarBab.add(new Bab(b.getNomor(), b.getJudul(), materiBaru));
                        } else {
                            daftarBab.add(b);
                        }
                    }
                    simpanKursus(kursus.denganDaftarBab(daftarBab));

                    session.setUjianAktif(ujianBaru);
                    session.setMateriFokus(mu);
                    SceneRouter.pindah(Layar.EDITOR_UJIAN);

                } catch (Exception e) {
                    DialogUtil.peringatan("Gagal membuat ujian baru: " + e.getMessage());
                }

            } else {
                String jwb = tfJudulPilih.getText().trim();
                String terpilih = cbUjian.getValue();
                if (StringUtil.isKosong(jwb) || terpilih == null) return;
                String kodeUjian = terpilih.split(" - ")[0].trim();
                String idMateri = (kursus.getKode().toLowerCase() + "-u" + (System.currentTimeMillis() % 10000));
                MateriUjian mu = new MateriUjian(idMateri, jwb, kodeUjian);

                List<Bab> daftarBab = new ArrayList<>();
                for (Bab b : kursus.getDaftarBab()) {
                    if (b.getNomor() == babTarget.getNomor()) {
                        List<Materi> materiBaru = new ArrayList<>(b.getDaftarMateri());
                        materiBaru.add(mu);
                        daftarBab.add(new Bab(b.getNomor(), b.getJudul(), materiBaru));
                    } else {
                        daftarBab.add(b);
                    }
                }
                simpanKursus(kursus.denganDaftarBab(daftarBab));
                tampilkanBab(kursus.getDaftarBab().get(babTarget.getNomor() - 1));
            }
        }
    }

    private void bukaEditorMateri(Bab bab, MateriTeks materiLama) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(materiLama == null ? "Tambah Materi Pembelajaran" : "Edit Materi Pembelajaran");
        dialog.setHeaderText("Bab: " + bab.getJudul() + " • Kursus: " + kursus.getNama());

        ButtonType btnSimpan = new ButtonType("Simpan Materi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSimpan, ButtonType.CANCEL);

        VBox root = new VBox(12);
        root.setPrefWidth(740);
        root.setPrefHeight(560);
        root.setPadding(new Insets(10));

        HBox barisJudul = new HBox(10);
        barisJudul.setAlignment(Pos.CENTER_LEFT);

        Label lblJudul = new Label("Judul Materi:");
        lblJudul.setStyle("-fx-font-weight: bold;");
        TextField tfJudul = new TextField(materiLama != null ? materiLama.getJudul() : "");
        tfJudul.setPromptText("Contoh: Pengenalan Kalimat Majemuk");
        HBox.setHgrow(tfJudul, Priority.ALWAYS);

        Button btnUpload = new Button("📁 Unggah File (.txt / .md)");
        btnUpload.getStyleClass().add("tombol-sekunder");

        barisJudul.getChildren().addAll(lblJudul, tfJudul, btnUpload);

        HBox toolbar = new HBox(6);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-padding: 6 10; -fx-background-color: -garis; -fx-background-radius: 8;");

        Label lblToolbar = new Label("Format Cepat:");
        lblToolbar.getStyleClass().add("teks-kecil");

        Button btnH1 = new Button("H1");
        btnH1.getStyleClass().add("tombol-hantu");
        btnH1.setStyle("-fx-padding: 3 8;");

        Button btnH2 = new Button("H2");
        btnH2.getStyleClass().add("tombol-hantu");
        btnH2.setStyle("-fx-padding: 3 8;");

        Button btnBold = new Button("B");
        btnBold.getStyleClass().add("tombol-hantu");
        btnBold.setStyle("-fx-padding: 3 8; -fx-font-weight: bold;");

        Button btnItalic = new Button("I");
        btnItalic.getStyleClass().add("tombol-hantu");
        btnItalic.setStyle("-fx-padding: 3 8; -fx-font-style: italic;");

        Button btnList = new Button("• List");
        btnList.getStyleClass().add("tombol-hantu");
        btnList.setStyle("-fx-padding: 3 8;");

        Button btnQuote = new Button("” Kutip");
        btnQuote.getStyleClass().add("tombol-hantu");
        btnQuote.setStyle("-fx-padding: 3 8;");

        Button btnCode = new Button("</> Kode");
        btnCode.getStyleClass().add("tombol-hantu");
        btnCode.setStyle("-fx-padding: 3 8;");

        Button btnHr = new Button("— Garis");
        btnHr.getStyleClass().add("tombol-hantu");
        btnHr.setStyle("-fx-padding: 3 8;");

        toolbar.getChildren().addAll(lblToolbar, btnH1, btnH2, btnBold, btnItalic, btnList, btnQuote, btnCode, btnHr);

        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        TextArea taEditor = new TextArea();
        taEditor.setWrapText(true);
        taEditor.setPromptText("Tulis isi materi pelajaran di sini. Mendukung format Markdown (# Judul, **tebal**, *miring*, daftar, tabel, dll)...");
        taEditor.setStyle("-fx-font-size: 13.5px; -fx-font-family: 'Segoe UI', system-ui, sans-serif;");

        if (materiLama != null) {
            try {
                taEditor.setText(kursusService.bacaTeks(materiLama));
            } catch (IOException ignored) {}
        }

        btnH1.setOnAction(e -> sisipkanTeks(taEditor, "\n# ", ""));
        btnH2.setOnAction(e -> sisipkanTeks(taEditor, "\n## ", ""));
        btnBold.setOnAction(e -> sisipkanTeks(taEditor, "**", "**"));
        btnItalic.setOnAction(e -> sisipkanTeks(taEditor, "*", "*"));
        btnList.setOnAction(e -> sisipkanTeks(taEditor, "\n- ", ""));
        btnQuote.setOnAction(e -> sisipkanTeks(taEditor, "\n> ", ""));
        btnCode.setOnAction(e -> sisipkanTeks(taEditor, "\n```\n", "\n```\n"));
        btnHr.setOnAction(e -> sisipkanTeks(taEditor, "\n\n---\n\n", ""));

        btnUpload.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Pilih File Materi (.txt atau .md)");
            fc.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Dokumen Teks / Markdown (*.txt, *.md)", "*.txt", "*.md", "*.markdown"),
                    new javafx.stage.FileChooser.ExtensionFilter("Semua File (*.*)", "*.*")
            );
            java.io.File file = fc.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                try {
                    String konten = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    taEditor.setText(konten);
                    if (tfJudul.getText().trim().isEmpty()) {
                        String nama = file.getName();
                        int titik = nama.lastIndexOf('.');
                        if (titik > 0) nama = nama.substring(0, titik);
                        tfJudul.setText(nama);
                    }
                    DialogUtil.info("File '" + file.getName() + "' berhasil dimuat ke editor!");
                } catch (IOException ex) {
                    DialogUtil.peringatan("Gagal membaca file: " + ex.getMessage());
                }
            }
        });

        Tab tabTulis = new Tab("Tulis Materi", taEditor);
        tabTulis.setClosable(false);

        ScrollPane spPreview = new ScrollPane();
        spPreview.setFitToWidth(true);
        VBox vbPreview = new VBox(10);
        vbPreview.setPadding(new Insets(16));
        spPreview.setContent(vbPreview);

        Tab tabPreview = new Tab("Pratinjau Siswa", spPreview);
        tabPreview.setClosable(false);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru == tabPreview) {
                vbPreview.getChildren().clear();
                Label lblPreviewJudul = new Label(tfJudul.getText().trim().isEmpty() ? "Judul Materi" : tfJudul.getText().trim());
                lblPreviewJudul.getStyleClass().add("judul-halaman");
                vbPreview.getChildren().addAll(lblPreviewJudul, MarkdownView.render(taEditor.getText()));
            }
        });

        tabPane.getTabs().addAll(tabTulis, tabPreview);
        root.getChildren().addAll(barisJudul, toolbar, tabPane);
        dialog.getDialogPane().setContent(root);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == btnSimpan) {
            String judulBaru = tfJudul.getText().trim();
            String konten = taEditor.getText();

            if (StringUtil.isKosong(judulBaru)) {
                DialogUtil.peringatan("Judul materi wajib diisi!");
                return;
            }
            if (StringUtil.isKosong(konten)) {
                DialogUtil.peringatan("Isi materi tidak boleh kosong!");
                return;
            }

            try {
                if (materiLama == null) {
                    MateriTeks mtBaru = kursusService.buatMateriTeksBaru(kursus, judulBaru, konten);
                    List<Bab> daftarBab = new ArrayList<>();
                    for (Bab b : kursus.getDaftarBab()) {
                        if (b.getNomor() == bab.getNomor()) {
                            List<Materi> daftarMateri = new ArrayList<>(b.getDaftarMateri());
                            daftarMateri.add(mtBaru);
                            daftarBab.add(new Bab(b.getNomor(), b.getJudul(), daftarMateri));
                        } else {
                            daftarBab.add(b);
                        }
                    }
                    simpanKursus(kursus.denganDaftarBab(daftarBab));
                    tampilkanBab(kursus.getDaftarBab().get(bab.getNomor() - 1));
                    DialogUtil.info("Materi '" + judulBaru + "' berhasil ditambahkan ke " + bab.getJudul() + "!");
                } else {
                    kursusService.simpanTeksMateri(materiLama, konten);
                    MateriTeks mtUpdate = new MateriTeks(materiLama.getIdMateri(), judulBaru, materiLama.getFile());
                    List<Bab> daftarBab = new ArrayList<>();
                    for (Bab b : kursus.getDaftarBab()) {
                        if (b.getNomor() == bab.getNomor()) {
                            List<Materi> daftarMateri = new ArrayList<>();
                            for (Materi m : b.getDaftarMateri()) {
                                if (m.getIdMateri().equals(materiLama.getIdMateri())) {
                                    daftarMateri.add(mtUpdate);
                                } else {
                                    daftarMateri.add(m);
                                }
                            }
                            daftarBab.add(new Bab(b.getNomor(), b.getJudul(), daftarMateri));
                        } else {
                            daftarBab.add(b);
                        }
                    }
                    simpanKursus(kursus.denganDaftarBab(daftarBab));
                    tampilkanTeks(mtUpdate);
                    DialogUtil.info("Materi '" + judulBaru + "' berhasil disimpan!");
                }
            } catch (IOException ex) {
                DialogUtil.peringatan("Gagal menyimpan materi: " + ex.getMessage());
            }
        }
    }

    private void sisipkanTeks(TextArea ta, String awalan, String akhiran) {
        javafx.scene.control.IndexRange sel = ta.getSelection();
        int awal = sel.getStart();
        int akhir = sel.getEnd();
        String teksTerpilih = ta.getSelectedText();
        String hasil = awalan + (teksTerpilih.isEmpty() ? "" : teksTerpilih) + akhiran;
        ta.replaceText(awal, akhir, hasil);
        ta.requestFocus();
    }

    private void simpanKursus(Kursus baru) {
        this.kursus = baru;
        session.setKursusAktif(baru);
        try {
            kursusService.perbaruiKursus(baru);
        } catch (IOException e) {
            DialogUtil.peringatan("Gagal menyimpan perubahan kursus: " + e.getMessage());
        }
        buatDaftarBab();
        perbaruiProgres();
    }

    // ------------------------------------------------------------------ helpers UI

    private Node tombolKembaliKeBab(Materi materi) {
        Bab bab = babDari(materi).orElseThrow();
        Button tombol = tombol(bab.getJudul(), Ikon.PANAH_KIRI, "tombol-hantu");
        tombol.setOnAction(e -> tampilkanBab(bab));
        return tombol;
    }

    private static VBox panel() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(28, 36, 32, 36));
        return box;
    }

    private static ScrollPane gulir(Node isi) {
        ScrollPane sp = new ScrollPane(isi);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private static Label label(String teks, String styleClass) {
        Label label = new Label(teks);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        return label;
    }

    private static Button tombol(String teks, Ikon ikon, String styleClass) {
        Button b = new Button(teks);
        b.getStyleClass().add(styleClass);
        if (ikon != null) {
            b.setGraphic(new IkonView(ikon, 16));
        }
        return b;
    }

    private static VBox kotakInfo(String judul, String nilai) {
        VBox box = new VBox(2, label(judul, "teks-kecil"), label(nilai, "judul-kartu"));
        box.getStyleClass().add("kartu-mini");
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private static Node ikonMateri(Materi materi, double ukuran) {
        Ikon ikon = switch (materi.getJenis()) {
            case TEKS -> Ikon.DOKUMEN;
            case LINK -> Ikon.TAUTAN;
            case PDF -> Ikon.PDF;
            case UJIAN -> Ikon.UJIAN;
        };
        return new IkonView(ikon, ukuran);
    }

    // ------------------------------------------------------------------ tab orang (Google Classroom style)

    private void muatTabOrang() {
        vbTabOrang.getChildren().clear();
        vbTabOrang.setSpacing(24);
        vbTabOrang.setPadding(new Insets(24, 32, 32, 32));

        // 1. Banner Info Kode Kursus
        HBox bannerKode = new HBox(14);
        bannerKode.setAlignment(Pos.CENTER_LEFT);
        bannerKode.setStyle("-fx-background-color: -garis; -fx-background-radius: 14; -fx-padding: 12 18;");

        VBox vbInfoKode = new VBox(2);
        HBox.setHgrow(vbInfoKode, Priority.ALWAYS);
        Label lblInfoTeks = new Label("Kode Kursus untuk Bergabung:");
        lblInfoTeks.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        HBox barisKode = new HBox(8);
        barisKode.setAlignment(Pos.CENTER_LEFT);
        Label lblNilaiKode = new Label(kursus.getKode());
        lblNilaiKode.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -aksen; -fx-font-family: 'Consolas', monospace;");
        Label lblPetunjuk = new Label("· Bagikan kode ini agar siswa dapat masuk ke kelas");
        lblPetunjuk.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        barisKode.getChildren().addAll(lblNilaiKode, lblPetunjuk);
        vbInfoKode.getChildren().addAll(lblInfoTeks, barisKode);

        Button btnSalin = new Button("Salin Kode");
        btnSalin.getStyleClass().add("tombol-sekunder");
        btnSalin.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(kursus.getKode());
            Clipboard.getSystemClipboard().setContent(content);
            btnSalin.setText("Tersalin! ✓");
        });
        bannerKode.getChildren().addAll(vbInfoKode, btnSalin);
        vbTabOrang.getChildren().add(bannerKode);

        // 2. Seksi PENGGAJAR (Guru)
        VBox boxPengajar = new VBox(12);
        HBox headerPengajar = new HBox(10);
        headerPengajar.setAlignment(Pos.CENTER_LEFT);
        Label lblJudulPengajar = new Label("Pengajar");
        lblJudulPengajar.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -aksen;");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label lblHitungPengajar = new Label("1 pengajar");
        lblHitungPengajar.setStyle("-fx-font-size: 13px; -fx-text-fill: -teks-redup; -fx-font-weight: bold;");
        headerPengajar.getChildren().addAll(lblJudulPengajar, spacer1, lblHitungPengajar);

        Region garisBawahPengajar = new Region();
        garisBawahPengajar.setStyle("-fx-background-color: -aksen; -fx-min-height: 2px; -fx-max-height: 2px;");

        VBox daftarItemPengajar = new VBox(8);

        // Baris Pengajar Utama
        HBox barisGuru = new HBox(14);
        barisGuru.setAlignment(Pos.CENTER_LEFT);
        barisGuru.setStyle("-fx-padding: 10 14; -fx-background-radius: 12;");

        String namaGuru = kursus.getPengajar();
        String inisialGuru = hitungInisial(namaGuru);
        Label avatarGuru = new Label(inisialGuru);
        avatarGuru.getStyleClass().add("avatar");
        avatarGuru.setStyle("-fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px; -fx-font-size: 15px;");

        VBox vbTeksGuru = new VBox(2);
        HBox.setHgrow(vbTeksGuru, Priority.ALWAYS);
        Label lblNamaGuru = new Label(namaGuru);
        lblNamaGuru.setStyle("-fx-font-size: 14.5px; -fx-font-weight: bold; -fx-text-fill: -teks;");

        Optional<Guru> optGuru = session.getAuthService().getSemuaGuru().stream()
                .filter(g -> g.getNama().equalsIgnoreCase(namaGuru) || namaGuru.contains(g.getNama()) || g.getNama().contains(namaGuru))
                .findFirst();
        String subGuru = optGuru.map(g -> "Guru Pengampu • NIP " + g.getNip()).orElse("Guru Pengampu Kursus");
        Label lblSubGuru = new Label(subGuru);
        lblSubGuru.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
        vbTeksGuru.getChildren().addAll(lblNamaGuru, lblSubGuru);

        Label badgeGuru = new Label("Pengajar Utama");
        badgeGuru.getStyleClass().addAll("badge", "badge-sukses");

        barisGuru.getChildren().addAll(avatarGuru, vbTeksGuru, badgeGuru);
        daftarItemPengajar.getChildren().add(barisGuru);

        boxPengajar.getChildren().addAll(headerPengajar, garisBawahPengajar, daftarItemPengajar);
        vbTabOrang.getChildren().add(boxPengajar);

        // 3. Seksi SISWA / TEMAN SEKELAS
        VBox boxSiswa = new VBox(12);
        HBox headerSiswa = new HBox(10);
        headerSiswa.setAlignment(Pos.CENTER_LEFT);
        String judulSeksiSiswa = session.isGuru() ? "Siswa" : "Teman Sekelas";
        Label lblJudulSiswa = new Label(judulSeksiSiswa);
        lblJudulSiswa.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -aksen;");

        List<Siswa> semuaSiswa = session.getAuthService().getSemuaSiswa();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        Label lblHitungSiswa = new Label(semuaSiswa.size() + " siswa");
        lblHitungSiswa.setStyle("-fx-font-size: 13px; -fx-text-fill: -teks-redup; -fx-font-weight: bold;");
        headerSiswa.getChildren().addAll(lblJudulSiswa, spacer2, lblHitungSiswa);

        Region garisBawahSiswa = new Region();
        garisBawahSiswa.setStyle("-fx-background-color: -aksen; -fx-min-height: 2px; -fx-max-height: 2px;");

        VBox daftarItemSiswa = new VBox(4);
        if (semuaSiswa.isEmpty()) {
            Label lblKosong = new Label("Belum ada siswa yang bergabung.");
            lblKosong.setStyle("-fx-text-fill: -teks-redup; -fx-padding: 16 0;");
            daftarItemSiswa.getChildren().add(lblKosong);
        } else {
            for (Siswa s : semuaSiswa) {
                HBox barisS = new HBox(14);
                barisS.setAlignment(Pos.CENTER_LEFT);
                barisS.setStyle("-fx-padding: 10 14; -fx-background-radius: 10; -fx-border-color: transparent transparent -garis transparent; -fx-border-width: 0 0 1 0;");

                String inisialS = hitungInisial(s.getNama());
                Label avatarS = new Label(inisialS);
                avatarS.setStyle("-fx-min-width: 38px; -fx-min-height: 38px; -fx-max-width: 38px; -fx-max-height: 38px; "
                        + "-fx-background-radius: 999; -fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 13px; "
                        + "-fx-background-color: rgba(26, 163, 232, 0.16); -fx-text-fill: -aksen;");

                VBox vbTeksS = new VBox(2);
                HBox.setHgrow(vbTeksS, Priority.ALWAYS);
                Label lblNamaS = new Label(s.getNama());
                lblNamaS.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: -teks;");
                Label lblSubS = new Label("Kelas " + s.getKelas() + " • NIS " + s.getNis());
                lblSubS.setStyle("-fx-font-size: 12px; -fx-text-fill: -teks-redup;");
                vbTeksS.getChildren().addAll(lblNamaS, lblSubS);

                if (session.isSiswa() && s.equals(session.getSiswaAktif())) {
                    Label badgeKamu = new Label("Kamu");
                    badgeKamu.getStyleClass().addAll("badge", "badge-kaca");
                    barisS.getChildren().addAll(avatarS, vbTeksS, badgeKamu);
                } else if (session.isGuru()) {
                    double p = progresService.getProgres(s, kursus);
                    int jmlSelesai = progresService.getJumlahSelesai(s, kursus);
                    int totalMateri = kursus.getJumlahMateri();
                    Label badgeProgres = new Label();
                    if (p >= 1.0) {
                        badgeProgres.setText("Selesai (100%)");
                        badgeProgres.getStyleClass().addAll("badge", "badge-sukses");
                    } else if (p > 0) {
                        badgeProgres.setText(jmlSelesai + "/" + totalMateri + " materi (" + (int)(p * 100) + "%)");
                        badgeProgres.getStyleClass().addAll("badge");
                    } else {
                        badgeProgres.setText("Belum mulai");
                        badgeProgres.getStyleClass().addAll("badge", "badge-netral");
                    }
                    barisS.getChildren().addAll(avatarS, vbTeksS, badgeProgres);
                } else {
                    barisS.getChildren().addAll(avatarS, vbTeksS);
                }

                daftarItemSiswa.getChildren().add(barisS);
            }
        }

        boxSiswa.getChildren().addAll(headerSiswa, garisBawahSiswa, daftarItemSiswa);
        vbTabOrang.getChildren().add(boxSiswa);
    }

    private String hitungInisial(String nama) {
        if (nama == null || nama.trim().isEmpty()) return "?";
        String[] kata = nama.trim().split("\\s+");
        if (kata.length == 1) {
            return kata[0].substring(0, Math.min(2, kata[0].length())).toUpperCase();
        }
        return ("" + kata[0].charAt(0) + kata[1].charAt(0)).toUpperCase();
    }

    @FXML
    private void handleKembali() {
        SceneRouter.pindah(Layar.KURSUS_SAYA);
    }
}
