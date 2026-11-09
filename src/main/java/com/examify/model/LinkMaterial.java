package com.examify.model;

import java.net.URI;

/** Tautan ke video, slide, atau halaman web yang dibuka di browser. */
public class LinkMaterial extends Material {

    private final URI alamat;

    public LinkMaterial(String idMateri, String judul, String alamat) {
        super(idMateri, judul, MaterialType.LINK);
        this.alamat = URI.create(alamat.trim());
        if (!"http".equals(this.alamat.getScheme()) && !"https".equals(this.alamat.getScheme())) {
            throw new IllegalArgumentException("Tautan harus diawali http:// atau https://");
        }
    }

    public URI getAlamat() {
        return alamat;
    }

    @Override
    public String getKeterangan() {
        return "Tautan - " + alamat.getHost();
    }
}
