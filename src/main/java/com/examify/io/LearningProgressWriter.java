package com.examify.io;

import com.examify.model.ProgressRecord;
import com.examify.utils.FormatUtil;

import java.nio.file.Path;

/** Mencatat materi yang sudah diselesaikan siswa ke progres_belajar.txt. */
public class LearningProgressWriter extends LineWriter<ProgressRecord> {

    static final String HEADER = "# TIMESTAMP|NIS|KODE_KURSUS|ID_MATERI";

    public LearningProgressWriter(Path file) {
        super(file);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }

    @Override
    protected String keBaris(ProgressRecord c) {
        return String.join(DataLine.PEMISAH_FIELD,
                FormatUtil.timestamp(c.waktu()), c.nis(), c.kodeKursus(), c.idMateri());
    }
}
