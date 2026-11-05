package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.model.JenisSoal;
import com.examify.model.Soal;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.Ujian;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EditorUjianTest {

    @Test
    void testDrafDanSoalBaru() {
        List<Soal> list = List.of(new SoalPilihanGanda(1, "Soal 1", 10.0, List.of("A", "B", "C", "D"), 'A'));
        Ujian u = new Ujian("BIN-01", "Bahasa Indonesia", "TOKEN1", 20, 70.0, list);
        AppSession.getInstance().setUjianAktif(u);

        // simulasi draf
        int maxId = u.getDaftarSoal().stream().mapToInt(Soal::getIdSoal).max().orElse(0);
        assertEquals(1, maxId);
    }
}
