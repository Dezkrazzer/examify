package com.examify.io;

import com.examify.exception.FormatDataException;
import com.examify.model.Bab;
import com.examify.model.JenisMateri;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriPdf;
import com.examify.model.MateriQuiz;
import com.examify.model.MateriTugas;
import com.examify.model.MateriTautan;
import com.examify.model.MateriTeks;
import com.examify.model.MateriUjian;

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
public class KursusReader implements DataReader<Kursus> {

    private static final String TANDA_KURSUS = "[KURSUS]";
    private static final String TANDA_BAB = "[BAB]";
    private static final String TANDA_END = "[END]";
    private static final int JUMLAH_FIELD_MATERI = 4;
    private static final Set<String> KUNCI_HEADER = Set.of("KODE", "NAMA", "PENGAJAR", "DESKRIPSI");

    /** Penampung sementara satu blok kursus yang sedang dibaca. */
    private static final class DrafKursus {
        final Map<String, String> header = new HashMap<>();
        final List<Bab> daftarBab = new ArrayList<>();
        String judulBab;
        List<Materi> materiBab;

        void tutupBab() {
            if (judulBab != null) {
                daftarBab.add(new Bab(daftarBab.size() + 1, judulBab, materiBab));
            }
        }
    }

    private final Path folderSumber;

    /** @param folderSumber folder acuan path relatif materi TEKS dan PDF */
    public KursusReader(Path folderSumber) {
        this.folderSumber = folderSumber;
    }

    @Override
    public List<Kursus> baca(Path path) throws IOException, FormatDataException {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return baca(br);
        }
    }

    List<Kursus> baca(BufferedReader br) throws IOException, FormatDataException {
        List<Kursus> daftarKursus = new ArrayList<>();
        Set<String> kodeTerpakai = new HashSet<>();
        DrafKursus draf = null;
        int noBaris = 0;
        String baris;

        while ((baris = br.readLine()) != null) {
            noBaris++;
            if (BarisData.dilewati(baris)) {
                continue;
            }
            String isi = baris.trim();
            if (draf == null) {
                if (!isi.equalsIgnoreCase(TANDA_KURSUS)) {
                    throw new FormatDataException(noBaris, baris, "diharapkan " + TANDA_KURSUS);
                }
                draf = new DrafKursus();
            } else if (isi.equalsIgnoreCase(TANDA_END)) {
                Kursus kursus = buatKursus(draf, noBaris, baris);
                if (!kodeTerpakai.add(kursus.getKode())) {
                    throw new FormatDataException(noBaris, baris, "kode kursus duplikat: " + kursus.getKode());
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
            throw new FormatDataException(noBaris, "<akhir file>", "blok " + TANDA_KURSUS + " tidak ditutup " + TANDA_END);
        }
        return daftarKursus;
    }

    private static void parseHeader(Map<String, String> header, String isi, int noBaris, String baris)
            throws FormatDataException {
        String[] kunciNilai = BarisData.pecahKunciNilai(isi);
        if (kunciNilai == null || !KUNCI_HEADER.contains(kunciNilai[0])) {
            throw new FormatDataException(noBaris, baris, "header kursus harus salah satu dari " + KUNCI_HEADER);
        }
        header.put(kunciNilai[0], kunciNilai[1]);
    }

    Materi parseMateri(String isi, int noBaris, String baris) throws FormatDataException {
        String[] f = BarisData.pecahField(isi);
        if (f.length < 4) {
            throw new FormatDataException(noBaris, baris,
                    "jumlah field materi minimal 4, ditemukan " + f.length);
        }
        JenisMateri jenis = JenisMateri.dariKode(f[0]);
        if (jenis == null) {
            throw new FormatDataException(noBaris, baris, "jenis materi tidak dikenal: " + f[0]);
        }
        if (jenis != JenisMateri.TUGAS && f.length != JUMLAH_FIELD_MATERI) {
            throw new FormatDataException(noBaris, baris,
                    "jumlah field materi harus " + JUMLAH_FIELD_MATERI + ", ditemukan " + f.length);
        }
        try {
            return switch (jenis) {
                case TEKS -> new MateriTeks(f[1], f[2], fileAda(f[3], noBaris, baris));
                case PDF -> new MateriPdf(f[1], f[2], fileAda(f[3], noBaris, baris));
                case LINK -> new MateriTautan(f[1], f[2], f[3]);
                case UJIAN -> new MateriUjian(f[1], f[2], f[3]);
                case QUIZ -> new MateriQuiz(f[1], f[2], f[3]);
                case TUGAS -> {
                    String deadlineStr = f[3];
                    String targetKelas = f.length > 4 ? f[4] : "Semua Kelas";
                    String deskripsi = f.length > 5 ? f[5].replace("\\n", "\n") : "";
                    yield new MateriTugas(f[1], f[2], deadlineStr, targetKelas, deskripsi);
                }
            };
        } catch (IllegalArgumentException e) {
            throw new FormatDataException(noBaris, baris, e.getMessage(), e);
        }
    }

    private Path fileAda(String relatif, int noBaris, String baris) throws FormatDataException {
        Path file = folderSumber.resolve(relatif).normalize();
        if (!Files.isRegularFile(file)) {
            throw new FormatDataException(noBaris, baris, "file materi tidak ditemukan: " + file);
        }
        return file;
    }

    private static Kursus buatKursus(DrafKursus draf, int noBaris, String baris) throws FormatDataException {
        draf.tutupBab();
        try {
            return new Kursus(draf.header.get("KODE"), draf.header.get("NAMA"),
                    draf.header.get("PENGAJAR"), draf.header.get("DESKRIPSI"), draf.daftarBab);
        } catch (IllegalArgumentException e) {
            throw new FormatDataException(noBaris, baris, e.getMessage(), e);
        }
    }
}
