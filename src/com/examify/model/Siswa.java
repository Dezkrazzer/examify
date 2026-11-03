package com.examify.model;

import com.examify.utils.StringUtil;

import java.util.Objects;

/** Identitas siswa. equals/hashCode hanya berdasarkan NIS agar aman dipakai sebagai key HashMap. */
public class Siswa {

    private final String nis;
    private final String nama;
    private final String kelas;
    private final String pin; // tidak pernah dikembalikan getter

    public Siswa(String nis, String nama, String kelas, String pin) {
        if (StringUtil.isKosong(nis) || StringUtil.isKosong(nama)
                || StringUtil.isKosong(kelas) || StringUtil.isKosong(pin)) {
            throw new IllegalArgumentException("NIS, nama, kelas, dan PIN wajib diisi");
        }
        this.nis = nis.trim();
        this.nama = nama.trim();
        this.kelas = kelas.trim();
        this.pin = pin.trim();
    }

    public boolean verifikasiPin(String input) {
        return input != null && input.trim().equals(pin);
    }

    public String getNis() {
        return nis;
    }

    public String getNama() {
        return nama;
    }

    public String getKelas() {
        return kelas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Siswa lain)) return false;
        return nis.equals(lain.nis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nis);
    }

    @Override
    public String toString() {
        return nis + " - " + nama;
    }
}
