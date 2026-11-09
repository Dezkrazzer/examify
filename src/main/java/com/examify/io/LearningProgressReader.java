package com.examify.io;

import com.examify.model.ProgressRecord;
import com.examify.utils.FormatUtil;

public class LearningProgressReader extends LineReader<ProgressRecord> {

    @Override
    protected int getJumlahField() {
        return 4;
    }

    @Override
    protected ProgressRecord dariField(String[] f) {
        return new ProgressRecord(FormatUtil.parseTimestamp(f[0]), f[1], f[2].toUpperCase(), f[3]);
    }
}
