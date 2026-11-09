package com.examify.service;

import com.examify.model.Course;
import com.examify.model.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Layanan pendaftaran kursus bagi siswa dengan validasi kode akses. */
public class CourseEnrollmentService {

    private final Path filePendaftaran;
    private final LearningProgressService progresService;
    private final Set<String> terdaftar = new HashSet<>(); // "NIS#KODEKURSUS"

    public CourseEnrollmentService(Path filePendaftaran, LearningProgressService progresService) {
        this.filePendaftaran = filePendaftaran;
        this.progresService = progresService;
        muatData();
    }

    private void muatData() {
        if (filePendaftaran == null || !Files.exists(filePendaftaran)) {
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(filePendaftaran, StandardCharsets.UTF_8)) {
            String baris;
            while ((baris = br.readLine()) != null) {
                if (baris.isBlank() || baris.startsWith("#")) continue;
                String[] f = baris.split("\\|");
                if (f.length >= 2) {
                    terdaftar.add(kunci(f[0].trim(), f[1].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("[PendaftaranKursusService] Gagal memuat pendaftaran: " + e.getMessage());
        }
    }

    private String kunci(String nis, String kodeKursus) {
        return nis + "#" + kodeKursus;
    }

    /** Memeriksa apakah siswa sudah terdaftar / pernah memasukkan kode untuk kursus ini. */
    public synchronized boolean isTerdaftar(Student siswa, Course kursus) {
        if (siswa == null || kursus == null) return false;
        if (terdaftar.contains(kunci(siswa.getNis(), kursus.getKode()))) {
            return true;
        }
        // Siswa yang sudah memiliki progres belajar otomatis dianggap terdaftar
        if (progresService != null && progresService.getJumlahSelesai(siswa, kursus) > 0) {
            terdaftar.add(kunci(siswa.getNis(), kursus.getKode()));
            return true;
        }
        return false;
    }

    /** Mendaftarkan siswa ke kursus dan mencatat ke file persistensi. */
    public synchronized void daftarkan(Student siswa, Course kursus) throws IOException {
        if (siswa == null || kursus == null) return;
        String k = kunci(siswa.getNis(), kursus.getKode());
        if (terdaftar.add(k)) {
            if (filePendaftaran != null) {
                if (filePendaftaran.getParent() != null) {
                    Files.createDirectories(filePendaftaran.getParent());
                }
                String baris = siswa.getNis() + "|" + kursus.getKode() + "|" + LocalDateTime.now() + System.lineSeparator();
                Files.writeString(filePendaftaran, baris, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        }
    }

    /** Memperbarui kode kursus pada data pendaftaran ketika guru mengubah kode kursus. */
    public synchronized void perbaruiKodeKursus(String kodeLama, String kodeBaru) throws IOException {
        if (kodeLama == null || kodeBaru == null || kodeLama.equals(kodeBaru)) {
            return;
        }
        String akhiranLama = "#" + kodeLama;
        List<String> kunciLama = new ArrayList<>();
        for (String k : terdaftar) {
            if (k.endsWith(akhiranLama)) {
                kunciLama.add(k);
            }
        }
        if (kunciLama.isEmpty()) {
            return;
        }
        for (String k : kunciLama) {
            terdaftar.remove(k);
            String nis = k.substring(0, k.length() - akhiranLama.length());
            terdaftar.add(kunci(nis, kodeBaru));
        }
        if (filePendaftaran != null && Files.exists(filePendaftaran)) {
            List<String> barisBaru = new ArrayList<>();
            for (String baris : Files.readAllLines(filePendaftaran, StandardCharsets.UTF_8)) {
                if (baris.isBlank() || baris.startsWith("#")) {
                    barisBaru.add(baris);
                    continue;
                }
                String[] f = baris.split("\\|");
                if (f.length >= 2 && f[1].trim().equals(kodeLama)) {
                    String waktu = f.length >= 3 ? f[2].trim() : LocalDateTime.now().toString();
                    barisBaru.add(f[0].trim() + "|" + kodeBaru + "|" + waktu);
                } else {
                    barisBaru.add(baris);
                }
            }
            Files.write(filePendaftaran, barisBaru, StandardCharsets.UTF_8);
        }
    }
}
