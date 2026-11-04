package com.examify.app;

/**
 * Setiap state UI berkorespondensi dengan satu file FXML. Layar "dalam shell" ditampilkan
 * di area konten sebelah sidebar; layar lain memenuhi seluruh jendela.
 */
public enum Layar {
    LANDING("landing.fxml", "Selamat Datang", false),
    LOGIN("login.fxml", "Masuk", false),
    KURSUS_SAYA("kursus_saya.fxml", "Kursus Saya", true),
    KURSUS_DETAIL("kursus_detail.fxml", "Kursus", true),
    PROFIL("profil.fxml", "Profil", true),
    TOKEN_GATE("token_gate.fxml", "Token Ujian", false),
    CBT_ROOM("cbt_room.fxml", "Ruang Ujian", false),
    RESULT("result.fxml", "Hasil Ujian", false),
    EDITOR_UJIAN("editor_ujian.fxml", "Editor Ujian", true);

    static final String FOLDER_FXML = "/com/examify/fxml/";

    private final String namaFile;
    private final String judul;
    private final boolean dalamShell;

    Layar(String namaFile, String judul, boolean dalamShell) {
        this.namaFile = namaFile;
        this.judul = judul;
        this.dalamShell = dalamShell;
    }

    public String getFxmlPath() {
        return FOLDER_FXML + namaFile;
    }

    public String getJudul() {
        return judul;
    }

    public boolean isDalamShell() {
        return dalamShell;
    }
}
