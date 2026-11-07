package com.examify.model;

/** Item ujian di dalam bab; merujuk ke ujian di bank soal lewat kodenya. */
public class MateriUjian extends Materi {

    private final String kodeUjian;

    public MateriUjian(String idMateri, String judul, String kodeUjian) {
        super(idMateri, judul, JenisMateri.UJIAN);
        this.kodeUjian = kodeUjian.trim().toUpperCase();
    }

    public String getKodeUjian() {
        return kodeUjian;
    }

    @Override
    public String getKeterangan() {
        return "Ujian - " + kodeUjian;
    }
}
