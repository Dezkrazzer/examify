package com.examify.io;

import com.examify.exception.FormatDataException;
import com.examify.model.Guru;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Membaca guru.txt berformat NIP|NAMA|PIN|MAPEL. */
public class GuruReader implements DataReader<Guru> {

    private static final int JUMLAH_FIELD = 4;

    @Override
    public List<Guru> baca(Path path) throws IOException, FormatDataException {
        List<Guru> daftarGuru = new ArrayList<>();
        if (Files.notExists(path)) {
            return daftarGuru;
        }
        Set<String> nipTerpakai = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int noBaris = 0;
            String baris;
            while ((baris = br.readLine()) != null) {
                noBaris++;
                if (BarisData.dilewati(baris)) {
                    continue;
                }
                Guru guru = parseBaris(baris, noBaris);
                if (!nipTerpakai.add(guru.getNip())) {
                    throw new FormatDataException(noBaris, baris, "NIP duplikat: " + guru.getNip());
                }
                daftarGuru.add(guru);
            }
        }
        return daftarGuru;
    }

    Guru parseBaris(String baris, int noBaris) throws FormatDataException {
        String[] f = BarisData.pecahField(baris);
        if (f.length < 3) {
            throw new FormatDataException(noBaris, baris,
                    "field guru minimal harus 3 (NIP|NAMA|PIN), ditemukan " + f.length);
        }
        String mapel = f.length >= 4 ? f[3] : "";
        try {
            return new Guru(f[0], f[1], f[2], mapel);
        } catch (IllegalArgumentException e) {
            throw new FormatDataException(noBaris, baris, e.getMessage());
        }
    }
}
