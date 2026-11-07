package com.examify.io;

import com.examify.model.ModeSubmit;
import com.examify.model.RiwayatNilai;
import com.examify.utils.FormatUtil;

/** Membaca rekap_nilai.txt agar riwayat nilai tetap ada setelah aplikasi ditutup. */
public class RekapNilaiReader extends PembacaBaris<RiwayatNilai> {

    private static final int JUMLAH_FIELD = 14;

    @Override
    protected int getJumlahField() {
        return JUMLAH_FIELD;
    }

    @Override
    protected RiwayatNilai dariField(String[] f) {
        return new RiwayatNilai(
                FormatUtil.parseTimestamp(f[0]),
                f[1], f[2], f[3], f[4], f[5],
                Integer.parseInt(f[6]), Integer.parseInt(f[7]), Integer.parseInt(f[8]),
                Double.parseDouble(f[9]),
                f[10],
                RekapNilaiWriter.LULUS.equals(f[11]),
                ModeSubmit.valueOf(f[12]),
                FormatUtil.parseHhmmss(f[13]));
    }
}
