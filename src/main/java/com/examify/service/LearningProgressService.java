package com.examify.service;

import com.examify.io.LineWriter;
import com.examify.model.ProgressRecord;
import com.examify.model.Course;
import com.examify.model.Material;
import com.examify.model.ExamMaterial;
import com.examify.model.Student;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Melacak materi yang sudah diselesaikan siswa. Materi ujian dianggap selesai
 * setelah ujiannya pernah dikerjakan, sehingga datanya diambil dari rekap nilai.
 */
public class LearningProgressService {

    private final Map<String, Set<String>> selesaiPerKursus = new HashMap<>(); // "NIS#KURSUS" -> id materi
    private final LineWriter<ProgressRecord> writer;
    private final CourseService kursusService;
    private final GradeRecapService rekapService;
    private final Clock clock;

    public LearningProgressService(LineWriter<ProgressRecord> writer, List<ProgressRecord> tersimpan,
                                 CourseService kursusService, GradeRecapService rekapService) {
        this(writer, tersimpan, kursusService, rekapService, Clock.systemDefaultZone());
    }

    public LearningProgressService(LineWriter<ProgressRecord> writer, List<ProgressRecord> tersimpan,
                                 CourseService kursusService, GradeRecapService rekapService, Clock clock) {
        this.writer = writer;
        this.kursusService = kursusService;
        this.rekapService = rekapService;
        this.clock = clock;
        tersimpan.forEach(c -> himpunanSelesai(c.nis(), c.kodeKursus()).add(c.idMateri()));
    }

    private Set<String> himpunanSelesai(String nis, String kodeKursus) {
        return selesaiPerKursus.computeIfAbsent(nis + "#" + kodeKursus, k -> new HashSet<>());
    }

    /** Dicatat sekali saja; pemanggilan berikutnya untuk materi yang sama diabaikan. */
    public void tandaiSelesai(Student siswa, Course kursus, Material materi) throws IOException {
        if (materi instanceof ExamMaterial) {
            return; // status ujian mengikuti rekap nilai
        }
        if (himpunanSelesai(siswa.getNis(), kursus.getKode()).add(materi.getIdMateri())) {
            writer.tulis(new ProgressRecord(LocalDateTime.now(clock), siswa.getNis(), kursus.getKode(),
                    materi.getIdMateri()));
        }
    }

    public boolean isSelesai(Student siswa, Course kursus, Material materi) {
        if (materi instanceof ExamMaterial mu) {
            return rekapService.sudahMengerjakan(siswa, kursusService.ujianDari(mu));
        }
        return himpunanSelesai(siswa.getNis(), kursus.getKode()).contains(materi.getIdMateri());
    }

    public int getJumlahSelesai(Student siswa, Course kursus) {
        return (int) kursus.getSemuaMateri().stream()
                .filter(materi -> isSelesai(siswa, kursus, materi))
                .count();
    }

    /** @return 0.0 - 1.0 */
    public double getProgres(Student siswa, Course kursus) {
        int total = kursus.getJumlahMateri();
        return total == 0 ? 0 : (double) getJumlahSelesai(siswa, kursus) / total;
    }

    public int getTotalMateriSelesai(Student siswa) {
        return kursusService.getDaftarKursus().stream()
                .mapToInt(kursus -> getJumlahSelesai(siswa, kursus))
                .sum();
    }
}
