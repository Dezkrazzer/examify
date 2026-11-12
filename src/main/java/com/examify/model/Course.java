package com.examify.model;

import com.examify.utils.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Kelas e-learning: kumpulan bab yang masing-masing berisi materi (komposisi). */
public class Course {

    private final String kode;
    private final String nama;
    private final String pengajar;
    private final String nipPembuat;
    private final String deskripsi;
    private final List<Chapter> daftarBab;

    public Course(String kode, String nama, String pengajar, String deskripsi, List<Chapter> daftarBab) {
        this(kode, nama, pengajar, "", deskripsi, daftarBab);
    }

    public Course(String kode, String nama, String pengajar, String nipPembuat,
                  String deskripsi, List<Chapter> daftarBab) {
        if (StringUtil.isKosong(kode) || StringUtil.isKosong(nama)) {
            throw new IllegalArgumentException("Kode dan nama kursus wajib diisi");
        }
        if (daftarBab == null || daftarBab.isEmpty()) {
            throw new IllegalArgumentException("Kursus minimal memiliki 1 bab");
        }
        this.kode = kode.trim().toUpperCase();
        this.nama = nama.trim();
        this.pengajar = pengajar == null ? "" : pengajar.trim();
        this.nipPembuat = nipPembuat == null ? "" : nipPembuat.trim();
        this.deskripsi = deskripsi == null ? "" : deskripsi.trim();
        this.daftarBab = List.copyOf(daftarBab);
        pastikanIdMateriUnik();
    }

    private void pastikanIdMateriUnik() {
        Set<String> idTerpakai = new HashSet<>();
        for (Material materi : getSemuaMateri()) {
            if (!idTerpakai.add(materi.getIdMateri())) {
                throw new IllegalArgumentException("ID materi duplikat: " + materi.getIdMateri());
            }
        }
    }

    public List<Material> getSemuaMateri() {
        return daftarBab.stream()
                .flatMap(bab -> bab.getDaftarMateri().stream())
                .toList();
    }

    /** Pencarian sederhana berdasarkan kode atau nama, tanpa peduli huruf besar/kecil. */
    public boolean cocokDengan(String kataKunci) {
        String kata = StringUtil.normalisasi(kataKunci);
        return kata.isEmpty() || kode.toUpperCase().contains(kata) || nama.toUpperCase().contains(kata);
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public String getPengajar() {
        return pengajar;
    }

    public String getNipPembuat() {
        return nipPembuat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public Course denganKode(String kodeBaru) {
        return new Course(kodeBaru, nama, pengajar, nipPembuat, deskripsi, daftarBab);
    }

    public Course denganDaftarBab(List<Chapter> babBaru) {
        return new Course(kode, nama, pengajar, nipPembuat, deskripsi, babBaru);
    }

    public List<Chapter> getDaftarBab() {
        return daftarBab;
    }

    public int getJumlahMateri() {
        return getSemuaMateri().size();
    }

    public int getJumlahMateriPembelajaran() {
        return (int) getSemuaMateri().stream()
                .filter(m -> !(m instanceof ExamMaterial || m instanceof QuizMaterial))
                .count();
    }

    public int getJumlahUjian() {
        return (int) getSemuaMateri().stream()
                .filter(m -> m instanceof ExamMaterial || m instanceof QuizMaterial)
                .count();
    }

    @Override
    public String toString() {
        return kode + " - " + nama;
    }
}
