package com.examify.io;

import com.examify.model.CatatanProgres;
import com.examify.utils.FormatUtil;

public class ProgresBelajarReader extends PembacaBaris<CatatanProgres> {

    @Override
    protected int getJumlahField() {
        return 4;
    }

    @Override
    protected CatatanProgres dariField(String[] f) {
        return new CatatanProgres(FormatUtil.parseTimestamp(f[0]), f[1], f[2].toUpperCase(), f[3]);
    }
}
