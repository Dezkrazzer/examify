package com.examify.io;

import com.examify.utils.FormatUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;

/** Log pelanggaran Mode Aman (append-only), satu kejadian per baris. */
public class ViolationWriter extends LineWriter<ViolationWriter.Catatan> {

    public static final String HEADER = "# WAKTU|NIS|NAMA|KODE_UJIAN|KE|JENIS|KETERANGAN";

    /** Data satu baris log pelanggaran. */
    public record Catatan(LocalDateTime waktu, String nis, String nama, String kodeUjian,
                          int ke, String jenis, String keterangan) {
    }

    public ViolationWriter(Path file) {
        super(file);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }

    @Override
    protected String keBaris(Catatan c) {
        return String.join(DataLine.PEMISAH_FIELD,
                FormatUtil.timestamp(c.waktu()), c.nis(), c.nama(), c.kodeUjian(),
                String.valueOf(c.ke()), c.jenis(), c.keterangan());
    }
}
