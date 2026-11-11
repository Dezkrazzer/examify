package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.Teacher;
import com.examify.model.Student;
import com.examify.utils.FormatUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

/** Kerangka halaman setelah login: sidebar navigasi + area konten. */
public class ShellController {

    @FXML private StackPane spKonten;
    @FXML private ToggleButton tbKursus;
    @FXML private ToggleButton tbKalender;
    @FXML private ToggleButton tbBankSoal;
    @FXML private ToggleButton tbSiswa;
    @FXML private ToggleButton tbProfil;
    @FXML private Button btnTema;
    @FXML private Label lblAvatar;
    @FXML private Label lblNama;
    @FXML private Label lblNis;

    private final AppSession session = AppSession.getInstance();
    private final ChangeListener<AppScreen> listenerLayar = (obs, lama, layar) -> sorotMenu(layar);

    @FXML
    private void initialize() {
        SceneRouter.pasangKontenShell(spKonten);
        ThemeUtil.pasang(btnTema, true);

        ToggleGroup grup = new ToggleGroup();
        tbKursus.setToggleGroup(grup);
        tbKalender.setToggleGroup(grup);
        tbBankSoal.setToggleGroup(grup);
        tbSiswa.setToggleGroup(grup);
        tbProfil.setToggleGroup(grup);
        SceneRouter.layarAktifProperty().addListener(new WeakChangeListener<>(listenerLayar));

        if (session.isGuru()) {
            Teacher guru = session.getGuruAktif();
            lblAvatar.setText(FormatUtil.inisial(guru.getNama()));
            lblNama.setText(guru.getNama());
            lblNis.setText("Guru • NIP " + guru.getNip());
            tbKalender.setVisible(true);
            tbKalender.setManaged(true);
            tbBankSoal.setVisible(true);
            tbBankSoal.setManaged(true);
            tbSiswa.setVisible(true);
            tbSiswa.setManaged(true);
            tbProfil.setVisible(false);
            tbProfil.setManaged(false);
        } else if (session.isSiswa()) {
            Student siswa = session.getSiswaAktif();
            lblAvatar.setText(FormatUtil.inisial(siswa.getNama()));
            lblNama.setText(siswa.getNama());
            lblNis.setText(siswa.getNis() + " - " + siswa.getKelas());
            tbKalender.setVisible(true);
            tbKalender.setManaged(true);
            tbBankSoal.setVisible(false);
            tbBankSoal.setManaged(false);
            tbSiswa.setVisible(false);
            tbSiswa.setManaged(false);
            tbProfil.setVisible(true);
            tbProfil.setManaged(true);
        }
    }

    private void sorotMenu(AppScreen layar) {
        boolean dariKursus = session.getLayarAsalPembahasan() == AppScreen.KURSUS_DETAIL;
        tbKursus.setSelected(layar == AppScreen.KURSUS_SAYA || layar == AppScreen.KURSUS_DETAIL || (layar == AppScreen.PEMBAHASAN_UJIAN && dariKursus));
        tbKalender.setSelected(layar == AppScreen.KALENDER);
        tbBankSoal.setSelected(layar == AppScreen.BANK_SOAL || layar == AppScreen.EDITOR_UJIAN);
        tbSiswa.setSelected(layar == AppScreen.GURU_SISWA || layar == AppScreen.GURU_DETAIL_SISWA);
        tbProfil.setSelected(layar == AppScreen.PROFIL || (layar == AppScreen.PEMBAHASAN_UJIAN && !dariKursus));
    }

    @FXML
    private void handleKursus() {
        SceneRouter.pindah(AppScreen.KURSUS_SAYA);
    }

    @FXML
    private void handleKalender() {
        SceneRouter.pindah(AppScreen.KALENDER);
    }

    @FXML
    private void handleBankSoal() {
        SceneRouter.pindah(AppScreen.BANK_SOAL);
    }

    @FXML
    private void handleSiswa() {
        SceneRouter.pindah(AppScreen.GURU_SISWA);
    }

    @FXML
    private void handleProfil() {
        SceneRouter.pindah(AppScreen.PROFIL);
    }

    @FXML
    private void handleKeluar() {
        String nama = session.getNamaPengguna();
        if (DialogUtil.konfirmasi("Keluar dari akun " + nama + "?")) {
            session.logout();
            SceneRouter.pindah(AppScreen.LANDING);
        }
    }
}
