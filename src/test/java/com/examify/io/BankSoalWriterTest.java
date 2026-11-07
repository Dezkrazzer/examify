package com.examify.io;

import com.examify.model.Soal;
import com.examify.model.SoalIsian;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankSoalWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void tulisDanBacaKembaliBankSoal() throws Exception {
        Path fileBankSoal = tempDir.resolve("bank_soal.txt");

        List<Soal> daftarSoal = List.of(
                new SoalPilihanGanda(1, "1 + 1 = ?", 10.0, List.of("1", "2", "3"), 'b'),
                new SoalTrueFalse(2, "Bumi itu bulat", 10.0, 'T'),
                new SoalIsian(3, "Ibukota Indonesia sekarang?", 10.0, Set.of("Nusantara", "IKN"))
        );

        Ujian ujian = new Ujian("TEST-01", "Ujian Uji Coba", "TOKEN9", 30, 75.0, daftarSoal);

        BankSoalWriter writer = new BankSoalWriter();
        writer.tulis(fileBankSoal, List.of(ujian));

        BankSoalReader reader = new BankSoalReader();
        List<Ujian> hasil = reader.baca(fileBankSoal);

        assertEquals(1, hasil.size());
        Ujian u = hasil.get(0);
        assertEquals("TEST-01", u.getKodeUjian());
        assertEquals("TOKEN9", u.getToken());
        assertEquals(3, u.getJumlahSoal());
        assertTrue(u.getDaftarSoal().get(0).evaluasiJawaban("B"));
        assertTrue(u.getDaftarSoal().get(1).evaluasiJawaban("TRUE"));
        assertTrue(u.getDaftarSoal().get(2).evaluasiJawaban("nusantara"));
    }
}
