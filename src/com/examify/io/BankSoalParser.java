package com.examify.io;

import com.examify.exception.FormatBankSoalException;
import com.examify.model.JenisSoal;
import com.examify.model.Soal;
import com.examify.model.SoalIsian;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
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
public class BankSoalParser {

    static final String KODE = "KODE";
    static final String MAPEL = "MAPEL";
    static final String TOKEN = "TOKEN";
    static final String DURASI = "DURASI";
    static final String KKM = "KKM";
    static final String ULANG = "ULANG"; // opsional, bawaan false
    static final String AKSES_MULAI = "AKSES_MULAI";
    static final String AKSES_SELESAI = "AKSES_SELESAI";
    private static final List<String> KUNCI_WAJIB = List.of(KODE, MAPEL, TOKEN, DURASI, KKM);
    private static final List<String> KUNCI_OPSIONAL = List.of(ULANG, AKSES_MULAI, AKSES_SELESAI);

    private static final Pattern POLA_KODE = Pattern.compile("^[A-Z]{2,5}-\\d{2}$");
    private static final Pattern POLA_TOKEN = Pattern.compile("^[A-Z0-9]{6}$");

    /** Membaca satu baris KUNCI=NILAI ke dalam map header. */
    public void parseHeader(String baris, int noBaris, Map<String, String> header) throws FormatBankSoalException {
        String[] pasang = BarisData.pecahKunciNilai(baris);
        if (pasang == null) {
            throw new FormatBankSoalException(noBaris, baris, "header harus berformat KUNCI=NILAI");
        }
        String kunci = pasang[0];
        String nilai = pasang[1];
        if (!KUNCI_WAJIB.contains(kunci) && !KUNCI_OPSIONAL.contains(kunci)) {
            throw new FormatBankSoalException(noBaris, baris,
                    "kunci header tidak dikenal: " + kunci + " (harus " + KUNCI_WAJIB + ")");
        }
        if (header.putIfAbsent(kunci, nilai) != null) {
            throw new FormatBankSoalException(noBaris, baris, "kunci header duplikat: " + kunci);
        }
    }

    /** Memastikan semua kunci header wajib ada dan nilainya sesuai data contract. */
    public void validasiHeader(Map<String, String> header, int noBaris, String baris) throws FormatBankSoalException {
        for (String kunci : KUNCI_WAJIB) {
            if (header.getOrDefault(kunci, "").isEmpty()) {
                throw new FormatBankSoalException(noBaris, baris, "header " + kunci + " wajib diisi");
            }
        }
        if (!POLA_KODE.matcher(header.get(KODE).toUpperCase()).matches()) {
            throw new FormatBankSoalException(noBaris, baris, "KODE harus berpola AAA-00: " + header.get(KODE));
        }
        if (!POLA_TOKEN.matcher(header.get(TOKEN).toUpperCase()).matches()) {
            throw new FormatBankSoalException(noBaris, baris, "TOKEN harus 6 karakter huruf/angka");
        }
        if (header.containsKey(ULANG) && BarisData.parseBoolean(header.get(ULANG)) == null) {
            throw new FormatBankSoalException(noBaris, baris, "ULANG harus true atau false");
        }
    }

    public Ujian buatUjian(Map<String, String> header, List<Soal> daftarSoal, int noBaris, String baris)
            throws FormatBankSoalException {
        try {
            LocalDateTime mulai = FormatUtil.parseInputAkses(header.get(AKSES_MULAI));
            LocalDateTime selesai = FormatUtil.parseInputAkses(header.get(AKSES_SELESAI));
            return new Ujian(header.get(KODE), header.get(MAPEL), header.get(TOKEN),
                    Integer.parseInt(header.get(DURASI)), Double.parseDouble(header.get(KKM)), daftarSoal,
                    Boolean.TRUE.equals(BarisData.parseBoolean(header.getOrDefault(ULANG, "false"))),
                    mulai, selesai);
        } catch (NumberFormatException e) {
            throw new FormatBankSoalException(noBaris, baris, "DURASI/KKM harus berupa angka", e);
        } catch (IllegalArgumentException e) {
            throw new FormatBankSoalException(noBaris, baris, e.getMessage(), e);
        }
    }

    public Soal parseBaris(String baris, int noBaris) throws FormatBankSoalException {
        String[] f = BarisData.pecahField(baris);
        JenisSoal jenis = JenisSoal.dariKode(f[0]);
        if (jenis == null) {
            throw new FormatBankSoalException(noBaris, baris, "jenis soal tidak dikenal: " + f[0]);
        }
        if (f.length != jenis.getJumlahField()) {
            throw new FormatBankSoalException(noBaris, baris, "jumlah field " + jenis
                    + " harus " + jenis.getJumlahField() + ", ditemukan " + f.length);
        }
        // NumberFormatException adalah subclass IllegalArgumentException, jadi ditangkap lebih dulu
        try {
            int id = Integer.parseInt(f[1]);
            String pertanyaan = f[2];
            return switch (jenis) {
                case PG -> new SoalPilihanGanda(id, pertanyaan, Double.parseDouble(f[5]),
                        BarisData.pecahSubNilai(f[3]), satuHuruf(f[4], noBaris, baris));
                case TF -> new SoalTrueFalse(id, pertanyaan, Double.parseDouble(f[4]),
                        satuHuruf(f[3], noBaris, baris));
                case IS -> new SoalIsian(id, pertanyaan, Double.parseDouble(f[4]),
                        new HashSet<>(BarisData.pecahSubNilai(f[3])));
            };
        } catch (NumberFormatException e) {
            throw new FormatBankSoalException(noBaris, baris, "id/bobot harus berupa angka", e);
        } catch (IllegalArgumentException e) {
            throw new FormatBankSoalException(noBaris, baris, e.getMessage(), e);
        }
    }

    private static char satuHuruf(String kunci, int noBaris, String baris) throws FormatBankSoalException {
        if (kunci.length() != 1) {
            throw new FormatBankSoalException(noBaris, baris, "kunci harus tepat 1 huruf: \"" + kunci + "\"");
        }
        return Character.toUpperCase(kunci.charAt(0));
    }

    /** Dipakai reader untuk mendeteksi KODE ganda antar blok. */
    static String kodeUjian(Map<String, String> header) {
        return header.get(KODE).toUpperCase();
    }
}
