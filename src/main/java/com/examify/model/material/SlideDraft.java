package com.examify.model.material;

import java.util.ArrayList;
import java.util.List;

public class SlideDraft {

    private String judulSlide;
    private final List<BlockDraft> daftarBlok = new ArrayList<>();

    public SlideDraft(String judulSlide) {
        this.judulSlide = judulSlide == null ? "" : judulSlide.trim();
    }

    public String getJudulSlide() {
        return judulSlide;
    }

    public void setJudulSlide(String judulSlide) {
        this.judulSlide = judulSlide == null ? "" : judulSlide.trim();
    }

    public List<BlockDraft> getDaftarBlok() {
        return daftarBlok;
    }

    public void tambahBlok(BlockDraft blok) {
        if (blok != null) {
            daftarBlok.add(blok);
        }
    }
}
