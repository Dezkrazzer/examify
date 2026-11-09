package com.examify.model;

import com.examify.utils.StringUtil;

import java.util.Optional;

/** Anggota kelompok pengembang Examify, ditampilkan di landing page. */
public class TeamMember {

    public enum Sosial { WEBSITE, GITHUB, INSTAGRAM, LINKEDIN }

    private final String nim;
    private final String nama;
    private final String foto;
    private final String[] tautan = new String[Sosial.values().length];

    /** @param tautanSosial URL berurutan sesuai {@link Sosial}; boleh kosong/null */
    public TeamMember(String nim, String nama, String foto, String... tautanSosial) {
        if (StringUtil.isKosong(nim) || StringUtil.isKosong(nama)) {
            throw new IllegalArgumentException("NIM dan nama anggota wajib diisi");
        }
        this.nim = nim.trim();
        this.nama = nama.trim();
        this.foto = foto == null ? "" : foto.trim();
        for (int i = 0; i < tautan.length; i++) {
            String url = i < tautanSosial.length ? tautanSosial[i] : null;
            tautan[i] = url == null ? "" : url.trim();
        }
    }

    public String getNim() {
        return nim;
    }

    public String getNama() {
        return nama;
    }

    /** Path foto relatif dari folder data, atau string kosong. */
    public String getFoto() {
        return foto;
    }

    public Optional<String> getTautan(Sosial sosial) {
        String url = tautan[sosial.ordinal()];
        return url.isEmpty() ? Optional.empty() : Optional.of(url);
    }

    /** Maksimal dua huruf inisial, mis. "Akbar Maulana" -> "AM". */
    public String getInisial() {
        StringBuilder sb = new StringBuilder();
        for (String kata : nama.split("\\s+")) {
            if (!kata.isEmpty() && Character.isLetter(kata.charAt(0)) && sb.length() < 2) {
                sb.append(Character.toUpperCase(kata.charAt(0)));
            }
        }
        return sb.isEmpty() ? "?" : sb.toString();
    }
}
