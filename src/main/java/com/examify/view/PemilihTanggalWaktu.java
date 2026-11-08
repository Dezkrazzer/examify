package com.examify.view;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 * Komponen pemilih tanggal dan waktu terpadu dalam satu input dan popover
 * dua kolom (Kalender bulanan di kiri + Jam/Menit/Slider/AM-PM di kanan).
 */
public class PemilihTanggalWaktu extends VBox {

    private static final DateTimeFormatter FORMAT_TAMPIL =
            DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter FORMAT_BULAN_TAHUN =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private final boolean bolehKosong;
    private final String teksPlaceholder;

    private LocalDateTime nilaiTerpilih;
    private YearMonth bulanTampil;
    private LocalDate tanggalSementara;
    private int jam12Sementara = 11;
    private int menitSementara = 59;
    private boolean pmSementara = true;
    private boolean sedangSinkronisasiSlider = false;
    private long waktuTutupOtomatisTerakhir = 0L;

    private final HBox kotakInput;
    private final Label lblNilai;
    private final StackPane btnHapus;
    private final Popup popup;
    private final VBox panelPopoverRoot;
    private final List<Scene> daftarSceneTerpasangFilter = new ArrayList<>();
    private final EventHandler<MouseEvent> filterKlikLuar;

    private Label lblBulanTahun;
    private GridPane gridHari;
    private Label lblKotakJam;
    private Label lblKotakMenit;
    private Slider sliderJam;
    private Slider sliderMenit;
    private StackPane btnAm;
    private Label lblAm;
    private StackPane btnPm;
    private Label lblPm;

    public PemilihTanggalWaktu(LocalDateTime nilaiAwal, boolean bolehKosong, String teksPlaceholder) {
        this.bolehKosong = bolehKosong;
        this.teksPlaceholder = teksPlaceholder != null ? teksPlaceholder : "Pilih tanggal & waktu...";

        setSpacing(0);
        setFillWidth(true);

        kotakInput = new HBox(10);
        kotakInput.setAlignment(Pos.CENTER_LEFT);
        kotakInput.setCursor(Cursor.HAND);
        kotakInput.setPadding(new Insets(10, 14, 10, 14));
        terapkanGayaKotakInput(false);

        lblNilai = new Label();
        lblNilai.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblNilai, Priority.ALWAYS);

        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        popup.setConsumeAutoHidingEvents(false);

        panelPopoverRoot = bangunPanelPopover();
        popup.getContent().add(panelPopoverRoot);

        filterKlikLuar = e -> {
            if (!popup.isShowing()) {
                return;
            }
            Object target = e.getTarget();
            if (target instanceof Node nodeTarget && apakahDiDalamNode(nodeTarget, panelPopoverRoot)) {
                return;
            }
            waktuTutupOtomatisTerakhir = System.currentTimeMillis();
            terapkanPilihanSementara();
            popup.hide();
            if (target instanceof Node nodeTarget && apakahDiDalamNode(nodeTarget, kotakInput)) {
                e.consume();
            }
        };

        Label ikonX = new Label("×");
        ikonX.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-font-weight: 800; -fx-padding: 0;");
        btnHapus = new StackPane(ikonX);
        btnHapus.setMinSize(22, 22);
        btnHapus.setPrefSize(22, 22);
        btnHapus.setMaxSize(22, 22);
        btnHapus.setCursor(Cursor.HAND);
        btnHapus.setOnMouseClicked(e -> {
            e.consume();
            setWaktu(null);
            if (popup.isShowing()) {
                popup.hide();
            }
        });

        StackPane ikonKalender = buatIkonKalenderKecil();

        kotakInput.getChildren().addAll(lblNilai, btnHapus, ikonKalender);
        getChildren().add(kotakInput);

        popup.setOnHidden(e -> {
            lepasFilterKlikLuar();
            terapkanGayaKotakInput(false);
        });

        kotakInput.setOnMouseClicked(e -> {
            if (System.currentTimeMillis() - waktuTutupOtomatisTerakhir < 200) {
                return;
            }
            if (popup.isShowing()) {
                terapkanPilihanSementara();
                popup.hide();
            } else {
                tampilkanPopover();
            }
        });

        setWaktu(nilaiAwal);
    }

    public PemilihTanggalWaktu(LocalDateTime nilaiAwal, boolean bolehKosong) {
        this(nilaiAwal, bolehKosong, bolehKosong ? "Pilih tanggal & waktu (opsional)..." : "Pilih tanggal & waktu...");
    }

    private boolean apakahDiDalamNode(Node target, Node induk) {
        Node cur = target;
        while (cur != null) {
            if (cur == induk) {
                return true;
            }
            cur = cur.getParent();
        }
        return false;
    }

    private void pasangFilterKlikLuar() {
        lepasFilterKlikLuar();
        for (Window w : Window.getWindows()) {
            if (w != null && w != popup && w.getScene() != null) {
                Scene sc = w.getScene();
                if (!daftarSceneTerpasangFilter.contains(sc)) {
                    sc.addEventFilter(MouseEvent.MOUSE_PRESSED, filterKlikLuar);
                    daftarSceneTerpasangFilter.add(sc);
                }
            }
        }
    }

    private void lepasFilterKlikLuar() {
        for (Scene sc : daftarSceneTerpasangFilter) {
            sc.removeEventFilter(MouseEvent.MOUSE_PRESSED, filterKlikLuar);
        }
        daftarSceneTerpasangFilter.clear();
    }

    public LocalDateTime getWaktu() {
        return nilaiTerpilih;
    }

    public void setWaktu(LocalDateTime waktu) {
        if (waktu == null && !bolehKosong) {
            this.nilaiTerpilih = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));
        } else {
            this.nilaiTerpilih = waktu;
        }
        muatKeSementara(this.nilaiTerpilih);
        perbaruiTampilanInput();
    }

    private void muatKeSementara(LocalDateTime sumber) {
        LocalDateTime acuan = sumber != null
                ? sumber
                : LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));
        this.tanggalSementara = acuan.toLocalDate();
        this.bulanTampil = YearMonth.from(this.tanggalSementara);
        int jam24 = acuan.getHour();
        this.pmSementara = jam24 >= 12;
        int j12 = jam24 % 12;
        this.jam12Sementara = (j12 == 0) ? 12 : j12;
        this.menitSementara = acuan.getMinute();
    }

    private void perbaruiTampilanInput() {
        if (nilaiTerpilih == null) {
            lblNilai.setText(teksPlaceholder);
            lblNilai.setStyle("-fx-font-size: 13.5px; -fx-font-weight: 500; -fx-text-fill: #94a3b8;");
            btnHapus.setVisible(false);
            btnHapus.setManaged(false);
        } else {
            lblNilai.setText(nilaiTerpilih.format(FORMAT_TAMPIL).toLowerCase(Locale.ENGLISH));
            lblNilai.setStyle("-fx-font-size: 13.5px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");
            btnHapus.setVisible(bolehKosong);
            btnHapus.setManaged(bolehKosong);
        }
    }

    private void terapkanGayaKotakInput(boolean aktif) {
        String warnaBorder = aktif ? "#0ea5e9" : "#cbd5e1";
        String efek = aktif
                ? "-fx-effect: dropshadow(gaussian, rgba(14, 165, 233, 0.18), 8, 0, 0, 2);"
                : "";
        kotakInput.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 10;"
                        + " -fx-border-color: " + warnaBorder + "; -fx-border-radius: 10;"
                        + " -fx-border-width: 1.4; " + efek);
    }

    private StackPane buatIkonKalenderKecil() {
        SVGPath svg = new SVGPath();
        svg.setContent(
                "M19 4h-1V2h-2v2H8V2H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9"
                        + " 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10zm0-12H5V6h14v2z");
        svg.setStyle("-fx-fill: #0284c7;");
        svg.setScaleX(0.78);
        svg.setScaleY(0.78);

        StackPane wadah = new StackPane(svg);
        wadah.setMinSize(24, 24);
        wadah.setPrefSize(24, 24);
        wadah.setMaxSize(24, 24);
        wadah.setStyle(
                "-fx-background-color: #e0f2fe; -fx-background-radius: 6;"
                        + " -fx-border-color: #bae6fd; -fx-border-radius: 6; -fx-border-width: 1;");
        return wadah;
    }

    private VBox bangunPanelPopover() {
        VBox kartuPopover = new VBox(0);
        kartuPopover.setPrefWidth(490);
        kartuPopover.setStyle(
                "-fx-background-color: #64748b; -fx-background-radius: 10;"
                        + " -fx-border-color: #475569; -fx-border-radius: 10; -fx-border-width: 1;"
                        + " -fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.28), 22, 0.15, 0, 8);");

        // Baris Header Tab: Date (Kiri) | Time (Kanan)
        GridPane headerTab = new GridPane();
        headerTab.setPadding(new Insets(12, 14, 8, 14));
        ColumnConstraints kol1 = new ColumnConstraints();
        kol1.setPercentWidth(53);
        ColumnConstraints kol2 = new ColumnConstraints();
        kol2.setPercentWidth(47);
        headerTab.getColumnConstraints().addAll(kol1, kol2);

        HBox tabDate = new HBox(6);
        tabDate.setAlignment(Pos.CENTER);
        tabDate.setPadding(new Insets(7, 10, 7, 10));
        tabDate.setStyle(
                "-fx-background-color: rgba(255,255,255,0.08);"
                        + " -fx-border-color: rgba(15,23,42,0.28);"
                        + " -fx-border-width: 1 0.5 1 1;"
                        + " -fx-background-radius: 4 0 0 4; -fx-border-radius: 4 0 0 4;");
        Label lblHeaderDate = new Label("Date");
        lblHeaderDate.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 12.5px; -fx-font-weight: 700;");
        tabDate.getChildren().add(lblHeaderDate);

        HBox tabTime = new HBox(6);
        tabTime.setAlignment(Pos.CENTER);
        tabTime.setPadding(new Insets(7, 10, 7, 10));
        tabTime.setStyle(
                "-fx-background-color: rgba(255,255,255,0.08);"
                        + " -fx-border-color: rgba(15,23,42,0.28);"
                        + " -fx-border-width: 1 1 1 0.5;"
                        + " -fx-background-radius: 0 4 4 0; -fx-border-radius: 0 4 4 0;");
        Label lblHeaderTime = new Label("Time");
        lblHeaderTime.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 12.5px; -fx-font-weight: 700;");
        tabTime.getChildren().add(lblHeaderTime);

        headerTab.add(tabDate, 0, 0);
        headerTab.add(tabTime, 1, 0);

        // Badan 2 Kolom: Kalender (Kiri) + Jam/Slider (Kanan)
        HBox badan = new HBox(16);
        badan.setPadding(new Insets(6, 14, 14, 14));
        badan.setAlignment(Pos.TOP_LEFT);

        VBox panelKalender = bangunPanelKalender();
        VBox panelWaktu = bangunPanelWaktu();
        HBox.setHgrow(panelWaktu, Priority.ALWAYS);

        badan.getChildren().addAll(panelKalender, panelWaktu);

        // Footer Bar: Now (+ Clear bila opsional) di kiri, Done di kanan
        HBox footer = new HBox(0);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(
                "-fx-background-color: #475569; -fx-background-radius: 0 0 9 9;"
                        + " -fx-border-color: rgba(15,23,42,0.22); -fx-border-width: 1 0 0 0;");

        Button btnNow = new Button("Now");
        btnNow.setCursor(Cursor.HAND);
        btnNow.setPrefHeight(38);
        btnNow.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.22); -fx-text-fill: #f8fafc;"
                        + " -fx-font-size: 12.5px; -fx-font-weight: 700;"
                        + " -fx-padding: 0 18 0 18; -fx-background-radius: 0 0 0 9;");
        btnNow.setOnAction(e -> {
            LocalDateTime sekarang = LocalDateTime.now().withSecond(0).withNano(0);
            muatKeSementara(sekarang);
            segarkanKalender();
            segarkanKontrolWaktu();
            terapkanPilihanSementara();
        });

        footer.getChildren().add(btnNow);

        if (bolehKosong) {
            Button btnKosongkan = new Button("Clear");
            btnKosongkan.setCursor(Cursor.HAND);
            btnKosongkan.setPrefHeight(38);
            btnKosongkan.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #cbd5e1;"
                            + " -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 0 14 0 14;");
            btnKosongkan.setOnAction(e -> {
                setWaktu(null);
                popup.hide();
            });
            footer.getChildren().add(btnKosongkan);
        }

        Region spasiFooter = new Region();
        HBox.setHgrow(spasiFooter, Priority.ALWAYS);

        Button btnDone = new Button("✓ Done");
        btnDone.setCursor(Cursor.HAND);
        btnDone.setPrefHeight(38);
        btnDone.setStyle(
                "-fx-background-color: #06b6d4; -fx-text-fill: #ffffff;"
                        + " -fx-font-size: 12.5px; -fx-font-weight: 800;"
                        + " -fx-padding: 0 20 0 20; -fx-background-radius: 0 0 9 0;");
        btnDone.setOnAction(e -> {
            terapkanPilihanSementara();
            popup.hide();
        });

        footer.getChildren().addAll(spasiFooter, btnDone);

        kartuPopover.getChildren().addAll(headerTab, badan, footer);
        return kartuPopover;
    }

    private VBox bangunPanelKalender() {
        VBox wadahKalender = new VBox(0);
        wadahKalender.setPrefWidth(244);
        wadahKalender.setMinWidth(244);
        wadahKalender.setMaxWidth(244);
        wadahKalender.setStyle(
                "-fx-background-color: #e2e8f0; -fx-background-radius: 6;"
                        + " -fx-border-color: rgba(15,23,42,0.18); -fx-border-radius: 6; -fx-border-width: 1;");

        // Header Bulan & Navigasi < > menggunakan StackPane + Label agar bebas override CSS .button
        HBox barBulan = new HBox(4);
        barBulan.setAlignment(Pos.CENTER);
        barBulan.setPadding(new Insets(6, 8, 6, 8));
        barBulan.setStyle("-fx-background-color: #22d3ee; -fx-background-radius: 5 5 0 0;");

        Label lblPanahKiri = new Label("‹");
        lblPanahKiri.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: 800; -fx-padding: 0;");
        StackPane btnBulanLalu = new StackPane(lblPanahKiri);
        btnBulanLalu.setMinSize(26, 26);
        btnBulanLalu.setPrefSize(26, 26);
        btnBulanLalu.setMaxSize(26, 26);
        btnBulanLalu.setCursor(Cursor.HAND);
        btnBulanLalu.setOnMouseClicked(e -> {
            bulanTampil = bulanTampil.minusMonths(1);
            segarkanKalender();
        });

        lblBulanTahun = new Label();
        lblBulanTahun.setMaxWidth(Double.MAX_VALUE);
        lblBulanTahun.setAlignment(Pos.CENTER);
        HBox.setHgrow(lblBulanTahun, Priority.ALWAYS);
        lblBulanTahun.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: 800;");

        Label lblPanahKanan = new Label("›");
        lblPanahKanan.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: 800; -fx-padding: 0;");
        StackPane btnBulanDepan = new StackPane(lblPanahKanan);
        btnBulanDepan.setMinSize(26, 26);
        btnBulanDepan.setPrefSize(26, 26);
        btnBulanDepan.setMaxSize(26, 26);
        btnBulanDepan.setCursor(Cursor.HAND);
        btnBulanDepan.setOnMouseClicked(e -> {
            bulanTampil = bulanTampil.plusMonths(1);
            segarkanKalender();
        });

        barBulan.getChildren().addAll(btnBulanLalu, lblBulanTahun, btnBulanDepan);

        // Baris Nama Hari (Mon..Sun)
        GridPane headerHari = new GridPane();
        headerHari.setPadding(new Insets(6, 6, 6, 6));
        headerHari.setStyle("-fx-background-color: #f8fafc;");
        String[] namaHari = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7.0);
            cc.setHalignment(HPos.CENTER);
            headerHari.getColumnConstraints().add(cc);

            Label lbl = new Label(namaHari[i]);
            lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10.5px; -fx-font-weight: 800;");
            headerHari.add(lbl, i, 0);
        }

        // Grid Tanggal 6x7
        gridHari = new GridPane();
        gridHari.setPadding(new Insets(6, 6, 8, 6));
        gridHari.setHgap(2);
        gridHari.setVgap(3);
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7.0);
            cc.setHalignment(HPos.CENTER);
            gridHari.getColumnConstraints().add(cc);
        }
        for (int r = 0; r < 6; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(28);
            rc.setPrefHeight(28);
            rc.setValignment(VPos.CENTER);
            gridHari.getRowConstraints().add(rc);
        }

        wadahKalender.getChildren().addAll(barBulan, headerHari, gridHari);
        return wadahKalender;
    }

    private VBox bangunPanelWaktu() {
        VBox panelWaktu = new VBox(12);
        panelWaktu.setAlignment(Pos.TOP_CENTER);
        panelWaktu.setPadding(new Insets(8, 4, 4, 4));

        // Kotak Jam : Menit
        HBox barisJamDigital = new HBox(10);
        barisJamDigital.setAlignment(Pos.CENTER);

        lblKotakJam = new Label("11");
        lblKotakJam.setAlignment(Pos.CENTER);
        lblKotakJam.setMinSize(58, 48);
        lblKotakJam.setPrefSize(58, 48);
        lblKotakJam.setStyle(
                "-fx-background-color: #475569; -fx-text-fill: #ffffff;"
                        + " -fx-font-size: 20px; -fx-font-weight: 800; -fx-background-radius: 4;"
                        + " -fx-border-color: rgba(15,23,42,0.35); -fx-border-radius: 4; -fx-border-width: 1;");

        VBox titikDua = new VBox(6);
        titikDua.setAlignment(Pos.CENTER);
        Circle c1 = new Circle(3, javafx.scene.paint.Color.web("#475569"));
        Circle c2 = new Circle(3, javafx.scene.paint.Color.web("#475569"));
        titikDua.getChildren().addAll(c1, c2);

        lblKotakMenit = new Label("59");
        lblKotakMenit.setAlignment(Pos.CENTER);
        lblKotakMenit.setMinSize(58, 48);
        lblKotakMenit.setPrefSize(58, 48);
        lblKotakMenit.setStyle(
                "-fx-background-color: #475569; -fx-text-fill: #ffffff;"
                        + " -fx-font-size: 20px; -fx-font-weight: 800; -fx-background-radius: 4;"
                        + " -fx-border-color: rgba(15,23,42,0.35); -fx-border-radius: 4; -fx-border-width: 1;");

        barisJamDigital.getChildren().addAll(lblKotakJam, titikDua, lblKotakMenit);

        // Slider Hours
        VBox boxSliderJam = new VBox(4);
        Label lblHours = new Label("Hours");
        lblHours.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 11.5px; -fx-font-weight: 700;");
        sliderJam = new Slider(1, 12, 11);
        sliderJam.setBlockIncrement(1);
        sliderJam.setMajorTickUnit(1);
        sliderJam.setMinorTickCount(0);
        sliderJam.setSnapToTicks(true);
        sliderJam.setCursor(Cursor.HAND);
        sliderJam.valueProperty().addListener((obs, lama, baru) -> {
            if (sedangSinkronisasiSlider) {
                return;
            }
            jam12Sementara = Math.max(1, Math.min(12, (int) Math.round(baru.doubleValue())));
            lblKotakJam.setText(String.format("%02d", jam12Sementara));
            terapkanPilihanSementara();
        });
        boxSliderJam.getChildren().addAll(lblHours, sliderJam);

        // Slider Minutes
        VBox boxSliderMenit = new VBox(4);
        Label lblMinutes = new Label("Minutes");
        lblMinutes.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 11.5px; -fx-font-weight: 700;");
        sliderMenit = new Slider(0, 59, 59);
        sliderMenit.setBlockIncrement(1);
        sliderMenit.setMajorTickUnit(5);
        sliderMenit.setMinorTickCount(4);
        sliderMenit.setSnapToTicks(true);
        sliderMenit.setCursor(Cursor.HAND);
        sliderMenit.valueProperty().addListener((obs, lama, baru) -> {
            if (sedangSinkronisasiSlider) {
                return;
            }
            menitSementara = Math.max(0, Math.min(59, (int) Math.round(baru.doubleValue())));
            lblKotakMenit.setText(String.format("%02d", menitSementara));
            terapkanPilihanSementara();
        });
        boxSliderMenit.getChildren().addAll(lblMinutes, sliderMenit);

        // Toggle AM / PM menggunakan StackPane + Label agar teks "AM" dan "PM" tidak pernah terpotong menjadi "..."
        HBox barisAmPm = new HBox(0);
        barisAmPm.setAlignment(Pos.CENTER);
        barisAmPm.setPadding(new Insets(4, 0, 0, 0));

        lblAm = new Label("AM");
        lblAm.setTextOverrun(OverrunStyle.CLIP);
        btnAm = new StackPane(lblAm);
        btnAm.setCursor(Cursor.HAND);
        btnAm.setMinSize(52, 34);
        btnAm.setPrefSize(52, 34);
        btnAm.setMaxSize(52, 34);
        btnAm.setOnMouseClicked(e -> {
            pmSementara = false;
            perbaruiGayaAmPm();
            terapkanPilihanSementara();
        });

        lblPm = new Label("PM");
        lblPm.setTextOverrun(OverrunStyle.CLIP);
        btnPm = new StackPane(lblPm);
        btnPm.setCursor(Cursor.HAND);
        btnPm.setMinSize(52, 34);
        btnPm.setPrefSize(52, 34);
        btnPm.setMaxSize(52, 34);
        btnPm.setOnMouseClicked(e -> {
            pmSementara = true;
            perbaruiGayaAmPm();
            terapkanPilihanSementara();
        });

        barisAmPm.getChildren().addAll(btnAm, btnPm);

        panelWaktu.getChildren().addAll(barisJamDigital, boxSliderJam, boxSliderMenit, barisAmPm);
        return panelWaktu;
    }

    private void segarkanKalender() {
        lblBulanTahun.setText(bulanTampil.format(FORMAT_BULAN_TAHUN));
        gridHari.getChildren().clear();

        LocalDate awalBulan = bulanTampil.atDay(1);
        int offsetSenin = awalBulan.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        LocalDate tanggalMulaiGrid = awalBulan.minusDays(offsetSenin);
        LocalDate hariIni = LocalDate.now();

        for (int baris = 0; baris < 6; baris++) {
            for (int kol = 0; kol < 7; kol++) {
                LocalDate tglSel = tanggalMulaiGrid.plusDays((long) baris * 7 + kol);
                boolean bulanIni = YearMonth.from(tglSel).equals(bulanTampil);
                boolean terpilih = tglSel.equals(tanggalSementara);
                boolean adalahHariIni = tglSel.equals(hariIni);

                Label lblAngka = new Label(String.valueOf(tglSel.getDayOfMonth()));
                lblAngka.setTextOverrun(OverrunStyle.CLIP);
                lblAngka.setAlignment(Pos.CENTER);

                StackPane selHari = new StackPane(lblAngka);
                selHari.setCursor(Cursor.HAND);
                selHari.setMinSize(28, 28);
                selHari.setPrefSize(28, 28);
                selHari.setMaxSize(28, 28);

                if (terpilih) {
                    selHari.setStyle("-fx-background-color: #06b6d4; -fx-background-radius: 999;");
                    lblAngka.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 0;");
                } else if (adalahHariIni) {
                    selHari.setStyle(
                            "-fx-background-color: transparent; -fx-border-color: #64748b;"
                                    + " -fx-border-radius: 999; -fx-border-width: 1.2;");
                    lblAngka.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 0;");
                } else if (bulanIni) {
                    selHari.setStyle("-fx-background-color: transparent;");
                    lblAngka.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px; -fx-font-weight: 700; -fx-padding: 0;");
                } else {
                    selHari.setStyle("-fx-background-color: transparent;");
                    lblAngka.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-padding: 0;");
                }

                selHari.setOnMouseClicked(e -> {
                    tanggalSementara = tglSel;
                    bulanTampil = YearMonth.from(tglSel);
                    segarkanKalender();
                    terapkanPilihanSementara();
                });

                GridPane.setHalignment(selHari, HPos.CENTER);
                GridPane.setValignment(selHari, VPos.CENTER);
                gridHari.add(selHari, kol, baris);
            }
        }
    }

    private void segarkanKontrolWaktu() {
        sedangSinkronisasiSlider = true;
        try {
            lblKotakJam.setText(String.format("%02d", jam12Sementara));
            lblKotakMenit.setText(String.format("%02d", menitSementara));
            sliderJam.setValue(jam12Sementara);
            sliderMenit.setValue(menitSementara);
            perbaruiGayaAmPm();
        } finally {
            sedangSinkronisasiSlider = false;
        }
    }

    private void perbaruiGayaAmPm() {
        String bgAktif = "-fx-background-color: #06b6d4;";
        String bgNonAktif = "-fx-background-color: #475569; -fx-border-color: rgba(15,23,42,0.3); -fx-border-width: 1;";

        btnAm.setStyle((pmSementara ? bgNonAktif : bgAktif)
                + " -fx-background-radius: 4 0 0 4; -fx-border-radius: 4 0 0 4;");
        lblAm.setStyle("-fx-text-fill: " + (pmSementara ? "#e2e8f0" : "#ffffff")
                + "; -fx-font-size: 12.5px; -fx-font-weight: 800; -fx-padding: 0;");

        btnPm.setStyle((pmSementara ? bgAktif : bgNonAktif)
                + " -fx-background-radius: 0 4 4 0; -fx-border-radius: 0 4 4 0;");
        lblPm.setStyle("-fx-text-fill: " + (pmSementara ? "#ffffff" : "#e2e8f0")
                + "; -fx-font-size: 12.5px; -fx-font-weight: 800; -fx-padding: 0;");
    }

    private void terapkanPilihanSementara() {
        int jam24;
        if (pmSementara) {
            jam24 = (jam12Sementara == 12) ? 12 : jam12Sementara + 12;
        } else {
            jam24 = (jam12Sementara == 12) ? 0 : jam12Sementara;
        }
        if (tanggalSementara == null) {
            tanggalSementara = LocalDate.now();
        }
        this.nilaiTerpilih = LocalDateTime.of(tanggalSementara, LocalTime.of(jam24, menitSementara));
        perbaruiTampilanInput();
    }

    private void tampilkanPopover() {
        if (getScene() == null || getScene().getWindow() == null) {
            return;
        }
        muatKeSementara(nilaiTerpilih);
        segarkanKalender();
        segarkanKontrolWaktu();
        terapkanGayaKotakInput(true);

        Window window = getScene().getWindow();
        Bounds boundsLayar = kotakInput.localToScreen(kotakInput.getBoundsInLocal());
        if (boundsLayar == null) {
            return;
        }
        double x = boundsLayar.getMinX();
        double y = boundsLayar.getMaxY() + 6;
        popup.show(window, x, y);
        pasangFilterKlikLuar();
        panelPopoverRoot.requestFocus();
    }
}
