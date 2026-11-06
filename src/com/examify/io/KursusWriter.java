package com.examify.io;

import com.examify.model.Bab;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriPdf;
import com.examify.model.MateriQuiz;
import com.examify.model.MateriTugas;
import com.examify.model.MateriTautan;
import com.examify.model.MateriTeks;
import com.examify.model.MateriUjian;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Menulis kembali daftar kursus ke format kursus.txt UTF-8. */
public class KursusWriter {

    private final Path folderData;

    public KursusWriter(Path folderData) {
        this.folderData = folderData;
    }

    public void tulis(Path target, List<Kursus> daftarKursus) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            bw.write("# =====================================================================");
            bw.newLine();
            bw.write("# Examify - Kursus E-Learning               Encoding: UTF-8");
            bw.newLine();
            bw.write("# =====================================================================");
            bw.newLine();
            bw.newLine();

            for (Kursus kursus : daftarKursus) {
                bw.write("[KURSUS]");
                bw.newLine();
                bw.write("KODE=" + kursus.getKode());
                bw.newLine();
                bw.write("NAMA=" + kursus.getNama());
                bw.newLine();
                bw.write("PENGAJAR=" + kursus.getPengajar());
                bw.newLine();
                bw.write("DESKRIPSI=" + kursus.getDeskripsi());
                bw.newLine();

                for (Bab bab : kursus.getDaftarBab()) {
                    bw.write("[BAB] " + bab.getJudul());
                    bw.newLine();
                    for (Materi m : bab.getDaftarMateri()) {
                        String barisMateri = formatMateri(m);
                        if (barisMateri != null) {
                            bw.write(barisMateri);
                            bw.newLine();
                        }
                    }
                }
                bw.write("[END]");
                bw.newLine();
                bw.newLine();
            }
        }
    }

    private String formatMateri(Materi m) {
        if (m instanceof MateriTeks mt) {
            String pathStr = hitungRelatifData(mt.getFile());
            return "TEKS|" + mt.getIdMateri() + "|" + mt.getJudul() + "|" + pathStr;
        } else if (m instanceof MateriTautan ml) {
            return "LINK|" + ml.getIdMateri() + "|" + ml.getJudul() + "|" + ml.getAlamat();
        } else if (m instanceof MateriPdf mp) {
            String pathStr = hitungRelatifData(mp.getFile());
            return "PDF|" + mp.getIdMateri() + "|" + mp.getJudul() + "|" + pathStr;
        } else if (m instanceof MateriUjian mu) {
            return "UJIAN|" + mu.getIdMateri() + "|" + mu.getJudul() + "|" + mu.getKodeUjian();
        } else if (m instanceof MateriQuiz mq) {
            return "QUIZ|" + mq.getIdMateri() + "|" + mq.getJudul() + "|" + mq.getKodeUjian();
        } else if (m instanceof MateriTugas mt) {
            String deskripsiEscaped = mt.getDeskripsi() == null ? ""
                    : mt.getDeskripsi().replace("\r\n", "\n").replace("\n", "\\n");
            return "TUGAS|" + mt.getIdMateri() + "|" + mt.getJudul() + "|" + mt.getDeadlineStr() + "|"
                    + mt.getTargetKelas() + "|" + deskripsiEscaped;
        }
        return null;
    }

    private String hitungRelatifData(Path file) {
        try {
            Path absFile = file.toAbsolutePath().normalize();
            Path absData = folderData.toAbsolutePath().normalize();
            if (absFile.startsWith(absData)) {
                return absData.relativize(absFile).toString().replace('\\', '/');
            }
            return file.toString().replace('\\', '/');
        } catch (Exception e) {
            return file.toString().replace('\\', '/');
        }
    }
}
