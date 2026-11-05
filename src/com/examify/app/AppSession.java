package com.examify.app;

import com.examify.model.Bab;
import com.examify.model.Guru;
import com.examify.model.HasilUjian;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriTeks;
import com.examify.model.RiwayatNilai;
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

/** State sesi aplikasi (singleton): service, siswa/guru yang login, serta kursus/ujian yang sedang aktif. */
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
    private Guru guruAktif;
    private Kursus kursusAktif;
    private Bab babAktifMateri;
    private MateriTeks materiTeksAktif;
    private Materi materiFokus; // dibuka kembali saat kembali ke halaman kursus
    private Ujian ujianAktif;
    private HasilUjian hasilAktif;
    private UjianTimer timerAktif;
    private Siswa siswaDipilih;
    private RiwayatNilai riwayatFokus;

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
        if (siswaAktif != null) {
            this.guruAktif = null;
        }
    }

    public Guru getGuruAktif() {
        return guruAktif;
    }

    public void setGuruAktif(Guru guruAktif) {
        this.guruAktif = guruAktif;
        if (guruAktif != null) {
            this.siswaAktif = null;
        }
    }

    public Kursus getKursusAktif() {
        return kursusAktif;
    }

    public void setKursusAktif(Kursus kursusAktif) {
        this.kursusAktif = kursusAktif;
    }

    public Bab getBabAktifMateri() {
        return babAktifMateri;
    }

    public void setBabAktifMateri(Bab babAktifMateri) {
        this.babAktifMateri = babAktifMateri;
    }

    public MateriTeks getMateriTeksAktif() {
        return materiTeksAktif;
    }

    public void setMateriTeksAktif(MateriTeks materiTeksAktif) {
        this.materiTeksAktif = materiTeksAktif;
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

    public Siswa getSiswaDipilih() {
        return siswaDipilih;
    }

    public void setSiswaDipilih(Siswa siswaDipilih) {
        this.siswaDipilih = siswaDipilih;
    }

    public RiwayatNilai getRiwayatFokus() {
        return riwayatFokus;
    }

    public void setRiwayatFokus(RiwayatNilai riwayatFokus) {
        this.riwayatFokus = riwayatFokus;
    }
}
