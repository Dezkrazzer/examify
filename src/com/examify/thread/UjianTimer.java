package com.examify.thread;

import javafx.application.Platform;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Countdown ujian di thread daemon terpisah dari FX Application Thread.
 * Timer tidak pernah menyentuh Node; semua callback dikirim lewat {@code uiExecutor}.
 */
public class UjianTimer implements Runnable {

    public static final String NAMA_THREAD = "Examify-Timer";
    private static final long INTERVAL_MILIDETIK = 1000;

    private final TimerListener listener;
    private final Executor uiExecutor;
    private final long deadlineNanos;
    private volatile int sisaWaktu; // detik; hanya ditulis oleh thread timer
    private volatile boolean berjalan = true;
    private Thread thread;

    public UjianTimer(int durasiDetik, TimerListener listener) {
        this(durasiDetik, listener, Platform::runLater);
    }

    /** uiExecutor dapat diganti agar timer dapat diuji tanpa toolkit JavaFX. */
    public UjianTimer(int durasiDetik, TimerListener listener, Executor uiExecutor) {
        this.listener = listener;
        this.uiExecutor = uiExecutor;
        this.sisaWaktu = durasiDetik;
        this.deadlineNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(durasiDetik);
    }

    public synchronized void mulai() {
        if (thread != null) {
            throw new IllegalStateException("Timer sudah dimulai");
        }
        thread = new Thread(this, NAMA_THREAD);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (berjalan && sisaWaktu > 0) {
            try {
                Thread.sleep(INTERVAL_MILIDETIK);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return; // dihentikan: submit manual / aplikasi ditutup
            }
            sisaWaktu = hitungSisaDetik(); // dari deadline, bukan decrement buta, agar tidak drift
            assert sisaWaktu >= 0;
            int snapshot = sisaWaktu;
            uiExecutor.execute(() -> listener.onTick(snapshot));
        }
        if (berjalan) {
            uiExecutor.execute(listener::onWaktuHabis);
        }
    }

    private int hitungSisaDetik() {
        long sisaNanos = deadlineNanos - System.nanoTime();
        long dibulatkanKeAtas = TimeUnit.NANOSECONDS.toSeconds(sisaNanos + TimeUnit.SECONDS.toNanos(1) - 1);
        return (int) Math.max(0, dibulatkanKeAtas);
    }

    public synchronized void stop() {
        berjalan = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    public int getSisaWaktu() {
        return sisaWaktu;
    }
}
