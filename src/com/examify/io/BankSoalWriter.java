package com.examify.io;

import com.examify.model.Soal;
import com.examify.model.SoalIsian;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Menulis kembali daftar ujian dan bank soal ke format bank_soal.txt UTF-8. */
public class BankSoalWriter {

    public void tulis(Path target, List<Ujian> daftarUjian) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            bw.write("# =====================================================================");
            bw.newLine();
            bw.write("# Examify - Bank Soal CBT                   Encoding: UTF-8");
            bw.newLine();
            bw.write("# =====================================================================");
            bw.newLine();
            bw.newLine();

            for (Ujian ujian : daftarUjian) {
                bw.write("[UJIAN]");
                bw.newLine();
                bw.write("KODE=" + ujian.getKodeUjian());
                bw.newLine();
                bw.write("MAPEL=" + ujian.getMataPelajaran());
                bw.newLine();
                bw.write("TOKEN=" + ujian.getToken());
                bw.newLine();
                bw.write("DURASI=" + ujian.getDurasiMenit());
                bw.newLine();
                bw.write("KKM=" + (ujian.getKkm() == (long) ujian.getKkm() ? String.format("%d", (long) ujian.getKkm()) : String.valueOf(ujian.getKkm())));
                bw.newLine();
                if (ujian.isBolehUlang()) {
                    bw.write("ULANG=true");
                    bw.newLine();
                }
                bw.write("[SOAL]");
                bw.newLine();
                for (Soal soal : ujian.getDaftarSoal()) {
                    String barisSoal = formatSoal(soal);
                    if (barisSoal != null) {
                        bw.write(barisSoal);
                        bw.newLine();
                    }
                }
                bw.write("[END]");
                bw.newLine();
                bw.newLine();
            }
        }
    }

    private String formatSoal(Soal soal) {
        String bobotStr = soal.getBobot() == (long) soal.getBobot() ? String.format("%d", (long) soal.getBobot()) : String.valueOf(soal.getBobot());
        if (soal instanceof SoalPilihanGanda pg) {
            String opsiGabung = String.join(";", pg.getOpsi());
            return "PG|" + pg.getIdSoal() + "|" + pg.getPertanyaan() + "|" + opsiGabung + "|" + pg.getKunciRaw() + "|" + bobotStr;
        } else if (soal instanceof SoalTrueFalse tf) {
            return "TF|" + tf.getIdSoal() + "|" + tf.getPertanyaan() + "|" + tf.getKunciRaw() + "|" + bobotStr;
        } else if (soal instanceof SoalIsian is) {
            String kunciGabung = String.join(";", is.getKunciJawaban());
            return "IS|" + is.getIdSoal() + "|" + is.getPertanyaan() + "|" + kunciGabung + "|" + bobotStr;
        }
        return null;
    }
}
