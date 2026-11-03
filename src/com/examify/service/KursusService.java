package com.examify.service;

import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriTeks;
import com.examify.model.MateriUjian;
import com.examify.model.Ujian;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Katalog kursus e-learning beserta akses ke isi materi dan ujian di dalamnya. */
public class KursusService {

    private final Map<String, Kursus> katalog = new LinkedHashMap<>();
    private final UjianService ujianService;

    /** @throws IllegalStateException bila ada materi ujian yang merujuk ke ujian tidak terdaftar */
    public KursusService(List<Kursus> daftarKursus, UjianService ujianService) {
        this.ujianService = ujianService;
        daftarKursus.forEach(kursus -> katalog.put(kursus.getKode(), kursus));
        pastikanReferensiUjianValid();
    }

    private void pastikanReferensiUjianValid() {
        for (Kursus kursus : katalog.values()) {
            for (Materi materi : kursus.getSemuaMateri()) {
                if (materi instanceof MateriUjian mu && ujianService.cariUjian(mu.getKodeUjian()).isEmpty()) {
                    throw new IllegalStateException("Kursus " + kursus.getKode() + " merujuk ujian tidak dikenal: "
                            + mu.getKodeUjian());
                }
            }
        }
    }

    public List<Kursus> getDaftarKursus() {
        return List.copyOf(katalog.values());
    }

    public Optional<Kursus> cariKursus(String kode) {
        return Optional.ofNullable(katalog.get(kode));
    }

    /** Kursus yang cocok dengan kata kunci, diurutkan berdasarkan nama. */
    public List<Kursus> cari(String kataKunci) {
        return katalog.values().stream()
                .filter(kursus -> kursus.cocokDengan(kataKunci))
                .sorted(Comparator.comparing(Kursus::getNama))
                .toList();
    }

    public String bacaTeks(MateriTeks materi) throws IOException {
        return Files.readString(materi.getFile(), StandardCharsets.UTF_8);
    }

    public Ujian ujianDari(MateriUjian materi) {
        return ujianService.cariUjian(materi.getKodeUjian()).orElseThrow();
    }

    public int getJumlahMateri() {
        return katalog.values().stream().mapToInt(Kursus::getJumlahMateri).sum();
    }
}
