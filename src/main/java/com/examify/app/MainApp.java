package com.examify.app;

import com.examify.exception.DataFormatException;
import com.examify.io.QuestionBankReader;
import com.examify.io.QuestionBankWriter;
import com.examify.io.DataReader;
import com.examify.io.TeacherReader;
import com.examify.io.CourseReader;
import com.examify.io.CourseWriter;
import com.examify.io.LearningProgressReader;
import com.examify.io.LearningProgressWriter;
import com.examify.io.GradeRecapReader;
import com.examify.io.GradeRecapWriter;
import com.examify.io.CertificateWriter;
import com.examify.io.StudentReader;
import com.examify.model.Teacher;
import com.examify.model.Student;
import com.examify.model.Exam;
import com.examify.service.AuthService;
import com.examify.service.CourseService;
import com.examify.service.CourseEnrollmentService;
import com.examify.service.LearningProgressService;
import com.examify.service.AssignmentService;
import com.examify.service.GradeRecapService;
import com.examify.service.CertificateService;
import com.examify.service.ExamService;
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
            List<Student> daftarSiswa = muat(new StudentReader(), AppConfig.FILE_SISWA);
            List<Teacher> daftarGuru = muat(new TeacherReader(), AppConfig.FILE_GURU);
            List<Exam> daftarUjian = muat(new QuestionBankReader(), AppConfig.FILE_BANK_SOAL);
            var rekapService = new GradeRecapService(new GradeRecapWriter(AppConfig.FILE_REKAP),
                    muat(new GradeRecapReader(), AppConfig.FILE_REKAP));
            var bankSoalWriter = new QuestionBankWriter();
            var ujianService = new ExamService(daftarUjian, rekapService, Clock.systemDefaultZone(),
                    bankSoalWriter, AppConfig.FILE_BANK_SOAL);
            var kursusWriter = new CourseWriter(AppConfig.DIR_DATA);
            var kursusService = new CourseService(
                    muat(new CourseReader(AppConfig.DIR_DATA), AppConfig.FILE_KURSUS), ujianService, kursusWriter, AppConfig.FILE_KURSUS);
            var progresService = new LearningProgressService(new LearningProgressWriter(AppConfig.FILE_PROGRES),
                    muat(new LearningProgressReader(), AppConfig.FILE_PROGRES), kursusService, rekapService);
            var pendaftaranService = new CourseEnrollmentService(AppConfig.FILE_PENDAFTARAN, progresService);
            var tugasService = new AssignmentService(AppConfig.FILE_PENGUMPULAN_TUGAS, AppConfig.DIR_PENGUMPULAN_TUGAS);
            AppSession.getInstance().pasangService(
                    new AuthService(daftarSiswa, daftarGuru),
                    ujianService,
                    rekapService,
                    new CertificateService(new CertificateWriter(AppConfig.DIR_SERTIFIKAT)),
                    kursusService,
                    progresService,
                    pendaftaranService,
                    tugasService);
        } catch (Exception e) {
            kesalahanStartup = e; // dialog hanya boleh dibuat di FX Application Thread (start)
        }
    }

    private static <T> List<T> muat(DataReader<T> reader, Path file) throws IOException, DataFormatException {
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
        SceneRouter.pindah(AppScreen.LANDING);
        stage.show();
    }

    @Override
    public void stop() {
        AppSession.getInstance().hentikanTimer();
        AppSession.getInstance().lepasModeAman();
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
