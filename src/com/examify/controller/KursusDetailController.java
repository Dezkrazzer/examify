package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.Bab;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriPdf;
import com.examify.model.MateriTautan;
import com.examify.model.MateriTeks;
import com.examify.model.MateriUjian;
import com.examify.model.RiwayatNilai;
import com.examify.model.Siswa;
import com.examify.model.Ujian;
import com.examify.service.KursusService;
import com.examify.service.ProgresBelajarService;
import com.examify.utils.FormatUtil;
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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
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
    @FXML private Label lblPersen;
    @FXML private Label lblRingkasProgres;
    @FXML private ProgressBar pbProgres;
    @FXML private VBox vbBab;
    @FXML private StackPane spKonten;

    private final AppSession session = AppSession.getInstance();
    private final KursusService kursusService = session.getKursusService();
    private final ProgresBelajarService progresService = session.getProgresService();
    private final Map<Bab, ToggleButton> tombolBab = new LinkedHashMap<>();
    private final ToggleGroup grupBab = new ToggleGroup();
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
        buatDaftarBab();
        perbaruiProgres();

        Materi fokus = session.getMateriFokus();
        if (fokus != null && babDari(fokus).isPresent()) {
            bukaMateri(fokus);
        } else {
            tampilkanBab(babPertamaBelumSelesai());
        }
    }

    // ------------------------------------------------------------------ daftar bab

    private void buatDaftarBab() {
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
        perbaruiStatusBab();
    }

    private boolean babSelesai(Bab bab) {
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
        double progres = progresService.getProgres(siswa, kursus);
        lblPersen.setText(Math.round(progres * 100) + "%");
        lblRingkasProgres.setText(progresService.getJumlahSelesai(siswa, kursus) + " dari "
                + kursus.getJumlahMateri() + " materi selesai");
        pbProgres.setProgress(progres);
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
        tombolBab.get(bab).setSelected(true);
        VBox isi = panel();
        Label label = new Label("BAB " + bab.getNomor());
        label.getStyleClass().add("label-kecil-kapital");
        Label judul = new Label(bab.getJudul());
        judul.getStyleClass().add("judul-halaman");
        Label info = new Label(bab.getDaftarMateri().size() + " item - klik untuk membuka");
        info.getStyleClass().add("teks-redup");
        isi.getChildren().addAll(label, judul, info);

        List<Node> baris = bab.getDaftarMateri().stream().map(this::barisMateri).toList();
        isi.getChildren().addAll(baris);
        tampilkanKonten(gulir(isi));
        AnimasiUtil.berurutan(baris, Duration.millis(50));
    }

    private Node barisMateri(Materi materi) {
        VBox teks = new VBox(2, label(materi.getJudul(), "judul-kartu"), label(materi.getKeterangan(), "teks-kecil"));
        HBox.setHgrow(teks, Priority.ALWAYS);
        Node status;
        if (progresService.isSelesai(siswa, kursus, materi)) {
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

    private static StackPane ikonMateri(Materi materi, double ukuran) {
        Ikon ikon = switch (materi.getJenis()) {
            case TEKS -> Ikon.DOKUMEN;
            case LINK -> Ikon.TAUTAN;
            case PDF -> Ikon.PDF;
            case UJIAN -> Ikon.UJIAN;
        };
        StackPane wadah = new StackPane(new IkonView(ikon, ukuran));
        wadah.getStyleClass().addAll("ikon-materi", "ikon-materi-" + materi.getJenis().name().toLowerCase());
        return wadah;
    }

    // ------------------------------------------------------------------ membuka materi

    private void bukaMateri(Materi materi) {
        babDari(materi).ifPresent(bab -> tombolBab.get(bab).setSelected(true));
        if (materi instanceof MateriTeks teks) {
            tampilkanBacaan(teks);
        } else if (materi instanceof MateriTautan tautan) {
            session.bukaDokumen(tautan.getAlamat().toString());
            tandaiLaluKembaliKeBab(materi);
        } else if (materi instanceof MateriPdf pdf) {
            session.bukaDokumen(pdf.getFile().toAbsolutePath().toUri().toString());
            tandaiLaluKembaliKeBab(materi);
        } else if (materi instanceof MateriUjian ujian) {
            tampilkanUjian(ujian);
        }
    }

    private void tandai(Materi materi) {
        try {
            progresService.tandaiSelesai(siswa, kursus, materi);
        } catch (IOException e) {
            DialogUtil.peringatan("Progres belajar gagal disimpan: " + e.getMessage());
        }
        perbaruiProgres();
        perbaruiStatusBab();
    }

    private void tandaiLaluKembaliKeBab(Materi materi) {
        tandai(materi);
        babDari(materi).ifPresent(this::tampilkanBab);
    }

    private void tampilkanBacaan(MateriTeks materi) {
        String markdown;
        try {
            markdown = kursusService.bacaTeks(materi);
        } catch (IOException e) {
            DialogUtil.peringatan("Materi tidak dapat dibaca: " + e.getMessage());
            return;
        }
        tandai(materi); // bacaan dianggap selesai saat dibuka
        VBox isi = panel();
        isi.getChildren().addAll(tombolKembaliKeBab(materi), label(materi.getKeterangan().toUpperCase(), "label-kecil-kapital"),
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
        int percobaan = session.getRekapService().getJumlahPercobaan(siswa, ujian);
        Optional<RiwayatNilai> terbaik = session.getRekapService().getNilaiTerbaik(siswa, ujian);
        boolean bolehMulai = session.getUjianService().bolehMulai(siswa, ujian);

        VBox isi = panel();
        HBox kepala = new HBox(16, ikonMateri(materi, 22),
                new VBox(2, label("UJIAN", "label-kecil-kapital"), label(materi.getJudul(), "judul-halaman"),
                        label(ujian.getMataPelajaran() + " - " + ujian.getKodeUjian(), "teks-redup")));
        kepala.setAlignment(Pos.CENTER_LEFT);

        HBox info = new HBox(14,
                kotakInfo("Durasi", ujian.getDurasiMenit() + " menit"),
                kotakInfo("Jumlah soal", String.valueOf(ujian.getJumlahSoal())),
                kotakInfo("KKM", FormatUtil.angka(ujian.getKkm())),
                kotakInfo("Percobaan", String.valueOf(percobaan)));

        Label aturan = label(ujian.isBolehUlang()
                ? "Ujian ini boleh dikerjakan ulang. Nilai terbaik yang dipakai."
                : "Ujian ini hanya dapat dikerjakan satu kali.", "teks-redup");
        aturan.setGraphic(new IkonView(ujian.isBolehUlang() ? Ikon.CENTANG : Ikon.GEMBOK, 16));

        isi.getChildren().addAll(tombolKembaliKeBab(materi), kepala, info, aturan);
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

    // ------------------------------------------------------------------ pembantu UI

    private static VBox panel() {
        VBox isi = new VBox(14);
        isi.getStyleClass().add("panel-baca");
        return isi;
    }

    private static ScrollPane gulir(Node isi) {
        ScrollPane sp = new ScrollPane(isi);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private static Label label(String teks, String kelas) {
        Label label = new Label(teks);
        label.getStyleClass().add(kelas);
        label.setWrapText(true);
        return label;
    }

    private static Button tombol(String teks, Ikon ikon, String kelas) {
        Button tombol = new Button(teks, new IkonView(ikon, 16));
        tombol.getStyleClass().add(kelas);
        return tombol;
    }

    private static VBox kotakInfo(String judul, String nilai) {
        VBox kotak = new VBox(2, label(judul, "teks-kecil"), label(nilai, "judul-kartu"));
        kotak.getStyleClass().add("kartu-ujian-info");
        HBox.setHgrow(kotak, Priority.ALWAYS);
        kotak.setMaxWidth(Double.MAX_VALUE);
        return kotak;
    }

    private Node tombolKembaliKeBab(Materi materi) {
        Bab bab = babDari(materi).orElseThrow();
        Button kembali = tombol("Bab " + bab.getNomor() + ": " + bab.getJudul(), Ikon.PANAH_KIRI, "tombol-hantu");
        kembali.setOnAction(e -> tampilkanBab(bab));
        return kembali;
    }

    @FXML
    private void handleKembali() {
        session.setMateriFokus(null);
        SceneRouter.pindah(Layar.KURSUS_SAYA);
    }
}
