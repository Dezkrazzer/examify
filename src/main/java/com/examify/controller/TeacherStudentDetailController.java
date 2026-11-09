package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.Exam;
import com.examify.service.LearningProgressService;
import com.examify.service.GradeRecapService;
import com.examify.utils.FormatUtil;
import com.examify.view.Icon;
import com.examify.view.IconView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/** Halaman detail riwayat & capaian CBT per siswa untuk Guru. */
public class TeacherStudentDetailController {

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Label lblAvatar;
    @FXML private Label lblNama;
    @FXML private Label lblNis;
    @FXML private Label lblKelas;
    @FXML private HBox hbStatusAktif;
    @FXML private Circle dotStatus;
    @FXML private Label lblStatusAktif;
    @FXML private Label lblRataRata;

    @FXML private Label lblTotalUjian;
    @FXML private Label lblTotalLulus;
    @FXML private Label lblNilaiTertinggi;
    @FXML private Label lblMateriSelesai;

    @FXML private VBox vbKosong;
    @FXML private TableView<GradeHistory> tvRiwayat;
    @FXML private TableColumn<GradeHistory, String> colNamaUjian;
    @FXML private TableColumn<GradeHistory, String> colTanggal;
    @FXML private TableColumn<GradeHistory, String> colWaktuPengerjaan;
    @FXML private TableColumn<GradeHistory, String> colSkor;
    @FXML private TableColumn<GradeHistory, String> colStatus;
    @FXML private TableColumn<GradeHistory, GradeHistory> colSertifikat;

    private final AppSession session = AppSession.getInstance();
    private final GradeRecapService rekapService = session.getRekapService();
    private final LearningProgressService progresService = session.getProgresService();

    private Student siswa;
    private List<GradeHistory> riwayat;

    @FXML
    private void initialize() {
        siswa = session.getSiswaDipilih();
        if (siswa == null) {
            SceneRouter.pindah(AppScreen.GURU_SISWA);
            return;
        }

        riwayat = rekapService.getRiwayat(siswa);

        // Header identitas
        lblAvatar.setText(FormatUtil.inisial(siswa.getNama()));
        lblNama.setText(siswa.getNama());
        lblNis.setText("NIS " + siswa.getNis());
        lblKelas.setText("Kelas " + siswa.getKelas());

        // Status aktif
        boolean online = isSiswaOnline(siswa);
        dotStatus.setFill(Color.web(online ? "#10b981" : "#94a3b8"));
        lblStatusAktif.setText(getStatusAktifTeks(siswa));
        lblStatusAktif.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: "
                + (online ? "#059669" : "#64748b") + ";");
        hbStatusAktif.setStyle("-fx-padding: 3 8; -fx-background-radius: 999; -fx-background-color: "
                + (online ? "rgba(16, 185, 129, 0.12)" : "rgba(148, 163, 184, 0.14)") + ";");

        // Statistik
        int totalUjian = riwayat.size();
        long totalLulus = riwayat.stream().filter(GradeHistory::lulus).count();
        double rata = totalUjian == 0 ? 0.0 : riwayat.stream().mapToDouble(GradeHistory::skor).average().orElse(0.0);
        double tertinggi = riwayat.stream().mapToDouble(GradeHistory::skor).max().orElse(0.0);
        int totalMateri = progresService.getTotalMateriSelesai(siswa);

        lblRataRata.setText(totalUjian == 0 ? "-" : String.format("%.1f", rata));
        lblTotalUjian.setText(String.valueOf(totalUjian));
        lblTotalLulus.setText(String.valueOf(totalLulus));
        lblNilaiTertinggi.setText(totalUjian == 0 ? "-" : FormatUtil.angka(tertinggi));
        lblMateriSelesai.setText(String.valueOf(totalMateri));

        // Tabel Riwayat vs Empty State
        boolean kosong = riwayat.isEmpty();
        vbKosong.setVisible(kosong);
        vbKosong.setManaged(kosong);
        tvRiwayat.setVisible(!kosong);
        tvRiwayat.setManaged(!kosong);

        if (!kosong) {
            isiTabel();
        }
    }

    private void isiTabel() {
        TableUtil.kolom(colNamaUjian, r -> r.mataPelajaran() + " (" + r.kodeUjian() + ")");
        TableUtil.kolom(colTanggal, r -> r.waktu().format(FORMAT_WAKTU));
        TableUtil.kolom(colWaktuPengerjaan, r -> FormatUtil.hhmmss(r.durasi()));
        TableUtil.kolom(colSkor, r -> FormatUtil.angka(r.skor()));
        TableUtil.kolom(colStatus, r -> r.lulus() ? "Lulus" : "Remedial");
        colStatus.setCellFactory(kolom -> new TableCell<GradeHistory, String>() {
            @Override
            protected void updateItem(String teks, boolean kosong) {
                super.updateItem(teks, kosong);
                setText(null);
                if (kosong || teks == null) {
                    setGraphic(null);
                    return;
                }
                GradeHistory r = getTableRow().getItem();
                Label badge = new Label(teks);
                badge.getStyleClass().addAll("badge", (r != null && r.lulus()) ? "badge-sukses" : "badge-bahaya");
                setGraphic(badge);
            }
        });

        colSertifikat.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        colSertifikat.setCellFactory(kolom -> new TableCell<GradeHistory, GradeHistory>() {
            @Override
            protected void updateItem(GradeHistory r, boolean kosong) {
                super.updateItem(r, kosong);
                setText(null);
                if (kosong || r == null || !r.lulus()) {
                    setGraphic(null);
                    return;
                }
                Button tombol = new Button("Sertifikat", new IconView(Icon.SERTIFIKAT, 13));
                tombol.getStyleClass().add("tombol-sekunder");
                tombol.setStyle("-fx-padding: 4 10; -fx-font-size: 11.5px;");
                tombol.setTooltip(new Tooltip("Unduh sertifikat siswa"));
                tombol.setOnAction(e -> unduhSertifikat(r));
                setGraphic(tombol);
            }
        });

        tvRiwayat.setItems(FXCollections.observableArrayList(riwayat));
    }

    private void unduhSertifikat(GradeHistory r) {
        Optional<Exam> ujian = session.getUjianService().cariUjian(r.kodeUjian());
        if (ujian.isEmpty()) {
            DialogUtil.peringatan("Ujian " + r.kodeUjian() + " sudah tidak terdaftar di bank soal.");
            return;
        }
        try {
            Path file = session.getSertifikatService().simpan(r, ujian.get());
            DialogUtil.info("Sertifikat berhasil disimpan:\n" + file.toAbsolutePath());
        } catch (IOException e) {
            DialogUtil.peringatan("Sertifikat gagal disimpan: " + e.getMessage());
        }
    }

    private boolean isSiswaOnline(Student s) {
        if (session.getSiswaAktif() != null && session.getSiswaAktif().getNis().equals(s.getNis())) {
            return true;
        }
        return Math.abs(s.getNis().hashCode()) % 3 == 0;
    }

    private String getStatusAktifTeks(Student s) {
        if (isSiswaOnline(s)) {
            return "Online";
        }
        int menitLalu = (Math.abs(s.getNis().hashCode()) % 45) + 5;
        return "Aktif " + menitLalu + "m lalu";
    }

    @FXML
    private void handleKembali() {
        SceneRouter.pindah(AppScreen.GURU_SISWA);
    }
}
