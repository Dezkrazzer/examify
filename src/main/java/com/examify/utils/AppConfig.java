package com.examify.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Konstanta konfigurasi aplikasi. Path relatif terhadap working directory. */
public final class AppConfig {

    public static final String NAMA_APLIKASI = "Examify";
    public static final String KODE_SERTIFIKAT = "EXF";

    public static final Path DIR_DATA = Path.of("data");
    public static final Path FILE_BANK_SOAL = DIR_DATA.resolve("bank_soal.txt");
    public static final Path FILE_SISWA = DIR_DATA.resolve("siswa.txt");
    public static final Path FILE_GURU = DIR_DATA.resolve("guru.txt");
    public static final Path FILE_KURSUS = DIR_DATA.resolve("kursus.txt");
    public static final Path FILE_TIM = DIR_DATA.resolve("tim.txt");
    public static final Path DIR_PENGUMPULAN_TUGAS = DIR_DATA.resolve("pengumpulan_tugas");
    public static final Path FILE_PENGUMPULAN_TUGAS = DIR_DATA.resolve("pengumpulan_tugas.txt");

    public static final Path DIR_OUTPUT = Path.of("output");
    public static final Path FILE_REKAP = DIR_OUTPUT.resolve("rekap_nilai.txt");
    public static final Path FILE_PROGRES = DIR_OUTPUT.resolve("progres_belajar.txt");
    public static final Path FILE_PENDAFTARAN = DIR_OUTPUT.resolve("pendaftaran_kursus.txt");
    public static final Path DIR_SERTIFIKAT = DIR_OUTPUT.resolve("sertifikat");

    public static final int MAKS_PERCOBAAN_TOKEN = 3;
    public static final int JEDA_RESET_TOKEN_DETIK = 60;
    public static final int GRACE_PERIOD_DETIK = 2;
    public static final int BATAS_WAKTU_KRITIS_DETIK = 60;
    public static final int LEBAR_SERTIFIKAT = 64;

    private AppConfig() {
    }

    public static void siapkanFolderOutput() throws IOException {
        Files.createDirectories(DIR_SERTIFIKAT);
        Files.createDirectories(DIR_PENGUMPULAN_TUGAS);
    }
}
