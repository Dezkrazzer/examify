package com.examify.io;

import com.examify.exception.DataFormatException;
import com.examify.model.TeamMember;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Membaca tim.txt berformat NIM|NAMA|FOTO|WEBSITE|GITHUB|INSTAGRAM|LINKEDIN. */
public class TeamReader implements DataReader<TeamMember> {

    private static final int FIELD_WAJIB = 2;
    private static final int INDEKS_SOSIAL = 3;

    @Override
    public List<TeamMember> baca(Path path) throws IOException, DataFormatException {
        List<TeamMember> daftar = new ArrayList<>();
        if (Files.notExists(path)) {
            return daftar;
        }
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int noBaris = 0;
            String baris;
            while ((baris = br.readLine()) != null) {
                noBaris++;
                if (!DataLine.dilewati(baris)) {
                    daftar.add(parseBaris(baris, noBaris));
                }
            }
        }
        return daftar;
    }

    TeamMember parseBaris(String baris, int noBaris) throws DataFormatException {
        String[] f = DataLine.pecahField(baris);
        if (f.length < FIELD_WAJIB) {
            throw new DataFormatException(noBaris, baris,
                    "field anggota minimal harus 2 (NIM|NAMA), ditemukan " + f.length);
        }
        String foto = f.length > 2 ? f[2] : "";
        String[] sosial = f.length > INDEKS_SOSIAL ? Arrays.copyOfRange(f, INDEKS_SOSIAL, f.length) : new String[0];
        try {
            return new TeamMember(f[0], f[1], foto, sosial);
        } catch (IllegalArgumentException e) {
            throw new DataFormatException(noBaris, baris, e.getMessage());
        }
    }
}
