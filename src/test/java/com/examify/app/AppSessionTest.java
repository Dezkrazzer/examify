package com.examify.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppSessionTest {

    private AppSession session;

    @BeforeEach
    void setup() {
        session = AppSession.getInstance();
        session.setLayarAsalPembahasan(AppScreen.PROFIL);
    }

    @AfterEach
    void tearDown() {
        session.logout();
    }

    @Test
    void defaultLayarAsalPembahasanAdalahProfil() {
        session.logout();
        assertEquals(AppScreen.PROFIL, session.getLayarAsalPembahasan());
    }

    @Test
    void setLayarAsalPembahasanKeKursusDetail() {
        session.setLayarAsalPembahasan(AppScreen.KURSUS_DETAIL);
        assertEquals(AppScreen.KURSUS_DETAIL, session.getLayarAsalPembahasan());
    }

    @Test
    void getterMengembalikanProfilJikaDisetNull() {
        session.setLayarAsalPembahasan(null);
        assertEquals(AppScreen.PROFIL, session.getLayarAsalPembahasan());
    }

    @Test
    void logoutMeresetLayarAsalPembahasan() {
        session.setLayarAsalPembahasan(AppScreen.KURSUS_DETAIL);
        session.logout();
        assertEquals(AppScreen.PROFIL, session.getLayarAsalPembahasan());
    }
}
