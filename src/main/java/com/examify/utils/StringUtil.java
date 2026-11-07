package com.examify.utils;

/** Fungsi murni untuk manipulasi string. */
public final class StringUtil {

    private StringUtil() {
    }

    public static boolean isKosong(String teks) {
        return teks == null || teks.isBlank();
    }

    /** null-safe: trim, rapatkan spasi ganda, lalu kapital. */
    public static String normalisasi(String teks) {
        if (teks == null) {
            return "";
        }
        return teks.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    /** Rata kiri dengan lebar tetap; teks yang terlalu panjang dipotong. */
    public static String kiri(String teks, int lebar) {
        return String.format("%-" + lebar + "s", potong(teks, lebar));
    }

    /** Rata tengah dengan lebar tetap; teks yang terlalu panjang dipotong. */
    public static String tengah(String teks, int lebar) {
        String isi = potong(teks, lebar);
        int kiri = (lebar - isi.length()) / 2;
        return kiri(" ".repeat(kiri) + isi, lebar);
    }

    private static String potong(String teks, int lebar) {
        return teks.length() <= lebar ? teks : teks.substring(0, lebar);
    }
}
