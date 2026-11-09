package com.examify.model;

import com.examify.utils.StringUtil;

/** Superclass semua jenis soal. Objek immutable; kunci hanya dapat dibuka setelah ujian selesai. */
public abstract class Question {

    private final int idSoal;
    private final String pertanyaan;
    private final double bobot;
    private final QuestionType jenis;

    protected Question(int idSoal, String pertanyaan, double bobot, QuestionType jenis) {
        if (StringUtil.isKosong(pertanyaan)) {
            throw new IllegalArgumentException("Pertanyaan tidak boleh kosong");
        }
        if (bobot <= 0) {
            throw new IllegalArgumentException("Bobot harus > 0");
        }
        this.idSoal = idSoal;
        this.pertanyaan = pertanyaan.trim();
        this.bobot = bobot;
        this.jenis = jenis;
    }

    /** Titik polimorfisme: setiap jenis soal punya aturan benar/salahnya sendiri. */
    public abstract boolean evaluasiJawaban(String inputJawaban);

    public abstract String getPetunjukFormat();

    /** @throws IllegalStateException bila ujian belum selesai */
    public abstract String bukaKunci(ExamStatus status);

    protected String normalisasi(String input) {
        return StringUtil.normalisasi(input);
    }

    protected static void pastikanSelesai(ExamStatus status) {
        if (status != ExamStatus.SELESAI) {
            throw new IllegalStateException("Kunci hanya dapat dibuka setelah ujian selesai");
        }
    }

    public int getIdSoal() {
        return idSoal;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }

    public double getBobot() {
        return bobot;
    }

    public QuestionType getJenis() {
        return jenis;
    }

    @Override
    public String toString() {
        return "[" + jenis + "#" + idSoal + "] " + pertanyaan;
    }
}
