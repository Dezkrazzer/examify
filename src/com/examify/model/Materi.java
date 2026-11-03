package com.examify.model;

import com.examify.utils.StringUtil;

/** Superclass semua item belajar di dalam bab (bacaan, tautan, PDF, ujian). */
public abstract class Materi {

    private final String idMateri;
    private final String judul;
    private final JenisMateri jenis;

    protected Materi(String idMateri, String judul, JenisMateri jenis) {
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

    public JenisMateri getJenis() {
        return jenis;
    }

    @Override
    public String toString() {
        return "[" + jenis + "] " + judul;
    }
}
