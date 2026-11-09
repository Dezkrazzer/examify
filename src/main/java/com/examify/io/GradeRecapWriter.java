package com.examify.io;

import com.examify.model.GradeHistory;
import com.examify.utils.FormatUtil;

import java.nio.file.Path;

/** Menulis rekap_nilai.txt secara append-only (14 field, Bagian 5.4 data contract). */
public class GradeRecapWriter extends LineWriter<GradeHistory> {

    static final String HEADER = "# TIMESTAMP|NIS|NAMA|KELAS|KODE_UJIAN|MAPEL|BENAR|SALAH|KOSONG"
            + "|SKOR|PREDIKAT|STATUS|MODE_SUBMIT|DURASI_PENGERJAAN";
    static final String LULUS = "LULUS";
    static final String TIDAK_LULUS = "TIDAK_LULUS";

    public GradeRecapWriter(Path file) {
        super(file);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }

    @Override
    protected String keBaris(GradeHistory r) {
        return String.join(DataLine.PEMISAH_FIELD,
                FormatUtil.timestamp(r.waktu()),
                r.nis(), r.nama(), r.kelas(),
                r.kodeUjian(), r.mataPelajaran(),
                String.valueOf(r.benar()), String.valueOf(r.salah()), String.valueOf(r.kosong()),
                FormatUtil.angka(r.skor()),
                r.predikat(),
                r.lulus() ? LULUS : TIDAK_LULUS,
                r.modeSubmit().name(),
                FormatUtil.hhmmss(r.durasi()));
    }
}
