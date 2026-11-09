package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.exception.EmptyAnswerException;
import com.examify.exception.TimeOutException;
import com.examify.io.ViolationWriter;
import com.examify.security.SecureMode;
import com.examify.model.ExamResult;
import com.examify.model.SubmitMode;
import com.examify.model.Question;
import com.examify.model.ExamStatus;
import com.examify.model.Exam;
import com.examify.service.ExamService;
import com.examify.thread.TimerListener;
import com.examify.thread.ExamTimer;
import com.examify.utils.AppConfig;
import com.examify.utils.FormatUtil;
import com.examify.view.AnimationUtil;
import com.examify.view.QuestionViewFactory;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;

public class CbtRoomController implements TimerListener {

    private static final String NAV_TERJAWAB = "nav-terjawab";
    private static final String NAV_KOSONG = "nav-kosong";
    private static final String NAV_RAGU = "nav-ragu";
    private static final String NAV_AKTIF = "nav-aktif";
    private static final String TIMER_KRITIS = "timer-kritis";
    private static final String BANNER_INFO = "banner-info";
    private static final Duration DURASI_BANNER_INFO = Duration.seconds(4);

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
    @FXML private Label lblModeAman;
    @FXML private HBox hbPeringatanAman;
    @FXML private Label lblPeringatanAman;

    private final AppSession session = AppSession.getInstance();
    private final ExamService ujianService = session.getUjianService();
    private ExamResult hasil;
    private Exam ujian;
    private int indeksAktif;
    private Animation denyutTimer;
    private final PauseTransition tutupBanner = new PauseTransition(DURASI_BANNER_INFO);
    private final ViolationWriter pelanggaranWriter = new ViolationWriter(AppConfig.FILE_PELANGGARAN);

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
        tutupBanner.setOnFinished(e -> tampilkanBanner(null, false));
        if (ujian.isModeAman()) {
            Platform.runLater(this::mulaiModeAman); // setelah halaman ini terpasang di scene
        }
    }

    // ---------------------------------------------------------------- Mode Aman (STRICTLY_SAFE)

    private void mulaiModeAman() {
        if (hasil.getStatus() == ExamStatus.SELESAI) {
            return;
        }
        SecureMode modeAman = new SecureMode(SceneRouter.getStage(), this::onPelanggaran, this::onAksiDiblokir);
        session.setModeAmanAktif(modeAman);
        modeAman.aktifkan();
        lblModeAman.setVisible(true);
        lblModeAman.setManaged(true);
    }

    private void onPelanggaran(SecureMode.Pelanggaran p) {
        tutupBanner.stop(); // peringatan pelanggaran tetap tampil sampai ada pesan baru
        tampilkanBanner("Peringatan ke-" + p.ke() + ": Anda terdeteksi meninggalkan jendela ujian. "
                + "Kejadian ini dicatat dan dapat dilihat oleh guru.", false);
        var siswa = hasil.getSiswa();
        try {
            pelanggaranWriter.tulis(new ViolationWriter.Catatan(p.waktu(), siswa.getNis(), siswa.getNama(),
                    ujian.getKodeUjian(), p.ke(), p.jenis(), p.keterangan()));
        } catch (IOException e) {
            System.err.println("Pelanggaran gagal dicatat: " + e.getMessage());
        }
    }

    private void onAksiDiblokir(String pesan) {
        tampilkanBanner(pesan, true);
        tutupBanner.playFromStart();
    }

    /** @param pesan null untuk menyembunyikan banner */
    private void tampilkanBanner(String pesan, boolean info) {
        boolean tampil = pesan != null;
        hbPeringatanAman.setVisible(tampil);
        hbPeringatanAman.setManaged(tampil);
        hbPeringatanAman.getStyleClass().remove(BANNER_INFO);
        if (tampil) {
            if (info) {
                hbPeringatanAman.getStyleClass().add(BANNER_INFO);
            }
            lblPeringatanAman.setText(pesan);
        }
    }

    private void mulaiTimer() {
        ExamTimer timer = new ExamTimer(ujian.getDurasiDetik(), this);
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

    private Question soalAktif() {
        return ujian.getSoalBerdasarkanIndeks(indeksAktif);
    }

    private void tampilkanSoal(int indeks) {
        indeksAktif = indeks;
        Question soal = soalAktif();
        lblNomor.setText("Soal " + (indeks + 1) + " dari " + ujian.getJumlahSoal());
        lblJenis.setText(soal.getJenis().getLabel());
        lblPertanyaan.setText(soal.getPertanyaan());
        lblPetunjuk.setText(soal.getPetunjukFormat());
        lblPeringatan.setText("");
        Node input = QuestionViewFactory.buatInput(soal, hasil.getJawaban(soal.getIdSoal()), this::simpanJawabanAktif);
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
        } catch (EmptyAnswerException e) {
            hasil.hapusJawaban(idSoal);
            lblPeringatan.setText("Jawaban kosong, soal ditandai belum dijawab.");
        } catch (TimeOutException e) {
            selesaikanUjian(SubmitMode.AUTO_SUBMIT);
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
            selesaikanUjian(SubmitMode.MANUAL);
        }
    }

    @Override
    public void onTick(int sisaDetik) {
        lblTimer.setText("Sisa " + FormatUtil.mmss(sisaDetik));
        boolean kritis = sisaDetik <= AppConfig.BATAS_WAKTU_KRITIS_DETIK;
        if (kritis && denyutTimer == null) {
            lblTimer.getStyleClass().add(TIMER_KRITIS);
            denyutTimer = AnimationUtil.denyut(lblTimer); // sekali dipasang, berdenyut sampai ujian selesai
        }
    }

    @Override
    public void onWaktuHabis() {
        // jawaban sudah tersimpan setiap kali berubah, jadi cukup dikumpulkan
        selesaikanUjian(SubmitMode.AUTO_SUBMIT);
    }

    private void selesaikanUjian(SubmitMode mode) {
        if (hasil.getStatus() == ExamStatus.SELESAI) {
            return; // klik Selesai dan waktu habis bisa terjadi bersamaan
        }
        session.hentikanTimer();
        session.lepasModeAman(); // mode aman berakhir begitu ujian dikumpulkan / waktu habis
        tutupBanner.stop();
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
        if (hasil.getModeSubmit() == SubmitMode.AUTO_SUBMIT) {
            DialogUtil.info("Waktu habis. Jawaban Anda telah dikumpulkan otomatis.");
        }
        SceneRouter.pindah(AppScreen.RESULT);
    }

    private void kumpulkan(SubmitMode mode) throws IOException {
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
