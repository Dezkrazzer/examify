package com.examify.io;

import com.examify.exception.QuestionBankFormatException;
import com.examify.model.Question;
import com.examify.model.Exam;

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

/** Membaca bank_soal.txt dengan state machine blok [UJIAN] / [SOAL] / [END]. */
public class QuestionBankReader implements DataReader<Exam> {

    private static final String TANDA_UJIAN = "[UJIAN]";
    private static final String TANDA_SOAL = "[SOAL]";
    private static final String TANDA_END = "[END]";

    private enum Posisi { LUAR_BLOK, HEADER, SOAL }

    private final QuestionBankParser parser = new QuestionBankParser();

    @Override
    public List<Exam> baca(Path path) throws IOException, QuestionBankFormatException {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return baca(br);
        }
    }

    List<Exam> baca(BufferedReader br) throws IOException, QuestionBankFormatException {
        List<Exam> daftarUjian = new ArrayList<>();
        Set<String> kodeTerpakai = new HashSet<>();
        Map<String, String> header = new HashMap<>();
        List<Question> daftarSoal = new ArrayList<>();
        Posisi posisi = Posisi.LUAR_BLOK;
        int noBaris = 0;
        String baris;

        while ((baris = br.readLine()) != null) {
            noBaris++;
            if (DataLine.dilewati(baris)) {
                continue;
            }
            String isi = baris.trim();
            switch (posisi) {
                case LUAR_BLOK -> {
                    if (!isi.equalsIgnoreCase(TANDA_UJIAN)) {
                        throw new QuestionBankFormatException(noBaris, baris, "diharapkan " + TANDA_UJIAN);
                    }
                    header = new HashMap<>();
                    daftarSoal = new ArrayList<>();
                    posisi = Posisi.HEADER;
                }
                case HEADER -> {
                    if (isi.equalsIgnoreCase(TANDA_SOAL)) {
                        parser.validasiHeader(header, noBaris, baris);
                        if (!kodeTerpakai.add(QuestionBankParser.kodeUjian(header))) {
                            throw new QuestionBankFormatException(noBaris, baris,
                                    "KODE ujian duplikat: " + QuestionBankParser.kodeUjian(header));
                        }
                        posisi = Posisi.SOAL;
                    } else {
                        parser.parseHeader(isi, noBaris, header);
                    }
                }
                case SOAL -> {
                    if (isi.equalsIgnoreCase(TANDA_END)) {
                        daftarUjian.add(parser.buatUjian(header, daftarSoal, noBaris, baris));
                        posisi = Posisi.LUAR_BLOK;
                    } else {
                        tambahSoal(daftarSoal, parser.parseBaris(isi, noBaris), noBaris, baris);
                    }
                }
            }
        }

        if (posisi != Posisi.LUAR_BLOK) {
            throw new QuestionBankFormatException(noBaris, "<akhir file>", "blok " + TANDA_UJIAN
                    + " tidak ditutup dengan " + TANDA_END);
        }
        if (daftarUjian.isEmpty()) {
            throw new QuestionBankFormatException(noBaris, "<akhir file>", "bank soal tidak berisi ujian");
        }
        return daftarUjian;
    }

    private static void tambahSoal(List<Question> daftarSoal, Question soal, int noBaris, String baris)
            throws QuestionBankFormatException {
        boolean duplikat = daftarSoal.stream().anyMatch(s -> s.getIdSoal() == soal.getIdSoal());
        if (duplikat) {
            throw new QuestionBankFormatException(noBaris, baris, "id soal duplikat: " + soal.getIdSoal());
        }
        daftarSoal.add(soal);
    }
}
