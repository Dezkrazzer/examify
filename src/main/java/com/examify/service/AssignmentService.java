package com.examify.service;

import com.examify.model.AssignmentSubmission;
import com.examify.model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Layanan pengelolaan tugas dan berkas pengumpulan tugas siswa. */
public class AssignmentService {

    private final Path filePengumpulan;
    private final Path folderBerkas;
    private final Map<String, List<AssignmentSubmission>> pengumpulanPerTugas = new LinkedHashMap<>();

    public AssignmentService(Path filePengumpulan, Path folderBerkas) {
        this.filePengumpulan = filePengumpulan;
        this.folderBerkas = folderBerkas;
        muatData();
    }

    private synchronized void muatData() {
        pengumpulanPerTugas.clear();
        if (folderBerkas != null && !Files.exists(folderBerkas)) {
            try {
                Files.createDirectories(folderBerkas);
            } catch (IOException ignored) {
            }
        }
        if (filePengumpulan == null || !Files.exists(filePengumpulan)) {
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(filePengumpulan, StandardCharsets.UTF_8)) {
            String baris;
            while ((baris = br.readLine()) != null) {
                if (baris.isBlank() || baris.startsWith("#")) continue;
                String[] f = baris.split("\\|", -1);
                if (f.length >= 8) {
                    String idPengumpulan = f[0];
                    String idTugas = f[1];
                    String nis = f[2];
                    String namaSiswa = f[3];
                    String kelasSiswa = f[4];
                    LocalDateTime waktu = null;
                    try {
                        waktu = LocalDateTime.parse(f[5], AssignmentSubmission.FORMAT_WAKTU);
                    } catch (Exception ignored) {
                    }
                    String namaBerkas = f[6];
                    String pathBerkas = f[7];
                    String catatan = f.length >= 9 ? f[8].replace("\\n", "\n") : "";

                    AssignmentSubmission pt = new AssignmentSubmission(idPengumpulan, idTugas, nis, namaSiswa,
                            kelasSiswa, waktu, namaBerkas, pathBerkas, catatan);
                    pengumpulanPerTugas.computeIfAbsent(idTugas, k -> new ArrayList<>()).add(pt);
                }
            }
        } catch (IOException e) {
            System.err.println("[TugasService] Gagal membaca data pengumpulan: " + e.getMessage());
        }
    }

    public synchronized Optional<AssignmentSubmission> cariPengumpulan(String idTugas, String nis) {
        if (idTugas == null || nis == null) return Optional.empty();
        List<AssignmentSubmission> list = pengumpulanPerTugas.get(idTugas);
        if (list == null) return Optional.empty();
        return list.stream().filter(p -> p.getNis().equals(nis)).findFirst();
    }

    public synchronized List<AssignmentSubmission> getDaftarPengumpulan(String idTugas) {
        if (idTugas == null) return Collections.emptyList();
        return Collections.unmodifiableList(pengumpulanPerTugas.getOrDefault(idTugas, Collections.emptyList()));
    }

    public synchronized AssignmentSubmission simpanPengumpulan(String idTugas, Student siswa, File fileSumber, String catatan)
            throws IOException {
        if (fileSumber == null || !fileSumber.exists()) {
            throw new IllegalArgumentException("Berkas sumber tidak ditemukan.");
        }
        if (folderBerkas != null && !Files.exists(folderBerkas)) {
            Files.createDirectories(folderBerkas);
        }

        String namaAsli = fileSumber.getName();
        String ekstensi = "";
        int idx = namaAsli.lastIndexOf('.');
        if (idx >= 0) {
            ekstensi = namaAsli.substring(idx);
        }

        String namaTersimpan = idTugas + "_" + siswa.getNis() + "_" + System.currentTimeMillis() + ekstensi;
        Path targetPath = folderBerkas.resolve(namaTersimpan);
        Files.copy(fileSumber.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        String idPengumpulan = "PENG-" + System.currentTimeMillis();
        AssignmentSubmission baru = new AssignmentSubmission(
                idPengumpulan,
                idTugas,
                siswa.getNis(),
                siswa.getNama(),
                siswa.getKelas(),
                LocalDateTime.now(),
                namaAsli,
                targetPath.toString(),
                catatan == null ? "" : catatan.trim()
        );

        List<AssignmentSubmission> list = pengumpulanPerTugas.computeIfAbsent(idTugas, k -> new ArrayList<>());
        list.removeIf(p -> p.getNis().equals(siswa.getNis()));
        list.add(baru);

        simpanKeFile();
        return baru;
    }

    private synchronized void simpanKeFile() throws IOException {
        if (filePengumpulan == null) return;
        if (filePengumpulan.getParent() != null) {
            Files.createDirectories(filePengumpulan.getParent());
        }
        try (BufferedWriter bw = Files.newBufferedWriter(filePengumpulan, StandardCharsets.UTF_8)) {
            bw.write("# ID|ID_TUGAS|NIS|NAMA|KELAS|WAKTU|NAMA_BERKAS|PATH|CATATAN");
            bw.newLine();
            for (List<AssignmentSubmission> list : pengumpulanPerTugas.values()) {
                for (AssignmentSubmission p : list) {
                    String waktuStr = p.getWaktuKumpul() != null
                            ? p.getWaktuKumpul().format(AssignmentSubmission.FORMAT_WAKTU) : "";
                    String catatanEscaped = p.getCatatan().replace("\r\n", "\n").replace("\n", "\\n");
                    bw.write(String.join("|",
                            p.getIdPengumpulan(),
                            p.getIdTugas(),
                            p.getNis(),
                            p.getNamaSiswa(),
                            p.getKelasSiswa(),
                            waktuStr,
                            p.getNamaBerkas(),
                            p.getPathBerkas(),
                            catatanEscaped
                    ));
                    bw.newLine();
                }
            }
        }
    }
}
