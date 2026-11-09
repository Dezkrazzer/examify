package com.examify.io;

import com.examify.model.Question;
import com.examify.model.ShortAnswerQuestion;
import com.examify.model.MultipleChoiceQuestion;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import com.examify.utils.FormatUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Menulis kembali daftar ujian dan bank soal ke format bank_soal.txt UTF-8. */
public class QuestionBankWriter {

    public void tulis(Path target, List<Exam> daftarUjian) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            bw.write("# =====================================================================");
            bw.newLine();
            bw.write("# Examify - Bank Soal CBT                   Encoding: UTF-8");
            bw.newLine();
            bw.write("# =====================================================================");
            bw.newLine();
            bw.newLine();

            for (Exam ujian : daftarUjian) {
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
                if (ujian.isModeAman()) {
                    bw.write("STRICTLY_SAFE=true");
                    bw.newLine();
                }
                if (ujian.getWaktuMulai() != null) {
                    bw.write("AKSES_MULAI=" + FormatUtil.formatInputAkses(ujian.getWaktuMulai()));
                    bw.newLine();
                }
                if (ujian.getWaktuSelesai() != null) {
                    bw.write("AKSES_SELESAI=" + FormatUtil.formatInputAkses(ujian.getWaktuSelesai()));
                    bw.newLine();
                }
                bw.write("[SOAL]");
                bw.newLine();
                for (Question soal : ujian.getDaftarSoal()) {
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

    private String formatSoal(Question soal) {
        String bobotStr = soal.getBobot() == (long) soal.getBobot() ? String.format("%d", (long) soal.getBobot()) : String.valueOf(soal.getBobot());
        if (soal instanceof MultipleChoiceQuestion pg) {
            String opsiGabung = String.join(";", pg.getOpsi());
            return "PG|" + pg.getIdSoal() + "|" + pg.getPertanyaan() + "|" + opsiGabung + "|" + pg.getKunciRaw() + "|" + bobotStr;
        } else if (soal instanceof TrueFalseQuestion tf) {
            return "TF|" + tf.getIdSoal() + "|" + tf.getPertanyaan() + "|" + tf.getKunciRaw() + "|" + bobotStr;
        } else if (soal instanceof ShortAnswerQuestion is) {
            String kunciGabung = String.join(";", is.getKunciJawaban());
            return "IS|" + is.getIdSoal() + "|" + is.getPertanyaan() + "|" + kunciGabung + "|" + bobotStr;
        }
        return null;
    }
}
