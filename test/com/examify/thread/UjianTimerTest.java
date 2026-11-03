package com.examify.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UjianTimerTest {

    /** Callback dijalankan langsung di thread timer, menggantikan Platform.runLater. */
    private static final Executor LANGSUNG = Runnable::run;

    private static class ListenerPalsu implements TimerListener {
        final AtomicInteger jumlahTick = new AtomicInteger();
        final AtomicInteger jumlahHabis = new AtomicInteger();
        final CountDownLatch habis = new CountDownLatch(1);

        @Override
        public void onTick(int sisaDetik) {
            jumlahTick.incrementAndGet();
        }

        @Override
        public void onWaktuHabis() {
            jumlahHabis.incrementAndGet();
            habis.countDown();
        }
    }

    @Test // TC-14
    void timerDuaDetikMemanggilWaktuHabisTepatSekali() throws Exception {
        ListenerPalsu listener = new ListenerPalsu();
        UjianTimer timer = new UjianTimer(2, listener, LANGSUNG);
        timer.mulai();

        assertTrue(listener.habis.await(5, TimeUnit.SECONDS));
        Thread.sleep(1200); // pastikan tidak ada panggilan susulan
        assertTrue(listener.jumlahTick.get() >= 2);
        assertEquals(1, listener.jumlahHabis.get());
        assertEquals(0, timer.getSisaWaktu());
    }

    @Test
    void timerYangDihentikanTidakMemanggilWaktuHabis() throws Exception {
        ListenerPalsu listener = new ListenerPalsu();
        UjianTimer timer = new UjianTimer(2, listener, LANGSUNG);
        timer.mulai();
        timer.stop();

        assertFalse(listener.habis.await(3, TimeUnit.SECONDS));
        assertEquals(0, listener.jumlahHabis.get());
    }
}
