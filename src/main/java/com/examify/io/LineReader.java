package com.examify.io;

import com.examify.exception.DataFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Pembaca file "satu baris = satu objek" (template method). File yang belum ada
 * dianggap kosong, karena file output baru dibuat setelah aktivitas pertama.
 */
public abstract class LineReader<T> implements DataReader<T> {

    protected abstract int getJumlahField();

    protected abstract T dariField(String[] field);

    @Override
    public List<T> baca(Path path) throws IOException, DataFormatException {
        List<T> hasil = new ArrayList<>();
        if (Files.notExists(path)) {
            return hasil;
        }
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int noBaris = 0;
            String baris;
            while ((baris = br.readLine()) != null) {
                noBaris++;
                if (!DataLine.dilewati(baris)) {
                    hasil.add(parseBaris(baris, noBaris));
                }
            }
        }
        return hasil;
    }

    T parseBaris(String baris, int noBaris) throws DataFormatException {
        String[] field = DataLine.pecahField(baris);
        if (field.length != getJumlahField()) {
            throw new DataFormatException(noBaris, baris,
                    "jumlah field harus " + getJumlahField() + ", ditemukan " + field.length);
        }
        try {
            return dariField(field);
        } catch (RuntimeException e) { // NumberFormat, DateTimeParse, IllegalArgument
            throw new DataFormatException(noBaris, baris, e.getMessage(), e);
        }
    }
}
