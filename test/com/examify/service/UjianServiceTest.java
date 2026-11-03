package com.examify.service;

import com.examify.exception.TimeOutException;
import com.examify.exception.TokenTidakValidException;
import com.examify.io.RekapNilaiReader;
import com.examify.io.RekapNilaiWriter;
import com.examify.model.HasilUjian;
import com.examify.model.ModeSubmit;
import com.examify.model.Siswa;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import com.examify.model.WaktuTerkendali;
import com.examify.utils.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UjianServiceTest {

    @TempDir
    Path folder;

    private final Siswa siswa = new Siswa("2410001", "Andi Pratama", "XI-IPA-1", "1234");
    private final Ujian ujian = new Ujian("MTK-01", "Matematika", "MTK7X2", 1, 50, List.of(
            new SoalTrueFalse(1, "a", 10, 'T')));
    private final WaktuTerkendali clock = new WaktuTerkendali();
    private Path fileRekap;
    private RekapService rekapService;
    private UjianService service;

    @BeforeEach
    void siapkan() {
        fileRekap = folder.resolve("rekap.txt");
        rekapService = new RekapService(new RekapNilaiWriter(fileRekap));
        service = new UjianService(List.of(ujian), rekapService, clock);
    }

    @Test // TC-13
    void tokenSalahTigaKaliDikunciDenganSisaNol() {
        assertEquals(2, tokenSalah().getSisaPercobaan());
        assertEquals(1, tokenSalah().getSisaPercobaan());
        assertEquals(0, tokenSalah().getSisaPercobaan());
        TokenTidakValidException terkunci = assertThrows(TokenTidakValidException.class,
                () -> service.verifikasiToken(siswa, ujian, "MTK7X2"));
        assertEquals(0, terkunci.getSisaPercobaan());
    }

    @Test
    void kunciTokenTerbukaSetelahJeda() {
        for (int i = 0; i < AppConfig.MAKS_PERCOBAAN_TOKEN; i++) {
            tokenSalah();
        }
        clock.maju(Duration.ofSeconds(AppConfig.JEDA_RESET_TOKEN_DETIK));
        assertDoesNotThrow(() -> service.verifikasiToken(siswa, ujian, "mtk7x2"));
    }

    private TokenTidakValidException tokenSalah() {
        return assertThrows(TokenTidakValidException.class, () -> service.verifikasiToken(siswa, ujian, "SALAH1"));
    }

    @Test
    void selesaikanMencatatRekapSekaliWalauDipanggilDuaKali() throws Exception {
        HasilUjian hasil = service.mulaiUjian(siswa, ujian);
        hasil.simpanJawaban(1, "T");
        service.selesaikan(hasil, ModeSubmit.MANUAL);
        service.selesaikanPaksa(hasil);

        assertEquals(2, Files.readAllLines(fileRekap).size()); // header + 1 baris
        assertTrue(rekapService.sudahMengerjakan(siswa, ujian));
        assertEquals(100.0, rekapService.getRekapSementara().get(siswa));
    }

    @Test
    void nilaiTerbaikDiambilDariSemuaPercobaan() throws Exception {
        Ujian ulang = new Ujian("UJI-01", "Uji", "UJI123", 1, 50, List.of(
                new SoalTrueFalse(1, "a", 10, 'F')), true);
        UjianService svc = new UjianService(List.of(ulang), rekapService, clock);
        svc.selesaikanPaksa(svc.mulaiUjian(siswa, ulang));           // skor 0
        HasilUjian tinggi = svc.mulaiUjian(siswa, ulang);
        tinggi.simpanJawaban(1, "F");                                // skor 100
        svc.selesaikanPaksa(tinggi);

        assertEquals(2, rekapService.getJumlahPercobaan(siswa, ulang));
        assertEquals(100.0, rekapService.getNilaiTerbaik(siswa, ulang).orElseThrow().skor());
    }

    @Test
    void ujianTanpaUlangTerkunciSetelahDikerjakan() throws Exception {
        assertTrue(service.bolehMulai(siswa, ujian));
        service.selesaikanPaksa(service.mulaiUjian(siswa, ujian));
        assertFalse(service.bolehMulai(siswa, ujian));
    }

    @Test
    void ujianDenganUlangTetapBolehDimulai() throws Exception {
        Ujian ulang = new Ujian("UJI-01", "Uji", "UJI123", 1, 50, List.of(
                new SoalTrueFalse(1, "a", 10, 'T')), true);
        UjianService svc = new UjianService(List.of(ulang), rekapService, clock);
        svc.selesaikanPaksa(svc.mulaiUjian(siswa, ulang));
        assertTrue(svc.bolehMulai(siswa, ulang));
    }

    @Test
    void riwayatPermanenTetapTerbacaDiSesiBaru() throws Exception {
        service.selesaikanPaksa(service.mulaiUjian(siswa, ujian));
        RekapService sesiBaru = new RekapService(new RekapNilaiWriter(fileRekap),
                new RekapNilaiReader().baca(fileRekap));
        assertTrue(sesiBaru.sudahMengerjakan(siswa, ujian));
        assertFalse(new UjianService(List.of(ujian), sesiBaru, clock).bolehMulai(siswa, ujian));
    }

    @Test
    void submitManualTerlambatDitolak() {
        HasilUjian hasil = service.mulaiUjian(siswa, ujian);
        clock.maju(Duration.ofMinutes(2));
        assertThrows(TimeOutException.class, () -> service.selesaikan(hasil, ModeSubmit.MANUAL));
    }

    @Test
    void katalogMengelompokkanMapel() {
        assertEquals(List.of("Matematika"), service.getDaftarMapel());
        assertEquals(List.of(ujian), service.getUjianByMapel("Matematika"));
    }

    @Test
    void loginMembutuhkanNisDanPinYangCocok() {
        AuthService auth = new AuthService(List.of(siswa));
        assertTrue(auth.login(" 2410001 ", "1234").isPresent());
        assertTrue(auth.login("2410001", "0000").isEmpty());
        assertTrue(auth.login("9999999", "1234").isEmpty());
    }
}
