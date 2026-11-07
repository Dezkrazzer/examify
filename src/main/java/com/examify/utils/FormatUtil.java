package com.examify.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Format waktu dan angka yang konsisten di UI maupun file. */
public final class FormatUtil {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter TANGGAL_RINGKAS = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TANGGAL_SINGKAT = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
    private static final DateTimeFormatter TANGGAL_PANJANG =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"));
    private static final DateTimeFormatter FORMAT_WAKTU_AKSES =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"));
    private static final DateTimeFormatter FORMAT_INPUT_AKSES =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private FormatUtil() {
    }

    /** 125 detik -> "02:05". */
    public static String mmss(int totalDetik) {
        return String.format("%02d:%02d", totalDetik / 60, totalDetik % 60);
    }

    /** Durasi -> "HH:mm:ss". */
    public static String hhmmss(Duration durasi) {
        long detik = Math.max(0, durasi.getSeconds());
        return String.format("%02d:%02d:%02d", detik / 3600, (detik % 3600) / 60, detik % 60);
    }

    /** Selalu titik desimal (Locale.US) agar file rekap konsisten. */
    public static String angka(double nilai) {
        return String.format(Locale.US, "%.2f", nilai);
    }

    /** "HH:mm:ss" -> Duration; kebalikan dari {@link #hhmmss(Duration)}. */
    public static Duration parseHhmmss(String teks) {
        String[] bagian = teks.trim().split(":");
        if (bagian.length != 3) {
            throw new IllegalArgumentException("Durasi harus berformat HH:mm:ss: " + teks);
        }
        return Duration.ofHours(Long.parseLong(bagian[0]))
                .plusMinutes(Long.parseLong(bagian[1]))
                .plusSeconds(Long.parseLong(bagian[2]));
    }

    public static String timestamp(LocalDateTime waktu) {
        return TIMESTAMP.format(waktu);
    }

    public static LocalDateTime parseTimestamp(String teks) {
        return LocalDateTime.parse(teks.trim(), TIMESTAMP);
    }

    public static String tanggalSingkat(LocalDateTime waktu) {
        return TANGGAL_SINGKAT.format(waktu);
    }

    /** 29435 -> "29.435" (pemisah ribuan gaya Indonesia, hanya untuk tampilan). */
    public static String ribuan(long nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "%,d", nilai);
    }

    /** "Andi Pratama" -> "AP"; dipakai sebagai avatar. */
    public static String inisial(String nama) {
        StringBuilder sb = new StringBuilder();
        for (String kata : nama.trim().split("\\s+")) {
            if (!kata.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(kata.charAt(0)));
            }
        }
        return sb.toString();
    }

    public static String tanggalRingkas(LocalDateTime waktu) {
        return TANGGAL_RINGKAS.format(waktu);
    }

    public static String tanggalPanjang(LocalDateTime waktu) {
        return TANGGAL_PANJANG.format(waktu);
    }

    public static String formatWaktuAkses(LocalDateTime waktu) {
        if (waktu == null) return "-";
        return FORMAT_WAKTU_AKSES.format(waktu);
    }

    public static String formatInputAkses(LocalDateTime waktu) {
        if (waktu == null) return "";
        return FORMAT_INPUT_AKSES.format(waktu);
    }

    public static String formatRentangWaktuAkses(LocalDateTime mulai, LocalDateTime selesai) {
        if (mulai == null && selesai == null) return "Bebas";
        if (mulai != null && selesai != null) {
            return formatWaktuAkses(mulai) + " s.d. " + formatWaktuAkses(selesai);
        }
        if (mulai != null) {
            return "Mulai " + formatWaktuAkses(mulai);
        }
        return "Sampai " + formatWaktuAkses(selesai);
    }

    public static LocalDateTime parseInputAkses(String teks) {
        if (teks == null || teks.trim().isEmpty()) return null;
        String s = teks.trim();
        try {
            if (s.contains("T")) {
                return LocalDateTime.parse(s);
            } else if (s.contains(" ")) {
                return LocalDateTime.parse(s, FORMAT_INPUT_AKSES);
            } else {
                return LocalDate.parse(s).atStartOfDay();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
