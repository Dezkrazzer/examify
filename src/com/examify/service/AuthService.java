package com.examify.service;

import com.examify.model.Siswa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    private final Map<String, Siswa> siswaByNis = new HashMap<>();

    public AuthService(List<Siswa> daftarSiswa) {
        daftarSiswa.forEach(siswa -> siswaByNis.put(siswa.getNis(), siswa));
    }

    public Optional<Siswa> login(String nis, String pin) {
        if (nis == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(siswaByNis.get(nis.trim()))
                .filter(siswa -> siswa.verifikasiPin(pin));
    }
}
