package com.examify.model;

/** Item kuis di dalam bab; merujuk ke ujian kuis di bank soal lewat kodenya. */
public class QuizMaterial extends Material {

    private final String kodeUjian;

    public QuizMaterial(String idMateri, String judul, String kodeUjian) {
        super(idMateri, judul, MaterialType.QUIZ);
        this.kodeUjian = kodeUjian.trim().toUpperCase();
    }

    public String getKodeUjian() {
        return kodeUjian;
    }

    @Override
    public String getKeterangan() {
        return "Kuis - " + kodeUjian;
    }
}
