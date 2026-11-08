package com.examify.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Representasi satu agenda akademik pada kalender (siswa maupun guru) yang disinkronkan
 * dari tugas, kuis bab, maupun ujian CBT.
 */
public record AgendaKalender(
        JenisAgenda jenis,
        String labelTipe,
        String judul,
        LocalDateTime waktu,
        LocalDateTime waktuAkhir,
        String infoJadwal,
        Kursus kursus,
        Bab bab,
        Materi materi,
        boolean selesai,
        String infoPartisipasiGuru
) implements Comparable<AgendaKalender> {

    public static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm");

    public enum JenisAgenda {
        TUGAS("Deadline Tugas", "Buka Tugas", "Kelola Tugas"),
        UJIAN("Ujian CBT", "Buka Ujian", "Kelola Ujian"),
        KUIS("Kuis Bab", "Buka Kuis", "Kelola Kuis");

        private final String namaDefault;
        private final String teksTombolSiswa;
        private final String teksTombolGuru;

        JenisAgenda(String namaDefault, String teksTombolSiswa, String teksTombolGuru) {
            this.namaDefault = namaDefault;
            this.teksTombolSiswa = teksTombolSiswa;
            this.teksTombolGuru = teksTombolGuru;
        }

        public String getNamaDefault() {
            return namaDefault;
        }

        public String getTeksTombol() {
            return teksTombolSiswa;
        }

        public String getTeksTombol(boolean modeGuru) {
            return modeGuru ? teksTombolGuru : teksTombolSiswa;
        }
    }

    public AgendaKalender(JenisAgenda jenis, String labelTipe, String judul,
                          LocalDateTime waktu, LocalDateTime waktuAkhir, String infoJadwal,
                          Kursus kursus, Bab bab, Materi materi, boolean selesai) {
        this(jenis, labelTipe, judul, waktu, waktuAkhir, infoJadwal, kursus, bab, materi, selesai, null);
    }

    public AgendaKalender {
        Objects.requireNonNull(jenis, "Jenis agenda wajib diisi");
        Objects.requireNonNull(judul, "Judul agenda wajib diisi");
        Objects.requireNonNull(waktu, "Waktu agenda wajib diisi");
        Objects.requireNonNull(kursus, "Kursus agenda wajib diisi");
        Objects.requireNonNull(bab, "Bab agenda wajib diisi");
        Objects.requireNonNull(materi, "Materi agenda wajib diisi");
        if (labelTipe == null || labelTipe.isBlank()) {
            labelTipe = jenis.getNamaDefault();
        }
        if (infoJadwal == null) {
            infoJadwal = "";
        }
    }

    public LocalDate tanggal() {
        return waktu.toLocalDate();
    }

    public boolean padaTanggal(LocalDate tgl) {
        return tgl != null && waktu.toLocalDate().equals(tgl);
    }

    public boolean padaBulan(YearMonth bulan) {
        return bulan != null && YearMonth.from(waktu).equals(bulan);
    }

    public boolean isMendatangAtauBerlangsung(LocalDateTime sekarang) {
        if (sekarang == null) return true;
        LocalDateTime batas = waktuAkhir != null ? waktuAkhir : waktu;
        return !batas.isBefore(sekarang);
    }

    public boolean isSedangBerlangsung(LocalDateTime sekarang) {
        if (sekarang == null || waktuAkhir == null) return false;
        return !sekarang.isBefore(waktu) && !sekarang.isAfter(waktuAkhir);
    }

    public boolean isTerlewat(LocalDateTime sekarang) {
        if (selesai || sekarang == null) return false;
        LocalDateTime batas = waktuAkhir != null ? waktuAkhir : waktu;
        return sekarang.isAfter(batas);
    }

    public boolean isWaktuBerakhir(LocalDateTime sekarang) {
        if (sekarang == null) return false;
        LocalDateTime batas = waktuAkhir != null ? waktuAkhir : waktu;
        return sekarang.isAfter(batas);
    }

    public String getLabelStatus(LocalDateTime sekarang) {
        if (infoPartisipasiGuru != null) {
            if (isWaktuBerakhir(sekarang)) {
                return "Berakhir · " + infoPartisipasiGuru;
            }
            if (isSedangBerlangsung(sekarang)) {
                return "Sedang Dibuka · " + infoPartisipasiGuru;
            }
            return infoPartisipasiGuru;
        }
        if (selesai) {
            return jenis == JenisAgenda.TUGAS ? "Sudah Dikumpulkan" : "Sudah Dikerjakan";
        }
        if (isTerlewat(sekarang)) {
            return "Terlewat";
        }
        if (isSedangBerlangsung(sekarang)) {
            return "Sedang Berlangsung";
        }
        return "Belum Selesai";
    }

    public String getFormatJam() {
        if (waktuAkhir != null && waktuAkhir.toLocalDate().equals(waktu.toLocalDate())) {
            return waktu.format(FORMAT_JAM) + " - " + waktuAkhir.format(FORMAT_JAM) + " WIB";
        }
        return waktu.format(FORMAT_JAM) + " WIB";
    }

    public String getFormatJamSingkat() {
        return waktu.format(FORMAT_JAM);
    }

    @Override
    public int compareTo(AgendaKalender o) {
        int cmpWaktu = this.waktu.compareTo(o.waktu);
        if (cmpWaktu != 0) return cmpWaktu;
        int cmpJenis = this.jenis.compareTo(o.jenis);
        if (cmpJenis != 0) return cmpJenis;
        return this.judul.compareToIgnoreCase(o.judul);
    }
}
