package com.examify.model;

import com.examify.utils.StringUtil;

import java.util.Objects;

/** Identitas guru / pengajar. */
public class Teacher {

    private final String nip;
    private final String nama;
    private final String pin;
    private final String mataPelajaran;

    public Teacher(String nip, String nama, String pin, String mataPelajaran) {
        if (StringUtil.isKosong(nip) || StringUtil.isKosong(nama) || StringUtil.isKosong(pin)) {
            throw new IllegalArgumentException("NIP, nama, dan PIN wajib diisi");
        }
        this.nip = nip.trim();
        this.nama = nama.trim();
        this.pin = pin.trim();
        this.mataPelajaran = mataPelajaran == null ? "" : mataPelajaran.trim();
    }

    public boolean verifikasiPin(String input) {
        return input != null && input.trim().equals(pin);
    }

    public String getNip() {
        return nip;
    }

    public String getNama() {
        return nama;
    }

    public String getMataPelajaran() {
        return mataPelajaran;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher lain)) return false;
        return nip.equals(lain.nip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nip);
    }

    @Override
    public String toString() {
        return nip + " - " + nama;
    }
}
