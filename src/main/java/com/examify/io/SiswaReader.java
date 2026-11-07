package com.examify.io;

import com.examify.exception.FormatDataException;
import com.examify.model.Siswa;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/** Membaca siswa.txt berformat NIS|NAMA|KELAS|PIN. */
public class SiswaReader implements DataReader<Siswa> {

    private static final int JUMLAH_FIELD = 4;
    private static final Pattern POLA_NIS = Pattern.compile("^\\d{5,10}$");
    private static final Pattern POLA_PIN = Pattern.compile("^\\d{4,6}$");

    @Override
    public List<Siswa> baca(Path path) throws IOException, FormatDataException {
        List<Siswa> daftarSiswa = new ArrayList<>();
        Set<String> nisTerpakai = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int noBaris = 0;
            String baris;
            while ((baris = br.readLine()) != null) {
                noBaris++;
                if (BarisData.dilewati(baris)) {
                    continue;
                }
                Siswa siswa = parseBaris(baris, noBaris);
                if (!nisTerpakai.add(siswa.getNis())) {
                    throw new FormatDataException(noBaris, baris, "NIS duplikat: " + siswa.getNis());
                }
                daftarSiswa.add(siswa);
            }
        }
        return daftarSiswa;
    }

    Siswa parseBaris(String baris, int noBaris) throws FormatDataException {
        String[] f = BarisData.pecahField(baris);
        if (f.length != JUMLAH_FIELD) {
            throw new FormatDataException(noBaris, baris,
                    "jumlah field siswa harus " + JUMLAH_FIELD + ", ditemukan " + f.length);
        }
        if (!POLA_NIS.matcher(f[0]).matches()) {
            throw new FormatDataException(noBaris, baris, "NIS harus 5-10 digit");
        }
        if (!POLA_PIN.matcher(f[3]).matches()) {
            throw new FormatDataException(noBaris, baris, "PIN harus 4-6 digit");
        }
        try {
            return new Siswa(f[0], f[1], f[2], f[3]);
        } catch (IllegalArgumentException e) {
            throw new FormatDataException(noBaris, baris, e.getMessage(), e);
        }
    }
}
