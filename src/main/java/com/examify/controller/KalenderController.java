package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.AgendaKalender;
import com.examify.model.Guru;
import com.examify.model.Kursus;
import com.examify.model.Siswa;
import com.examify.service.KalenderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller halaman Kalender Akademik untuk Siswa (berdasarkan kursus yang diikuti)
 * maupun Guru (berdasarkan kursus yang dibuat oleh guru tersebut).
 */
public class KalenderController {

    private static final Locale LOKAL_ID = Locale.of("id", "ID");
    private static final DateTimeFormatter FORMAT_BULAN_TAHUN =
            DateTimeFormatter.ofPattern("MMMM yyyy", LOKAL_ID);
    private static final DateTimeFormatter FORMAT_HARI_LENGKAP =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", LOKAL_ID);
    private static final DateTimeFormatter FORMAT_HARI_PENDEK =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", LOKAL_ID);
    private static final String[] NAMA_HARI = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

    private enum ModeTampilan {
        MONTH,
        DAY,
        UPCOMING
    }

    /** Opsi dropdown filter kursus: Semua Kursus (kode == null) atau satu kursus spesifik. */
    public record PilihanKursus(String kode, String label) {
        @Override
        public String toString() {
            return label;
        }
    }

    @FXML private Label lblSubjudul;
    @FXML private Label lblRingkasanKursus;
    @FXML private Label lblRingkasanMendatang;

    @FXML private ToggleButton tbMonth;
    @FXML private ToggleButton tbDay;
    @FXML private ToggleButton tbUpcoming;
    @FXML private ComboBox<PilihanKursus> cbKursus;

    @FXML private VBox boxBelumAdaKursus;
    @FXML private Label lblKosongKursusJudul;
    @FXML private Label lblKosongKursusPesan;
    @FXML private StackPane spKontenKalender;

    // Month View
    @FXML private HBox boxTampilanMonth;
    @FXML private Label lblJudulBulan;
    @FXML private Label lblJumlahBulan;
    @FXML private GridPane gridHeaderHari;
    @FXML private GridPane gridBulan;
    @FXML private HBox boxLegendaSelesai;
    @FXML private Label lblDetailTanggalJudul;
    @FXML private Label lblDetailTanggalSub;
    @FXML private VBox boxDaftarDetailTanggal;

    // Day View
    @FXML private VBox boxTampilanDay;
    @FXML private Label lblJudulHari;
    @FXML private Label lblBadgeHariIni;
    @FXML private Label lblJumlahHari;
    @FXML private VBox boxDaftarHari;

    // Upcoming View
    @FXML private VBox boxTampilanUpcoming;
    @FXML private Label lblSubUpcoming;
    @FXML private Label lblJumlahUpcoming;
    @FXML private VBox boxDaftarUpcoming;

    private final AppSession session = AppSession.getInstance();
    private KalenderService kalenderService;
    private boolean modeGuru;
    private Guru guru;
    private Siswa siswa;

    private ModeTampilan modeAktif = ModeTampilan.MONTH;
    private YearMonth bulanAktif = YearMonth.now();
    private LocalDate tanggalTerpilih = LocalDate.now();
    private List<Kursus> daftarKursusRelevan = List.of();

    @FXML
    private void initialize() {
        modeGuru = session.isGuru();
        guru = session.getGuruAktif();
        siswa = session.getSiswaAktif();

        kalenderService = new KalenderService(
                session.getKursusService(),
                session.getUjianService(),
                session.getPendaftaranService(),
                session.getProgresService(),
                session.getTugasService(),
                session.getRekapService()
        );

        if (modeGuru) {
            lblSubjudul.setText("Jadwal batas tugas, kuis bab, dan ujian CBT berdasarkan kursus yang Anda buat.");
            lblKosongKursusJudul.setText("Belum Ada Kursus yang Anda Buat");
            lblKosongKursusPesan.setText("Buat kursus baru di menu Kursus Saya lalu tambahkan jadwal tugas, kuis bab, atau ujian CBT agar otomatis tampil di kalender ini.");
            if (boxLegendaSelesai != null) {
                boxLegendaSelesai.setVisible(false);
                boxLegendaSelesai.setManaged(false);
            }
        } else {
            lblSubjudul.setText("Jadwal batas tugas, kuis bab, dan ujian CBT berdasarkan kursus yang kamu ikuti.");
            lblKosongKursusJudul.setText("Belum Ada Kursus yang Diikuti");
            lblKosongKursusPesan.setText("Masukkan kode akses kursus dari pengajar di menu Kursus Saya agar jadwal tugas, kuis, dan ujian otomatis tersinkron ke kalender ini.");
        }

        ToggleGroup grupMode = new ToggleGroup();
        tbMonth.setToggleGroup(grupMode);
        tbDay.setToggleGroup(grupMode);
        tbUpcoming.setToggleGroup(grupMode);
        tbMonth.setSelected(true);

        grupMode.selectedToggleProperty().addListener((obs, lama, baru) -> {
            if (baru == null && lama != null) {
                lama.setSelected(true);
            }
        });

        siapkanHeaderHari();
        muatDaftarKursusFilter();
        cbKursus.valueProperty().addListener((obs, lama, baru) -> segarkanTampilan());

        segarkanTampilan();
    }

    private List<Kursus> ambilDaftarKursusRelevan() {
        return modeGuru
                ? kalenderService.getKursusDibuatGuru(guru)
                : kalenderService.getKursusDiikuti(siswa);
    }

    private List<AgendaKalender> ambilSemuaAgenda(String filterKode) {
        return modeGuru
                ? kalenderService.getSemuaAgendaGuru(guru, filterKode)
                : kalenderService.getSemuaAgenda(siswa, filterKode);
    }

    private List<AgendaKalender> ambilAgendaBulan(YearMonth bulan, String filterKode) {
        return modeGuru
                ? kalenderService.filterBulanGuru(guru, bulan, filterKode)
                : kalenderService.filterBulan(siswa, bulan, filterKode);
    }

    private List<AgendaKalender> ambilAgendaHari(LocalDate tanggal, String filterKode) {
        return modeGuru
                ? kalenderService.filterHariGuru(guru, tanggal, filterKode)
                : kalenderService.filterHari(siswa, tanggal, filterKode);
    }

    private List<AgendaKalender> ambilAgendaMendatang(LocalDateTime sekarang, String filterKode) {
        return modeGuru
                ? kalenderService.filterMendatangGuru(guru, sekarang, filterKode)
                : kalenderService.filterMendatang(siswa, sekarang, filterKode);
    }

    private void siapkanHeaderHari() {
        gridHeaderHari.getColumnConstraints().clear();
        gridHeaderHari.getChildren().clear();
        gridBulan.getColumnConstraints().clear();

        for (int kol = 0; kol < 7; kol++) {
            ColumnConstraints ccHeader = new ColumnConstraints();
            ccHeader.setPercentWidth(100.0 / 7.0);
            ccHeader.setHgrow(Priority.ALWAYS);
            ccHeader.setFillWidth(true);
            ccHeader.setHalignment(HPos.CENTER);
            gridHeaderHari.getColumnConstraints().add(ccHeader);

            ColumnConstraints ccBulan = new ColumnConstraints();
            ccBulan.setPercentWidth(100.0 / 7.0);
            ccBulan.setHgrow(Priority.ALWAYS);
            ccBulan.setFillWidth(true);
            gridBulan.getColumnConstraints().add(ccBulan);

            Label lbl = new Label(NAMA_HARI[kol]);
            lbl.getStyleClass().add("kalender-header-hari");
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            gridHeaderHari.add(lbl, kol, 0);
        }
    }

    private void muatDaftarKursusFilter() {
        daftarKursusRelevan = ambilDaftarKursusRelevan();
        List<PilihanKursus> opsi = new ArrayList<>();
        opsi.add(new PilihanKursus(null, "Semua Kursus (" + daftarKursusRelevan.size() + ")"));
        for (Kursus k : daftarKursusRelevan) {
            opsi.add(new PilihanKursus(k.getKode(), k.getKode() + " · " + k.getNama()));
        }
        cbKursus.setItems(FXCollections.observableArrayList(opsi));
        cbKursus.getSelectionModel().selectFirst();
        cbKursus.setDisable(daftarKursusRelevan.isEmpty());
    }

    private String kodeKursusTerpilih() {
        PilihanKursus p = cbKursus.getValue();
        return p == null ? null : p.kode();
    }

    private void segarkanTampilan() {
        daftarKursusRelevan = ambilDaftarKursusRelevan();
        LocalDateTime sekarang = LocalDateTime.now();
        String filterKode = kodeKursusTerpilih();

        List<AgendaKalender> mendatang = ambilAgendaMendatang(sekarang, filterKode);
        lblRingkasanKursus.setText(daftarKursusRelevan.size()
                + (modeGuru ? " kursus dibuat" : " kursus diikuti"));
        lblRingkasanMendatang.setText(mendatang.size() + " agenda mendatang");

        boolean belumAdaKursus = daftarKursusRelevan.isEmpty();
        boxBelumAdaKursus.setVisible(belumAdaKursus);
        boxBelumAdaKursus.setManaged(belumAdaKursus);
        spKontenKalender.setVisible(!belumAdaKursus);
        spKontenKalender.setManaged(!belumAdaKursus);

        if (belumAdaKursus) {
            return;
        }

        aturVisibilitasPanel(boxTampilanMonth, modeAktif == ModeTampilan.MONTH);
        aturVisibilitasPanel(boxTampilanDay, modeAktif == ModeTampilan.DAY);
        aturVisibilitasPanel(boxTampilanUpcoming, modeAktif == ModeTampilan.UPCOMING);

        switch (modeAktif) {
            case MONTH -> renderTampilanMonth(sekarang, filterKode);
            case DAY -> renderTampilanDay(sekarang, filterKode);
            case UPCOMING -> renderTampilanUpcoming(mendatang, sekarang);
        }
    }

    private void aturVisibilitasPanel(Node panel, boolean tampil) {
        panel.setVisible(tampil);
        panel.setManaged(tampil);
    }

    // ==================== 1. RENDER MONTH ====================

    private void renderTampilanMonth(LocalDateTime sekarang, String filterKode) {
        lblJudulBulan.setText(kapitalAwal(bulanAktif.atDay(1).format(FORMAT_BULAN_TAHUN)));

        List<AgendaKalender> agendaBulan = ambilAgendaBulan(bulanAktif, filterKode);
        lblJumlahBulan.setText(agendaBulan.size() + " agenda");

        Map<LocalDate, List<AgendaKalender>> perTanggal = ambilSemuaAgenda(filterKode)
                .stream()
                .collect(Collectors.groupingBy(AgendaKalender::tanggal));

        gridBulan.getChildren().clear();
        gridBulan.getRowConstraints().clear();

        LocalDate awalBulan = bulanAktif.atDay(1);
        int offsetSenin = awalBulan.getDayOfWeek().getValue() - 1;
        int totalSelAktif = offsetSenin + bulanAktif.lengthOfMonth();
        int jumlahBaris = (int) Math.ceil(totalSelAktif / 7.0);

        for (int b = 0; b < jumlahBaris; b++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(92);
            rc.setPrefHeight(98);
            rc.setVgrow(Priority.ALWAYS);
            gridBulan.getRowConstraints().add(rc);
        }

        LocalDate tanggalMulaiGrid = awalBulan.minusDays(offsetSenin);
        LocalDate hariIni = LocalDate.now();

        for (int i = 0; i < jumlahBaris * 7; i++) {
            LocalDate tgl = tanggalMulaiGrid.plusDays(i);
            boolean dalamBulan = YearMonth.from(tgl).equals(bulanAktif);
            List<AgendaKalender> agendaHari = perTanggal.getOrDefault(tgl, List.of());

            VBox sel = buatSelTanggalBulan(tgl, dalamBulan, tgl.equals(hariIni),
                    tgl.equals(tanggalTerpilih), agendaHari);
            gridBulan.add(sel, i % 7, i / 7);
        }

        renderPanelDetailTanggal(sekarang, perTanggal.getOrDefault(tanggalTerpilih, List.of()));
    }

    private VBox buatSelTanggalBulan(LocalDate tgl, boolean dalamBulan, boolean isHariIni,
                                     boolean isTerpilih, List<AgendaKalender> agendaHari) {
        VBox sel = new VBox(5);
        sel.getStyleClass().add("kalender-sel");
        if (!dalamBulan) {
            sel.getStyleClass().add("kalender-sel-luar");
        }
        if (isHariIni) {
            sel.getStyleClass().add("kalender-sel-hari-ini");
        }
        if (isTerpilih) {
            sel.getStyleClass().add("kalender-sel-terpilih");
        }
        sel.setMaxWidth(Double.MAX_VALUE);

        HBox headerSel = new HBox(4);
        headerSel.setAlignment(Pos.CENTER_LEFT);

        Label lblNomor = new Label(String.valueOf(tgl.getDayOfMonth()));
        lblNomor.getStyleClass().add("kalender-nomor-tgl");
        lblNomor.setTextOverrun(OverrunStyle.CLIP);
        if (isHariIni) {
            lblNomor.getStyleClass().add("kalender-nomor-hari-ini");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerSel.getChildren().addAll(lblNomor, spacer);

        if (!agendaHari.isEmpty()) {
            Label lblHitungan = new Label(String.valueOf(agendaHari.size()));
            lblHitungan.getStyleClass().add("teks-kecil");
            lblHitungan.setStyle("-fx-font-size: 10.5px; -fx-font-weight: bold;");
            headerSel.getChildren().add(lblHitungan);
        }

        sel.getChildren().add(headerSel);

        int batasChip = 2;
        int tampil = Math.min(batasChip, agendaHari.size());
        for (int idx = 0; idx < tampil; idx++) {
            AgendaKalender ag = agendaHari.get(idx);
            Label chip = new Label(ag.judul());
            chip.getStyleClass().addAll("kalender-chip", kelasChipAgenda(ag));
            chip.setMaxWidth(Double.MAX_VALUE);
            Tooltip.install(chip, new Tooltip(
                    ag.labelTipe() + " · " + ag.kursus().getNama() + "\n"
                            + ag.judul() + " (" + ag.getFormatJam() + ")"
            ));
            sel.getChildren().add(chip);
        }

        if (agendaHari.size() > batasChip) {
            Label lblLainnya = new Label("+" + (agendaHari.size() - batasChip) + " agenda lainnya");
            lblLainnya.getStyleClass().add("teks-kecil");
            lblLainnya.setStyle("-fx-font-size: 10px; -fx-font-weight: 600; -fx-padding: 0 4;");
            sel.getChildren().add(lblLainnya);
        }

        sel.setOnMouseClicked(e -> {
            tanggalTerpilih = tgl;
            if (!YearMonth.from(tgl).equals(bulanAktif)) {
                bulanAktif = YearMonth.from(tgl);
            }
            if (e.getClickCount() >= 2) {
                aktifkanMode(ModeTampilan.DAY);
            } else {
                segarkanTampilan();
            }
        });

        return sel;
    }

    private void renderPanelDetailTanggal(LocalDateTime sekarang, List<AgendaKalender> daftar) {
        lblDetailTanggalJudul.setText(kapitalAwal(tanggalTerpilih.format(FORMAT_HARI_PENDEK)));
        if (daftar.isEmpty()) {
            lblDetailTanggalSub.setText("Tidak ada jadwal pada tanggal ini.");
        } else {
            lblDetailTanggalSub.setText(daftar.size() + " agenda terjadwal pada tanggal ini.");
        }

        boxDaftarDetailTanggal.getChildren().clear();
        if (daftar.isEmpty()) {
            VBox kosong = new VBox(6);
            kosong.getStyleClass().add("modal-kotak-jadwal");
            kosong.setAlignment(Pos.CENTER_LEFT);
            Label lblInfo = new Label("Belum ada batas tugas, kuis, atau ujian di tanggal ini.");
            lblInfo.getStyleClass().add("teks-redup");
            lblInfo.setWrapText(true);
            lblInfo.setStyle("-fx-font-size: 12.5px;");
            kosong.getChildren().add(lblInfo);
            boxDaftarDetailTanggal.getChildren().add(kosong);
            return;
        }

        for (AgendaKalender ag : daftar) {
            VBox item = new VBox(8);
            item.getStyleClass().add("modal-kotak-jadwal");

            HBox barisBadge = new HBox(6);
            barisBadge.setAlignment(Pos.CENTER_LEFT);
            Label bTipe = new Label(ag.labelTipe());
            bTipe.getStyleClass().addAll("kalender-chip", kelasChipAgenda(ag));
            Label bJam = new Label(ag.getFormatJam());
            bJam.getStyleClass().add("teks-kecil");
            bJam.setStyle("-fx-font-weight: bold;");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            barisBadge.getChildren().addAll(bTipe, sp, bJam);

            Label lblJudul = new Label(ag.judul());
            lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px;");
            lblJudul.setWrapText(true);

            Label lblKursus = new Label(ag.kursus().getKode() + " · " + ag.kursus().getNama()
                    + " (" + ag.getLabelStatus(sekarang) + ")");
            lblKursus.getStyleClass().add("teks-kecil");
            lblKursus.setWrapText(true);

            Button btnBuka = new Button(ag.jenis().getTeksTombol(modeGuru));
            btnBuka.getStyleClass().add((!modeGuru && ag.selesai()) ? "tombol-sekunder" : "tombol-utama");
            btnBuka.setStyle("-fx-padding: 6 12; -fx-font-size: 12px;");
            btnBuka.setMaxWidth(Double.MAX_VALUE);
            btnBuka.setOnAction(e -> bukaAgendaDiKursus(ag));

            item.getChildren().addAll(barisBadge, lblJudul, lblKursus, btnBuka);
            boxDaftarDetailTanggal.getChildren().add(item);
        }
    }

    // ==================== 2. RENDER DAY ====================

    private void renderTampilanDay(LocalDateTime sekarang, String filterKode) {
        lblJudulHari.setText(kapitalAwal(tanggalTerpilih.format(FORMAT_HARI_LENGKAP)));
        boolean hariIni = tanggalTerpilih.equals(LocalDate.now());
        lblBadgeHariIni.setVisible(hariIni);
        lblBadgeHariIni.setManaged(hariIni);

        List<AgendaKalender> agendaHari = ambilAgendaHari(tanggalTerpilih, filterKode);
        lblJumlahHari.setText(agendaHari.size() + " agenda pada hari ini");

        boxDaftarHari.getChildren().clear();
        if (agendaHari.isEmpty()) {
            VBox kosong = buatKotakKosong(
                    "Tidak Ada Agenda pada Tanggal Ini",
                    "Tidak ditemukan batas pengumpulan tugas, kuis bab, maupun jadwal ujian pada "
                            + kapitalAwal(tanggalTerpilih.format(FORMAT_HARI_LENGKAP)) + ".",
                    "Lihat Upcoming Event",
                    () -> aktifkanMode(ModeTampilan.UPCOMING)
            );
            boxDaftarHari.getChildren().add(kosong);
            return;
        }

        for (AgendaKalender ag : agendaHari) {
            boxDaftarHari.getChildren().add(buatKartuAgendaLengkap(ag, sekarang, false));
        }
    }

    // ==================== 3. RENDER UPCOMING EVENT ====================

    private void renderTampilanUpcoming(List<AgendaKalender> mendatang, LocalDateTime sekarang) {
        PilihanKursus pilihan = cbKursus.getValue();
        String labelSemua = modeGuru ? "semua kursus yang Anda buat" : "semua kursus yang diikuti";
        String namaFilter = (pilihan == null || pilihan.kode() == null) ? labelSemua : pilihan.label();
        lblSubUpcoming.setText("Menampilkan agenda yang akan datang atau sedang berlangsung untuk " + namaFilter + ".");
        lblJumlahUpcoming.setText(mendatang.size() + " agenda mendatang");

        boxDaftarUpcoming.getChildren().clear();
        if (mendatang.isEmpty()) {
            VBox kosong = buatKotakKosong(
                    "Belum Ada Agenda Mendatang",
                    "Tidak ada tugas, kuis bab, atau ujian yang dijadwalkan dalam waktu mendatang pada filter kursus ini.",
                    "Lihat Kalender Month",
                    () -> aktifkanMode(ModeTampilan.MONTH)
            );
            boxDaftarUpcoming.getChildren().add(kosong);
            return;
        }

        Map<LocalDate, List<AgendaKalender>> grupTanggal = new LinkedHashMap<>();
        for (AgendaKalender ag : mendatang) {
            grupTanggal.computeIfAbsent(ag.tanggal(), k -> new ArrayList<>()).add(ag);
        }

        LocalDate hariIni = sekarang.toLocalDate();
        for (Map.Entry<LocalDate, List<AgendaKalender>> entry : grupTanggal.entrySet()) {
            LocalDate tgl = entry.getKey();
            VBox seksiTanggal = new VBox(10);

            HBox headerTanggal = new HBox(10);
            headerTanggal.setAlignment(Pos.CENTER_LEFT);

            Label lblHeader = new Label(formatJudulGrupTanggal(tgl, hariIni));
            lblHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label lblRelatif = new Label(formatSelisihHari(tgl, hariIni));
            lblRelatif.getStyleClass().addAll("badge", tgl.equals(hariIni) ? "badge" : "badge-netral");

            headerTanggal.getChildren().addAll(lblHeader, lblRelatif);
            seksiTanggal.getChildren().add(headerTanggal);

            for (AgendaKalender ag : entry.getValue()) {
                seksiTanggal.getChildren().add(buatKartuAgendaLengkap(ag, sekarang, true));
            }
            boxDaftarUpcoming.getChildren().add(seksiTanggal);
        }
    }

    // ==================== KOMPONEN KARTU AGENDA ====================

    private HBox buatKartuAgendaLengkap(AgendaKalender ag, LocalDateTime sekarang, boolean tampilkanRelatif) {
        HBox kartu = new HBox(16);
        kartu.getStyleClass().add("kartu-agenda");
        kartu.setAlignment(Pos.CENTER_LEFT);

        // Kolom Jam
        VBox kolJam = new VBox(3);
        kolJam.setMinWidth(115);
        kolJam.setPrefWidth(120);
        kolJam.setAlignment(Pos.CENTER_LEFT);

        Label lblJam = new Label(ag.getFormatJam());
        lblJam.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px;");
        Label lblSubJam = new Label(tampilkanRelatif
                ? formatSelisihWaktuDetail(ag, sekarang)
                : ag.labelTipe());
        lblSubJam.getStyleClass().add("teks-kecil");
        kolJam.getChildren().addAll(lblJam, lblSubJam);

        // Garis aksen vertikal sesuai tipe / status
        Region garis = new Region();
        garis.getStyleClass().addAll("garis-agenda", kelasGarisAgenda(ag));
        garis.setMinHeight(48);

        // Informasi Utama Agenda
        VBox kolInfo = new VBox(5);
        HBox.setHgrow(kolInfo, Priority.ALWAYS);

        FlowPane barisBadge = new FlowPane(8, 6);
        barisBadge.setAlignment(Pos.CENTER_LEFT);

        Label bTipe = new Label(ag.labelTipe());
        bTipe.getStyleClass().addAll("kalender-chip", kelasChipAgenda(ag));

        Label bKursus = new Label(ag.kursus().getKode() + " · " + ag.kursus().getNama());
        bKursus.getStyleClass().addAll("badge", "badge-netral");
        bKursus.setStyle("-fx-padding: 2 9; -fx-font-size: 11px;");

        Label bStatus = buatBadgeStatus(ag, sekarang);
        barisBadge.getChildren().addAll(bTipe, bKursus, bStatus);

        Label lblJudul = new Label(ag.judul());
        lblJudul.getStyleClass().add("judul-kartu");
        lblJudul.setWrapText(true);

        Label lblRincian = new Label(ag.bab().getJudul() + " · " + ag.infoJadwal());
        lblRincian.getStyleClass().add("teks-redup");
        lblRincian.setStyle("-fx-font-size: 12.5px;");
        lblRincian.setWrapText(true);

        kolInfo.getChildren().addAll(barisBadge, lblJudul, lblRincian);

        // Tombol Navigasi ke Materi di Kursus
        Button btnAksi = new Button(ag.jenis().getTeksTombol(modeGuru));
        btnAksi.getStyleClass().add((!modeGuru && ag.selesai()) ? "tombol-sekunder" : "tombol-utama");
        btnAksi.setMinWidth(126);
        btnAksi.setOnAction(e -> bukaAgendaDiKursus(ag));

        kartu.getChildren().addAll(kolJam, garis, kolInfo, btnAksi);
        return kartu;
    }

    private Label buatBadgeStatus(AgendaKalender ag, LocalDateTime sekarang) {
        Label badge = new Label(ag.getLabelStatus(sekarang));
        badge.getStyleClass().add("badge");
        badge.setStyle("-fx-padding: 2 9; -fx-font-size: 11px;");
        if (modeGuru) {
            if (!ag.isSedangBerlangsung(sekarang)) {
                badge.getStyleClass().add("badge-netral");
            }
            return badge;
        }
        if (ag.selesai()) {
            badge.getStyleClass().add("badge-sukses");
        } else if (ag.isTerlewat(sekarang)) {
            badge.getStyleClass().add("badge-bahaya");
        } else if (!ag.isSedangBerlangsung(sekarang)) {
            badge.getStyleClass().add("badge-netral");
        }
        return badge;
    }

    private VBox buatKotakKosong(String judul, String pesan, String teksTombol, Runnable aksiTombol) {
        VBox box = new VBox(10);
        box.getStyleClass().add("kotak-kosong-kalender");
        box.setAlignment(Pos.CENTER);

        Label lblJudul = new Label(judul);
        lblJudul.getStyleClass().add("judul-kartu");

        Label lblPesan = new Label(pesan);
        lblPesan.getStyleClass().add("teks-redup");
        lblPesan.setWrapText(true);
        lblPesan.setMaxWidth(480);
        lblPesan.setAlignment(Pos.CENTER);

        Button btn = new Button(teksTombol);
        btn.getStyleClass().add("tombol-sekunder");
        btn.setOnAction(e -> aksiTombol.run());

        box.getChildren().addAll(lblJudul, lblPesan, btn);
        return box;
    }

    private String kelasChipAgenda(AgendaKalender ag) {
        if (!modeGuru && ag.selesai()) {
            return "kalender-chip-selesai";
        }
        return switch (ag.jenis()) {
            case TUGAS -> "kalender-chip-tugas";
            case UJIAN -> "kalender-chip-ujian";
            case KUIS -> "kalender-chip-kuis";
        };
    }

    private String kelasGarisAgenda(AgendaKalender ag) {
        if (!modeGuru && ag.selesai()) {
            return "garis-agenda-selesai";
        }
        return switch (ag.jenis()) {
            case TUGAS -> "garis-agenda-tugas";
            case UJIAN -> "garis-agenda-ujian";
            case KUIS -> "garis-agenda-kuis";
        };
    }

    private String formatJudulGrupTanggal(LocalDate tgl, LocalDate hariIni) {
        String terformat = kapitalAwal(tgl.format(FORMAT_HARI_LENGKAP));
        if (tgl.equals(hariIni)) {
            return "Hari Ini · " + terformat;
        }
        if (tgl.equals(hariIni.plusDays(1))) {
            return "Besok · " + terformat;
        }
        return terformat;
    }

    private String formatSelisihHari(LocalDate tgl, LocalDate hariIni) {
        long hari = ChronoUnit.DAYS.between(hariIni, tgl);
        if (hari <= 0) return "Hari ini";
        if (hari == 1) return "Besok";
        return hari + " hari lagi";
    }

    private String formatSelisihWaktuDetail(AgendaKalender ag, LocalDateTime sekarang) {
        if (ag.isSedangBerlangsung(sekarang)) {
            return "Sedang dibuka";
        }
        long menit = ChronoUnit.MINUTES.between(sekarang, ag.waktu());
        if (menit <= 0) {
            return "Hari ini";
        }
        if (menit < 60) {
            return menit + " menit lagi";
        }
        long jam = menit / 60;
        if (jam < 24) {
            return jam + " jam lagi";
        }
        long hari = ChronoUnit.DAYS.between(sekarang.toLocalDate(), ag.tanggal());
        return hari == 1 ? "Besok" : hari + " hari lagi";
    }

    private static String kapitalAwal(String teks) {
        if (teks == null || teks.isEmpty()) return "";
        return Character.toUpperCase(teks.charAt(0)) + teks.substring(1);
    }

    private void bukaAgendaDiKursus(AgendaKalender ag) {
        session.setKursusAktif(ag.kursus());
        session.setMateriFokus(ag.materi());
        SceneRouter.pindah(Layar.KURSUS_DETAIL);
    }

    private void aktifkanMode(ModeTampilan modeBaru) {
        this.modeAktif = modeBaru;
        tbMonth.setSelected(modeBaru == ModeTampilan.MONTH);
        tbDay.setSelected(modeBaru == ModeTampilan.DAY);
        tbUpcoming.setSelected(modeBaru == ModeTampilan.UPCOMING);
        segarkanTampilan();
    }

    // ==================== EVENT HANDLER FXML ====================

    @FXML
    private void handleModeMonth() {
        aktifkanMode(ModeTampilan.MONTH);
    }

    @FXML
    private void handleModeDay() {
        aktifkanMode(ModeTampilan.DAY);
    }

    @FXML
    private void handleModeUpcoming() {
        aktifkanMode(ModeTampilan.UPCOMING);
    }

    @FXML
    private void handleBulanSebelumnya() {
        bulanAktif = bulanAktif.minusMonths(1);
        tanggalTerpilih = bulanAktif.atDay(Math.min(tanggalTerpilih.getDayOfMonth(), bulanAktif.lengthOfMonth()));
        segarkanTampilan();
    }

    @FXML
    private void handleBulanBerikutnya() {
        bulanAktif = bulanAktif.plusMonths(1);
        tanggalTerpilih = bulanAktif.atDay(Math.min(tanggalTerpilih.getDayOfMonth(), bulanAktif.lengthOfMonth()));
        segarkanTampilan();
    }

    @FXML
    private void handleHariSebelumnya() {
        tanggalTerpilih = tanggalTerpilih.minusDays(1);
        bulanAktif = YearMonth.from(tanggalTerpilih);
        segarkanTampilan();
    }

    @FXML
    private void handleHariBerikutnya() {
        tanggalTerpilih = tanggalTerpilih.plusDays(1);
        bulanAktif = YearMonth.from(tanggalTerpilih);
        segarkanTampilan();
    }

    @FXML
    private void handleLoncatHariIni() {
        tanggalTerpilih = LocalDate.now();
        bulanAktif = YearMonth.from(tanggalTerpilih);
        segarkanTampilan();
    }

    @FXML
    private void handleBukaDayDariDetail() {
        aktifkanMode(ModeTampilan.DAY);
    }

    @FXML
    private void handleBukaKursusSaya() {
        SceneRouter.pindah(Layar.KURSUS_SAYA);
    }
}
