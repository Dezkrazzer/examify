package com.examify.service;

import com.examify.model.AgendaKalender;
import com.examify.model.AgendaKalender.JenisAgenda;
import com.examify.model.Bab;
import com.examify.model.Guru;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriQuiz;
import com.examify.model.MateriTugas;
import com.examify.model.MateriUjian;
import com.examify.model.RiwayatNilai;
import com.examify.model.Siswa;
import com.examify.model.Ujian;

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
public class KalenderService {

    private static final Locale LOKAL_ID = Locale.of("id", "ID");
    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMAT_RINGKAS = DateTimeFormatter.ofPattern("dd MMM HH:mm", LOKAL_ID);

    private final KursusService kursusService;
    private final UjianService ujianService;
    private final PendaftaranKursusService pendaftaranService;
    private final ProgresBelajarService progresService;
    private final TugasService tugasService;
    private final RekapService rekapService;

    public KalenderService(KursusService kursusService,
                           UjianService ujianService,
                           PendaftaranKursusService pendaftaranService,
                           ProgresBelajarService progresService,
                           TugasService tugasService,
                           RekapService rekapService) {
        this.kursusService = kursusService;
        this.ujianService = ujianService;
        this.pendaftaranService = pendaftaranService;
        this.progresService = progresService;
        this.tugasService = tugasService;
        this.rekapService = rekapService;
    }

    // ==================== KURSUS MILIK SISWA / GURU ====================

    /** Daftar kursus yang sudah diikuti oleh siswa (sudah masuk lewat kode atau memiliki progres). */
    public List<Kursus> getKursusDiikuti(Siswa siswa) {
        if (siswa == null || kursusService == null) {
            return List.of();
        }
        return kursusService.getDaftarKursus().stream()
                .filter(k -> isTerdaftar(siswa, k))
                .sorted(Comparator.comparing(Kursus::getNama, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /** Daftar kursus yang dibuat/diampu oleh guru (berdasarkan NIP pembuat atau nama pengajar). */
    public List<Kursus> getKursusDibuatGuru(Guru guru) {
        if (guru == null || kursusService == null) {
            return List.of();
        }
        return kursusService.getDaftarKursus().stream()
                .filter(k -> isKursusMilikGuru(guru, k))
                .sorted(Comparator.comparing(Kursus::getNama, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public boolean isKursusMilikGuru(Guru guru, Kursus kursus) {
        if (guru == null || kursus == null) {
            return false;
        }
        if (!kursus.getNipPembuat().isBlank()) {
            return kursus.getNipPembuat().equalsIgnoreCase(guru.getNip());
        }
        return !kursus.getPengajar().isBlank()
                && kursus.getPengajar().equalsIgnoreCase(guru.getNama().trim());
    }

    private boolean isTerdaftar(Siswa siswa, Kursus kursus) {
        boolean tercatat = pendaftaranService != null && pendaftaranService.isTerdaftar(siswa, kursus);
        boolean adaProgres = progresService != null && progresService.getProgres(siswa, kursus) > 0;
        return tercatat || adaProgres;
    }

    // ==================== FILTER KALENDER SISWA ====================

    public List<AgendaKalender> getSemuaAgenda(Siswa siswa, String kodeKursusFilter) {
        List<Kursus> diikuti = getKursusDiikuti(siswa);
        if (diikuti.isEmpty()) {
            return List.of();
        }

        String filterBersih = kodeKursusFilter == null ? "" : kodeKursusFilter.trim();
        List<AgendaKalender> hasil = new ArrayList<>();

        for (Kursus kursus : diikuti) {
            if (!filterBersih.isEmpty() && !kursus.getKode().equals(filterBersih)) {
                continue;
            }
            for (Bab bab : kursus.getDaftarBab()) {
                for (Materi materi : bab.getDaftarMateri()) {
                    if (materi instanceof MateriTugas tugas) {
                        kumpulkanAgendaTugasSiswa(siswa, kursus, bab, tugas, hasil);
                    } else if (materi instanceof MateriUjian mu) {
                        kumpulkanAgendaUjianAtauKuis(siswa, false, kursus, bab, mu,
                                mu.getKodeUjian(), JenisAgenda.UJIAN, hasil);
                    } else if (materi instanceof MateriQuiz mq) {
                        kumpulkanAgendaUjianAtauKuis(siswa, false, kursus, bab, mq,
                                mq.getKodeUjian(), JenisAgenda.KUIS, hasil);
                    }
                }
            }
        }

        hasil.sort(AgendaKalender::compareTo);
        return List.copyOf(hasil);
    }

    public List<AgendaKalender> filterBulan(Siswa siswa, YearMonth bulan, String kodeKursusFilter) {
        if (bulan == null) return List.of();
        return getSemuaAgenda(siswa, kodeKursusFilter).stream()
                .filter(a -> a.padaBulan(bulan))
                .toList();
    }

    public List<AgendaKalender> filterHari(Siswa siswa, LocalDate tanggal, String kodeKursusFilter) {
        if (tanggal == null) return List.of();
        return getSemuaAgenda(siswa, kodeKursusFilter).stream()
                .filter(a -> a.padaTanggal(tanggal))
                .toList();
    }

    public List<AgendaKalender> filterMendatang(Siswa siswa, LocalDateTime sekarang, String kodeKursusFilter) {
        LocalDateTime acuan = sekarang != null ? sekarang : LocalDateTime.now();
        return getSemuaAgenda(siswa, kodeKursusFilter).stream()
                .filter(a -> a.isMendatangAtauBerlangsung(acuan))
                .toList();
    }

    // ==================== FILTER KALENDER GURU ====================

    public List<AgendaKalender> getSemuaAgendaGuru(Guru guru, String kodeKursusFilter) {
        List<Kursus> dibuat = getKursusDibuatGuru(guru);
        if (dibuat.isEmpty()) {
            return List.of();
        }

        String filterBersih = kodeKursusFilter == null ? "" : kodeKursusFilter.trim();
        List<AgendaKalender> hasil = new ArrayList<>();

        for (Kursus kursus : dibuat) {
            if (!filterBersih.isEmpty() && !kursus.getKode().equals(filterBersih)) {
                continue;
            }
            for (Bab bab : kursus.getDaftarBab()) {
                for (Materi materi : bab.getDaftarMateri()) {
                    if (materi instanceof MateriTugas tugas) {
                        kumpulkanAgendaTugasGuru(kursus, bab, tugas, hasil);
                    } else if (materi instanceof MateriUjian mu) {
                        kumpulkanAgendaUjianAtauKuis(null, true, kursus, bab, mu,
                                mu.getKodeUjian(), JenisAgenda.UJIAN, hasil);
                    } else if (materi instanceof MateriQuiz mq) {
                        kumpulkanAgendaUjianAtauKuis(null, true, kursus, bab, mq,
                                mq.getKodeUjian(), JenisAgenda.KUIS, hasil);
                    }
                }
            }
        }

        hasil.sort(AgendaKalender::compareTo);
        return List.copyOf(hasil);
    }

    public List<AgendaKalender> filterBulanGuru(Guru guru, YearMonth bulan, String kodeKursusFilter) {
        if (bulan == null) return List.of();
        return getSemuaAgendaGuru(guru, kodeKursusFilter).stream()
                .filter(a -> a.padaBulan(bulan))
                .toList();
    }

    public List<AgendaKalender> filterHariGuru(Guru guru, LocalDate tanggal, String kodeKursusFilter) {
        if (tanggal == null) return List.of();
        return getSemuaAgendaGuru(guru, kodeKursusFilter).stream()
                .filter(a -> a.padaTanggal(tanggal))
                .toList();
    }

    public List<AgendaKalender> filterMendatangGuru(Guru guru, LocalDateTime sekarang, String kodeKursusFilter) {
        LocalDateTime acuan = sekarang != null ? sekarang : LocalDateTime.now();
        return getSemuaAgendaGuru(guru, kodeKursusFilter).stream()
                .filter(a -> a.isMendatangAtauBerlangsung(acuan))
                .toList();
    }

    // ==================== HELPER PENGUMPUL AGENDA ====================

    private void kumpulkanAgendaTugasSiswa(Siswa siswa, Kursus kursus, Bab bab, MateriTugas tugas,
                                           List<AgendaKalender> hasil) {
        if (tugas.getDeadline() == null || !tugas.isBolehAkses(siswa)) {
            return;
        }
        boolean sudahKumpul = (tugasService != null
                && tugasService.cariPengumpulan(tugas.getIdMateri(), siswa.getNis()).isPresent())
                || (progresService != null && progresService.isSelesai(siswa, kursus, tugas));

        String info = "Batas pengumpulan pukul " + tugas.getDeadline().format(FORMAT_JAM) + " WIB";
        hasil.add(new AgendaKalender(
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

    private void kumpulkanAgendaTugasGuru(Kursus kursus, Bab bab, MateriTugas tugas,
                                          List<AgendaKalender> hasil) {
        if (tugas.getDeadline() == null) {
            return;
        }
        int jmlKumpul = tugasService != null
                ? tugasService.getDaftarPengumpulan(tugas.getIdMateri()).size() : 0;
        String labelPartisipasi = jmlKumpul + " dikumpulkan";
        String target = tugas.isSemuaKelas() ? "Semua Kelas" : "Kelas " + tugas.getTargetKelas();
        String info = "Batas pukul " + tugas.getDeadline().format(FORMAT_JAM) + " WIB · Target: " + target;

        hasil.add(new AgendaKalender(
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

    private void kumpulkanAgendaUjianAtauKuis(Siswa siswa, boolean modeGuru, Kursus kursus, Bab bab,
                                              Materi materi, String kodeUjian, JenisAgenda jenis,
                                              List<AgendaKalender> hasil) {
        if (ujianService == null || kodeUjian == null) {
            return;
        }
        Optional<Ujian> opt = ujianService.cariUjian(kodeUjian);
        if (opt.isEmpty()) {
            return;
        }
        Ujian u = opt.get();
        if (!u.hasJadwalAkses()) {
            return;
        }

        boolean sudahKerja = false;
        String infoPartisipasiGuru = null;

        if (modeGuru) {
            long pesertaUnik = rekapService == null ? 0 : rekapService.getSemuaRiwayat().stream()
                    .filter(r -> r.kodeUjian().equals(u.getKodeUjian()))
                    .map(RiwayatNilai::nis)
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
                hasil.add(new AgendaKalender(
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
                hasil.add(new AgendaKalender(
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
                hasil.add(new AgendaKalender(
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
            hasil.add(new AgendaKalender(
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
            hasil.add(new AgendaKalender(
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
