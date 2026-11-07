package com.examify.service;

import com.examify.io.PenulisBaris;
import com.examify.model.CatatanProgres;
import com.examify.model.Kursus;
import com.examify.model.Materi;
import com.examify.model.MateriUjian;
import com.examify.model.Siswa;

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
public class ProgresBelajarService {

    private final Map<String, Set<String>> selesaiPerKursus = new HashMap<>(); // "NIS#KURSUS" -> id materi
    private final PenulisBaris<CatatanProgres> writer;
    private final KursusService kursusService;
    private final RekapService rekapService;
    private final Clock clock;

    public ProgresBelajarService(PenulisBaris<CatatanProgres> writer, List<CatatanProgres> tersimpan,
                                 KursusService kursusService, RekapService rekapService) {
        this(writer, tersimpan, kursusService, rekapService, Clock.systemDefaultZone());
    }

    public ProgresBelajarService(PenulisBaris<CatatanProgres> writer, List<CatatanProgres> tersimpan,
                                 KursusService kursusService, RekapService rekapService, Clock clock) {
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
    public void tandaiSelesai(Siswa siswa, Kursus kursus, Materi materi) throws IOException {
        if (materi instanceof MateriUjian) {
            return; // status ujian mengikuti rekap nilai
        }
        if (himpunanSelesai(siswa.getNis(), kursus.getKode()).add(materi.getIdMateri())) {
            writer.tulis(new CatatanProgres(LocalDateTime.now(clock), siswa.getNis(), kursus.getKode(),
                    materi.getIdMateri()));
        }
    }

    public boolean isSelesai(Siswa siswa, Kursus kursus, Materi materi) {
        if (materi instanceof MateriUjian mu) {
            return rekapService.sudahMengerjakan(siswa, kursusService.ujianDari(mu));
        }
        return himpunanSelesai(siswa.getNis(), kursus.getKode()).contains(materi.getIdMateri());
    }

    public int getJumlahSelesai(Siswa siswa, Kursus kursus) {
        return (int) kursus.getSemuaMateri().stream()
                .filter(materi -> isSelesai(siswa, kursus, materi))
                .count();
    }

    /** @return 0.0 - 1.0 */
    public double getProgres(Siswa siswa, Kursus kursus) {
        int total = kursus.getJumlahMateri();
        return total == 0 ? 0 : (double) getJumlahSelesai(siswa, kursus) / total;
    }

    public int getTotalMateriSelesai(Siswa siswa) {
        return kursusService.getDaftarKursus().stream()
                .mapToInt(kursus -> getJumlahSelesai(siswa, kursus))
                .sum();
    }
}
