package com.examify.service;

import com.examify.io.LineWriter;
import com.examify.model.ExamResult;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.Exam;

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
public class GradeRecapService {

    private final Map<Student, Double> rekapSementara = new HashMap<>();
    private final List<GradeHistory> riwayat;
    private final LineWriter<GradeHistory> writer;

    public GradeRecapService(LineWriter<GradeHistory> writer) {
        this(writer, List.of());
    }

    public GradeRecapService(LineWriter<GradeHistory> writer, List<GradeHistory> riwayatTersimpan) {
        this.writer = writer;
        this.riwayat = new ArrayList<>(riwayatTersimpan);
    }

    /** Hasil tetap tercatat di memori walaupun penulisan file gagal. */
    public GradeHistory catat(ExamResult hasil) throws IOException {
        GradeHistory baru = GradeHistory.dari(hasil);
        rekapSementara.put(hasil.getSiswa(), hasil.getSkor());
        riwayat.add(baru);
        writer.tulis(baru);
        return baru;
    }

    public Map<Student, Double> getRekapSementara() {
        return Collections.unmodifiableMap(rekapSementara);
    }

    public List<Map.Entry<Student, Double>> getRanking() {
        List<Map.Entry<Student, Double>> ranking = new ArrayList<>(rekapSementara.entrySet());
        ranking.sort(Map.Entry.<Student, Double>comparingByValue().reversed());
        return ranking;
    }

    /** Riwayat milik siswa, terbaru lebih dulu. */
    public List<GradeHistory> getRiwayat(Student siswa) {
        return riwayat.stream()
                .filter(r -> r.milik(siswa))
                .sorted(Comparator.comparing(GradeHistory::waktu).reversed())
                .toList();
    }

    /** Nilai terbaik memakai urutan alami RiwayatNilai (Comparable: skor tertinggi dulu). */
    public Optional<GradeHistory> getNilaiTerbaik(Student siswa, Exam ujian) {
        return riwayat.stream()
                .filter(r -> r.milik(siswa) && r.kodeUjian().equals(ujian.getKodeUjian()))
                .sorted()
                .findFirst();
    }

    public int getJumlahPercobaan(Student siswa, Exam ujian) {
        return (int) riwayat.stream()
                .filter(r -> r.milik(siswa) && r.kodeUjian().equals(ujian.getKodeUjian()))
                .count();
    }

    public boolean sudahMengerjakan(Student siswa, Exam ujian) {
        return getJumlahPercobaan(siswa, ujian) > 0;
    }

    public long getJumlahPeserta() {
        return riwayat.stream().map(GradeHistory::nis).distinct().count();
    }

    public List<GradeHistory> getSemuaRiwayat() {
        return Collections.unmodifiableList(riwayat);
    }
}
