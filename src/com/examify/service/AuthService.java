package com.examify.service;

import com.examify.model.Guru;
import com.examify.model.Siswa;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    private final Map<String, Siswa> siswaByNis = new LinkedHashMap<>();
    private final Map<String, Guru> guruByNip = new LinkedHashMap<>();

    public AuthService(List<Siswa> daftarSiswa) {
        this(daftarSiswa, List.of());
    }

    public AuthService(List<Siswa> daftarSiswa, List<Guru> daftarGuru) {
        if (daftarSiswa != null) {
            daftarSiswa.forEach(siswa -> siswaByNis.put(siswa.getNis(), siswa));
        }
        if (daftarGuru != null) {
            daftarGuru.forEach(guru -> guruByNip.put(guru.getNip(), guru));
        }
    }

    public Optional<Siswa> login(String nis, String pin) {
        return loginSiswa(nis, pin);
    }

    public Optional<Siswa> loginSiswa(String nis, String pin) {
        if (nis == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(siswaByNis.get(nis.trim()))
                .filter(siswa -> siswa.verifikasiPin(pin));
    }

    public Optional<Guru> loginGuru(String nip, String pin) {
        if (nip == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(guruByNip.get(nip.trim()))
                .filter(guru -> guru.verifikasiPin(pin));
    }

    public List<Siswa> getSemuaSiswa() {
        return new ArrayList<>(siswaByNis.values());
    }

    public List<Guru> getSemuaGuru() {
        return new ArrayList<>(guruByNip.values());
    }

    public Optional<Siswa> cariSiswa(String nis) {
        if (nis == null) return Optional.empty();
        return Optional.ofNullable(siswaByNis.get(nis.trim()));
    }

    public Optional<Guru> cariGuru(String nip) {
        if (nip == null) return Optional.empty();
        return Optional.ofNullable(guruByNip.get(nip.trim()));
    }
}
