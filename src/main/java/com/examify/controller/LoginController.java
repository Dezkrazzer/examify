package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.AppScreen;
import com.examify.app.SceneRouter;
import com.examify.model.Teacher;
import com.examify.model.Student;
import com.examify.utils.StringUtil;
import com.examify.view.AnimationUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class LoginController {

    private static final String POLA_IDENTITAS = "[a-zA-Z0-9]{0,20}";

    @FXML private VBox vbKartu;
    @FXML private TextField tfNis;
    @FXML private PasswordField pfPin;
    @FXML private Label lblError;
    @FXML private Button btnTema;

    private final AppSession session = AppSession.getInstance();

    @FXML
    private void initialize() {
        ThemeUtil.pasang(btnTema, false);
        tfNis.setTextFormatter(new TextFormatter<String>(ubah ->
                ubah.getControlNewText().matches(POLA_IDENTITAS) ? ubah : null));
        lblError.setText("");
        Platform.runLater(tfNis::requestFocus);
    }

    @FXML
    private void handleMasuk() {
        String identitas = tfNis.getText();
        String pin = pfPin.getText();
        if (StringUtil.isKosong(identitas) || StringUtil.isKosong(pin)) {
            tampilkanError("Nomor Identitas dan PIN wajib diisi.");
            return;
        }

        var authService = session.getAuthService();
        Optional<Teacher> optGuru = authService.loginGuru(identitas, pin);
        if (optGuru.isPresent()) {
            session.setGuruAktif(optGuru.get());
            SceneRouter.pindah(AppScreen.KURSUS_SAYA);
            return;
        }

        Optional<Student> optSiswa = authService.loginSiswa(identitas, pin);
        if (optSiswa.isPresent()) {
            session.setSiswaAktif(optSiswa.get());
            SceneRouter.pindah(AppScreen.KURSUS_SAYA);
            return;
        }

        tampilkanError("Nomor Identitas atau PIN salah.");
    }

    private void tampilkanError(String pesan) {
        lblError.setText(pesan);
        pfPin.clear();
        pfPin.requestFocus();
        AnimationUtil.getar(vbKartu);
    }

    @FXML
    private void handleKembali() {
        SceneRouter.pindah(AppScreen.LANDING);
    }
}
