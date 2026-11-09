package com.examify.model;

/** Item ujian di dalam bab; merujuk ke ujian di bank soal lewat kodenya. */
public class ExamMaterial extends Material {

    private final String kodeUjian;

    public ExamMaterial(String idMateri, String judul, String kodeUjian) {
        super(idMateri, judul, MaterialType.UJIAN);
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
