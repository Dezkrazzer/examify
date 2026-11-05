package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.Guru;
import com.examify.model.Siswa;
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
    @FXML private ToggleButton tbBankSoal;
    @FXML private ToggleButton tbSiswa;
    @FXML private ToggleButton tbProfil;
    @FXML private Button btnTema;
    @FXML private Label lblAvatar;
    @FXML private Label lblNama;
    @FXML private Label lblNis;

    private final AppSession session = AppSession.getInstance();
    private final ChangeListener<Layar> listenerLayar = (obs, lama, layar) -> sorotMenu(layar);

    @FXML
    private void initialize() {
        SceneRouter.pasangKontenShell(spKonten);
        TemaUtil.pasang(btnTema, true);

        ToggleGroup grup = new ToggleGroup();
        tbKursus.setToggleGroup(grup);
        tbBankSoal.setToggleGroup(grup);
        tbSiswa.setToggleGroup(grup);
        tbProfil.setToggleGroup(grup);
        SceneRouter.layarAktifProperty().addListener(new WeakChangeListener<>(listenerLayar));

        if (session.isGuru()) {
            Guru guru = session.getGuruAktif();
            lblAvatar.setText(FormatUtil.inisial(guru.getNama()));
            lblNama.setText(guru.getNama());
            lblNis.setText("Guru • NIP " + guru.getNip());
            tbBankSoal.setVisible(true);
            tbBankSoal.setManaged(true);
            tbSiswa.setVisible(true);
            tbSiswa.setManaged(true);
            tbProfil.setVisible(false);
            tbProfil.setManaged(false);
        } else if (session.isSiswa()) {
            Siswa siswa = session.getSiswaAktif();
            lblAvatar.setText(FormatUtil.inisial(siswa.getNama()));
            lblNama.setText(siswa.getNama());
            lblNis.setText(siswa.getNis() + " - " + siswa.getKelas());
            tbBankSoal.setVisible(false);
            tbBankSoal.setManaged(false);
            tbSiswa.setVisible(false);
            tbSiswa.setManaged(false);
            tbProfil.setVisible(true);
            tbProfil.setManaged(true);
        }
    }

    private void sorotMenu(Layar layar) {
        tbKursus.setSelected(layar == Layar.KURSUS_SAYA || layar == Layar.KURSUS_DETAIL);
        tbBankSoal.setSelected(layar == Layar.BANK_SOAL || layar == Layar.EDITOR_UJIAN);
        tbSiswa.setSelected(layar == Layar.GURU_SISWA);
        tbProfil.setSelected(layar == Layar.PROFIL);
    }

    @FXML
    private void handleKursus() {
        SceneRouter.pindah(Layar.KURSUS_SAYA);
    }

    @FXML
    private void handleBankSoal() {
        SceneRouter.pindah(Layar.BANK_SOAL);
    }

    @FXML
    private void handleSiswa() {
        SceneRouter.pindah(Layar.GURU_SISWA);
    }

    @FXML
    private void handleProfil() {
        SceneRouter.pindah(Layar.PROFIL);
    }

    @FXML
    private void handleKeluar() {
        String nama = session.getNamaPengguna();
        if (DialogUtil.konfirmasi("Keluar dari akun " + nama + "?")) {
            session.logout();
            SceneRouter.pindah(Layar.LANDING);
        }
    }
}
