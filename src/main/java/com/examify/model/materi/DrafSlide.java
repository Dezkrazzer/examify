package com.examify.model.materi;

import java.util.ArrayList;
import java.util.List;

public class DrafSlide {

    private String judulSlide;
    private final List<DrafBlok> daftarBlok = new ArrayList<>();

    public DrafSlide(String judulSlide) {
        this.judulSlide = judulSlide == null ? "" : judulSlide.trim();
    }

    public String getJudulSlide() {
        return judulSlide;
    }

    public void setJudulSlide(String judulSlide) {
        this.judulSlide = judulSlide == null ? "" : judulSlide.trim();
    }

    public List<DrafBlok> getDaftarBlok() {
        return daftarBlok;
    }

    public void tambahBlok(DrafBlok blok) {
        if (blok != null) {
            daftarBlok.add(blok);
        }
    }
}
