package com.examify.service;

import com.examify.io.CourseReader;
import com.examify.io.CourseWriter;
import com.examify.model.Chapter;
import com.examify.model.MaterialType;
import com.examify.model.Course;
import com.examify.model.TextMaterial;
import com.examify.model.AssignmentMaterial;
import com.examify.model.AssignmentSubmission;
import com.examify.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentAndEnrollmentTest {

    @TempDir
    Path tempDir;

    private Student siswaKelasA;
    private Student siswaKelasB;

    @BeforeEach
    void setUp() {
        siswaKelasA = new Student("1001", "Budi Kelas A", "XI-IPA-1", "1111");
        siswaKelasB = new Student("1002", "Siti Kelas B", "XI-IPA-2", "2222");
    }

    @Test
    void ujiAksesKelasMateriTugas() {
        AssignmentMaterial tugasKhususA = new AssignmentMaterial(
                "tgs-01",
                "Tugas Eksperimen Fisika",
                LocalDateTime.now().plusDays(2),
                "XI-IPA-1",
                "Unggah laporan praktikum dalam format PDF atau Word"
        );

        assertTrue(tugasKhususA.isBolehAkses(siswaKelasA), "Siswa kelas XI-IPA-1 harus boleh akses");
        assertFalse(tugasKhususA.isBolehAkses(siswaKelasB), "Siswa kelas XI-IPA-2 tidak boleh akses tugas khusus XI-IPA-1");

        AssignmentMaterial tugasSemua = new AssignmentMaterial(
                "tgs-02",
                "Tugas Umum",
                LocalDateTime.now().plusDays(5),
                "Semua Kelas",
                "Tugas untuk semua siswa"
        );

        assertTrue(tugasSemua.isBolehAkses(siswaKelasA));
        assertTrue(tugasSemua.isBolehAkses(siswaKelasB));
        assertFalse(tugasSemua.isBolehAkses(null));
    }

    @Test
    void ujiDeadlineMateriTugas() {
        AssignmentMaterial tugasAktif = new AssignmentMaterial(
                "tgs-aktif",
                "Tugas Aktif",
                LocalDateTime.now().plusDays(3),
                "Semua Kelas",
                "Instruksi tugas aktif"
        );
        assertFalse(tugasAktif.isLewatDeadline(), "Tugas dengan deadline di masa depan belum lewat deadline");

        AssignmentMaterial tugasKedaluwarsa = new AssignmentMaterial(
                "tgs-lewat",
                "Tugas Kedaluwarsa",
                LocalDateTime.now().minusDays(1),
                "Semua Kelas",
                "Instruksi tugas lewat"
        );
        assertTrue(tugasKedaluwarsa.isLewatDeadline(), "Tugas dengan deadline di masa lalu harus lewat deadline");
    }

    @Test
    void ujiPendaftaranKursus() throws IOException {
        Path fileDaftar = tempDir.resolve("pendaftaran.txt");
        CourseEnrollmentService pendaftaranService = new CourseEnrollmentService(fileDaftar, null);

        Course kursus = new Course("BIN", "Bahasa Indonesia", "Sri", "Deskripsi",
                List.of(new Chapter(1, "Bab 1", List.of())));

        assertFalse(pendaftaranService.isTerdaftar(siswaKelasA, kursus), "Siswa belum terdaftar");

        pendaftaranService.daftarkan(siswaKelasA, kursus);
        assertTrue(pendaftaranService.isTerdaftar(siswaKelasA, kursus), "Siswa sekarang harus terdaftar");

        // Reload data from file to ensure persistence
        CourseEnrollmentService serviceBaru = new CourseEnrollmentService(fileDaftar, null);
        assertTrue(serviceBaru.isTerdaftar(siswaKelasA, kursus), "Status terdaftar harus persisten ke file");
        assertFalse(serviceBaru.isTerdaftar(siswaKelasB, kursus), "Siswa B belum pernah mendaftar");
        Course kursusKodeBaru = kursus.denganKode("BIN2026");
        serviceBaru.perbaruiKodeKursus("BIN", "BIN2026");
        assertTrue(serviceBaru.isTerdaftar(siswaKelasA, kursusKodeBaru), "Siswa A tetap terdaftar setelah kode kursus diubah");
        assertFalse(serviceBaru.isTerdaftar(siswaKelasB, kursusKodeBaru), "Siswa B tetap belum terdaftar");
    }

    @Test
    void ujiPengumpulanTugasService() throws IOException {
        Path filePengumpulan = tempDir.resolve("pengumpulan.txt");
        Path folderBerkas = tempDir.resolve("berkas");
        AssignmentService tugasService = new AssignmentService(filePengumpulan, folderBerkas);

        File berkasUpload = tempDir.resolve("laporan_budi.docx").toFile();
        Files.writeString(berkasUpload.toPath(), "Ini isi dokumen tugas siswa Budi");

        AssignmentSubmission pt = tugasService.simpanPengumpulan("tgs-01", siswaKelasA, berkasUpload, "Tolong diperiksa pak");

        assertNotNull(pt);
        assertEquals("tgs-01", pt.getIdTugas());
        assertEquals("1001", pt.getNis());
        assertEquals("laporan_budi.docx", pt.getNamaBerkas());

        Optional<AssignmentSubmission> opt = tugasService.cariPengumpulan("tgs-01", "1001");
        assertTrue(opt.isPresent());
        assertEquals("Tolong diperiksa pak", opt.get().getCatatan());

        // Reload from file to ensure persistence
        AssignmentService serviceBaru = new AssignmentService(filePengumpulan, folderBerkas);
        Optional<AssignmentSubmission> optBaru = serviceBaru.cariPengumpulan("tgs-01", "1001");
        assertTrue(optBaru.isPresent());
        assertEquals("laporan_budi.docx", optBaru.get().getNamaBerkas());
    }

    @Test
    void ujiUbahKodeKursusDanCaseSensitive() throws IOException {
        Path fileKursus = tempDir.resolve("kursus.txt");
        CourseWriter writer = new CourseWriter(tempDir);

        Course k1 = new Course("MAT101", "Matematika Dasar", "Pak Budi", "Deskripsi",
                List.of(new Chapter(1, "Bab 1", List.of())));
        Course k2 = new Course("FIS101", "Fisika Dasar", "Bu Ani", "Deskripsi",
                List.of(new Chapter(1, "Bab 1", List.of())));

        writer.tulis(fileKursus, List.of(k1, k2));

        CourseService kursusService = new CourseService(List.of(k1, k2), null, writer, fileKursus);

        // Ubah kode k1
        Course terupdate = kursusService.ubahKodeKursus(k1, "MAT102");
        assertEquals("MAT102", terupdate.getKode());
        assertTrue(kursusService.cariKursus("MAT102").isPresent());
        assertFalse(kursusService.cariKursus("MAT101").isPresent());

        // Tidak boleh duplikat dengan kursus lain
        assertThrows(IllegalArgumentException.class, () -> kursusService.ubahKodeKursus(terupdate, "FIS101"));

        // Huruf besar dan kecil berpengaruh (case-sensitive) saat pencarian kode
        assertTrue(kursusService.cariKursus("MAT102").isPresent(), "Kode MAT102 harus ditemukan dengan huruf kapital");
        assertFalse(kursusService.cariKursus("mat102").isPresent(), "Pencarian huruf kecil tidak boleh cocok dengan kode MAT102");
        assertFalse(kursusService.cariKursus("Mat102").isPresent(), "Pencarian huruf campuran tidak boleh cocok dengan kode MAT102");
    }

    @Test
    void ujiBacaTulisMateriTugas() throws Exception {
        Path fileKursus = tempDir.resolve("kursus_tugas.txt");
        CourseWriter writer = new CourseWriter(tempDir);
        CourseReader reader = new CourseReader(tempDir);

        LocalDateTime deadline = LocalDateTime.of(2026, 12, 1, 23, 59);
        AssignmentMaterial tugas = new AssignmentMaterial("tgs-09", "Tugas Analisis", deadline, "XI-IPA-1", "Analisis cerpen bab 1");

        Course k = new Course("IND", "Bahasa Indonesia", "Sri", "Deskripsi",
                List.of(new Chapter(1, "Bab 1", List.of(tugas))));

        writer.tulis(fileKursus, List.of(k));

        List<Course> hasilBaca = reader.baca(fileKursus);
        assertEquals(1, hasilBaca.size());
        assertEquals(1, hasilBaca.get(0).getJumlahMateri());

        var materi = hasilBaca.get(0).getDaftarBab().get(0).getDaftarMateri().get(0);
        assertTrue(materi instanceof AssignmentMaterial);
        AssignmentMaterial tugasBaca = (AssignmentMaterial) materi;
        assertEquals("tgs-09", tugasBaca.getIdMateri());
        assertEquals("Tugas Analisis", tugasBaca.getJudul());
        assertEquals("XI-IPA-1", tugasBaca.getTargetKelas());
        assertEquals("Analisis cerpen bab 1", tugasBaca.getDeskripsi());
    }

    @Test
    void ujiTentukanFolderMateriKonsistenDanTidakMembuatBin() throws Exception {
        Path folderMateri = tempDir.resolve("materi").resolve("bahasaindo");
        Files.createDirectories(folderMateri);
        Path fileMateri1 = folderMateri.resolve("01-kata-baku.md");
        Files.writeString(fileMateri1, "# Kata Baku");

        TextMaterial teks = new TextMaterial("bin-01", "Mengenal Kata Baku", fileMateri1);
        Course kBin = new Course("BIN", "Bahasa Indonesia", "Sri", "Deskripsi",
                List.of(new Chapter(1, "Bab 1", List.of(teks))));

        CourseService kursusService = new CourseService(List.of(kBin), null);
        Path folderTarget = kursusService.tentukanFolderMateri(kBin);
        assertEquals(folderMateri.toAbsolutePath().normalize(), folderTarget.toAbsolutePath().normalize());
        assertFalse(folderTarget.getFileName().toString().equalsIgnoreCase("bin"),
                "Folder materi Bahasa Indonesia tidak boleh bernama bin");
    }
}
