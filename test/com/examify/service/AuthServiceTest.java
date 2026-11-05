package com.examify.service;

import com.examify.model.Guru;
import com.examify.model.Siswa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    private AuthService authService;
    private Siswa siswa;
    private Guru guru;

    @BeforeEach
    void setup() {
        siswa = new Siswa("12345", "Budi", "XII-IPA", "1234");
        guru = new Guru("19850101", "Dr. Rina Kartika", "1234", "Pemrograman Java");
        authService = new AuthService(List.of(siswa), List.of(guru));
    }

    @Test
    void loginSiswaBerhasilDanGagal() {
        Optional<Siswa> berhasil = authService.loginSiswa("12345", "1234");
        assertTrue(berhasil.isPresent());
        assertEquals("Budi", berhasil.get().getNama());

        Optional<Siswa> pinSalah = authService.loginSiswa("12345", "9999");
        assertFalse(pinSalah.isPresent());

        Optional<Siswa> nisSalah = authService.loginSiswa("99999", "1234");
        assertFalse(nisSalah.isPresent());
    }

    @Test
    void loginGuruBerhasilDanGagal() {
        Optional<Guru> berhasil = authService.loginGuru("19850101", "1234");
        assertTrue(berhasil.isPresent());
        assertEquals("Dr. Rina Kartika", berhasil.get().getNama());

        Optional<Guru> pinSalah = authService.loginGuru("19850101", "0000");
        assertFalse(pinSalah.isPresent());

        Optional<Guru> nipSalah = authService.loginGuru("99999999", "1234");
        assertFalse(nipSalah.isPresent());
    }

    @Test
    void daftarSemuaSiswaDanGuruTersedia() {
        assertEquals(1, authService.getSemuaSiswa().size());
        assertEquals("Budi", authService.getSemuaSiswa().get(0).getNama());

        assertEquals(1, authService.getSemuaGuru().size());
        assertEquals("Dr. Rina Kartika", authService.getSemuaGuru().get(0).getNama());

        assertTrue(authService.cariSiswa("12345").isPresent());
        assertFalse(authService.cariSiswa("999").isPresent());

        assertTrue(authService.cariGuru("19850101").isPresent());
        assertFalse(authService.cariGuru("999").isPresent());
    }
}
