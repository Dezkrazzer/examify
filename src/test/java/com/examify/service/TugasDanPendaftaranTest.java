package com.examify.service;

import com.examify.io.KursusReader;
import com.examify.io.KursusWriter;
import com.examify.model.Bab;
import com.examify.model.JenisMateri;
import com.examify.model.Kursus;
import com.examify.model.MateriTeks;
import com.examify.model.MateriTugas;
import com.examify.model.PengumpulanTugas;
import com.examify.model.Siswa;
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

class TugasDanPendaftaranTest {

    @TempDir
    Path tempDir;

    private Siswa siswaKelasA;
    private Siswa siswaKelasB;

    @BeforeEach
    void setUp() {
        siswaKelasA = new Siswa("1001", "Budi Kelas A", "XI-IPA-1", "1111");
        siswaKelasB = new Siswa("1002", "Siti Kelas B", "XI-IPA-2", "2222");
    }

    @Test
    void ujiAksesKelasMateriTugas() {
        MateriTugas tugasKhususA = new MateriTugas(
                "tgs-01",
                "Tugas Eksperimen Fisika",
                LocalDateTime.now().plusDays(2),
                "XI-IPA-1",
                "Unggah laporan praktikum dalam format PDF atau Word"
        );

        assertTrue(tugasKhususA.isBolehAkses(siswaKelasA), "Siswa kelas XI-IPA-1 harus boleh akses");
        assertFalse(tugasKhususA.isBolehAkses(siswaKelasB), "Siswa kelas XI-IPA-2 tidak boleh akses tugas khusus XI-IPA-1");

        MateriTugas tugasSemua = new MateriTugas(
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
        MateriTugas tugasAktif = new MateriTugas(
                "tgs-aktif",
                "Tugas Aktif",
                LocalDateTime.now().plusDays(3),
                "Semua Kelas",
                "Instruksi tugas aktif"
        );
        assertFalse(tugasAktif.isLewatDeadline(), "Tugas dengan deadline di masa depan belum lewat deadline");

        MateriTugas tugasKedaluwarsa = new MateriTugas(
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
        PendaftaranKursusService pendaftaranService = new PendaftaranKursusService(fileDaftar, null);

        Kursus kursus = new Kursus("BIN", "Bahasa Indonesia", "Sri", "Deskripsi",
                List.of(new Bab(1, "Bab 1", List.of())));

        assertFalse(pendaftaranService.isTerdaftar(siswaKelasA, kursus), "Siswa belum terdaftar");

        pendaftaranService.daftarkan(siswaKelasA, kursus);
        assertTrue(pendaftaranService.isTerdaftar(siswaKelasA, kursus), "Siswa sekarang harus terdaftar");

        // Reload data from file to ensure persistence
        PendaftaranKursusService serviceBaru = new PendaftaranKursusService(fileDaftar, null);
        assertTrue(serviceBaru.isTerdaftar(siswaKelasA, kursus), "Status terdaftar harus persisten ke file");
        assertFalse(serviceBaru.isTerdaftar(siswaKelasB, kursus), "Siswa B belum pernah mendaftar");
        Kursus kursusKodeBaru = kursus.denganKode("BIN2026");
        serviceBaru.perbaruiKodeKursus("BIN", "BIN2026");
        assertTrue(serviceBaru.isTerdaftar(siswaKelasA, kursusKodeBaru), "Siswa A tetap terdaftar setelah kode kursus diubah");
        assertFalse(serviceBaru.isTerdaftar(siswaKelasB, kursusKodeBaru), "Siswa B tetap belum terdaftar");
    }

    @Test
    void ujiPengumpulanTugasService() throws IOException {
        Path filePengumpulan = tempDir.resolve("pengumpulan.txt");
        Path folderBerkas = tempDir.resolve("berkas");
        TugasService tugasService = new TugasService(filePengumpulan, folderBerkas);

        File berkasUpload = tempDir.resolve("laporan_budi.docx").toFile();
        Files.writeString(berkasUpload.toPath(), "Ini isi dokumen tugas siswa Budi");

        PengumpulanTugas pt = tugasService.simpanPengumpulan("tgs-01", siswaKelasA, berkasUpload, "Tolong diperiksa pak");

        assertNotNull(pt);
        assertEquals("tgs-01", pt.getIdTugas());
        assertEquals("1001", pt.getNis());
        assertEquals("laporan_budi.docx", pt.getNamaBerkas());

        Optional<PengumpulanTugas> opt = tugasService.cariPengumpulan("tgs-01", "1001");
        assertTrue(opt.isPresent());
        assertEquals("Tolong diperiksa pak", opt.get().getCatatan());

        // Reload from file to ensure persistence
        TugasService serviceBaru = new TugasService(filePengumpulan, folderBerkas);
        Optional<PengumpulanTugas> optBaru = serviceBaru.cariPengumpulan("tgs-01", "1001");
        assertTrue(optBaru.isPresent());
        assertEquals("laporan_budi.docx", optBaru.get().getNamaBerkas());
    }

    @Test
    void ujiUbahKodeKursusDanCaseSensitive() throws IOException {
        Path fileKursus = tempDir.resolve("kursus.txt");
        KursusWriter writer = new KursusWriter(tempDir);

        Kursus k1 = new Kursus("MAT101", "Matematika Dasar", "Pak Budi", "Deskripsi",
                List.of(new Bab(1, "Bab 1", List.of())));
        Kursus k2 = new Kursus("FIS101", "Fisika Dasar", "Bu Ani", "Deskripsi",
                List.of(new Bab(1, "Bab 1", List.of())));

        writer.tulis(fileKursus, List.of(k1, k2));

        KursusService kursusService = new KursusService(List.of(k1, k2), null, writer, fileKursus);

        // Ubah kode k1
        Kursus terupdate = kursusService.ubahKodeKursus(k1, "MAT102");
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
        KursusWriter writer = new KursusWriter(tempDir);
        KursusReader reader = new KursusReader(tempDir);

        LocalDateTime deadline = LocalDateTime.of(2026, 12, 1, 23, 59);
        MateriTugas tugas = new MateriTugas("tgs-09", "Tugas Analisis", deadline, "XI-IPA-1", "Analisis cerpen bab 1");

        Kursus k = new Kursus("IND", "Bahasa Indonesia", "Sri", "Deskripsi",
                List.of(new Bab(1, "Bab 1", List.of(tugas))));

        writer.tulis(fileKursus, List.of(k));

        List<Kursus> hasilBaca = reader.baca(fileKursus);
        assertEquals(1, hasilBaca.size());
        assertEquals(1, hasilBaca.get(0).getJumlahMateri());

        var materi = hasilBaca.get(0).getDaftarBab().get(0).getDaftarMateri().get(0);
        assertTrue(materi instanceof MateriTugas);
        MateriTugas tugasBaca = (MateriTugas) materi;
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

        MateriTeks teks = new MateriTeks("bin-01", "Mengenal Kata Baku", fileMateri1);
        Kursus kBin = new Kursus("BIN", "Bahasa Indonesia", "Sri", "Deskripsi",
                List.of(new Bab(1, "Bab 1", List.of(teks))));

        KursusService kursusService = new KursusService(List.of(kBin), null);
        Path folderTarget = kursusService.tentukanFolderMateri(kBin);
        assertEquals(folderMateri.toAbsolutePath().normalize(), folderTarget.toAbsolutePath().normalize());
        assertFalse(folderTarget.getFileName().toString().equalsIgnoreCase("bin"),
                "Folder materi Bahasa Indonesia tidak boleh bernama bin");
    }
}
