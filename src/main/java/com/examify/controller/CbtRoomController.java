package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.exception.JawabanKosongException;
import com.examify.exception.TimeOutException;
import com.examify.model.HasilUjian;
import com.examify.model.ModeSubmit;
import com.examify.model.Soal;
import com.examify.model.StatusUjian;
import com.examify.model.Ujian;
import com.examify.service.UjianService;
import com.examify.thread.TimerListener;
import com.examify.thread.UjianTimer;
import com.examify.utils.AppConfig;
import com.examify.utils.FormatUtil;
import com.examify.view.AnimasiUtil;
import com.examify.view.SoalViewFactory;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class CbtRoomController implements TimerListener {

    private static final String NAV_TERJAWAB = "nav-terjawab";
    private static final String NAV_KOSONG = "nav-kosong";
    private static final String NAV_RAGU = "nav-ragu";
    private static final String NAV_AKTIF = "nav-aktif";
    private static final String TIMER_KRITIS = "timer-kritis";

    @FXML private Label lblJudulUjian;
    @FXML private Label lblSiswa;
    @FXML private Label lblTimer;
    @FXML private Label lblNomor;
    @FXML private Label lblJenis;
    @FXML private Label lblPertanyaan;
    @FXML private Label lblPetunjuk;
    @FXML private Label lblPeringatan;
    @FXML private Label lblProgres;
    @FXML private VBox vbInput;
    @FXML private FlowPane fpNavigasi;
    @FXML private ProgressBar pbProgres;
    @FXML private Button btnSebelumnya;
    @FXML private Button btnBerikutnya;
    @FXML private Button btnSelesai;
    @FXML private ToggleButton tbRagu;

    private final AppSession session = AppSession.getInstance();
    private final UjianService ujianService = session.getUjianService();
    private HasilUjian hasil;
    private Ujian ujian;
    private int indeksAktif;
    private Animation denyutTimer;

    @FXML
    private void initialize() {
        hasil = session.getHasilAktif();
        ujian = hasil.getUjian();
        lblJudulUjian.setText(ujian.getMataPelajaran() + " - " + ujian.getKodeUjian());
        lblSiswa.setText(hasil.getSiswa().getNama() + " (" + hasil.getSiswa().getNis() + ")");
        buatGridNavigasi();
        tampilkanSoal(0);
        onTick(ujian.getDurasiDetik());
        mulaiTimer();
        SceneRouter.getStage().setOnCloseRequest(this::handleTutupJendela);
    }

    private void mulaiTimer() {
        UjianTimer timer = new UjianTimer(ujian.getDurasiDetik(), this);
        session.setTimerAktif(timer);
        timer.mulai();
    }

    private void buatGridNavigasi() {
        for (int i = 0; i < ujian.getJumlahSoal(); i++) {
            int indeks = i;
            Button tombol = new Button(String.valueOf(i + 1));
            tombol.getStyleClass().add("nav-soal");
            tombol.setOnAction(e -> tampilkanSoal(indeks));
            fpNavigasi.getChildren().add(tombol);
        }
    }

    private Soal soalAktif() {
        return ujian.getSoalBerdasarkanIndeks(indeksAktif);
    }

    private void tampilkanSoal(int indeks) {
        indeksAktif = indeks;
        Soal soal = soalAktif();
        lblNomor.setText("Soal " + (indeks + 1) + " dari " + ujian.getJumlahSoal());
        lblJenis.setText(soal.getJenis().getLabel());
        lblPertanyaan.setText(soal.getPertanyaan());
        lblPetunjuk.setText(soal.getPetunjukFormat());
        lblPeringatan.setText("");
        Node input = SoalViewFactory.buatInput(soal, hasil.getJawaban(soal.getIdSoal()), this::simpanJawabanAktif);
        vbInput.getChildren().setAll(input);
        input.requestFocus(); // siswa bisa langsung mengetik jawaban isian
        btnSebelumnya.setDisable(indeks == 0);
        boolean nomorTerakhir = (indeks == ujian.getJumlahSoal() - 1);
        btnBerikutnya.setDisable(nomorTerakhir);
        btnSelesai.setVisible(nomorTerakhir);
        btnSelesai.setManaged(nomorTerakhir);
        tbRagu.setSelected(hasil.isDitandai(soal.getIdSoal()));
        perbaruiNavigasi();
    }

    /** Dipanggil setiap jawaban berubah, sehingga tidak ada tombol simpan yang wajib ditekan. */
    private void simpanJawabanAktif(String jawaban) {
        int idSoal = soalAktif().getIdSoal();
        try {
            hasil.simpanJawaban(idSoal, jawaban);
            lblPeringatan.setText("");
        } catch (JawabanKosongException e) {
            hasil.hapusJawaban(idSoal);
            lblPeringatan.setText("Jawaban kosong, soal ditandai belum dijawab.");
        } catch (TimeOutException e) {
            selesaikanUjian(ModeSubmit.AUTO_SUBMIT);
            return;
        }
        perbaruiNavigasi();
    }

    private void perbaruiNavigasi() {
        for (int i = 0; i < fpNavigasi.getChildren().size(); i++) {
            Node tombol = fpNavigasi.getChildren().get(i);
            int idSoal = ujian.getSoalBerdasarkanIndeks(i).getIdSoal();
            boolean ditandai = hasil.isDitandai(idSoal);
            boolean terjawab = hasil.isTerjawab(idSoal);
            tombol.getStyleClass().removeAll(NAV_TERJAWAB, NAV_KOSONG, NAV_RAGU, NAV_AKTIF);
            if (ditandai) {
                tombol.getStyleClass().add(NAV_RAGU);
            } else if (terjawab) {
                tombol.getStyleClass().add(NAV_TERJAWAB);
            } else {
                tombol.getStyleClass().add(NAV_KOSONG);
            }
            if (i == indeksAktif) {
                tombol.getStyleClass().add(NAV_AKTIF);
            }
        }
        float proporsi = hasil.getProporsiTerjawab();
        pbProgres.setProgress(proporsi);
        int ragu = hasil.getJumlahDitandai();
        String infoRagu = ragu > 0 ? " • " + ragu + " ragu" : "";
        lblProgres.setText(String.format("Progres %d/%d (%.0f%%)%s",
                hasil.getJumlahTerjawab(), ujian.getJumlahSoal(), proporsi * 100, infoRagu));
    }

    @FXML
    private void handleSebelumnya() {
        tampilkanSoal(indeksAktif - 1);
    }

    @FXML
    private void handleBerikutnya() {
        tampilkanSoal(indeksAktif + 1);
    }

    @FXML
    private void handleTandai() {
        int idSoal = soalAktif().getIdSoal();
        hasil.setDitandai(idSoal, tbRagu.isSelected());
        perbaruiNavigasi();
    }

    @FXML
    private void handleKosongkan() {
        hasil.hapusJawaban(soalAktif().getIdSoal());
        tampilkanSoal(indeksAktif);
    }

    @FXML
    private void handleSelesai() {
        int kosong = ujian.getJumlahSoal() - hasil.getJumlahTerjawab();
        int ragu = hasil.getJumlahDitandai();
        StringBuilder sb = new StringBuilder();
        if (kosong > 0 && ragu > 0) {
            sb.append("Masih ada ").append(kosong).append(" soal belum dijawab dan ")
                    .append(ragu).append(" soal ditandai ragu-ragu. ");
        } else if (kosong > 0) {
            sb.append("Masih ada ").append(kosong).append(" soal yang belum dijawab. ");
        } else if (ragu > 0) {
            sb.append("Semua soal sudah dijawab, namun masih ada ")
                    .append(ragu).append(" soal ditandai ragu-ragu. ");
        } else {
            sb.append("Semua soal sudah dijawab. ");
        }
        sb.append("Kumpulkan jawaban sekarang?");



        if (DialogUtil.konfirmasi(sb.toString())) {
            selesaikanUjian(ModeSubmit.MANUAL);
        }
    }

    @Override
    public void onTick(int sisaDetik) {
        lblTimer.setText("Sisa " + FormatUtil.mmss(sisaDetik));
        boolean kritis = sisaDetik <= AppConfig.BATAS_WAKTU_KRITIS_DETIK;
        if (kritis && denyutTimer == null) {
            lblTimer.getStyleClass().add(TIMER_KRITIS);
            denyutTimer = AnimasiUtil.denyut(lblTimer); // sekali dipasang, berdenyut sampai ujian selesai
        }
    }

    @Override
    public void onWaktuHabis() {
        // jawaban sudah tersimpan setiap kali berubah, jadi cukup dikumpulkan
        selesaikanUjian(ModeSubmit.AUTO_SUBMIT);
    }

    private void selesaikanUjian(ModeSubmit mode) {
        if (hasil.getStatus() == StatusUjian.SELESAI) {
            return; // klik Selesai dan waktu habis bisa terjadi bersamaan
        }
        session.hentikanTimer();
        if (denyutTimer != null) {
            denyutTimer.stop();
        }
        try {
            kumpulkan(mode);
            if (session.getKursusAktif() != null && session.getMateriFokus() != null && session.getSiswaAktif() != null) {
                try {
                    session.getProgresService().tandaiSelesai(session.getSiswaAktif(), session.getKursusAktif(), session.getMateriFokus());
                } catch (Exception ex) {
                    System.err.println("Gagal mencatat progres materi/kuis: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            DialogUtil.peringatan("Nilai tersimpan di sesi ini, tetapi rekap gagal ditulis ke file: " + e.getMessage());
        }
        if (hasil.getModeSubmit() == ModeSubmit.AUTO_SUBMIT) {
            DialogUtil.info("Waktu habis. Jawaban Anda telah dikumpulkan otomatis.");
        }
        SceneRouter.pindah(Layar.RESULT);
    }

    private void kumpulkan(ModeSubmit mode) throws IOException {
        try {
            ujianService.selesaikan(hasil, mode);
        } catch (TimeOutException e) {
            ujianService.selesaikanPaksa(hasil); // submit manual yang terlambat dicatat AUTO_SUBMIT
        }
    }

    private void handleTutupJendela(WindowEvent event) {
        event.consume();
        boolean keluar = DialogUtil.konfirmasi(
                "Ujian sedang berlangsung. Keluar sekarang akan mengumpulkan jawaban secara otomatis. Lanjutkan?");
        if (!keluar) {
            return;
        }
        session.hentikanTimer();
        try {
            ujianService.selesaikanPaksa(hasil);
            if (session.getKursusAktif() != null && session.getMateriFokus() != null && session.getSiswaAktif() != null) {
                try {
                    session.getProgresService().tandaiSelesai(session.getSiswaAktif(), session.getKursusAktif(), session.getMateriFokus());
                } catch (Exception ex) {
                    System.err.println("Gagal mencatat progres materi/kuis: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Rekap gagal ditulis saat keluar: " + e.getMessage());
        } finally {
            Platform.exit(); // aplikasi tetap ditutup walaupun rekap gagal ditulis
        }
    }
}
