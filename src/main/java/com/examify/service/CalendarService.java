package com.examify.service;

import com.examify.model.CalendarAgenda;
import com.examify.model.CalendarAgenda.JenisAgenda;
import com.examify.model.Chapter;
import com.examify.model.Teacher;
import com.examify.model.Course;
import com.examify.model.Material;
import com.examify.model.QuizMaterial;
import com.examify.model.AssignmentMaterial;
import com.examify.model.ExamMaterial;
import com.examify.model.GradeHistory;
import com.examify.model.Student;
import com.examify.model.Exam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Layanan sinkronisasi agenda kalender akademik:
 * - untuk Siswa berdasarkan kursus yang diikuti,
 * - untuk Guru berdasarkan kursus yang dibuat/diampu oleh guru tersebut.
 */
public class CalendarService {

    private static final Locale LOKAL_ID = Locale.of("id", "ID");
    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMAT_RINGKAS = DateTimeFormatter.ofPattern("dd MMM HH:mm", LOKAL_ID);

    private final CourseService kursusService;
    private final ExamService ujianService;
    private final CourseEnrollmentService pendaftaranService;
    private final LearningProgressService progresService;
    private final AssignmentService tugasService;
    private final GradeRecapService rekapService;

    public CalendarService(CourseService kursusService,
                           ExamService ujianService,
                           CourseEnrollmentService pendaftaranService,
                           LearningProgressService progresService,
                           AssignmentService tugasService,
                           GradeRecapService rekapService) {
        this.kursusService = kursusService;
        this.ujianService = ujianService;
        this.pendaftaranService = pendaftaranService;
        this.progresService = progresService;
        this.tugasService = tugasService;
        this.rekapService = rekapService;
    }

    // ==================== KURSUS MILIK SISWA / GURU ====================

    /** Daftar kursus yang sudah diikuti oleh siswa (sudah masuk lewat kode atau memiliki progres). */
    public List<Course> getKursusDiikuti(Student siswa) {
        if (siswa == null || kursusService == null) {
            return List.of();
        }
        return kursusService.getDaftarKursus().stream()
                .filter(k -> isTerdaftar(siswa, k))
                .sorted(Comparator.comparing(Course::getNama, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /** Daftar kursus yang dibuat/diampu oleh guru (berdasarkan NIP pembuat atau nama pengajar). */
    public List<Course> getKursusDibuatGuru(Teacher guru) {
        if (guru == null || kursusService == null) {
            return List.of();
        }
        return kursusService.getDaftarKursus().stream()
                .filter(k -> isKursusMilikGuru(guru, k))
                .sorted(Comparator.comparing(Course::getNama, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public boolean isKursusMilikGuru(Teacher guru, Course kursus) {
        if (guru == null || kursus == null) {
            return false;
        }
        if (!kursus.getNipPembuat().isBlank()) {
            return kursus.getNipPembuat().equalsIgnoreCase(guru.getNip());
        }
        return !kursus.getPengajar().isBlank()
                && kursus.getPengajar().equalsIgnoreCase(guru.getNama().trim());
    }

    private boolean isTerdaftar(Student siswa, Course kursus) {
        boolean tercatat = pendaftaranService != null && pendaftaranService.isTerdaftar(siswa, kursus);
        boolean adaProgres = progresService != null && progresService.getProgres(siswa, kursus) > 0;
        return tercatat || adaProgres;
    }

    // ==================== FILTER KALENDER SISWA ====================

    public List<CalendarAgenda> getSemuaAgenda(Student siswa, String kodeKursusFilter) {
        List<Course> diikuti = getKursusDiikuti(siswa);
        if (diikuti.isEmpty()) {
            return List.of();
        }

        String filterBersih = kodeKursusFilter == null ? "" : kodeKursusFilter.trim();
        List<CalendarAgenda> hasil = new ArrayList<>();

        for (Course kursus : diikuti) {
            if (!filterBersih.isEmpty() && !kursus.getKode().equals(filterBersih)) {
                continue;
            }
            for (Chapter bab : kursus.getDaftarBab()) {
                for (Material materi : bab.getDaftarMateri()) {
                    if (materi instanceof AssignmentMaterial tugas) {
                        kumpulkanAgendaTugasSiswa(siswa, kursus, bab, tugas, hasil);
                    } else if (materi instanceof ExamMaterial mu) {
                        kumpulkanAgendaUjianAtauKuis(siswa, false, kursus, bab, mu,
                                mu.getKodeUjian(), JenisAgenda.UJIAN, hasil);
                    } else if (materi instanceof QuizMaterial mq) {
                        kumpulkanAgendaUjianAtauKuis(siswa, false, kursus, bab, mq,
                                mq.getKodeUjian(), JenisAgenda.KUIS, hasil);
                    }
                }
            }
        }

        hasil.sort(CalendarAgenda::compareTo);
        return List.copyOf(hasil);
    }

    public List<CalendarAgenda> filterBulan(Student siswa, YearMonth bulan, String kodeKursusFilter) {
        if (bulan == null) return List.of();
        return getSemuaAgenda(siswa, kodeKursusFilter).stream()
                .filter(a -> a.padaBulan(bulan))
                .toList();
    }

    public List<CalendarAgenda> filterHari(Student siswa, LocalDate tanggal, String kodeKursusFilter) {
        if (tanggal == null) return List.of();
        return getSemuaAgenda(siswa, kodeKursusFilter).stream()
                .filter(a -> a.padaTanggal(tanggal))
                .toList();
    }

    public List<CalendarAgenda> filterMendatang(Student siswa, LocalDateTime sekarang, String kodeKursusFilter) {
        LocalDateTime acuan = sekarang != null ? sekarang : LocalDateTime.now();
        return getSemuaAgenda(siswa, kodeKursusFilter).stream()
                .filter(a -> a.isMendatangAtauBerlangsung(acuan))
                .toList();
    }

    // ==================== FILTER KALENDER GURU ====================

    public List<CalendarAgenda> getSemuaAgendaGuru(Teacher guru, String kodeKursusFilter) {
        List<Course> dibuat = getKursusDibuatGuru(guru);
        if (dibuat.isEmpty()) {
            return List.of();
        }

        String filterBersih = kodeKursusFilter == null ? "" : kodeKursusFilter.trim();
        List<CalendarAgenda> hasil = new ArrayList<>();

        for (Course kursus : dibuat) {
            if (!filterBersih.isEmpty() && !kursus.getKode().equals(filterBersih)) {
                continue;
            }
            for (Chapter bab : kursus.getDaftarBab()) {
                for (Material materi : bab.getDaftarMateri()) {
                    if (materi instanceof AssignmentMaterial tugas) {
                        kumpulkanAgendaTugasGuru(kursus, bab, tugas, hasil);
                    } else if (materi instanceof ExamMaterial mu) {
                        kumpulkanAgendaUjianAtauKuis(null, true, kursus, bab, mu,
                                mu.getKodeUjian(), JenisAgenda.UJIAN, hasil);
                    } else if (materi instanceof QuizMaterial mq) {
                        kumpulkanAgendaUjianAtauKuis(null, true, kursus, bab, mq,
                                mq.getKodeUjian(), JenisAgenda.KUIS, hasil);
                    }
                }
            }
        }

        hasil.sort(CalendarAgenda::compareTo);
        return List.copyOf(hasil);
    }

    public List<CalendarAgenda> filterBulanGuru(Teacher guru, YearMonth bulan, String kodeKursusFilter) {
        if (bulan == null) return List.of();
        return getSemuaAgendaGuru(guru, kodeKursusFilter).stream()
                .filter(a -> a.padaBulan(bulan))
                .toList();
    }

    public List<CalendarAgenda> filterHariGuru(Teacher guru, LocalDate tanggal, String kodeKursusFilter) {
        if (tanggal == null) return List.of();
        return getSemuaAgendaGuru(guru, kodeKursusFilter).stream()
                .filter(a -> a.padaTanggal(tanggal))
                .toList();
    }

    public List<CalendarAgenda> filterMendatangGuru(Teacher guru, LocalDateTime sekarang, String kodeKursusFilter) {
        LocalDateTime acuan = sekarang != null ? sekarang : LocalDateTime.now();
        return getSemuaAgendaGuru(guru, kodeKursusFilter).stream()
                .filter(a -> a.isMendatangAtauBerlangsung(acuan))
                .toList();
    }

    // ==================== HELPER PENGUMPUL AGENDA ====================

    private void kumpulkanAgendaTugasSiswa(Student siswa, Course kursus, Chapter bab, AssignmentMaterial tugas,
                                           List<CalendarAgenda> hasil) {
        if (tugas.getDeadline() == null || !tugas.isBolehAkses(siswa)) {
            return;
        }
        boolean sudahKumpul = (tugasService != null
                && tugasService.cariPengumpulan(tugas.getIdMateri(), siswa.getNis()).isPresent())
                || (progresService != null && progresService.isSelesai(siswa, kursus, tugas));

        String info = "Batas pengumpulan pukul " + tugas.getDeadline().format(FORMAT_JAM) + " WIB";
        hasil.add(new CalendarAgenda(
                JenisAgenda.TUGAS,
                "Deadline Tugas",
                tugas.getJudul(),
                tugas.getDeadline(),
                null,
                info,
                kursus,
                bab,
                tugas,
                sudahKumpul,
                null
        ));
    }

    private void kumpulkanAgendaTugasGuru(Course kursus, Chapter bab, AssignmentMaterial tugas,
                                          List<CalendarAgenda> hasil) {
        if (tugas.getDeadline() == null) {
            return;
        }
        int jmlKumpul = tugasService != null
                ? tugasService.getDaftarPengumpulan(tugas.getIdMateri()).size() : 0;
        String labelPartisipasi = jmlKumpul + " dikumpulkan";
        String target = tugas.isSemuaKelas() ? "Semua Kelas" : "Kelas " + tugas.getTargetKelas();
        String info = "Batas pukul " + tugas.getDeadline().format(FORMAT_JAM) + " WIB · Target: " + target;

        hasil.add(new CalendarAgenda(
                JenisAgenda.TUGAS,
                "Deadline Tugas",
                tugas.getJudul(),
                tugas.getDeadline(),
                null,
                info,
                kursus,
                bab,
                tugas,
                false,
                labelPartisipasi
        ));
    }

    private void kumpulkanAgendaUjianAtauKuis(Student siswa, boolean modeGuru, Course kursus, Chapter bab,
                                              Material materi, String kodeUjian, JenisAgenda jenis,
                                              List<CalendarAgenda> hasil) {
        if (ujianService == null || kodeUjian == null) {
            return;
        }
        Optional<Exam> opt = ujianService.cariUjian(kodeUjian);
        if (opt.isEmpty()) {
            return;
        }
        Exam u = opt.get();
        if (!u.hasJadwalAkses()) {
            return;
        }

        boolean sudahKerja = false;
        String infoPartisipasiGuru = null;

        if (modeGuru) {
            long pesertaUnik = rekapService == null ? 0 : rekapService.getSemuaRiwayat().stream()
                    .filter(r -> r.kodeUjian().equals(u.getKodeUjian()))
                    .map(GradeHistory::nis)
                    .distinct()
                    .count();
            infoPartisipasiGuru = pesertaUnik + " peserta";
        } else if (siswa != null) {
            sudahKerja = (rekapService != null && rekapService.sudahMengerjakan(siswa, u))
                    || (progresService != null && progresService.isSelesai(siswa, kursus, materi));
        }

        LocalDateTime mulai = u.getWaktuMulai();
        LocalDateTime akhir = u.getWaktuSelesai();
        String namaJenis = jenis == JenisAgenda.UJIAN ? "Ujian" : "Kuis";
        String infoDurasi = " (" + u.getDurasiMenit() + " menit)";

        if (mulai != null && akhir != null) {
            if (mulai.toLocalDate().equals(akhir.toLocalDate())) {
                String info = "Jadwal pengerjaan " + mulai.format(FORMAT_JAM) + " - "
                        + akhir.format(FORMAT_JAM) + " WIB" + infoDurasi;
                hasil.add(new CalendarAgenda(
                        jenis,
                        jenis.getNamaDefault(),
                        materi.getJudul(),
                        mulai,
                        akhir,
                        info,
                        kursus,
                        bab,
                        materi,
                        sudahKerja,
                        infoPartisipasiGuru
                ));
            } else {
                String infoBuka = "Dibuka " + mulai.format(FORMAT_RINGKAS) + " s.d. "
                        + akhir.format(FORMAT_RINGKAS) + " WIB" + infoDurasi;
                hasil.add(new CalendarAgenda(
                        jenis,
                        namaJenis + " Dibuka",
                        materi.getJudul(),
                        mulai,
                        null,
                        infoBuka,
                        kursus,
                        bab,
                        materi,
                        sudahKerja,
                        infoPartisipasiGuru
                ));

                String infoTutup = "Batas akhir pengerjaan pukul " + akhir.format(FORMAT_JAM)
                        + " WIB" + infoDurasi;
                hasil.add(new CalendarAgenda(
                        jenis,
                        "Batas " + namaJenis,
                        materi.getJudul(),
                        akhir,
                        null,
                        infoTutup,
                        kursus,
                        bab,
                        materi,
                        sudahKerja,
                        infoPartisipasiGuru
                ));
            }
        } else if (mulai != null) {
            String info = "Dibuka mulai pukul " + mulai.format(FORMAT_JAM) + " WIB" + infoDurasi;
            hasil.add(new CalendarAgenda(
                    jenis,
                    namaJenis + " Dibuka",
                    materi.getJudul(),
                    mulai,
                    null,
                    info,
                    kursus,
                    bab,
                    materi,
                    sudahKerja,
                    infoPartisipasiGuru
            ));
        } else {
            String info = "Batas akhir pengerjaan pukul " + akhir.format(FORMAT_JAM) + " WIB" + infoDurasi;
            hasil.add(new CalendarAgenda(
                    jenis,
                    "Batas " + namaJenis,
                    materi.getJudul(),
                    akhir,
                    null,
                    info,
                    kursus,
                    bab,
                    materi,
                    sudahKerja,
                    infoPartisipasiGuru
            ));
        }
    }
}
