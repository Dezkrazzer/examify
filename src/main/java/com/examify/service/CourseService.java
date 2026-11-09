package com.examify.service;

import com.examify.io.CourseWriter;
import com.examify.model.Course;
import com.examify.model.Material;
import com.examify.model.PdfMaterial;
import com.examify.model.QuizMaterial;
import com.examify.model.TextMaterial;
import com.examify.model.ExamMaterial;
import com.examify.model.Exam;
import com.examify.utils.AppConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Katalog kursus e-learning beserta akses ke isi materi dan ujian di dalamnya. */
public class CourseService {

    private final Map<String, Course> katalog = new LinkedHashMap<>();
    private final ExamService ujianService;
    private final CourseWriter writer;
    private final Path fileKursus;

    public CourseService(List<Course> daftarKursus, ExamService ujianService) {
        this(daftarKursus, ujianService, null, null);
    }

    public CourseService(List<Course> daftarKursus, ExamService ujianService,
                         CourseWriter writer, Path fileKursus) {
        this.ujianService = ujianService;
        this.writer = writer;
        this.fileKursus = fileKursus;
        daftarKursus.forEach(kursus -> katalog.put(kursus.getKode(), kursus));
        pastikanReferensiUjianValid();
    }

    private void pastikanReferensiUjianValid() {
        for (Course kursus : katalog.values()) {
            for (Material materi : kursus.getSemuaMateri()) {
                if (materi instanceof ExamMaterial mu && ujianService.cariUjian(mu.getKodeUjian()).isEmpty()) {
                    throw new IllegalStateException("Kursus " + kursus.getKode() + " merujuk ujian tidak dikenal: "
                            + mu.getKodeUjian());
                }
                if (materi instanceof QuizMaterial mq && ujianService.cariUjian(mq.getKodeUjian()).isEmpty()) {
                    throw new IllegalStateException("Kursus " + kursus.getKode() + " merujuk kuis tidak dikenal: "
                            + mq.getKodeUjian());
                }
            }
        }
    }

    public List<Course> getDaftarKursus() {
        return List.copyOf(katalog.values());
    }

    public Optional<Course> cariKursus(String kode) {
        if (kode == null) return Optional.empty();
        return Optional.ofNullable(katalog.get(kode.trim()));
    }

    /** Kursus yang cocok dengan kata kunci, diurutkan berdasarkan nama. */
    public List<Course> cari(String kataKunci) {
        return katalog.values().stream()
                .filter(kursus -> kursus.cocokDengan(kataKunci))
                .sorted(Comparator.comparing(Course::getNama))
                .toList();
    }

    public synchronized void tambahKursus(Course kursus) throws IOException {
        if (katalog.containsKey(kursus.getKode())) {
            throw new IllegalArgumentException("Kode kursus sudah digunakan: " + kursus.getKode());
        }
        katalog.put(kursus.getKode(), kursus);
        simpanKeFile();
    }

    public synchronized void perbaruiKursus(Course kursus) throws IOException {
        katalog.put(kursus.getKode(), kursus);
        simpanKeFile();
    }

    public synchronized Course ubahKodeKursus(Course kursusLama, String kodeBaru) throws IOException {
        if (kodeBaru == null || kodeBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode kursus tidak boleh kosong");
        }
        String kodeBersih = kodeBaru.trim();
        if (!kodeBersih.equals(kursusLama.getKode()) && katalog.containsKey(kodeBersih)) {
            throw new IllegalArgumentException("Kode kursus sudah digunakan: " + kodeBersih);
        }
        katalog.remove(kursusLama.getKode());
        Course kursusBaru = kursusLama.denganKode(kodeBersih);
        katalog.put(kodeBersih, kursusBaru);
        simpanKeFile();
        return kursusBaru;
    }

    public synchronized void simpanKeFile() throws IOException {
        if (writer != null && fileKursus != null) {
            writer.tulis(fileKursus, getDaftarKursus());
        }
    }

    public String bacaTeks(TextMaterial materi) throws IOException {
        return Files.readString(materi.getFile(), StandardCharsets.UTF_8);
    }

    public void simpanTeksMateri(TextMaterial materi, String isiKonten) throws IOException {
        Path file = materi.getFile();
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.writeString(file, isiKonten, StandardCharsets.UTF_8);
    }

    /** Menentukan folder penyimpanan berkas materi sesuai folder materi yang sudah ada atau nama foldernya. */
    public Path tentukanFolderMateri(Course kursus) {
        if (kursus != null) {
            for (Material m : kursus.getSemuaMateri()) {
                if (m instanceof TextMaterial mt && mt.getFile() != null && mt.getFile().getParent() != null) {
                    return mt.getFile().getParent();
                }
                if (m instanceof PdfMaterial mp && mp.getFile() != null && mp.getFile().getParent() != null) {
                    return mp.getFile().getParent();
                }
            }
            if ("BIN".equalsIgnoreCase(kursus.getKode())) {
                Path folderBahasaIndo = AppConfig.DIR_DATA.resolve("materi").resolve("bahasaindo");
                if (Files.exists(folderBahasaIndo)) {
                    return folderBahasaIndo;
                }
            }
            return AppConfig.DIR_DATA.resolve("materi").resolve(kursus.getKode().toLowerCase());
        }
        return AppConfig.DIR_DATA.resolve("materi");
    }

    public TextMaterial buatMateriTeksBaru(Course kursus, String judul, String isiKonten) throws IOException {
        String idMateri = kursus.getKode().toLowerCase() + "-m" + (System.currentTimeMillis() % 100000);
        Path folderKursus = tentukanFolderMateri(kursus);
        Files.createDirectories(folderKursus);
        Path fileTarget = folderKursus.resolve(idMateri + ".md");
        Files.writeString(fileTarget, isiKonten, StandardCharsets.UTF_8);
        return new TextMaterial(idMateri, judul, fileTarget);
    }

    public Exam ujianDari(ExamMaterial materi) {
        return ujianService.cariUjian(materi.getKodeUjian()).orElseThrow();
    }

    public Exam ujianDari(QuizMaterial materi) {
        return ujianService.cariUjian(materi.getKodeUjian()).orElseThrow();
    }

    public int getJumlahKursus() {
        return katalog.size();
    }

    public int getJumlahMateri() {
        return getJumlahMateriSemuaKursus();
    }

    public int getJumlahMateriSemuaKursus() {
        return katalog.values().stream()
                .mapToInt(Course::getJumlahMateri)
                .sum();
    }
}
