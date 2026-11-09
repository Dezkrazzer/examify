package com.examify.io;

import com.examify.exception.QuestionBankFormatException;
import com.examify.model.QuestionType;
import com.examify.model.Question;
import com.examify.model.ShortAnswerQuestion;
import com.examify.model.MultipleChoiceQuestion;
import com.examify.model.TrueFalseQuestion;
import com.examify.model.Exam;
import com.examify.utils.FormatUtil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parsing dan validasi baris individual bank_soal.txt.
 * Parser ini stateless sehingga aman dipakai bersamaan.
 */
public class QuestionBankParser {

    static final String KODE = "KODE";
    static final String MAPEL = "MAPEL";
    static final String TOKEN = "TOKEN";
    static final String DURASI = "DURASI";
    static final String KKM = "KKM";
    static final String ULANG = "ULANG"; // opsional, bawaan false
    static final String STRICTLY_SAFE = "STRICTLY_SAFE"; // opsional, bawaan false
    static final String AKSES_MULAI = "AKSES_MULAI";
    static final String AKSES_SELESAI = "AKSES_SELESAI";
    private static final List<String> KUNCI_WAJIB = List.of(KODE, MAPEL, TOKEN, DURASI, KKM);
    private static final List<String> KUNCI_OPSIONAL = List.of(ULANG, STRICTLY_SAFE, AKSES_MULAI, AKSES_SELESAI);

    private static final Pattern POLA_KODE = Pattern.compile("^[A-Z]{2,5}-\\d{2}$");
    private static final Pattern POLA_TOKEN = Pattern.compile("^[A-Z0-9]{6}$");

    /** Membaca satu baris KUNCI=NILAI ke dalam map header. */
    public void parseHeader(String baris, int noBaris, Map<String, String> header) throws QuestionBankFormatException {
        String[] pasang = DataLine.pecahKunciNilai(baris);
        if (pasang == null) {
            throw new QuestionBankFormatException(noBaris, baris, "header harus berformat KUNCI=NILAI");
        }
        String kunci = pasang[0];
        String nilai = pasang[1];
        if (!KUNCI_WAJIB.contains(kunci) && !KUNCI_OPSIONAL.contains(kunci)) {
            throw new QuestionBankFormatException(noBaris, baris,
                    "kunci header tidak dikenal: " + kunci + " (harus " + KUNCI_WAJIB + ")");
        }
        if (header.putIfAbsent(kunci, nilai) != null) {
            throw new QuestionBankFormatException(noBaris, baris, "kunci header duplikat: " + kunci);
        }
    }

    /** Memastikan semua kunci header wajib ada dan nilainya sesuai data contract. */
    public void validasiHeader(Map<String, String> header, int noBaris, String baris) throws QuestionBankFormatException {
        for (String kunci : KUNCI_WAJIB) {
            if (header.getOrDefault(kunci, "").isEmpty()) {
                throw new QuestionBankFormatException(noBaris, baris, "header " + kunci + " wajib diisi");
            }
        }
        if (!POLA_KODE.matcher(header.get(KODE).toUpperCase()).matches()) {
            throw new QuestionBankFormatException(noBaris, baris, "KODE harus berpola AAA-00: " + header.get(KODE));
        }
        if (!POLA_TOKEN.matcher(header.get(TOKEN).toUpperCase()).matches()) {
            throw new QuestionBankFormatException(noBaris, baris, "TOKEN harus 6 karakter huruf/angka");
        }
        if (header.containsKey(ULANG) && DataLine.parseBoolean(header.get(ULANG)) == null) {
            throw new QuestionBankFormatException(noBaris, baris, "ULANG harus true atau false");
        }
        if (header.containsKey(STRICTLY_SAFE) && DataLine.parseBoolean(header.get(STRICTLY_SAFE)) == null) {
            throw new QuestionBankFormatException(noBaris, baris, "STRICTLY_SAFE harus true atau false");
        }
    }

    public Exam buatUjian(Map<String, String> header, List<Question> daftarSoal, int noBaris, String baris)
            throws QuestionBankFormatException {
        try {
            LocalDateTime mulai = FormatUtil.parseInputAkses(header.get(AKSES_MULAI));
            LocalDateTime selesai = FormatUtil.parseInputAkses(header.get(AKSES_SELESAI));
            return new Exam(header.get(KODE), header.get(MAPEL), header.get(TOKEN),
                    Integer.parseInt(header.get(DURASI)), Double.parseDouble(header.get(KKM)), daftarSoal,
                    Boolean.TRUE.equals(DataLine.parseBoolean(header.getOrDefault(ULANG, "false"))),
                    mulai, selesai,
                    Boolean.TRUE.equals(DataLine.parseBoolean(header.getOrDefault(STRICTLY_SAFE, "false"))));
        } catch (NumberFormatException e) {
            throw new QuestionBankFormatException(noBaris, baris, "DURASI/KKM harus berupa angka", e);
        } catch (IllegalArgumentException e) {
            throw new QuestionBankFormatException(noBaris, baris, e.getMessage(), e);
        }
    }

    public Question parseBaris(String baris, int noBaris) throws QuestionBankFormatException {
        String[] f = DataLine.pecahField(baris);
        QuestionType jenis = QuestionType.dariKode(f[0]);
        if (jenis == null) {
            throw new QuestionBankFormatException(noBaris, baris, "jenis soal tidak dikenal: " + f[0]);
        }
        if (f.length != jenis.getJumlahField()) {
            throw new QuestionBankFormatException(noBaris, baris, "jumlah field " + jenis
                    + " harus " + jenis.getJumlahField() + ", ditemukan " + f.length);
        }
        // NumberFormatException adalah subclass IllegalArgumentException, jadi ditangkap lebih dulu
        try {
            int id = Integer.parseInt(f[1]);
            String pertanyaan = f[2];
            return switch (jenis) {
                case PG -> new MultipleChoiceQuestion(id, pertanyaan, Double.parseDouble(f[5]),
                        DataLine.pecahSubNilai(f[3]), satuHuruf(f[4], noBaris, baris));
                case TF -> new TrueFalseQuestion(id, pertanyaan, Double.parseDouble(f[4]),
                        satuHuruf(f[3], noBaris, baris));
                case IS -> new ShortAnswerQuestion(id, pertanyaan, Double.parseDouble(f[4]),
                        new HashSet<>(DataLine.pecahSubNilai(f[3])));
            };
        } catch (NumberFormatException e) {
            throw new QuestionBankFormatException(noBaris, baris, "id/bobot harus berupa angka", e);
        } catch (IllegalArgumentException e) {
            throw new QuestionBankFormatException(noBaris, baris, e.getMessage(), e);
        }
    }

    private static char satuHuruf(String kunci, int noBaris, String baris) throws QuestionBankFormatException {
        if (kunci.length() != 1) {
            throw new QuestionBankFormatException(noBaris, baris, "kunci harus tepat 1 huruf: \"" + kunci + "\"");
        }
        return Character.toUpperCase(kunci.charAt(0));
    }

    /** Dipakai reader untuk mendeteksi KODE ganda antar blok. */
    static String kodeUjian(Map<String, String> header) {
        return header.get(KODE).toUpperCase();
    }
}
