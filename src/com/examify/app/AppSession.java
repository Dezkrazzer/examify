package com.examify.app;

import com.examify.model.HasilUjian;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.Siswa;
import com.examify.model.Ujian;
import com.examify.service.AuthService;
import com.examify.service.KursusService;
import com.examify.service.ProgresBelajarService;
import com.examify.service.RekapService;
import com.examify.service.SertifikatService;
import com.examify.service.UjianService;
import com.examify.thread.UjianTimer;
import javafx.application.HostServices;

/** State sesi aplikasi (singleton): service, siswa yang login, serta kursus/ujian yang sedang aktif. */
public final class AppSession {

    private static final AppSession INSTANCE = new AppSession();

    private AuthService authService;
    private UjianService ujianService;
    private RekapService rekapService;
    private SertifikatService sertifikatService;
    private KursusService kursusService;
    private ProgresBelajarService progresService;
    private HostServices hostServices;

    private Siswa siswaAktif;
    private Kursus kursusAktif;
    private Materi materiFokus; // dibuka kembali saat kembali ke halaman kursus
    private Ujian ujianAktif;
    private HasilUjian hasilAktif;
    private UjianTimer timerAktif;

    private AppSession() {
    }

    public static AppSession getInstance() {
        return INSTANCE;
    }

    void pasangService(AuthService auth, UjianService ujian, RekapService rekap, SertifikatService sertifikat,
                       KursusService kursus, ProgresBelajarService progres) {
        this.authService = auth;
        this.ujianService = ujian;
        this.rekapService = rekap;
        this.sertifikatService = sertifikat;
        this.kursusService = kursus;
        this.progresService = progres;
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
        ujianAktif = null;
        hasilAktif = null;
    }

    public void logout() {
        resetUjian();
        kursusAktif = null;
        materiFokus = null;
        siswaAktif = null;
    }

    public void hentikanTimer() {
        if (timerAktif != null) {
            timerAktif.stop();
            timerAktif = null;
        }
    }

    public boolean isLogin() {
        return siswaAktif != null;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public UjianService getUjianService() {
        return ujianService;
    }

    public RekapService getRekapService() {
        return rekapService;
    }

    public SertifikatService getSertifikatService() {
        return sertifikatService;
    }

    public KursusService getKursusService() {
        return kursusService;
    }

    public ProgresBelajarService getProgresService() {
        return progresService;
    }

    public Siswa getSiswaAktif() {
        return siswaAktif;
    }

    public void setSiswaAktif(Siswa siswaAktif) {
        this.siswaAktif = siswaAktif;
    }

    public Kursus getKursusAktif() {
        return kursusAktif;
    }

    public void setKursusAktif(Kursus kursusAktif) {
        this.kursusAktif = kursusAktif;
    }

    public Materi getMateriFokus() {
        return materiFokus;
    }

    public void setMateriFokus(Materi materiFokus) {
        this.materiFokus = materiFokus;
    }

    public Ujian getUjianAktif() {
        return ujianAktif;
    }

    public void setUjianAktif(Ujian ujianAktif) {
        this.ujianAktif = ujianAktif;
    }

    public HasilUjian getHasilAktif() {
        return hasilAktif;
    }

    public void setHasilAktif(HasilUjian hasilAktif) {
        this.hasilAktif = hasilAktif;
    }

    public void setTimerAktif(UjianTimer timerAktif) {
        this.timerAktif = timerAktif;
    }
}
