package com.examify.exception;

/** Dilempar ketika aksi ujian dilakukan setelah batas waktu terlewati. */
public class TimeOutException extends Exception {

    private final String kodeUjian;

    public TimeOutException(String kodeUjian) {
        this(kodeUjian, "Waktu ujian " + kodeUjian + " telah habis");
    }

    public TimeOutException(String kodeUjian, String message) {
        super(message);
        this.kodeUjian = kodeUjian;
    }

    public String getKodeUjian() {
        return kodeUjian;
    }
}
