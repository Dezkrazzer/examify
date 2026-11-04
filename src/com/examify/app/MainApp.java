package com.examify.app;

import com.examify.exception.FormatDataException;
import com.examify.io.BankSoalReader;
import com.examify.io.BankSoalWriter;
import com.examify.io.DataReader;
import com.examify.io.GuruReader;
import com.examify.io.KursusReader;
import com.examify.io.KursusWriter;
import com.examify.io.ProgresBelajarReader;
import com.examify.io.ProgresBelajarWriter;
import com.examify.io.RekapNilaiReader;
import com.examify.io.RekapNilaiWriter;
import com.examify.io.SertifikatWriter;
import com.examify.io.SiswaReader;
import com.examify.model.Guru;
import com.examify.model.Siswa;
import com.examify.model.Ujian;
import com.examify.service.AuthService;
import com.examify.service.KursusService;
import com.examify.service.ProgresBelajarService;
import com.examify.service.RekapService;
import com.examify.service.SertifikatService;
import com.examify.service.UjianService;
import com.examify.utils.AppConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

/** Entry point JavaFX: memuat font dan data, menyiapkan service, lalu menampilkan landing page. */
public class MainApp extends Application {

    private static final String[] FONT = {"Regular", "Medium", "SemiBold", "Bold"};

    private Exception kesalahanStartup;

    @Override
    public void init() {
        System.err.println("[" + AppConfig.NAMA_APLIKASI + "] assertion aktif: "
                + MainApp.class.desiredAssertionStatus());
        try {
            AppConfig.siapkanFolderOutput();
            List<Siswa> daftarSiswa = muat(new SiswaReader(), AppConfig.FILE_SISWA);
            List<Guru> daftarGuru = muat(new GuruReader(), AppConfig.FILE_GURU);
            List<Ujian> daftarUjian = muat(new BankSoalReader(), AppConfig.FILE_BANK_SOAL);
            var rekapService = new RekapService(new RekapNilaiWriter(AppConfig.FILE_REKAP),
                    muat(new RekapNilaiReader(), AppConfig.FILE_REKAP));
            var bankSoalWriter = new BankSoalWriter();
            var ujianService = new UjianService(daftarUjian, rekapService, Clock.systemDefaultZone(),
                    bankSoalWriter, AppConfig.FILE_BANK_SOAL);
            var kursusWriter = new KursusWriter(AppConfig.DIR_DATA);
            var kursusService = new KursusService(
                    muat(new KursusReader(AppConfig.DIR_DATA), AppConfig.FILE_KURSUS), ujianService, kursusWriter, AppConfig.FILE_KURSUS);
            var progresService = new ProgresBelajarService(new ProgresBelajarWriter(AppConfig.FILE_PROGRES),
                    muat(new ProgresBelajarReader(), AppConfig.FILE_PROGRES), kursusService, rekapService);
            AppSession.getInstance().pasangService(
                    new AuthService(daftarSiswa, daftarGuru),
                    ujianService,
                    rekapService,
                    new SertifikatService(new SertifikatWriter(AppConfig.DIR_SERTIFIKAT)),
                    kursusService,
                    progresService);
        } catch (Exception e) {
            kesalahanStartup = e; // dialog hanya boleh dibuat di FX Application Thread (start)
        }
    }

    private static <T> List<T> muat(DataReader<T> reader, Path file) throws IOException, FormatDataException {
        List<T> data = reader.baca(file);
        System.err.println("[" + AppConfig.NAMA_APLIKASI + "] " + data.size() + " data dimuat dari " + file);
        return data;
    }

    private static void muatFont() {
        for (String ketebalan : FONT) {
            try (InputStream in = MainApp.class.getResourceAsStream("/com/examify/font/Poppins-" + ketebalan + ".ttf")) {
                if (in == null || Font.loadFont(in, 13) == null) {
                    System.err.println("Font Poppins " + ketebalan + " gagal dimuat, memakai font sistem");
                }
            } catch (IOException e) {
                System.err.println("Font Poppins " + ketebalan + " gagal dibaca: " + e.getMessage());
            }
        }
    }

    @Override
    public void start(Stage stage) {
        if (kesalahanStartup != null) {
            tampilkanErrorStartup();
            Platform.exit();
            return;
        }
        muatFont();
        AppSession.getInstance().pasangHostServices(getHostServices());
        SceneRouter.init(stage);
        SceneRouter.pindah(Layar.LANDING);
        stage.show();
    }

    @Override
    public void stop() {
        AppSession.getInstance().hentikanTimer();
    }

    private void tampilkanErrorStartup() {
        kesalahanStartup.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(AppConfig.NAMA_APLIKASI);
        alert.setHeaderText("Data aplikasi tidak dapat dimuat");
        alert.setContentText(kesalahanStartup.getClass().getSimpleName() + ": " + kesalahanStartup.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
