package com.examify.model;

import com.examify.utils.StringUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Materi penugasan dengan target kelas, batas waktu pengumpulan, dan deskripsi tugas. */
public class MateriTugas extends Materi {

    public static final DateTimeFormatter FORMAT_TANGGAL_WAKTU = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter FORMAT_TAMPILAN = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

    private final LocalDateTime deadline;
    private final String deadlineStr;
    private final String targetKelas;
    private final String deskripsi;

    public MateriTugas(String idMateri, String judul, String deadlineStr, String targetKelas, String deskripsi) {
        super(idMateri, judul, JenisMateri.TUGAS);
        this.deadlineStr = deadlineStr == null ? "" : deadlineStr.trim();
        this.deadline = parseDeadline(this.deadlineStr);
        this.targetKelas = (targetKelas == null || targetKelas.isBlank()) ? "Semua Kelas" : targetKelas.trim();
        this.deskripsi = deskripsi == null ? "" : deskripsi.trim();
    }

    public MateriTugas(String idMateri, String judul, LocalDateTime deadline, String targetKelas, String deskripsi) {
        super(idMateri, judul, JenisMateri.TUGAS);
        this.deadline = deadline;
        this.deadlineStr = deadline == null ? "" : deadline.format(FORMAT_TANGGAL_WAKTU);
        this.targetKelas = (targetKelas == null || targetKelas.isBlank()) ? "Semua Kelas" : targetKelas.trim();
        this.deskripsi = deskripsi == null ? "" : deskripsi.trim();
    }

    private static LocalDateTime parseDeadline(String str) {
        if (StringUtil.isKosong(str)) return null;
        try {
            return LocalDateTime.parse(str.trim(), FORMAT_TANGGAL_WAKTU);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(str.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getDeadlineStr() {
        return deadlineStr;
    }

    public String getDeadlineFormatted() {
        if (deadline == null) return "Tanpa Batas Waktu";
        try {
            return deadline.format(FORMAT_TAMPILAN) + " WIB";
        } catch (Exception e) {
            return deadlineStr;
        }
    }

    public String getTargetKelas() {
        return targetKelas;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public boolean isSemuaKelas() {
        return targetKelas == null || targetKelas.isBlank()
                || targetKelas.equalsIgnoreCase("Semua")
                || targetKelas.equalsIgnoreCase("Semua Kelas");
    }

    /** Memeriksa apakah siswa diizinkan mengakses tugas berdasarkan kelasnya. */
    public boolean isBolehAkses(Siswa siswa) {
        if (siswa == null) return false;
        if (isSemuaKelas()) return true;
        if (siswa.getKelas() == null || siswa.getKelas().isBlank()) return false;

        String kelasSiswa = siswa.getKelas().trim().toLowerCase();
        for (String k : targetKelas.split(",")) {
            if (k.trim().toLowerCase().equals(kelasSiswa)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLewatDeadline() {
        return deadline != null && LocalDateTime.now().isAfter(deadline);
    }

    @Override
    public String getKeterangan() {
        String infoKelas = isSemuaKelas() ? "Semua Kelas" : "Kelas: " + targetKelas;
        return "Tugas · " + infoKelas;
    }
}
