package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.utils.StringUtil;
import com.examify.view.AnimasiUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

public class LoginController {

    private static final String POLA_DIGIT = "\\d{0,10}";

    @FXML private VBox vbKartu;
    @FXML private TextField tfNis;
    @FXML private PasswordField pfPin;
    @FXML private Label lblError;
    @FXML private Button btnTema;

    private final AppSession session = AppSession.getInstance();

    @FXML
    private void initialize() {
        TemaUtil.pasang(btnTema, false);
        tfNis.setTextFormatter(new TextFormatter<String>(ubah ->
                ubah.getControlNewText().matches(POLA_DIGIT) ? ubah : null));
        lblError.setText("");
        Platform.runLater(tfNis::requestFocus); // fokus awal ke NIS, bukan tombol tema
    }

    @FXML
    private void handleMasuk() {
        String nis = tfNis.getText();
        String pin = pfPin.getText();
        if (StringUtil.isKosong(nis) || StringUtil.isKosong(pin)) {
            tampilkanError("NIS dan PIN wajib diisi.");
            return;
        }
        session.getAuthService().login(nis, pin).ifPresentOrElse(siswa -> {
            session.setSiswaAktif(siswa);
            SceneRouter.pindah(Layar.KURSUS_SAYA);
        }, () -> tampilkanError("NIS atau PIN salah."));
    }

    private void tampilkanError(String pesan) {
        lblError.setText(pesan);
        pfPin.clear();
        pfPin.requestFocus();
        AnimasiUtil.getar(vbKartu);
    }

    @FXML
    private void handleKembali() {
        SceneRouter.pindah(Layar.LANDING);
    }
}
