package com.examify.view;

import com.examify.model.AnggotaTim;
import com.examify.model.AnggotaTim.Sosial;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

/** Kartu kaca profil anggota kelompok: foto, nama, NIM, dan tombol tautan sosial. */
public class KartuAnggota extends VBox {

    public static final double LEBAR = 260;
    private static final double UKURAN_FOTO = 104;
    private static final Map<Sosial, Ikon> IKON_SOSIAL = Map.of(
            Sosial.WEBSITE, Ikon.GLOBE, Sosial.GITHUB, Ikon.GITHUB,
            Sosial.INSTAGRAM, Ikon.INSTAGRAM, Sosial.LINKEDIN, Ikon.LINKEDIN);
    private static final Map<Sosial, String> LABEL_SOSIAL = Map.of(
            Sosial.WEBSITE, "Website", Sosial.GITHUB, "GitHub",
            Sosial.INSTAGRAM, "Instagram", Sosial.LINKEDIN, "LinkedIn");

    /**
     * @param dirData   folder tempat path foto relatif di-resolve
     * @param bukaTautan aksi saat tombol sosial aktif diklik (menerima URL)
     */
    public KartuAnggota(AnggotaTim anggota, Path dirData, Consumer<String> bukaTautan) {
        super(12);
        getStyleClass().addAll("kaca", "kartu-anggota");
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(LEBAR);
        setMaxWidth(LEBAR);

        Label nama = new Label(anggota.getNama());
        nama.getStyleClass().add("judul-kartu");
        nama.setWrapText(true);
        nama.setStyle("-fx-text-alignment: center;");

        Label nim = new Label("NIM " + anggota.getNim());
        nim.getStyleClass().add("badge-nim");

        HBox sosial = new HBox(10);
        sosial.setAlignment(Pos.CENTER);
        for (Sosial jenis : Sosial.values()) {
            sosial.getChildren().add(tombolSosial(jenis, anggota, bukaTautan));
        }

        getChildren().addAll(foto(anggota, dirData), nama, nim, sosial);
        AnimasiUtil.angkatSaatHover(this);
    }

    private static Node foto(AnggotaTim anggota, Path dirData) {
        StackPane bingkai = new StackPane();
        bingkai.getStyleClass().add("bingkai-foto-anggota");
        bingkai.setMinSize(UKURAN_FOTO + 8, UKURAN_FOTO + 8);
        bingkai.setMaxSize(UKURAN_FOTO + 8, UKURAN_FOTO + 8);

        Image gambar = muatGambar(anggota.getFoto(), dirData);
        if (gambar != null) {
            ImageView view = new ImageView(gambar);
            double sisi = Math.min(gambar.getWidth(), gambar.getHeight());
            // potong tengah menjadi persegi agar wajah tidak gepeng
            view.setViewport(new Rectangle2D((gambar.getWidth() - sisi) / 2, (gambar.getHeight() - sisi) / 2, sisi, sisi));
            view.setFitWidth(UKURAN_FOTO);
            view.setFitHeight(UKURAN_FOTO);
            view.setSmooth(true);
            view.setClip(new Circle(UKURAN_FOTO / 2, UKURAN_FOTO / 2, UKURAN_FOTO / 2));
            bingkai.getChildren().add(view);
        } else {
            Label inisial = new Label(anggota.getInisial());
            inisial.getStyleClass().addAll("avatar", "avatar-anggota");
            inisial.setMinSize(UKURAN_FOTO, UKURAN_FOTO);
            inisial.setMaxSize(UKURAN_FOTO, UKURAN_FOTO);
            bingkai.getChildren().add(inisial);
        }
        return bingkai;
    }

    private static Image muatGambar(String foto, Path dirData) {
        if (foto.isEmpty()) {
            return null;
        }
        Path path = dirData.resolve(foto);
        if (!Files.isRegularFile(path)) {
            return null;
        }
        Image gambar = new Image(path.toUri().toString(), UKURAN_FOTO * 2, UKURAN_FOTO * 2, true, true);
        return gambar.isError() ? null : gambar;
    }

    private static Button tombolSosial(Sosial jenis, AnggotaTim anggota, Consumer<String> bukaTautan) {
        Button tombol = new Button();
        tombol.getStyleClass().addAll("tombol-ikon", "tombol-sosial");
        tombol.setGraphic(new IkonView(IKON_SOSIAL.get(jenis), 18));
        tombol.setAccessibleText(LABEL_SOSIAL.get(jenis));
        anggota.getTautan(jenis).ifPresentOrElse(url -> {
            tombol.setTooltip(new Tooltip(LABEL_SOSIAL.get(jenis)));
            tombol.setOnAction(e -> bukaTautan.accept(url));
        }, () -> tombol.setDisable(true));
        return tombol;
    }
}
