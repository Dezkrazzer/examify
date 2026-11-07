package com.examify.service;

import com.examify.io.PenulisBaris;
import com.examify.model.HasilUjian;
import com.examify.model.RiwayatNilai;
import com.examify.model.Siswa;
import com.examify.model.Ujian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Riwayat nilai permanen (dimuat dari rekap_nilai.txt) ditambah rekap sementara
 * HashMap&lt;Siswa, Double&gt; untuk nilai terakhir tiap siswa pada sesi berjalan.
 */
public class RekapService {

    private final Map<Siswa, Double> rekapSementara = new HashMap<>();
    private final List<RiwayatNilai> riwayat;
    private final PenulisBaris<RiwayatNilai> writer;

    public RekapService(PenulisBaris<RiwayatNilai> writer) {
        this(writer, List.of());
    }

    public RekapService(PenulisBaris<RiwayatNilai> writer, List<RiwayatNilai> riwayatTersimpan) {
        this.writer = writer;
        this.riwayat = new ArrayList<>(riwayatTersimpan);
    }

    /** Hasil tetap tercatat di memori walaupun penulisan file gagal. */
    public RiwayatNilai catat(HasilUjian hasil) throws IOException {
        RiwayatNilai baru = RiwayatNilai.dari(hasil);
        rekapSementara.put(hasil.getSiswa(), hasil.getSkor());
        riwayat.add(baru);
        writer.tulis(baru);
        return baru;
    }

    public Map<Siswa, Double> getRekapSementara() {
        return Collections.unmodifiableMap(rekapSementara);
    }

    public List<Map.Entry<Siswa, Double>> getRanking() {
        List<Map.Entry<Siswa, Double>> ranking = new ArrayList<>(rekapSementara.entrySet());
        ranking.sort(Map.Entry.<Siswa, Double>comparingByValue().reversed());
        return ranking;
    }

    /** Riwayat milik siswa, terbaru lebih dulu. */
    public List<RiwayatNilai> getRiwayat(Siswa siswa) {
        return riwayat.stream()
                .filter(r -> r.milik(siswa))
                .sorted(Comparator.comparing(RiwayatNilai::waktu).reversed())
                .toList();
    }

    /** Nilai terbaik memakai urutan alami RiwayatNilai (Comparable: skor tertinggi dulu). */
    public Optional<RiwayatNilai> getNilaiTerbaik(Siswa siswa, Ujian ujian) {
        return riwayat.stream()
                .filter(r -> r.milik(siswa) && r.kodeUjian().equals(ujian.getKodeUjian()))
                .sorted()
                .findFirst();
    }

    public int getJumlahPercobaan(Siswa siswa, Ujian ujian) {
        return (int) riwayat.stream()
                .filter(r -> r.milik(siswa) && r.kodeUjian().equals(ujian.getKodeUjian()))
                .count();
    }

    public boolean sudahMengerjakan(Siswa siswa, Ujian ujian) {
        return getJumlahPercobaan(siswa, ujian) > 0;
    }

    public long getJumlahPeserta() {
        return riwayat.stream().map(RiwayatNilai::nis).distinct().count();
    }

    public List<RiwayatNilai> getSemuaRiwayat() {
        return Collections.unmodifiableList(riwayat);
    }
}
