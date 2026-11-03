package com.examify.model;

import com.examify.utils.StringUtil;

import java.util.List;

public class Bab {

    private final int nomor;
    private final String judul;
    private final List<Materi> daftarMateri;

    public Bab(int nomor, String judul, List<Materi> daftarMateri) {
        if (StringUtil.isKosong(judul)) {
            throw new IllegalArgumentException("Judul bab wajib diisi");
        }
        this.nomor = nomor;
        this.judul = judul.trim();
        this.daftarMateri = List.copyOf(daftarMateri);
    }

    public int getNomor() {
        return nomor;
    }

    public String getJudul() {
        return judul;
    }

    public List<Materi> getDaftarMateri() {
        return daftarMateri;
    }
}
