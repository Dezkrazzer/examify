package com.examify.controller;















import com.examify.app.AppSession;







import com.examify.app.Layar;







import com.examify.app.SceneRouter;







import com.examify.io.SlideMarkdownConverter;







import com.examify.model.Bab;







import com.examify.model.Guru;







import com.examify.model.HasilUjian;







import com.examify.model.Kursus;







import com.examify.model.Materi;







import com.examify.model.MateriPdf;







import com.examify.model.MateriQuiz;







import com.examify.model.MateriTugas;







import com.examify.model.PengumpulanTugas;







import javafx.stage.FileChooser;







import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;







import javafx.scene.control.TextArea;







import java.io.File;







import java.time.LocalDate;







import java.time.LocalTime;







import java.time.format.DateTimeFormatter;







import java.util.stream.Collectors;







import com.examify.model.MateriTautan;







import com.examify.model.MateriTeks;







import com.examify.model.MateriUjian;







import com.examify.model.RiwayatNilai;







import com.examify.model.Siswa;







import com.examify.model.Soal;







import com.examify.model.SoalPilihanGanda;







import com.examify.model.Ujian;







import com.examify.model.materi.DrafSlide;







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







import javafx.geometry.Side;







import javafx.scene.control.ContextMenu;







import javafx.scene.control.MenuItem;







import javafx.scene.control.Tooltip;







import javafx.scene.Node;







import javafx.scene.control.Button;







import javafx.scene.control.ButtonBar;







import javafx.scene.control.ButtonType;







import javafx.scene.control.ComboBox;







import javafx.scene.control.ContentDisplay;







import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Separator;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import java.security.SecureRandom;







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







import java.time.LocalDateTime;







import java.util.ArrayList;







import java.util.LinkedHashMap;







import java.util.List;







import java.util.Map;







import java.util.Optional;















/**







 * Halaman satu kursus: daftar bab di kiri, isi bab / bacaan / info ujian di







 * kanan.







 */







public class KursusDetailController {















    @FXML







    private StackPane spPola;







    @FXML







    private Label lblKode;







    @FXML







    private Button btnUbahKode;







    @FXML







    private Label lblNama;







    @FXML







    private Label lblPengajar;







    @FXML







    private Label lblDeskripsi;







    @FXML







    private Label lblJudulProgres;







    @FXML







    private Label lblPersen;







    @FXML







    private Label lblRingkasProgres;







    @FXML







    private ProgressBar pbProgres;







    @FXML







    private VBox vbBab;







    @FXML







    private StackPane spKonten;







    @FXML







    private ToggleButton tabMateri;







    @FXML







    private ToggleButton tabOrang;







    @FXML







    private HBox boxTabMateri;







    @FXML







    private ScrollPane spTabOrang;







    @FXML







    private VBox vbTabOrang;















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















        if (btnUbahKode != null) {







            btnUbahKode.setVisible(session.isGuru());







            btnUbahKode.setManaged(session.isGuru());







        }















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







            tombol.setAlignment(Pos.CENTER_LEFT);







            tombol.setOnAction(e -> tampilkanBab(bab));







            tombolBab.put(bab, tombol);















            if (session.isGuru()) {







                tombol.getStyleClass().add("item-bab-guru");















                StackPane barisBab = new StackPane();







                barisBab.setMaxWidth(Double.MAX_VALUE);















                Button btnMenu = new Button();







                btnMenu.getStyleClass().addAll("tombol-hantu", "tombol-kebab-bab");







                btnMenu.setGraphic(new IkonView(Ikon.TITIK_TIGA, 14));







                btnMenu.setTooltip(new Tooltip("Menu Bab"));







                btnMenu.setFocusTraversable(false);















                ContextMenu menu = new ContextMenu();







                menu.getStyleClass().add("menu-kaca");















                MenuItem itemUbah = new MenuItem("Ubah Judul Bab");







                itemUbah.setGraphic(new IkonView(Ikon.PENSIL, 14));







                itemUbah.setOnAction(e -> handleUbahJudulBab(bab));















                MenuItem itemHapus = new MenuItem("Hapus Bab");







                itemHapus.setGraphic(new IkonView(Ikon.SAMPAH, 14));







                itemHapus.getStyleClass().add("menu-item-bahaya");







                itemHapus.setOnAction(e -> handleHapusBab(bab));















                menu.getItems().addAll(itemUbah, itemHapus);















                btnMenu.setOnAction(e -> {







                    if (menu.isShowing()) {







                        menu.hide();







                    } else {







                        menu.show(btnMenu, Side.BOTTOM, -120, 4);







                    }







                    e.consume();







                });















                tombol.setContextMenu(menu);















                StackPane.setAlignment(btnMenu, Pos.CENTER_RIGHT);







                StackPane.setMargin(btnMenu, new Insets(0, 8, 0, 0));















                barisBab.getChildren().addAll(tombol, btnMenu);







                vbBab.getChildren().add(barisBab);







            } else {







                vbBab.getChildren().add(tombol);







            }







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







        if (siswa == null)







            return false;







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







            if (lblJudulProgres != null)







                lblJudulProgres.setText("Jumlah Bab");







            lblPersen.setText(kursus.getDaftarBab().size() + " Bab");







            lblRingkasProgres.setText(kursus.getJumlahMateri() + " materi terdaftar");







            pbProgres.setVisible(false);







            pbProgres.setManaged(false);







            return;







        }







        if (lblJudulProgres != null)







            lblJudulProgres.setText("Progres kamu");







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















            Button btnTambahMateri = new Button("+ Tambah Materi");







            btnTambahMateri.getStyleClass().add("tombol-utama");







            btnTambahMateri.setOnAction(e -> bukaEditorMateri(bab, null));















            Button btnTambahKuis = new Button("+ Buat Kuis Bab");







            btnTambahKuis.getStyleClass().add("tombol-sekunder");







            btnTambahKuis.setGraphic(new IkonView(Ikon.BINTANG, 14));







            btnTambahKuis.setOnAction(e -> handleBuatKuisBab(bab));















            Button btnTambahTugas = new Button("+ Tambah Tugas");







            btnTambahTugas.getStyleClass().add("tombol-sekunder");







            btnTambahTugas.setGraphic(new IkonView(Ikon.DOKUMEN, 14));







            btnTambahTugas.setOnAction(e -> handleTambahTugas(bab));















            aksiBab.getChildren().addAll(btnTambahMateri, btnTambahKuis, btnTambahTugas);







            isi.getChildren().add(aksiBab);







        }















        if (bab.getDaftarMateri().isEmpty()) {







            VBox boxKosong = new VBox(8);







            boxKosong.setAlignment(Pos.CENTER);







            boxKosong.setStyle("-fx-padding: 36 20; -fx-background-color: -garis; -fx-background-radius: 12;");







            Label lblKsg = new Label("Belum ada materi atau ujian di bab ini");







            lblKsg.getStyleClass().add("judul-kartu");







            Label lblPnjk = new Label(







                    "Klik '+ Tambah Materi' di atas untuk menulis bacaan baru atau tombol 'Tambah Ujian' di panel kiri.");







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







            if (materi instanceof MateriQuiz mq) {







                Button btnEditKuis = new Button("Edit Kuis");







                btnEditKuis.getStyleClass().addAll("tombol-sekunder");







                btnEditKuis.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");







                btnEditKuis.setOnAction(e -> {







                    e.consume();







                    Ujian u = kursusService.ujianDari(mq);







                    session.setUjianAktif(u);







                    session.setMateriFokus(mq);







                    SceneRouter.pindah(Layar.EDITOR_UJIAN);







                });







                aksi.getChildren().add(btnEditKuis);







            }







            if (materi instanceof MateriTugas mt) {







                Button btnEditTugas = new Button("Edit Tugas");







                btnEditTugas.getStyleClass().addAll("tombol-sekunder");







                btnEditTugas.setStyle("-fx-padding: 4 10; -fx-font-size: 12px;");







                btnEditTugas.setOnAction(e -> {







                    e.consume();







                    handleEditTugas(babDari(mt).orElse(kursus.getDaftarBab().get(0)), mt);







                });







                aksi.getChildren().add(btnEditTugas);







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







        } else if (siswa != null && materi instanceof MateriTugas mt && !mt.isBolehAkses(siswa)) {







            Label badgeTerkunci = label("Khusus " + mt.getTargetKelas(), "badge");







            badgeTerkunci.setStyle("-fx-background-color: -garis; -fx-text-fill: -teks-redup;");







            status = badgeTerkunci;







        } else if (siswa != null && (progresService.isSelesai(siswa, kursus, materi)







                || (materi instanceof MateriQuiz mq







                        && session.getRekapService().sudahMengerjakan(siswa, kursusService.ujianDari(mq)))







                || (materi instanceof MateriTugas mt







                        && session.getTugasService().cariPengumpulan(mt.getIdMateri(), siswa.getNis()).isPresent()))) {







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







        if (babOpt.isEmpty())







            return;







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







            case MateriQuiz q -> tampilkanQuiz(q);







            case MateriTugas g -> tampilkanTugas(g);







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















    // ------------------------------------------------------------------ tampilan







    // materi















    private void tampilkanTeks(MateriTeks materi) {







        tampilkanTeksSlide(materi, 0);







    }















    private void tampilkanTeksSlide(MateriTeks materi, int slideIndex) {







        String markdown;







        try {







            markdown = kursusService.bacaTeks(materi);







        } catch (IOException e) {







            DialogUtil.peringatan("Materi tidak dapat dibaca: " + e.getMessage());







            return;







        }







        tandai(materi); // bacaan dianggap selesai saat dibuka















        List<DrafSlide> daftarSlide = SlideMarkdownConverter.parse(markdown);







        if (daftarSlide == null || daftarSlide.isEmpty()) {







            daftarSlide = List.of(new DrafSlide(materi.getJudul()));







        }















        int totalSlide = daftarSlide.size();







        int indeksAktif = Math.max(0, Math.min(slideIndex, totalSlide - 1));







        DrafSlide slideAktif = daftarSlide.get(indeksAktif);















        VBox isi = panel();















        HBox barisAtas = new HBox(12, tombolKembaliKeBab(materi));







        barisAtas.setAlignment(Pos.CENTER_LEFT);















        if (totalSlide > 1) {







            Label lblSlideInfo = new Label("Halaman " + (indeksAktif + 1) + " dari " + totalSlide);







            lblSlideInfo.getStyleClass().addAll("badge", "badge-netral");







            barisAtas.getChildren().add(lblSlideInfo);







        }















        if (session.isGuru()) {







            Region sp = new Region();







            HBox.setHgrow(sp, Priority.ALWAYS);







            Button btnEdit = new Button("Edit Isi Materi");







            btnEdit.setGraphic(new IkonView(Ikon.DOKUMEN, 14));







            btnEdit.getStyleClass().add("tombol-sekunder");







            btnEdit.setOnAction(e -> bukaEditorMateri(babDari(materi).orElse(kursus.getDaftarBab().get(0)), materi));







            barisAtas.getChildren().addAll(sp, btnEdit);







        }















        String labelKategori = materi.getKeterangan().toUpperCase();







        if (totalSlide > 1 && slideAktif.getJudulSlide() != null && !slideAktif.getJudulSlide().trim().isEmpty()) {







            labelKategori += " • " + slideAktif.getJudulSlide().trim().toUpperCase();







        }















        String slideMarkdown = (totalSlide > 1)







                ? SlideMarkdownConverter.toMarkdown(slideAktif)







                : markdown;















        VBox boxSlideKonten = new VBox(12);







        boxSlideKonten.getChildren().add(MarkdownView.render(slideMarkdown));















        HBox barisBawah = new HBox(12);







        barisBawah.setAlignment(Pos.CENTER_LEFT);







        barisBawah.setPadding(new Insets(20, 0, 0, 0));















        if (totalSlide > 1) {







            Button btnPrev = new Button("Halaman Sebelumnya");







            btnPrev.setGraphic(new IkonView(Ikon.PANAH_KIRI, 14));







            btnPrev.getStyleClass().add("tombol-sekunder");







            btnPrev.setDisable(indeksAktif == 0);







            btnPrev.setOnAction(e -> tampilkanTeksSlide(materi, indeksAktif - 1));















            Button btnNext = new Button("Halaman Berikutnya");







            btnNext.setGraphic(new IkonView(Ikon.PANAH_KANAN, 14));







            btnNext.setContentDisplay(ContentDisplay.RIGHT);







            btnNext.getStyleClass().add("tombol-utama");







            btnNext.setOnAction(e -> tampilkanTeksSlide(materi, indeksAktif + 1));















            HBox dots = new HBox(6);







            dots.setAlignment(Pos.CENTER);







            for (int i = 0; i < totalSlide; i++) {







                final int targetIdx = i;







                Button dot = new Button();







                dot.setPrefSize(i == indeksAktif ? 20 : 8, 8);







                dot.setMaxSize(i == indeksAktif ? 20 : 8, 8);







                dot.setMinSize(i == indeksAktif ? 20 : 8, 8);







                dot.setStyle(i == indeksAktif







                        ? "-fx-background-color: -aksen; -fx-background-radius: 4;"







                        : "-fx-background-color: -garis; -fx-background-radius: 4; -fx-cursor: hand;");







                dot.setOnAction(e -> tampilkanTeksSlide(materi, targetIdx));







                dots.getChildren().add(dot);







            }















            Region spacer = new Region();







            HBox.setHgrow(spacer, Priority.ALWAYS);















            barisBawah.getChildren().addAll(btnPrev, dots, spacer);







            if (indeksAktif < totalSlide - 1) {







                barisBawah.getChildren().add(btnNext);







            } else {







                barisBawah.getChildren().add(navigasiBerikutnya(materi));







            }







        } else {







            Region spacer = new Region();







            HBox.setHgrow(spacer, Priority.ALWAYS);







            barisBawah.getChildren().addAll(spacer, navigasiBerikutnya(materi));







        }















        isi.getChildren().addAll(barisAtas, label(labelKategori, "label-kecil-kapital"), boxSlideKonten, barisBawah);







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















        if (session.isGuru()) {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info, aturan);
        } else {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info);
        }















        if (ujian.hasJadwalAkses()) {







            HBox jadwalBox = new HBox(10);







            jadwalBox.setAlignment(Pos.CENTER_LEFT);







            jadwalBox.getStyleClass().addAll("kaca");







            jadwalBox.setStyle("-fx-padding: 8 14; -fx-background-radius: 8;");







            Label lblJadwal = new Label("Waktu Akses: "







                    + FormatUtil.formatRentangWaktuAkses(ujian.getWaktuMulai(), ujian.getWaktuSelesai()));







            lblJadwal.setGraphic(new IkonView(Ikon.JAM, 14));







            lblJadwal.getStyleClass().add("teks-kecil");







            jadwalBox.getChildren().add(lblJadwal);







            isi.getChildren().add(jadwalBox);







        }















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







            boolean aksesDibuka = ujian.isAksesDibuka();







            int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);







            terbaik.ifPresent(r -> isi.getChildren().add(kartuNilaiTerbaik(r)));















            if (!aksesDibuka) {







                Label pesanAkses = label(session.getUjianService().getPesanStatusAkses(ujian), "badge");







                pesanAkses.getStyleClass().add("badge-peringatan");







                isi.getChildren().add(pesanAkses);







            }















            String teksTombol;







            if (!aksesDibuka) {







                teksTombol = ujian.isBelumMulai() ? "Akses Belum Dibuka" : "Waktu Akses Berakhir";







            } else if (!bolehMulai) {







                teksTombol = "Sudah dikerjakan";







            } else if (percobaan > 0) {







                teksTombol = "Kerjakan ulang";







            } else {







                teksTombol = "Mulai ujian";







            }















            Button mulai = tombol(teksTombol, bolehMulai ? Ikon.MAIN : Ikon.GEMBOK, "tombol-utama");







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















    private void tampilkanQuiz(MateriQuiz materi) {







        Ujian ujian = kursusService.ujianDari(materi);







        VBox isi = panel();







        HBox kepala = new HBox(16, ikonMateri(materi, 22),







                new VBox(2, label("KUIS BAB", "label-kecil-kapital"), label(materi.getJudul(), "judul-halaman"),







                        label(ujian.getMataPelajaran() + " - " + ujian.getKodeUjian(), "teks-redup")));







        kepala.setAlignment(Pos.CENTER_LEFT);















        HBox info;







        if (session.isGuru()) {







            info = new HBox(14,







                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),







                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),







                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),







                    kotakInfo("Kode Kuis", ujian.getKodeUjian()));







        } else {







            int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);







            info = new HBox(14,







                    kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),







                    kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),







                    kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),







                    kotakInfo("Status", percobaan > 0 ? "Sudah Dikerjakan" : "Belum Dikerjakan"));







        }















        Label aturan = label(







                "Kuis bab ini hanya dapat dikerjakan satu kali. Setelah selesai, Anda dapat melihat evaluasi dan pembahasan soal.",







                "teks-redup");







        aturan.setGraphic(new IkonView(Ikon.BINTANG, 16));















        if (session.isGuru()) {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info, aturan);
        } else {
            isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info);
        }















        if (ujian.hasJadwalAkses()) {







            HBox jadwalBox = new HBox(10);







            jadwalBox.setAlignment(Pos.CENTER_LEFT);







            jadwalBox.getStyleClass().addAll("kaca");







            jadwalBox.setStyle("-fx-padding: 8 14; -fx-background-radius: 8;");







            Label lblJadwal = new Label("Waktu Akses: "







                    + FormatUtil.formatRentangWaktuAkses(ujian.getWaktuMulai(), ujian.getWaktuSelesai()));







            lblJadwal.setGraphic(new IkonView(Ikon.JAM, 14));







            lblJadwal.getStyleClass().add("teks-kecil");







            jadwalBox.getChildren().add(lblJadwal);







            isi.getChildren().add(jadwalBox);







        }















        if (session.isGuru()) {







            Button editUjian = tombol("Edit Soal Kuis", Ikon.DOKUMEN, "tombol-utama");







            editUjian.getStyleClass().add("tombol-besar");







            editUjian.setOnAction(e -> {







                session.setUjianAktif(ujian);







                session.setMateriFokus(materi);







                SceneRouter.pindah(Layar.EDITOR_UJIAN);







            });







            isi.getChildren().add(editUjian);







        } else {







            boolean sudahMengerjakan = session.getRekapService().sudahMengerjakan(siswa, ujian);







            Optional<RiwayatNilai> riwayatOpt = session.getRekapService().getNilaiTerbaik(siswa, ujian);















            if (sudahMengerjakan && riwayatOpt.isPresent()) {







                RiwayatNilai r = riwayatOpt.get();







                isi.getChildren().add(kartuNilaiTerbaik(r));















                Label pesanSelesai = label(







                        "Kuis telah diselesaikan. Anda dapat melihat kembali evaluasi dan pembahasan jawaban kuis di bawah ini.",







                        "teks-redup");







                pesanSelesai.setWrapText(true);







                pesanSelesai.setGraphic(new IkonView(Ikon.CENTANG, 16));















                Button btnPembahasan = tombol("Lihat Pembahasan Kuis", Ikon.MATA, "tombol-utama");







                btnPembahasan.getStyleClass().add("tombol-besar");







                btnPembahasan.setOnAction(e -> {







                    session.setRiwayatFokus(r);







                    session.setUjianAktif(ujian);







                    session.setMateriFokus(materi);







                    SceneRouter.pindah(Layar.PEMBAHASAN_UJIAN);







                });















                isi.getChildren().addAll(pesanSelesai, btnPembahasan);







            } else {







                boolean aksesDibuka = ujian.isAksesDibuka();







                if (!aksesDibuka) {







                    Label pesanAkses = label(session.getUjianService().getPesanStatusAkses(ujian), "badge");







                    pesanAkses.getStyleClass().add("badge-peringatan");







                    isi.getChildren().add(pesanAkses);







                }















                Button mulai = tombol(







                        !aksesDibuka ? (ujian.isBelumMulai() ? "Akses Belum Dibuka" : "Waktu Akses Berakhir")







                                : "Mulai Kuis",







                        aksesDibuka ? Ikon.MAIN : Ikon.GEMBOK, "tombol-utama");







                mulai.getStyleClass().add("tombol-besar");







                mulai.setDisable(!aksesDibuka);







                mulai.setOnAction(e -> {







                    session.setUjianAktif(ujian);







                    session.setMateriFokus(materi);







                    HasilUjian hasilUjian = session.getUjianService().mulaiUjian(siswa, ujian);







                    session.setHasilAktif(hasilUjian);







                    SceneRouter.pindah(Layar.CBT_ROOM);







                });







                isi.getChildren().add(mulai);







            }







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















    // ------------------------------------------------------------------ aksi guru







    // untuk bab















    private void handleTambahBab() {







        TextInputDialog dialog = new TextInputDialog("Bab " + (kursus.getDaftarBab().size() + 1));







        dialog.setTitle("Tambah Bab Baru");







        dialog.setHeaderText("Masukkan judul bab baru untuk kursus " + kursus.getKode());







        dialog.setContentText("Judul Bab:");







        dialog.showAndWait().ifPresent(judul -> {







            if (StringUtil.isKosong(judul))







                return;







            List<Bab> daftar = new ArrayList<>(kursus.getDaftarBab());







            Bab babBaru = new Bab(daftar.size() + 1, judul.trim(), List.of());







            daftar.add(babBaru);







            simpanKursus(kursus.denganDaftarBab(daftar));







            tampilkanBab(babBaru);







        });







    }















    private Bab babAktifSaatIni() {







        for (Map.Entry<Bab, ToggleButton> e : tombolBab.entrySet()) {







            if (e.getValue().isSelected()) {







                return e.getKey();







            }







        }







        return null;







    }















    private void handleUbahJudulBab(Bab bab) {







        TextInputDialog dialog = new TextInputDialog(bab.getJudul());







        dialog.setTitle("Ubah Judul Bab");







        dialog.setHeaderText("Ubah judul bab " + bab.getNomor());







        dialog.setContentText("Judul Bab:");







        dialog.showAndWait().ifPresent(judulBaru -> {







            if (StringUtil.isKosong(judulBaru))







                return;







            Bab babAktif = babAktifSaatIni();







            List<Bab> daftar = new ArrayList<>();







            for (Bab b : kursus.getDaftarBab()) {







                if (b.getNomor() == bab.getNomor()) {







                    daftar.add(new Bab(b.getNomor(), judulBaru.trim(), b.getDaftarMateri()));







                } else {







                    daftar.add(b);







                }







            }







            simpanKursus(kursus.denganDaftarBab(daftar));







            if (babAktif != null && babAktif.getNomor() != bab.getNomor()) {







                tampilkanBab(kursus.getDaftarBab().get(babAktif.getNomor() - 1));







            } else {







                tampilkanBab(kursus.getDaftarBab().get(bab.getNomor() - 1));







            }







        });







    }















    private void handleHapusBab(Bab bab) {







        if (kursus.getDaftarBab().size() <= 1) {







            DialogUtil.peringatan("Kursus minimal harus memiliki 1 bab.");







            return;







        }







        if (DialogUtil.konfirmasi("Hapus bab '" + bab.getJudul() + "' beserta semua materi di dalamnya?")) {







            Bab babAktif = babAktifSaatIni();







            List<Bab> sisa = new ArrayList<>();







            int no = 1;







            int nomorBabAktifBaru = 1;







            for (Bab b : kursus.getDaftarBab()) {







                if (b.getNomor() != bab.getNomor()) {







                    int noSekarang = no++;







                    sisa.add(new Bab(noSekarang, b.getJudul(), b.getDaftarMateri()));







                    if (babAktif != null && b.getNomor() == babAktif.getNomor()) {







                        nomorBabAktifBaru = noSekarang;







                    }







                }







            }







            simpanKursus(kursus.denganDaftarBab(sisa));







            int idx = Math.max(0, Math.min(nomorBabAktifBaru - 1, sisa.size() - 1));







            tampilkanBab(kursus.getDaftarBab().get(idx));







        }







    }















    private void handleTambahUjianLangsung() {







        if (kursus.getDaftarBab().isEmpty()) {







            DialogUtil.peringatan("Tambahkan bab terlebih dahulu sebelum menambah ujian.");







            return;







        }















        Bab babTerpilih = babAktifSaatIni();







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







        String saranToken = (kursus.getKode() + "7X" + (int) (Math.random() * 9 + 1)).toUpperCase();







        TextField tfToken = new TextField(saranToken);







        TextField tfAksesMulai = new TextField("");







        tfAksesMulai.setPromptText("yyyy-MM-dd HH:mm (opsional)");







        TextField tfAksesSelesai = new TextField("");







        tfAksesSelesai.setPromptText("yyyy-MM-dd HH:mm (opsional)");















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







        gridBaru.add(new Label("Waktu Buka:"), 0, 5);







        gridBaru.add(tfAksesMulai, 1, 5);







        gridBaru.add(new Label("Waktu Tutup:"), 0, 6);







        gridBaru.add(tfAksesSelesai, 1, 6);

        CheckBox cbBolehUlang = new CheckBox("Izinkan siswa mengerjakan ulang ujian ini");
        cbBolehUlang.setSelected(false);
        gridBaru.add(new Label("Bisa Diulang:"), 0, 7);
        gridBaru.add(cbBolehUlang, 1, 7);















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







        boxPilih.getChildren().addAll(new Label("Judul di Kursus:"), tfJudulPilih, new Label("Pilih Ujian CBT:"),







                cbUjian);















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







                    DialogUtil.peringatan(







                            "Format Kode Ujian harus 2-5 huruf diikuti '-' dan 2 angka (contoh: " + saranKode + ")");







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















                LocalDateTime mulai = FormatUtil.parseInputAkses(tfAksesMulai.getText());







                LocalDateTime selesai = FormatUtil.parseInputAkses(tfAksesSelesai.getText());







                if (mulai != null && selesai != null && mulai.isAfter(selesai)) {







                    DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup");







                    return;







                }















                try {







                    Soal sampleSoal = new SoalPilihanGanda(1, "Pertanyaan nomor 1 untuk " + jwb + "...", 10.0,







                            List.of("Pilihan A", "Pilihan B", "Pilihan C", "Pilihan D"), 'A');







                    Ujian ujianBaru = new Ujian(kode, jwb, token, durasi, kkm, List.of(sampleSoal), cbBolehUlang.isSelected(), mulai,







                            selesai);







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







                if (StringUtil.isKosong(jwb) || terpilih == null)







                    return;







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















    private void handleBuatKuisBab(Bab babTarget) {







        Dialog<ButtonType> dialog = new Dialog<>();







        dialog.setTitle("Buat Kuis Bab");







        dialog.setHeaderText("Buat Kuis Pemahaman Baru di Bab: " + babTarget.getJudul());















        ButtonType btnLanjut = new ButtonType("Buat & Rancang Soal Kuis", ButtonBar.ButtonData.OK_DONE);







        dialog.getDialogPane().getButtonTypes().addAll(btnLanjut, ButtonType.CANCEL);















        GridPane grid = new GridPane();







        grid.setHgap(10);







        grid.setVgap(8);







        grid.setPadding(new Insets(10, 10, 10, 10));















        int counter = 1;







        String kodeKuis = "QZ-" + String.format("%02d", counter);







        while (session.getUjianService().cariUjian(kodeKuis).isPresent()) {







            counter++;







            kodeKuis = "QZ-" + String.format("%02d", counter);







        }















        TextField tfKode = new TextField(kodeKuis);







        TextField tfJudul = new TextField("Kuis " + babTarget.getJudul());







        TextField tfDurasi = new TextField("15");







        TextField tfKkm = new TextField("70");







        String saranToken = ("QZ" + (int) (Math.random() * 8999 + 1000)).toUpperCase();







        TextField tfToken = new TextField(saranToken);







        TextField tfAksesMulai = new TextField("");







        tfAksesMulai.setPromptText("yyyy-MM-dd HH:mm (opsional)");







        TextField tfAksesSelesai = new TextField("");







        tfAksesSelesai.setPromptText("yyyy-MM-dd HH:mm (opsional)");















        grid.add(new Label("Kode Kuis:"), 0, 0);







        grid.add(tfKode, 1, 0);







        grid.add(new Label("Judul Kuis:"), 0, 1);







        grid.add(tfJudul, 1, 1);







        grid.add(new Label("Durasi (menit):"), 0, 2);







        grid.add(tfDurasi, 1, 2);







        grid.add(new Label("KKM:"), 0, 3);







        grid.add(tfKkm, 1, 3);







        grid.add(new Label("Token Akses:"), 0, 4);







        grid.add(tfToken, 1, 4);







        grid.add(new Label("Waktu Buka:"), 0, 5);







        grid.add(tfAksesMulai, 1, 5);







        grid.add(new Label("Waktu Tutup:"), 0, 6);







        grid.add(tfAksesSelesai, 1, 6);

        CheckBox cbKuisBolehUlang = new CheckBox("Izinkan kuis dikerjakan ulang");
        cbKuisBolehUlang.setSelected(false);
        grid.add(new Label("Bisa Diulang:"), 0, 7);
        grid.add(cbKuisBolehUlang, 1, 7);















        dialog.getDialogPane().setContent(grid);















        Optional<ButtonType> res = dialog.showAndWait();







        if (res.isPresent() && res.get() == btnLanjut) {







            String kode = tfKode.getText().trim().toUpperCase();







            String judul = tfJudul.getText().trim();







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







            if (StringUtil.isKosong(kode) || StringUtil.isKosong(judul) || StringUtil.isKosong(token)) {







                DialogUtil.peringatan("Kode, Judul, dan Token wajib diisi");







                return;







            }







            if (!kode.matches("^[A-Z]{2,5}-\\d{2}$")) {







                DialogUtil.peringatan("Format Kode Kuis harus 2-5 huruf diikuti '-' dan 2 angka (contoh: QZ-01)");







                return;







            }







            if (token.length() != 6 || !token.matches("^[A-Z0-9]{6}$")) {







                DialogUtil.peringatan("Token kuis harus tepat 6 huruf/angka");







                return;







            }







            if (session.getUjianService().cariUjian(kode).isPresent()) {







                DialogUtil.peringatan("Kode kuis sudah terpakai: " + kode);







                return;







            }















            LocalDateTime mulai = FormatUtil.parseInputAkses(tfAksesMulai.getText());







            LocalDateTime selesai = FormatUtil.parseInputAkses(tfAksesSelesai.getText());







            if (mulai != null && selesai != null && mulai.isAfter(selesai)) {







                DialogUtil.peringatan("Waktu buka akses tidak boleh setelah waktu tutup");







                return;







            }















            try {







                Soal sampleSoal = new SoalPilihanGanda(1, "Pertanyaan nomor 1 untuk " + judul + "...", 10.0,







                        List.of("Pilihan A", "Pilihan B", "Pilihan C", "Pilihan D"), 'A');







                // Kuis di bab tidak dapat dikerjakan ulang (bolehUlang = false)







                Ujian ujianKuis = new Ujian(kode, judul, token, durasi, kkm, List.of(sampleSoal), cbKuisBolehUlang.isSelected(), mulai,







                        selesai);







                session.getUjianService().tambahUjian(ujianKuis);















                String idMateri = (kursus.getKode().toLowerCase() + "-qz" + (System.currentTimeMillis() % 10000));







                MateriQuiz mq = new MateriQuiz(idMateri, judul, kode);















                List<Bab> daftarBab = new ArrayList<>();







                for (Bab b : kursus.getDaftarBab()) {







                    if (b.getNomor() == babTarget.getNomor()) {







                        List<Materi> materiBaru = new ArrayList<>(b.getDaftarMateri());







                        materiBaru.add(mq);







                        daftarBab.add(new Bab(b.getNomor(), b.getJudul(), materiBaru));







                    } else {







                        daftarBab.add(b);







                    }







                }







                simpanKursus(kursus.denganDaftarBab(daftarBab));















                session.setUjianAktif(ujianKuis);







                session.setMateriFokus(mq);







                SceneRouter.pindah(Layar.EDITOR_UJIAN);















            } catch (Exception e) {







                DialogUtil.peringatan("Gagal membuat kuis baru: " + e.getMessage());







            }







        }







    }















    private void bukaEditorMateri(Bab bab, MateriTeks materiLama) {







        session.setKursusAktif(kursus);







        session.setBabAktifMateri(bab);







        session.setMateriTeksAktif(materiLama);







        SceneRouter.pindah(Layar.EDITOR_MATERI);







    }















    private void simpanKursus(Kursus baru) {







        this.kursus = baru;







        try {







            kursusService.perbaruiKursus(baru);







        } catch (IOException e) {







            DialogUtil.peringatan("Gagal menyimpan perubahan kursus: " + e.getMessage());







        }







        session.setKursusAktif(baru);







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















    private static Label label(String teks, String... styleClasses) {

        Label label = new Label(teks);

        label.getStyleClass().addAll(styleClasses);

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







            case QUIZ -> Ikon.BINTANG;







            case TUGAS -> Ikon.DOKUMEN;







        };







        return new IkonView(ikon, ukuran);







    }















    // ------------------------------------------------------------------ tab orang







    // (Google Classroom style)















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







        lblNilaiKode.setStyle(







                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -aksen; -fx-font-family: 'Consolas', monospace;");







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







            btnSalin.setText("Tersalin!");







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







        avatarGuru.setStyle(







                "-fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px; -fx-font-size: 15px;");















        VBox vbTeksGuru = new VBox(2);







        HBox.setHgrow(vbTeksGuru, Priority.ALWAYS);







        Label lblNamaGuru = new Label(namaGuru);







        lblNamaGuru.setStyle("-fx-font-size: 14.5px; -fx-font-weight: bold; -fx-text-fill: -teks;");















        Optional<Guru> optGuru = session.getAuthService().getSemuaGuru().stream()







                .filter(g -> g.getNama().equalsIgnoreCase(namaGuru) || namaGuru.contains(g.getNama())







                        || g.getNama().contains(namaGuru))







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















        List<Siswa> semuaSiswa = session.getAuthService().getSemuaSiswa().stream()
                .filter(s -> session.getPendaftaranService().isTerdaftar(s, kursus))
                .toList();







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







                barisS.setStyle(







                        "-fx-padding: 10 14; -fx-background-radius: 10; -fx-border-color: transparent transparent -garis transparent; -fx-border-width: 0 0 1 0;");















                String inisialS = hitungInisial(s.getNama());







                Label avatarS = new Label(inisialS);







                avatarS.setStyle(







                        "-fx-min-width: 38px; -fx-min-height: 38px; -fx-max-width: 38px; -fx-max-height: 38px; "







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







                        badgeProgres.setText(jmlSelesai + "/" + totalMateri + " materi (" + (int) (p * 100) + "%)");







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







        if (nama == null || nama.trim().isEmpty())







            return "?";







        String[] kata = nama.trim().split("\\s+");







        if (kata.length == 1) {







            return kata[0].substring(0, Math.min(2, kata[0].length())).toUpperCase();







        }







        return ("" + kata[0].charAt(0) + kata[1].charAt(0)).toUpperCase();







    }















    @FXML







    private void handleUbahKode() {
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
        kartu.setPrefWidth(450);
        kartu.setMaxWidth(470);
        kartu.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.25), 30, 0.12, 0, 8));

        // Header Modal (Bisa di-drag)
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

        StackPane kotakIkon = new StackPane(new IkonView(Ikon.PENSIL, 18));
        kotakIkon.getStyleClass().add("kotak-ikon-stat");
        kotakIkon.setMinSize(42, 42);
        kotakIkon.setMaxSize(42, 42);

        VBox boxJudul = new VBox(2);
        HBox barisJudulBadge = new HBox(8);
        barisJudulBadge.setAlignment(Pos.CENTER_LEFT);
        Label lblJudul = new Label("Ubah Kode Kursus");
        lblJudul.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        Label badgeKodeLama = new Label(kursus.getKode());
        badgeKodeLama.getStyleClass().add("badge");
        barisJudulBadge.getChildren().addAll(lblJudul, badgeKodeLama);

        Label lblSub = new Label("Perbarui kode akses kursus '" + kursus.getNama() + "'.");
        lblSub.getStyleClass().add("teks-redup");
        lblSub.setStyle("-fx-font-size: 12px;");
        boxJudul.getChildren().addAll(barisJudulBadge, lblSub);
        HBox.setHgrow(boxJudul, Priority.ALWAYS);

        Button btnTutup = new Button();
        btnTutup.setGraphic(new IkonView(Ikon.SILANG, 14));
        btnTutup.getStyleClass().add("tombol-ikon");
        btnTutup.setTooltip(new Tooltip("Tutup (ESC)"));
        btnTutup.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });

        barisHeader.getChildren().addAll(kotakIkon, boxJudul, btnTutup);

        // Info Kode Saat Ini + Tombol Salin
        HBox kotakKodeSaatIni = new HBox(12);
        kotakKodeSaatIni.getStyleClass().add("modal-kotak-jadwal");
        kotakKodeSaatIni.setAlignment(Pos.CENTER_LEFT);

        Node polaThumb = PolaKursus.buat(kursus.getKode(), 40, 40, 10);

        VBox infoSaatIni = new VBox(2);
        Label lblKetSaatIni = new Label("Kode Akses Saat Ini");
        lblKetSaatIni.getStyleClass().add("teks-redup");
        lblKetSaatIni.setStyle("-fx-font-size: 11.5px;");
        Label lblNilaiSaatIni = new Label(kursus.getKode());
        lblNilaiSaatIni.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold; -fx-font-size: 15px;");
        infoSaatIni.getChildren().addAll(lblKetSaatIni, lblNilaiSaatIni);
        HBox.setHgrow(infoSaatIni, Priority.ALWAYS);

        Button btnSalin = new Button("Salin");
        btnSalin.setGraphic(new IkonView(Ikon.SALIN, 13));
        btnSalin.getStyleClass().add("tombol-sekunder");
        btnSalin.setTooltip(new Tooltip("Salin kode akses saat ini"));
        btnSalin.setOnAction(e -> {
            ClipboardContent cc = new ClipboardContent();
            cc.putString(kursus.getKode());
            Clipboard.getSystemClipboard().setContent(cc);
            btnSalin.setText("Tersalin!");
        });

        kotakKodeSaatIni.getChildren().addAll(polaThumb, infoSaatIni, btnSalin);

        // Input Kode Baru
        VBox boxInput = new VBox(6);
        Label lblField = new Label("Kode Baru");
        lblField.setStyle("-fx-font-weight: bold; -fx-font-size: 12.5px;");

        TextField tfKodeBaru = new TextField(kursus.getKode());
        tfKodeBaru.setPromptText("Masukkan kode akses baru...");
        tfKodeBaru.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold; -fx-font-size: 14px;");
        HBox.setHgrow(tfKodeBaru, Priority.ALWAYS);

        Button btnAcak = new Button("Acak");
        btnAcak.setGraphic(new IkonView(Ikon.ACAK, 14));
        btnAcak.getStyleClass().add("tombol-sekunder");
        btnAcak.setTooltip(new Tooltip("Buat kode acak 6 karakter"));
        btnAcak.setOnAction(e -> tfKodeBaru.setText(hasilkanKodeKursusAcak()));

        HBox barisInput = new HBox(8, tfKodeBaru, btnAcak);
        barisInput.setAlignment(Pos.CENTER_LEFT);

        Label lblCatatan = new Label("Huruf besar dan kecil berpengaruh (case-sensitive). Siswa baru wajib memasukkan kode ini.");
        lblCatatan.getStyleClass().add("teks-redup");
        lblCatatan.setStyle("-fx-font-size: 11.5px;");
        lblCatatan.setWrapText(true);

        Label lblGalat = new Label();
        lblGalat.setWrapText(true);
        lblGalat.setMaxWidth(Double.MAX_VALUE);
        lblGalat.setStyle("-fx-text-fill: -bahaya; -fx-background-color: rgba(239, 68, 68, 0.1); "
                + "-fx-background-radius: 10; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblGalat.setVisible(false);
        lblGalat.setManaged(false);

        tfKodeBaru.textProperty().addListener((obs, lama, baru) -> {
            if (lblGalat.isVisible()) {
                lblGalat.setVisible(false);
                lblGalat.setManaged(false);
            }
        });

        boxInput.getChildren().addAll(lblField, barisInput, lblCatatan, lblGalat);

        // Footer Tombol
        HBox barisTombol = new HBox(10);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("tombol-hantu");
        btnBatal.setCancelButton(true);
        btnBatal.setOnAction(e -> {
            dialog.setResult(Boolean.FALSE);
            dialog.close();
        });

        Button btnSimpan = new Button("Simpan Kode");
        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setDefaultButton(true);
        btnSimpan.setOnAction(e -> {
            String kodeBaru = tfKodeBaru.getText() != null ? tfKodeBaru.getText().trim() : "";
            if (kodeBaru.isEmpty()) {
                lblGalat.setText("Kode kursus tidak boleh kosong.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKodeBaru.requestFocus();
                return;
            }
            if (kodeBaru.equals(kursus.getKode())) {
                dialog.setResult(Boolean.FALSE);
                dialog.close();
                return;
            }
            try {
                String kodeLama = kursus.getKode();
                Kursus baru = kursusService.ubahKodeKursus(kursus, kodeBaru);
                session.getPendaftaranService().perbaruiKodeKursus(kodeLama, baru.getKode());
                this.kursus = baru;
                session.setKursusAktif(baru);
                lblKode.setText(baru.getKode());
                spPola.getChildren().setAll(PolaKursus.buat(baru.getKode(), 84, 84, 20));
                muatTabOrang();
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                DialogUtil.info("Kode kursus berhasil diubah menjadi: " + baru.getKode());
            } catch (Exception ex) {
                lblGalat.setText(ex.getMessage() != null ? ex.getMessage() : "Gagal mengubah kode kursus.");
                lblGalat.setVisible(true);
                lblGalat.setManaged(true);
                tfKodeBaru.requestFocus();
                tfKodeBaru.selectAll();
            }
        });

        barisTombol.getChildren().addAll(btnBatal, btnSimpan);

        kartu.getChildren().addAll(barisHeader, new Separator(), kotakKodeSaatIni, boxInput, new Separator(), barisTombol);
        pane.setContent(kartu);

        Platform.runLater(() -> {
            tfKodeBaru.requestFocus();
            tfKodeBaru.selectAll();
        });
        dialog.showAndWait();
    }

    private static final String KARAKTER_KODE_KURSUS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM_KODE_KURSUS = new SecureRandom();

    private static String hasilkanKodeKursusAcak() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(KARAKTER_KODE_KURSUS.charAt(RANDOM_KODE_KURSUS.nextInt(KARAKTER_KODE_KURSUS.length())));
        }
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private void handleUbahKodeLama() {







        TextInputDialog dialog = new TextInputDialog(kursus.getKode());







        dialog.setTitle("Ubah Kode Kursus");







        dialog.setHeaderText("Ubah kode akses kursus '" + kursus.getNama() + "'");







        dialog.setContentText("Kode Baru:");















        Optional<String> hasil = dialog.showAndWait();







        hasil.ifPresent(input -> {







            String kodeBaru = input.trim().toUpperCase();







            if (kodeBaru.isEmpty()) {







                DialogUtil.peringatan("Kode kursus tidak boleh kosong.");







                return;







            }







            if (kodeBaru.equals(kursus.getKode())) {







                return;







            }







            try {







                String kodeLama = kursus.getKode();
                Kursus baru = kursusService.ubahKodeKursus(kursus, kodeBaru);
                session.getPendaftaranService().perbaruiKodeKursus(kodeLama, baru.getKode());







                this.kursus = baru;







                session.setKursusAktif(baru);







                lblKode.setText(baru.getKode());







                spPola.getChildren().setAll(PolaKursus.buat(baru.getKode(), 84, 84, 20));







                muatTabOrang();







                DialogUtil.info("Kode kursus berhasil diubah menjadi: " + baru.getKode());







            } catch (Exception e) {







                DialogUtil.peringatan("Gagal mengubah kode kursus: " + e.getMessage());







            }







        });







    }















    @FXML







    private void handleKembali() {







        SceneRouter.pindah(Layar.KURSUS_SAYA);







    }











    // ------------------------------------------------------------------ tugas







    private void handleTambahTugas(Bab bab) {



        handleEditTugas(bab, null);



    }







    private void handleEditTugas(Bab bab, MateriTugas tugasEdit) {



        Dialog<MateriTugas> dialog = new Dialog<>();



        dialog.setTitle(tugasEdit == null ? "Tambah Tugas Baru" : "Edit Tugas");



        dialog.setHeaderText(tugasEdit == null ? "Buat tugas baru untuk Bab " + bab.getNomor() + " - " + bab.getJudul()



                : "Ubah data tugas");







        ButtonType btnSimpanType = new ButtonType("Simpan Tugas", ButtonBar.ButtonData.OK_DONE);



        dialog.getDialogPane().getButtonTypes().addAll(btnSimpanType, ButtonType.CANCEL);







        GridPane grid = new GridPane();



        grid.setHgap(12);



        grid.setVgap(12);



        grid.setPadding(new Insets(16, 20, 16, 20));







        TextField tfJudul = new TextField(tugasEdit != null ? tugasEdit.getJudul() : "");



        tfJudul.setPromptText("Contoh: Tugas 1 - Analisis Struktur");



        tfJudul.setPrefWidth(320);







        DatePicker dpDeadline = new DatePicker();



        TextField tfJam = new TextField("23:59");



        tfJam.setPrefWidth(70);



        tfJam.setPromptText("HH:mm");







        if (tugasEdit != null && tugasEdit.getDeadline() != null) {



            dpDeadline.setValue(tugasEdit.getDeadline().toLocalDate());



            tfJam.setText(String.format("%02d:%02d", tugasEdit.getDeadline().getHour(), tugasEdit.getDeadline().getMinute()));



        } else {



            dpDeadline.setValue(LocalDate.now().plusDays(7));



        }







        HBox boxDeadline = new HBox(8, dpDeadline, new Label("Pukul:"), tfJam);



        boxDeadline.setAlignment(Pos.CENTER_LEFT);







        ComboBox<String> cbKelas = new ComboBox<>();



        cbKelas.getItems().add("Semua Kelas");



        List<String> daftarKelasSiswa = session.getAuthService().getSemuaSiswa().stream()



                .map(Siswa::getKelas)



                .filter(k -> k != null && !k.isBlank())



                .distinct()



                .sorted()



                .toList();



        cbKelas.getItems().addAll(daftarKelasSiswa);



        cbKelas.setEditable(true);



        if (tugasEdit != null) {



            cbKelas.setValue(tugasEdit.getTargetKelas());



        } else {



            cbKelas.setValue("Semua Kelas");



        }







        TextArea taDeskripsi = new TextArea(tugasEdit != null ? tugasEdit.getDeskripsi() : "");



        taDeskripsi.setPromptText("Tuliskan deskripsi atau instruksi pengerjaan tugas...");



        taDeskripsi.setPrefRowCount(4);



        taDeskripsi.setWrapText(true);







        grid.add(new Label("Judul Tugas:"), 0, 0);



        grid.add(tfJudul, 1, 0);



        grid.add(new Label("Batas Waktu:"), 0, 1);



        grid.add(boxDeadline, 1, 1);



        grid.add(new Label("Target Kelas:"), 0, 2);



        grid.add(cbKelas, 1, 2);



        grid.add(new Label("Deskripsi:"), 0, 3);



        grid.add(taDeskripsi, 1, 3);







        dialog.getDialogPane().setContent(grid);







        dialog.setResultConverter(btn -> {



            if (btn == btnSimpanType) {



                String judul = tfJudul.getText().trim();



                if (judul.isEmpty()) {



                    DialogUtil.peringatan("Judul tugas wajib diisi.");



                    return null;



                }



                LocalDate tgl = dpDeadline.getValue();



                if (tgl == null) {



                    tgl = LocalDate.now().plusDays(7);



                }



                String jamStr = tfJam.getText().trim();



                LocalTime waktu = LocalTime.of(23, 59);



                if (!jamStr.isEmpty() && jamStr.contains(":")) {



                    try {



                        String[] p = jamStr.split(":");



                        waktu = LocalTime.of(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()));



                    } catch (Exception ignored) {}



                }



                LocalDateTime deadline = LocalDateTime.of(tgl, waktu);



                String targetKelas = cbKelas.getValue();



                if (targetKelas == null || targetKelas.isBlank()) {



                    targetKelas = "Semua Kelas";



                }



                String deskripsi = taDeskripsi.getText().trim();







                String id = tugasEdit != null ? tugasEdit.getIdMateri()



                        : "tgs-" + (System.currentTimeMillis() % 100000);



                return new MateriTugas(id, judul, deadline, targetKelas, deskripsi);



            }



            return null;



        });







        Optional<MateriTugas> hasil = dialog.showAndWait();



        hasil.ifPresent(tugasBaru -> {



            List<Bab> daftarBabBaru = new ArrayList<>();



            for (Bab b : kursus.getDaftarBab()) {



                if (b.getNomor() == bab.getNomor()) {



                    List<Materi> materiBaru = new ArrayList<>(b.getDaftarMateri());



                    if (tugasEdit != null) {



                        int idx = materiBaru.indexOf(tugasEdit);



                        if (idx >= 0) {



                            materiBaru.set(idx, tugasBaru);



                        } else {



                            materiBaru.add(tugasBaru);



                        }



                    } else {



                        materiBaru.add(tugasBaru);



                    }



                    daftarBabBaru.add(new Bab(b.getNomor(), b.getJudul(), materiBaru));



                } else {



                    daftarBabBaru.add(b);



                }



            }



            simpanKursus(kursus.denganDaftarBab(daftarBabBaru));



            tampilkanBab(kursus.getDaftarBab().get(bab.getNomor() - 1));



        });



    }







    private void tampilkanTugas(MateriTugas tugas) {



        VBox isi = panel();







        Label label = new Label("TUGAS · " + tugas.getTargetKelas());



        label.getStyleClass().add("label-kecil-kapital");



        Label judul = new Label(tugas.getJudul());



        judul.getStyleClass().add("judul-halaman");



        judul.setWrapText(true);







        HBox barisBadge = new HBox(8);



        barisBadge.setAlignment(Pos.CENTER_LEFT);



        Label badgeKelas = label("Target: " + tugas.getTargetKelas(), "badge");



        badgeKelas.setStyle("-fx-background-color: -garis; -fx-text-fill: -teks;");







        Label badgeDeadline;



        if (tugas.isLewatDeadline()) {



            badgeDeadline = label("Batas Waktu Berakhir: " + tugas.getDeadlineFormatted(), "badge", "badge-bahaya");



        } else {



            badgeDeadline = label("Batas Waktu: " + tugas.getDeadlineFormatted(), "badge", "badge-sukses");



        }



        barisBadge.getChildren().addAll(badgeKelas, badgeDeadline);







        isi.getChildren().addAll(label, judul, barisBadge);







        // Deskripsi tugas



        VBox boxDeskripsi = new VBox(8);



        boxDeskripsi.setStyle("-fx-padding: 18; -fx-background-color: -garis; -fx-background-radius: 12;");



        Label lblJudulDeskripsi = new Label("Deskripsi / Petunjuk Tugas");



        lblJudulDeskripsi.getStyleClass().add("judul-kartu");



        Label lblIsiDeskripsi = new Label(tugas.getDeskripsi().isEmpty() ? "Tidak ada petunjuk tambahan dari guru." : tugas.getDeskripsi());



        lblIsiDeskripsi.setWrapText(true);



        lblIsiDeskripsi.getStyleClass().add("teks-redup");



        boxDeskripsi.getChildren().addAll(lblJudulDeskripsi, lblIsiDeskripsi);



        isi.getChildren().add(boxDeskripsi);







        // Tampilan SISWA



        if (session.isSiswa()) {



            if (!tugas.isBolehAkses(siswa)) {



                // Akses ditolak karena bukan kelasnya



                VBox boxDibatasi = new VBox(12);



                boxDibatasi.setAlignment(Pos.CENTER);



                boxDibatasi.setStyle("-fx-padding: 36 20; -fx-background-color: rgba(220, 53, 69, 0.08); -fx-background-radius: 14; -fx-border-color: rgba(220, 53, 69, 0.2); -fx-border-radius: 14;");



                Label ikonGembok = new Label();



                ikonGembok.setGraphic(new IkonView(Ikon.GEMBOK, 40));



                Label lblAksesDibatasi = new Label("Tugas Dibatasi");



                lblAksesDibatasi.getStyleClass().add("judul-kartu");



                Label lblKet = new Label("Tugas ini hanya dapat diakses oleh kelas: " + tugas.getTargetKelas()



                        + ".\nKelas Anda saat ini adalah: " + (siswa != null ? siswa.getKelas() : "-"));



                lblKet.setStyle("-fx-text-fill: -teks-redup; -fx-text-alignment: center;");



                boxDibatasi.getChildren().addAll(ikonGembok, lblAksesDibatasi, lblKet);



                isi.getChildren().add(boxDibatasi);



            } else {



                // Siswa berhak mengakses



                Optional<PengumpulanTugas> optPengumpulan = session.getTugasService().cariPengumpulan(tugas.getIdMateri(), siswa.getNis());



                VBox boxPengumpulan = new VBox(14);



                boxPengumpulan.setStyle("-fx-padding: 20; -fx-background-color: -latar-kartu; -fx-background-radius: 14; -fx-border-color: -garis; -fx-border-radius: 14;");







                if (optPengumpulan.isPresent()) {



                    PengumpulanTugas p = optPengumpulan.get();



                    tandai(tugas); // Tandai progres selesai







                    Label lblStatus = new Label("Sudah Dikumpulkan");



                    lblStatus.getStyleClass().addAll("badge", "badge-sukses");







                    Label lblWaktu = new Label("Waktu Pengumpulan: " + p.getWaktuKumpulFormatted());



                    lblWaktu.getStyleClass().add("teks-redup");







                    HBox barisBerkas = new HBox(10);



                    barisBerkas.setAlignment(Pos.CENTER_LEFT);



                    barisBerkas.setStyle("-fx-padding: 10 14; -fx-background-color: -garis; -fx-background-radius: 10;");



                    barisBerkas.getChildren().addAll(new IkonView(Ikon.DOKUMEN, 20), new Label(p.getNamaBerkas()));







                    HBox aksiSiswa = new HBox(10);



                    aksiSiswa.setAlignment(Pos.CENTER_LEFT);







                    Button btnBukaBerkas = new Button("Buka Berkas Saya");



                    btnBukaBerkas.getStyleClass().add("tombol-sekunder");



                    btnBukaBerkas.setGraphic(new IkonView(Ikon.MATA, 14));



                    btnBukaBerkas.setOnAction(e -> {



                        File f = new File(p.getPathBerkas());



                        if (f.exists()) {



                            session.bukaDokumen(f.toURI().toString());



                        } else {



                            DialogUtil.peringatan("Berkas tidak ditemukan pada direktori penyimpanan.");



                        }



                    });



                    aksiSiswa.getChildren().add(btnBukaBerkas);







                    if (!tugas.isLewatDeadline()) {



                        Button btnKumpulUlang = new Button("Kumpulkan Ulang");



                        btnKumpulUlang.getStyleClass().add("tombol-hantu");



                        btnKumpulUlang.setOnAction(e -> tampilkanFormUpload(boxPengumpulan, tugas, true));



                        aksiSiswa.getChildren().add(btnKumpulUlang);



                    }







                    boxPengumpulan.getChildren().addAll(lblStatus, lblWaktu, barisBerkas);



                    if (!p.getCatatan().isBlank()) {



                        Label lblCat = new Label("Catatan Anda: " + p.getCatatan());



                        lblCat.getStyleClass().add("teks-kecil");



                        boxPengumpulan.getChildren().add(lblCat);



                    }



                    boxPengumpulan.getChildren().add(aksiSiswa);



                } else {



                    if (tugas.isLewatDeadline()) {



                        Label lblTerlambat = new Label("Batas Waktu Pengumpulan Telah Berakhir");



                        lblTerlambat.getStyleClass().addAll("badge", "badge-bahaya");



                        Label lblTutup = new Label("Pengumpulan tugas ini sudah ditutup oleh pengajar.");



                        lblTutup.getStyleClass().add("teks-redup");



                        boxPengumpulan.getChildren().addAll(lblTerlambat, lblTutup);



                    } else {



                        tampilkanFormUpload(boxPengumpulan, tugas, false);



                    }



                }



                isi.getChildren().add(boxPengumpulan);



            }



        }







        // Tampilan GURU



        if (session.isGuru()) {



            HBox barisAksiGuru = new HBox(10);



            barisAksiGuru.setAlignment(Pos.CENTER_LEFT);







            Button btnEditTugas = new Button("Edit Tugas");



            btnEditTugas.getStyleClass().add("tombol-sekunder");



            btnEditTugas.setGraphic(new IkonView(Ikon.PENSIL, 14));



            btnEditTugas.setOnAction(e -> handleEditTugas(babDari(tugas).orElse(kursus.getDaftarBab().get(0)), tugas));







            Button btnHapusTugas = new Button("Hapus Tugas");



            btnHapusTugas.getStyleClass().addAll("tombol-sekunder", "tombol-bahaya");



            btnHapusTugas.setGraphic(new IkonView(Ikon.SAMPAH, 14));



            btnHapusTugas.setOnAction(e -> handleHapusMateri(tugas));







            barisAksiGuru.getChildren().addAll(btnEditTugas, btnHapusTugas);



            isi.getChildren().add(barisAksiGuru);







            // Daftar Pengumpulan Siswa



            VBox boxSubmissions = new VBox(12);



            boxSubmissions.setStyle("-fx-padding: 20; -fx-background-color: -latar-kartu; -fx-background-radius: 14; -fx-border-color: -garis; -fx-border-radius: 14;");







            List<PengumpulanTugas> daftarPengumpulan = session.getTugasService().getDaftarPengumpulan(tugas.getIdMateri());







            Label lblJudulSub = new Label("Daftar Pengumpulan Siswa (" + daftarPengumpulan.size() + " terkumpul)");



            lblJudulSub.getStyleClass().add("judul-kartu");



            boxSubmissions.getChildren().add(lblJudulSub);







            if (daftarPengumpulan.isEmpty()) {



                VBox boxKosong = new VBox(8);



                boxKosong.setAlignment(Pos.CENTER);



                boxKosong.setStyle("-fx-padding: 24 16;");



                Label lblIconKosong = new Label();



                lblIconKosong.setGraphic(new IkonView(Ikon.FOLDER_KOSONG, 32));



                Label lblTeksKosong = new Label("Belum ada siswa yang mengumpulkan tugas ini.");



                lblTeksKosong.getStyleClass().add("teks-redup");



                boxKosong.getChildren().addAll(lblIconKosong, lblTeksKosong);



                boxSubmissions.getChildren().add(boxKosong);



            } else {



                VBox tabel = new VBox(6);



                for (PengumpulanTugas p : daftarPengumpulan) {



                    HBox barisS = new HBox(12);



                    barisS.setAlignment(Pos.CENTER_LEFT);



                    barisS.setStyle("-fx-padding: 10 14; -fx-background-color: -garis; -fx-background-radius: 10;");







                    Label avatar = new Label(hitungInisial(p.getNamaSiswa()));



                    avatar.getStyleClass().add("avatar");



                    avatar.setStyle("-fx-min-width: 34px; -fx-min-height: 34px; -fx-max-width: 34px; -fx-max-height: 34px; -fx-font-size: 12px;");







                    VBox infoSiswa = new VBox(2);



                    HBox.setHgrow(infoSiswa, Priority.ALWAYS);



                    Label lblNama = new Label(p.getNamaSiswa());



                    lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px;");



                    String ket = "Kelas " + p.getKelasSiswa() + " · NIS " + p.getNis() + " · Dikumpulkan: " + p.getWaktuKumpulFormatted();



                    Label lblKet = new Label(ket);



                    lblKet.getStyleClass().add("teks-kecil");



                    infoSiswa.getChildren().addAll(lblNama, lblKet);



                    if (!p.getCatatan().isBlank()) {



                        Label lblCat = new Label("Catatan: " + p.getCatatan());



                        lblCat.setStyle("-fx-font-size: 11px; -fx-text-fill: -teks-redup; -fx-font-style: italic;");



                        infoSiswa.getChildren().add(lblCat);



                    }







                    Label namaBerkas = new Label(p.getNamaBerkas());



                    namaBerkas.setStyle("-fx-font-size: 12px; -fx-text-fill: -aksen;");



                    namaBerkas.setGraphic(new IkonView(Ikon.DOKUMEN, 14));







                    Button btnBuka = new Button("Buka Berkas");



                    btnBuka.getStyleClass().add("tombol-sekunder");



                    btnBuka.setGraphic(new IkonView(Ikon.UNDUH, 14));



                    btnBuka.setOnAction(e -> {



                        File f = new File(p.getPathBerkas());



                        if (f.exists()) {



                            session.bukaDokumen(f.toURI().toString());



                        } else {



                            DialogUtil.peringatan("Berkas tidak ditemukan pada direktori penyimpanan.");



                        }



                    });







                    barisS.getChildren().addAll(avatar, infoSiswa, namaBerkas, btnBuka);



                    tabel.getChildren().add(barisS);



                }



                boxSubmissions.getChildren().add(tabel);



            }



            isi.getChildren().add(boxSubmissions);



        }







        tampilkanKonten(gulir(isi));



    }







    private void tampilkanFormUpload(VBox container, MateriTugas tugas, boolean kumpulUlang) {



        container.getChildren().clear();







        Label lblJudulUpload = new Label(kumpulUlang ? "Kumpulkan Ulang Berkas Tugas" : "Form Pengumpulan Tugas");



        lblJudulUpload.getStyleClass().add("judul-kartu");







        Label lblPetunjuk = new Label("Pilih berkas tugas Anda (mendukung format .pdf, .docx, .doc, .xlsx, .xls, .pptx, .ppt, .txt, .zip, dll).");



        lblPetunjuk.getStyleClass().add("teks-kecil");







        File[] berkasDipilih = new File[1];







        HBox barisPilih = new HBox(12);



        barisPilih.setAlignment(Pos.CENTER_LEFT);







        Button btnPilih = new Button("Pilih Berkas");



        btnPilih.getStyleClass().add("tombol-sekunder");



        btnPilih.setGraphic(new IkonView(Ikon.UNGGAH, 16));







        Label lblNamaFile = new Label("Belum ada berkas yang dipilih");



        lblNamaFile.getStyleClass().add("teks-redup");







        btnPilih.setOnAction(e -> {



            FileChooser chooser = new FileChooser();



            chooser.setTitle("Pilih Berkas Tugas");



            chooser.getExtensionFilters().addAll(



                    new FileChooser.ExtensionFilter("Semua Dokumen yang Didukung (*.pdf, *.docx, *.xlsx, *.pptx, *.txt, *.zip)",



                            "*.pdf", "*.docx", "*.doc", "*.xlsx", "*.xls", "*.pptx", "*.ppt", "*.txt", "*.zip", "*.rar", "*.csv"),



                    new FileChooser.ExtensionFilter("Dokumen PDF (*.pdf)", "*.pdf"),



                    new FileChooser.ExtensionFilter("Dokumen Microsoft Word (*.docx, *.doc)", "*.docx", "*.doc"),



                    new FileChooser.ExtensionFilter("Dokumen Microsoft Excel (*.xlsx, *.xls)", "*.xlsx", "*.xls"),



                    new FileChooser.ExtensionFilter("Dokumen Microsoft PowerPoint (*.pptx, *.ppt)", "*.pptx", "*.ppt"),



                    new FileChooser.ExtensionFilter("Semua Berkas (*.*)", "*.*")



            );



            File f = chooser.showOpenDialog(container.getScene().getWindow());



            if (f != null) {



                berkasDipilih[0] = f;



                long kb = f.length() / 1024;



                lblNamaFile.setText(f.getName() + " (" + (kb > 1024 ? (kb / 1024) + " MB" : kb + " KB") + ")");



                lblNamaFile.setStyle("-fx-text-fill: -teks; -fx-font-weight: bold;");



            }



        });







        barisPilih.getChildren().addAll(btnPilih, lblNamaFile);







        TextField tfCatatan = new TextField();



        tfCatatan.setPromptText("Catatan opsional untuk pengajar...");







        HBox barisAksi = new HBox(10);



        barisAksi.setAlignment(Pos.CENTER_LEFT);







        Button btnKirim = new Button("Kumpulkan Tugas");



        btnKirim.getStyleClass().add("tombol-utama");



        btnKirim.setOnAction(e -> {



            if (berkasDipilih[0] == null) {



                DialogUtil.peringatan("Silakan pilih berkas tugas terlebih dahulu sebelum mengumpulkan.");



                return;



            }



            try {



                session.getTugasService().simpanPengumpulan(tugas.getIdMateri(), siswa, berkasDipilih[0], tfCatatan.getText());



                tandai(tugas);



                DialogUtil.info("Tugas berhasil dikumpulkan!");



                tampilkanTugas(tugas);



            } catch (Exception ex) {



                DialogUtil.peringatan("Gagal mengumpulkan tugas: " + ex.getMessage());



            }



        });







        barisAksi.getChildren().add(btnKirim);



        if (kumpulUlang) {



            Button btnBatal = new Button("Batal");



            btnBatal.getStyleClass().add("tombol-hantu");



            btnBatal.setOnAction(e -> tampilkanTugas(tugas));



            barisAksi.getChildren().add(btnBatal);



        }







        container.getChildren().addAll(lblJudulUpload, lblPetunjuk, barisPilih, tfCatatan, barisAksi);



    }







}



