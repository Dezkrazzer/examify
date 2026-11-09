package com.examify.service;

import com.examify.io.CertificateWriter;
import com.examify.model.Gradable;
import com.examify.model.GradeHistory;
import com.examify.model.Exam;
import com.examify.utils.AppConfig;
import com.examify.utils.FormatUtil;
import com.examify.utils.StringUtil;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Membentuk teks e-sertifikat (lebar tetap, monospace) dari satu riwayat nilai, sehingga
 * sertifikat dapat diunduh langsung setelah ujian maupun kapan saja dari halaman profil.
 */
public class CertificateService {

    private static final int LEBAR = AppConfig.LEBAR_SERTIFIKAT;
    private static final String GARIS_TEBAL = "+" + "=".repeat(LEBAR) + "+";
    private static final String GARIS_TIPIS = "+" + "-".repeat(LEBAR) + "+";

    private final CertificateWriter writer;

    public CertificateService(CertificateWriter writer) {
        this.writer = writer;
    }

    /** @throws IllegalStateException bila siswa tidak lulus */
    public Path simpan(GradeHistory riwayat, Exam ujian) throws IOException {
        if (!riwayat.lulus()) {
            throw new IllegalStateException("Sertifikat hanya untuk siswa yang lulus");
        }
        return writer.tulis(namaFile(riwayat), buatTeks(riwayat, ujian));
    }

    public String namaFile(GradeHistory riwayat) {
        return "sertifikat_" + riwayat.nis() + "_" + riwayat.kodeUjian() + ".txt";
    }

    public String nomorSertifikat(GradeHistory riwayat) {
        return String.join("/", AppConfig.KODE_SERTIFIKAT, riwayat.kodeUjian(),
                riwayat.nis(), FormatUtil.tanggalRingkas(riwayat.waktu()));
    }

    public String buatTeks(GradeHistory r, Exam ujian) {
        StringBuilder sb = new StringBuilder();
        sb.append(GARIS_TEBAL).append('\n');
        tengah(sb, "E-SERTIFIKAT HASIL UJIAN");
        tengah(sb, AppConfig.NAMA_APLIKASI + " - Computer Based Test");
        sb.append(GARIS_TEBAL).append('\n');
        kiri(sb, "");
        kiri(sb, "  Nomor Sertifikat : " + nomorSertifikat(r));
        kiri(sb, "");
        tengah(sb, "Diberikan kepada:");
        tengah(sb, r.nama().toUpperCase());
        kiri(sb, "");
        kiri(sb, "  NIS              : " + r.nis());
        kiri(sb, "  Kelas            : " + r.kelas());
        kiri(sb, "  Mata Pelajaran   : " + r.mataPelajaran() + " (" + r.kodeUjian() + ")");
        kiri(sb, "  Skor Akhir       : " + FormatUtil.angka(r.skor()) + " / "
                + FormatUtil.angka(Gradable.NILAI_MAKSIMUM) + "   Predikat : " + r.predikat());
        kiri(sb, "  Status           : " + (r.lulus() ? "LULUS" : "TIDAK LULUS")
                + "  (KKM " + FormatUtil.angka(ujian.getKkm()) + ")");
        kiri(sb, "  Benar/Salah/Kosong: " + r.benar() + " / " + r.salah() + " / " + r.kosong());
        kiri(sb, "");
        sb.append(GARIS_TIPIS).append('\n');
        kiri(sb, "  Diterbitkan      : " + FormatUtil.tanggalPanjang(r.waktu()) + " WIB");
        sb.append(GARIS_TEBAL).append('\n');
        return sb.toString();
    }

    private static void kiri(StringBuilder sb, String isi) {
        sb.append('|').append(StringUtil.kiri(isi, LEBAR)).append("|\n");
    }

    private static void tengah(StringBuilder sb, String isi) {
        sb.append('|').append(StringUtil.tengah(isi, LEBAR)).append("|\n");
    }
}
