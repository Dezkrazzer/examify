package com.examify.model;

import com.examify.utils.StringUtil;

/** Superclass semua item belajar di dalam bab (bacaan, tautan, PDF, ujian). */
public abstract class Material {

    private final String idMateri;
    private final String judul;
    private final MaterialType jenis;

    protected Material(String idMateri, String judul, MaterialType jenis) {
        if (StringUtil.isKosong(idMateri) || StringUtil.isKosong(judul)) {
            throw new IllegalArgumentException("ID dan judul materi wajib diisi");
        }
        this.idMateri = idMateri.trim();
        this.judul = judul.trim();
        this.jenis = jenis;
    }

    /** Teks singkat di bawah judul pada daftar materi; berbeda untuk setiap jenis. */
    public abstract String getKeterangan();

    public String getIdMateri() {
        return idMateri;
    }

    public String getJudul() {
        return judul;
    }

    public MaterialType getJenis() {
        return jenis;
    }

    @Override
    public String toString() {
        return "[" + jenis + "] " + judul;
    }
}
