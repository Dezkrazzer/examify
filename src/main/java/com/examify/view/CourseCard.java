package com.examify.view;



import com.examify.model.Course;

import javafx.geometry.Insets;

import javafx.geometry.Pos;

import javafx.scene.control.Button;

import javafx.scene.control.ContentDisplay;

import javafx.scene.control.Label;

import javafx.scene.control.ProgressBar;

import javafx.scene.layout.HBox;

import javafx.scene.layout.Priority;

import javafx.scene.layout.Region;

import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;



/** Kartu kursus kaca dengan header bermotif, dipakai di landing page dan Kursus Saya. */

public class CourseCard extends VBox {



    public static final double LEBAR = 270;

    private static final double TINGGI_HEADER = 120;

    private static final double RADIUS = 16;



    private final Button tombol = new Button();



    /** @param progres 0.0-1.0, atau negatif untuk menyembunyikan progres (pengunjung belum login) */

    public CourseCard(Course kursus, double progres, String teksTombol) {

        super(10);

        getStyleClass().addAll("kaca", "kartu-kursus");

        setPrefWidth(LEBAR);

        setMaxWidth(LEBAR);



        StackPane header = new StackPane(CoursePattern.buat(kursus.getKode(), LEBAR - 24, TINGGI_HEADER, RADIUS));



        Label nama = new Label(kursus.getNama());

        nama.getStyleClass().add("judul-kartu");

        nama.setWrapText(true);

        nama.setMinHeight(48);



        Label pengajar = new Label(kursus.getPengajar());

        pengajar.getStyleClass().add("teks-redup");

        pengajar.setGraphic(new IconView(Icon.PROFIL, 14));



        int jmlMateri = kursus.getJumlahMateriPembelajaran();
        int jmlUjian = kursus.getJumlahUjian();
        String teksInfo = jmlUjian > 0
                ? kursus.getDaftarBab().size() + " bab  ·  " + jmlMateri + " materi  ·  " + jmlUjian + " ujian"
                : kursus.getDaftarBab().size() + " bab  ·  " + jmlMateri + " materi";
        Label info = new Label(teksInfo);

        info.getStyleClass().add("teks-kecil");

        info.setGraphic(new IconView(Icon.BUKU, 14));



        getChildren().addAll(header, nama, pengajar, info);

        if (progres >= 0) {

            getChildren().add(barisProgres(progres));

        }



        Region pengisi = new Region();

        VBox.setVgrow(pengisi, Priority.ALWAYS);

        tombol.setText(teksTombol);

        tombol.getStyleClass().add("tombol-utama");

        tombol.setMaxWidth(Double.MAX_VALUE);

        tombol.setGraphic(new IconView(Icon.PANAH_KANAN, 16));

        tombol.setContentDisplay(ContentDisplay.RIGHT);

        getChildren().addAll(pengisi, tombol);



        AnimationUtil.angkatSaatHover(this);

    }



    private static HBox barisProgres(double progres) {

        ProgressBar bar = new ProgressBar(progres);

        bar.getStyleClass().add("progres-tipis");

        bar.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(bar, Priority.ALWAYS);

        Label persen = new Label(Math.round(progres * 100) + "%");

        persen.getStyleClass().add("teks-kecil");

        HBox baris = new HBox(8, bar, persen);

        baris.setAlignment(Pos.CENTER_LEFT);

        return baris;

    }



    public void setOnBuka(Runnable aksi) {

        tombol.setOnAction(e -> aksi.run());

        setOnMouseClicked(e -> aksi.run());

    }

}

