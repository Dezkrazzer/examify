package com.examify.io;

import java.util.Arrays;
import java.util.List;

/** Aturan umum baris file data (Bagian 5.1 data contract). */
final class BarisData {

    static final String PEMISAH_FIELD = "|";
    static final String PEMISAH_SUB_NILAI = ";";
    private static final String PENANDA_KOMENTAR = "#";

    private BarisData() {
    }

    /** Baris kosong dan baris komentar (#) dilewati. */
    static boolean dilewati(String baris) {
        String isi = baris.trim();
        return isi.isEmpty() || isi.startsWith(PENANDA_KOMENTAR);
    }

    /** Limit -1 agar field kosong di akhir tetap terhitung; tiap field di-trim. */
    static String[] pecahField(String baris) {
        String[] field = baris.split("\\|", -1);
        for (int i = 0; i < field.length; i++) {
            field[i] = field[i].trim();
        }
        return field;
    }

    /**
     * "KUNCI=NILAI" -> {KUNCI (kapital), NILAI}. Memakai indexOf, bukan split,
     * agar nilai boleh berisi '='.
     *
     * @return null bila baris tidak berformat KUNCI=NILAI
     */
    static String[] pecahKunciNilai(String baris) {
        int posisi = baris.indexOf('=');
        if (posisi <= 0) {
            return null;
        }
        return new String[]{baris.substring(0, posisi).trim().toUpperCase(), baris.substring(posisi + 1).trim()};
    }

    /** @return TRUE/FALSE untuk "true"/"false" (tanpa peduli kapital), null untuk nilai lain */
    static Boolean parseBoolean(String nilai) {
        return switch (nilai.trim().toLowerCase()) {
            case "true", "ya" -> Boolean.TRUE;
            case "false", "tidak" -> Boolean.FALSE;
            default -> null;
        };
    }

    static List<String> pecahSubNilai(String nilai) {
        return Arrays.stream(nilai.split(PEMISAH_SUB_NILAI))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
