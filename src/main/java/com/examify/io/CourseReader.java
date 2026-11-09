package com.examify.io;

import com.examify.exception.DataFormatException;
import com.examify.model.Chapter;
import com.examify.model.MaterialType;
import com.examify.model.Course;
import com.examify.model.Material;
import com.examify.model.PdfMaterial;
import com.examify.model.QuizMaterial;
import com.examify.model.AssignmentMaterial;
import com.examify.model.LinkMaterial;
import com.examify.model.TextMaterial;
import com.examify.model.ExamMaterial;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Membaca kursus.txt. Satu blok [KURSUS] berisi header KUNCI=NILAI, lalu satu atau lebih
 * "[BAB] Judul" yang masing-masing diikuti baris materi JENIS|id|judul|sumber, dan ditutup [END].
 */
public class CourseReader implements DataReader<Course> {

    private static final String TANDA_KURSUS = "[KURSUS]";
    private static final String TANDA_BAB = "[BAB]";
    private static final String TANDA_END = "[END]";
    private static final int JUMLAH_FIELD_MATERI = 4;
    private static final Set<String> KUNCI_HEADER =
            Set.of("KODE", "NAMA", "PENGAJAR", "NIP_PEMBUAT", "DESKRIPSI");

    /** Penampung sementara satu blok kursus yang sedang dibaca. */
    private static final class DrafKursus {
        final Map<String, String> header = new HashMap<>();
        final List<Chapter> daftarBab = new ArrayList<>();
        String judulBab;
        List<Material> materiBab;

        void tutupBab() {
            if (judulBab != null) {
                daftarBab.add(new Chapter(daftarBab.size() + 1, judulBab, materiBab));
            }
        }
    }

    private final Path folderSumber;

    /** @param folderSumber folder acuan path relatif materi TEKS dan PDF */
    public CourseReader(Path folderSumber) {
        this.folderSumber = folderSumber;
    }

    @Override
    public List<Course> baca(Path path) throws IOException, DataFormatException {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return baca(br);
        }
    }

    List<Course> baca(BufferedReader br) throws IOException, DataFormatException {
        List<Course> daftarKursus = new ArrayList<>();
        Set<String> kodeTerpakai = new HashSet<>();
        DrafKursus draf = null;
        int noBaris = 0;
        String baris;

        while ((baris = br.readLine()) != null) {
            noBaris++;
            if (DataLine.dilewati(baris)) {
                continue;
            }
            String isi = baris.trim();
            if (draf == null) {
                if (!isi.equalsIgnoreCase(TANDA_KURSUS)) {
                    throw new DataFormatException(noBaris, baris, "diharapkan " + TANDA_KURSUS);
                }
                draf = new DrafKursus();
            } else if (isi.equalsIgnoreCase(TANDA_END)) {
                Course kursus = buatKursus(draf, noBaris, baris);
                if (!kodeTerpakai.add(kursus.getKode())) {
                    throw new DataFormatException(noBaris, baris, "kode kursus duplikat: " + kursus.getKode());
                }
                daftarKursus.add(kursus);
                draf = null;
            } else if (isi.toUpperCase().startsWith(TANDA_BAB)) {
                draf.tutupBab();
                draf.judulBab = isi.substring(TANDA_BAB.length()).trim();
                draf.materiBab = new ArrayList<>();
            } else if (draf.judulBab == null) {
                parseHeader(draf.header, isi, noBaris, baris);
            } else {
                draf.materiBab.add(parseMateri(isi, noBaris, baris));
            }
        }
        if (draf != null) {
            throw new DataFormatException(noBaris, "<akhir file>", "blok " + TANDA_KURSUS + " tidak ditutup " + TANDA_END);
        }
        return daftarKursus;
    }

    private static void parseHeader(Map<String, String> header, String isi, int noBaris, String baris)
            throws DataFormatException {
        String[] kunciNilai = DataLine.pecahKunciNilai(isi);
        if (kunciNilai == null || !KUNCI_HEADER.contains(kunciNilai[0])) {
            throw new DataFormatException(noBaris, baris, "header kursus harus salah satu dari " + KUNCI_HEADER);
        }
        header.put(kunciNilai[0], kunciNilai[1]);
    }

    Material parseMateri(String isi, int noBaris, String baris) throws DataFormatException {
        String[] f = DataLine.pecahField(isi);
        if (f.length < 4) {
            throw new DataFormatException(noBaris, baris,
                    "jumlah field materi minimal 4, ditemukan " + f.length);
        }
        MaterialType jenis = MaterialType.dariKode(f[0]);
        if (jenis == null) {
            throw new DataFormatException(noBaris, baris, "jenis materi tidak dikenal: " + f[0]);
        }
        if (jenis != MaterialType.TUGAS && f.length != JUMLAH_FIELD_MATERI) {
            throw new DataFormatException(noBaris, baris,
                    "jumlah field materi harus " + JUMLAH_FIELD_MATERI + ", ditemukan " + f.length);
        }
        try {
            return switch (jenis) {
                case TEKS -> new TextMaterial(f[1], f[2], fileAda(f[3], noBaris, baris));
                case PDF -> new PdfMaterial(f[1], f[2], fileAda(f[3], noBaris, baris));
                case LINK -> new LinkMaterial(f[1], f[2], f[3]);
                case UJIAN -> new ExamMaterial(f[1], f[2], f[3]);
                case QUIZ -> new QuizMaterial(f[1], f[2], f[3]);
                case TUGAS -> {
                    String deadlineStr = f[3];
                    String targetKelas = f.length > 4 ? f[4] : "Semua Kelas";
                    String deskripsi = f.length > 5 ? f[5].replace("\\n", "\n") : "";
                    yield new AssignmentMaterial(f[1], f[2], deadlineStr, targetKelas, deskripsi);
                }
            };
        } catch (IllegalArgumentException e) {
            throw new DataFormatException(noBaris, baris, e.getMessage(), e);
        }
    }

    private Path fileAda(String relatif, int noBaris, String baris) throws DataFormatException {
        Path file = folderSumber.resolve(relatif).normalize();
        if (!Files.isRegularFile(file)) {
            throw new DataFormatException(noBaris, baris, "file materi tidak ditemukan: " + file);
        }
        return file;
    }

    private static Course buatKursus(DrafKursus draf, int noBaris, String baris) throws DataFormatException {
        draf.tutupBab();
        try {
            return new Course(
                    draf.header.get("KODE"),
                    draf.header.get("NAMA"),
                    draf.header.get("PENGAJAR"),
                    draf.header.getOrDefault("NIP_PEMBUAT", ""),
                    draf.header.get("DESKRIPSI"),
                    draf.daftarBab
            );
        } catch (IllegalArgumentException e) {
            throw new DataFormatException(noBaris, baris, e.getMessage(), e);
        }
    }
}
