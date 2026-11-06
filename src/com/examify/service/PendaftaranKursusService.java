package com.examify.service;

import com.examify.model.Kursus;
import com.examify.model.Siswa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/** Layanan pendaftaran kursus bagi siswa dengan validasi kode akses. */
public class PendaftaranKursusService {

    private final Path filePendaftaran;
    private final ProgresBelajarService progresService;
    private final Set<String> terdaftar = new HashSet<>(); // "NIS#KODEKURSUS"

    public PendaftaranKursusService(Path filePendaftaran, ProgresBelajarService progresService) {
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
    public synchronized boolean isTerdaftar(Siswa siswa, Kursus kursus) {
        if (siswa == null || kursus == null) return true;
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
    public synchronized void daftarkan(Siswa siswa, Kursus kursus) throws IOException {
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
}
