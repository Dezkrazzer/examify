package com.examify.service;

import com.examify.model.Teacher;
import com.examify.model.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    private final Map<String, Student> siswaByNis = new LinkedHashMap<>();
    private final Map<String, Teacher> guruByNip = new LinkedHashMap<>();

    public AuthService(List<Student> daftarSiswa) {
        this(daftarSiswa, List.of());
    }

    public AuthService(List<Student> daftarSiswa, List<Teacher> daftarGuru) {
        if (daftarSiswa != null) {
            daftarSiswa.forEach(siswa -> siswaByNis.put(siswa.getNis(), siswa));
        }
        if (daftarGuru != null) {
            daftarGuru.forEach(guru -> guruByNip.put(guru.getNip(), guru));
        }
    }

    public Optional<Student> login(String nis, String pin) {
        return loginSiswa(nis, pin);
    }

    public Optional<Student> loginSiswa(String nis, String pin) {
        if (nis == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(siswaByNis.get(nis.trim()))
                .filter(siswa -> siswa.verifikasiPin(pin));
    }

    public Optional<Teacher> loginGuru(String nip, String pin) {
        if (nip == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(guruByNip.get(nip.trim()))
                .filter(guru -> guru.verifikasiPin(pin));
    }

    public List<Student> getSemuaSiswa() {
        return new ArrayList<>(siswaByNis.values());
    }

    public List<Teacher> getSemuaGuru() {
        return new ArrayList<>(guruByNip.values());
    }

    public Optional<Student> cariSiswa(String nis) {
        if (nis == null) return Optional.empty();
        return Optional.ofNullable(siswaByNis.get(nis.trim()));
    }

    public Optional<Teacher> cariGuru(String nip) {
        if (nip == null) return Optional.empty();
        return Optional.ofNullable(guruByNip.get(nip.trim()));
    }
}
