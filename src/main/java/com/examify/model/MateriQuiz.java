package com.examify.model;

/** Item kuis di dalam bab; merujuk ke ujian kuis di bank soal lewat kodenya. */
public class MateriQuiz extends Materi {

    private final String kodeUjian;

    public MateriQuiz(String idMateri, String judul, String kodeUjian) {
        super(idMateri, judul, JenisMateri.QUIZ);
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
