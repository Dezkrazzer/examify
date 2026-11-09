package com.examify.io;

import com.examify.model.SubmitMode;
import com.examify.model.GradeHistory;
import com.examify.utils.FormatUtil;

/** Membaca rekap_nilai.txt agar riwayat nilai tetap ada setelah aplikasi ditutup. */
public class GradeRecapReader extends LineReader<GradeHistory> {

    private static final int JUMLAH_FIELD = 14;

    @Override
    protected int getJumlahField() {
        return JUMLAH_FIELD;
    }

    @Override
    protected GradeHistory dariField(String[] f) {
        return new GradeHistory(
                FormatUtil.parseTimestamp(f[0]),
                f[1], f[2], f[3], f[4], f[5],
                Integer.parseInt(f[6]), Integer.parseInt(f[7]), Integer.parseInt(f[8]),
                Double.parseDouble(f[9]),
                f[10],
                GradeRecapWriter.LULUS.equals(f[11]),
                SubmitMode.valueOf(f[12]),
                FormatUtil.parseHhmmss(f[13]));
    }
}
