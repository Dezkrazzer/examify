package com.examify.model;

import java.time.LocalDateTime;

/** Satu baris progres_belajar.txt: siswa menyelesaikan satu materi. */
public record CatatanProgres(LocalDateTime waktu, String nis, String kodeKursus, String idMateri) {
}
