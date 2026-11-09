package com.examify.app;

import com.examify.security.SecureMode;
import com.examify.model.Chapter;
import com.examify.model.Teacher;
import com.examify.model.ExamResult;
import com.examify.model.Course;
import com.examify.model.Material;
import com.examify.model.TextMaterial;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.Exam;
import com.examify.service.AuthService;
import com.examify.service.CourseService;
import com.examify.service.CourseEnrollmentService;
import com.examify.service.LearningProgressService;
import com.examify.service.AssignmentService;
import com.examify.utils.AppConfig;
import com.examify.service.GradeRecapService;
import com.examify.service.CertificateService;
import com.examify.service.ExamService;
import com.examify.thread.ExamTimer;
import javafx.application.HostServices;

/** State sesi aplikasi (singleton): service, siswa/guru yang login, serta kursus/ujian yang sedang aktif. */
public final class AppSession {

    private static final AppSession INSTANCE = new AppSession();

    private AuthService authService;
    private ExamService ujianService;
    private GradeRecapService rekapService;
    private CertificateService sertifikatService;
    private CourseService kursusService;
    private LearningProgressService progresService;
    private CourseEnrollmentService pendaftaranService;
    private AssignmentService tugasService;
    private HostServices hostServices;

    private Student siswaAktif;
    private Teacher guruAktif;
    private Course kursusAktif;
    private Chapter babAktifMateri;
    private TextMaterial materiTeksAktif;
    private Material materiFokus; // dibuka kembali saat kembali ke halaman kursus
    private Exam ujianAktif;
    private ExamResult hasilAktif;
    private ExamTimer timerAktif;
    private SecureMode modeAmanAktif;
    private Student siswaDipilih;
    private GradeHistory riwayatFokus;

    private AppSession() {
    }

    public static AppSession getInstance() {
        return INSTANCE;
    }

    void pasangService(AuthService auth, ExamService ujian, GradeRecapService rekap, CertificateService sertifikat,
                       CourseService kursus, LearningProgressService progres) {
        pasangService(auth, ujian, rekap, sertifikat, kursus, progres, null, null);
    }

    void pasangService(AuthService auth, ExamService ujian, GradeRecapService rekap, CertificateService sertifikat,
                       CourseService kursus, LearningProgressService progres,
                       CourseEnrollmentService pendaftaran, AssignmentService tugas) {
        this.authService = auth;
        this.ujianService = ujian;
        this.rekapService = rekap;
        this.sertifikatService = sertifikat;
        this.kursusService = kursus;
        this.progresService = progres;
        this.pendaftaranService = pendaftaran;
        this.tugasService = tugas;
    }

    void pasangHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    /** Membuka URL atau file lokal dengan aplikasi bawaan sistem operasi. */
    public void bukaDokumen(String uri) {
        hostServices.showDocument(uri);
    }

    public void resetUjian() {
        hentikanTimer();
        lepasModeAman();
        ujianAktif = null;
        hasilAktif = null;
    }

    public void logout() {
        resetUjian();
        kursusAktif = null;
        babAktifMateri = null;
        materiTeksAktif = null;
        materiFokus = null;
        siswaAktif = null;
        guruAktif = null;
        siswaDipilih = null;
        riwayatFokus = null;
    }

    public void hentikanTimer() {
        if (timerAktif != null) {
            timerAktif.stop();
            timerAktif = null;
        }
    }

    public boolean isLogin() {
        return siswaAktif != null || guruAktif != null;
    }

    public boolean isGuru() {
        return guruAktif != null;
    }

    public boolean isSiswa() {
        return siswaAktif != null;
    }

    public String getNamaPengguna() {
        if (guruAktif != null) return guruAktif.getNama();
        if (siswaAktif != null) return siswaAktif.getNama();
        return "";
    }

    public String getIdentitasPengguna() {
        if (guruAktif != null) return "Guru • NIP " + guruAktif.getNip();
        if (siswaAktif != null) return siswaAktif.getNis() + " - " + siswaAktif.getKelas();
        return "";
    }

    public AuthService getAuthService() {
        return authService;
    }

    public ExamService getUjianService() {
        return ujianService;
    }

    public GradeRecapService getRekapService() {
        return rekapService;
    }

    public CertificateService getSertifikatService() {
        return sertifikatService;
    }

    public CourseService getKursusService() {
        return kursusService;
    }

    public LearningProgressService getProgresService() {
        return progresService;
    }

    public Student getSiswaAktif() {
        return siswaAktif;
    }

    public void setSiswaAktif(Student siswaAktif) {
        this.siswaAktif = siswaAktif;
        if (siswaAktif != null) {
            this.guruAktif = null;
        }
    }

    public Teacher getGuruAktif() {
        return guruAktif;
    }

    public void setGuruAktif(Teacher guruAktif) {
        this.guruAktif = guruAktif;
        if (guruAktif != null) {
            this.siswaAktif = null;
        }
    }

    public Course getKursusAktif() {
        return kursusAktif;
    }

    public void setKursusAktif(Course kursusAktif) {
        this.kursusAktif = kursusAktif;
    }

    public Chapter getBabAktifMateri() {
        return babAktifMateri;
    }

    public void setBabAktifMateri(Chapter babAktifMateri) {
        this.babAktifMateri = babAktifMateri;
    }

    public TextMaterial getMateriTeksAktif() {
        return materiTeksAktif;
    }

    public void setMateriTeksAktif(TextMaterial materiTeksAktif) {
        this.materiTeksAktif = materiTeksAktif;
    }

    public Material getMateriFokus() {
        return materiFokus;
    }

    public void setMateriFokus(Material materiFokus) {
        this.materiFokus = materiFokus;
    }

    public Exam getUjianAktif() {
        return ujianAktif;
    }

    public void setUjianAktif(Exam ujianAktif) {
        this.ujianAktif = ujianAktif;
    }

    public ExamResult getHasilAktif() {
        return hasilAktif;
    }

    public void setHasilAktif(ExamResult hasilAktif) {
        this.hasilAktif = hasilAktif;
    }

    public void setTimerAktif(ExamTimer timerAktif) {
        this.timerAktif = timerAktif;
    }

    public void setModeAmanAktif(SecureMode modeAman) {
        this.modeAmanAktif = modeAman;
    }

    /** Mengakhiri Mode Aman (layar penuh, hook keyboard) bila sedang berjalan. */
    public void lepasModeAman() {
        if (modeAmanAktif != null) {
            modeAmanAktif.nonaktifkan();
            modeAmanAktif = null;
        }
    }

    public Student getSiswaDipilih() {
        return siswaDipilih;
    }

    public void setSiswaDipilih(Student siswaDipilih) {
        this.siswaDipilih = siswaDipilih;
    }

    public GradeHistory getRiwayatFokus() {
        return riwayatFokus;
    }

    public void setRiwayatFokus(GradeHistory riwayatFokus) {
        this.riwayatFokus = riwayatFokus;
    }

    public CourseEnrollmentService getPendaftaranService() {
        if (pendaftaranService == null) {
            pendaftaranService = new CourseEnrollmentService(AppConfig.FILE_PENDAFTARAN, progresService);
        }
        return pendaftaranService;
    }

    public void setPendaftaranService(CourseEnrollmentService pendaftaranService) {
        this.pendaftaranService = pendaftaranService;
    }

    public AssignmentService getTugasService() {
        if (tugasService == null) {
            tugasService = new AssignmentService(AppConfig.FILE_PENGUMPULAN_TUGAS, AppConfig.DIR_PENGUMPULAN_TUGAS);
        }
        return tugasService;
    }

    public void setTugasService(AssignmentService tugasService) {
        this.tugasService = tugasService;
    }
}
