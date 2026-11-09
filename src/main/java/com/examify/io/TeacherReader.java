package com.examify.io;

import com.examify.exception.DataFormatException;
import com.examify.model.Teacher;

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
public class TeacherReader implements DataReader<Teacher> {

    private static final int JUMLAH_FIELD = 4;

    @Override
    public List<Teacher> baca(Path path) throws IOException, DataFormatException {
        List<Teacher> daftarGuru = new ArrayList<>();
        if (Files.notExists(path)) {
            return daftarGuru;
        }
        Set<String> nipTerpakai = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int noBaris = 0;
            String baris;
            while ((baris = br.readLine()) != null) {
                noBaris++;
                if (DataLine.dilewati(baris)) {
                    continue;
                }
                Teacher guru = parseBaris(baris, noBaris);
                if (!nipTerpakai.add(guru.getNip())) {
                    throw new DataFormatException(noBaris, baris, "NIP duplikat: " + guru.getNip());
                }
                daftarGuru.add(guru);
            }
        }
        return daftarGuru;
    }

    Teacher parseBaris(String baris, int noBaris) throws DataFormatException {
        String[] f = DataLine.pecahField(baris);
        if (f.length < 3) {
            throw new DataFormatException(noBaris, baris,
                    "field guru minimal harus 3 (NIP|NAMA|PIN), ditemukan " + f.length);
        }
        String mapel = f.length >= 4 ? f[3] : "";
        try {
            return new Teacher(f[0], f[1], f[2], mapel);
        } catch (IllegalArgumentException e) {
            throw new DataFormatException(noBaris, baris, e.getMessage());
        }
    }
}
