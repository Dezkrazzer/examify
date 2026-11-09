package com.examify.model.material;

public class BlockDraft {

    private BlockType jenis;
    private String isi;
    private String ekstra; // caption gambar, jenis callout, judul video youtube, atau bahasa kode

    public BlockDraft(BlockType jenis, String isi) {
        this(jenis, isi, "");
    }

    public BlockDraft(BlockType jenis, String isi, String ekstra) {
        this.jenis = jenis;
        this.isi = isi == null ? "" : isi;
        this.ekstra = ekstra == null ? "" : ekstra;
    }

    public BlockType getJenis() {
        return jenis;
    }

    public void setJenis(BlockType jenis) {
        this.jenis = jenis;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi == null ? "" : isi;
    }

    public String getEkstra() {
        return ekstra;
    }

    public void setEkstra(String ekstra) {
        this.ekstra = ekstra == null ? "" : ekstra;
    }
}
