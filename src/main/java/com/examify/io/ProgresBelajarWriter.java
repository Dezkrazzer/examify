package com.examify.io;

import com.examify.model.CatatanProgres;
import com.examify.utils.FormatUtil;

import java.nio.file.Path;

/** Mencatat materi yang sudah diselesaikan siswa ke progres_belajar.txt. */
public class ProgresBelajarWriter extends PenulisBaris<CatatanProgres> {

    static final String HEADER = "# TIMESTAMP|NIS|KODE_KURSUS|ID_MATERI";

    public ProgresBelajarWriter(Path file) {
        super(file);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }

    @Override
    protected String keBaris(CatatanProgres c) {
        return String.join(BarisData.PEMISAH_FIELD,
                FormatUtil.timestamp(c.waktu()), c.nis(), c.kodeKursus(), c.idMateri());
    }
}
