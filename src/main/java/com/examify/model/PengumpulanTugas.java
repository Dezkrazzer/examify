package com.examify.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Data rekaman berkas pengumpulan tugas yang dikirim oleh siswa. */
public class PengumpulanTugas {

    public static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMAT_TAMPILAN = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final String idPengumpulan;
    private final String idTugas;
    private final String nis;
    private final String namaSiswa;
    private final String kelasSiswa;
    private final LocalDateTime waktuKumpul;
    private final String namaBerkas;
    private final String pathBerkas;
    private final String catatan;

    public PengumpulanTugas(String idPengumpulan, String idTugas, String nis, String namaSiswa,
                            String kelasSiswa, LocalDateTime waktuKumpul, String namaBerkas,
                            String pathBerkas, String catatan) {
        this.idPengumpulan = idPengumpulan;
        this.idTugas = idTugas;
        this.nis = nis;
        this.namaSiswa = namaSiswa;
        this.kelasSiswa = kelasSiswa;
        this.waktuKumpul = waktuKumpul;
        this.namaBerkas = namaBerkas;
        this.pathBerkas = pathBerkas;
        this.catatan = catatan == null ? "" : catatan;
    }

    public String getIdPengumpulan() {
        return idPengumpulan;
    }

    public String getIdTugas() {
        return idTugas;
    }

    public String getNis() {
        return nis;
    }

    public String getNamaSiswa() {
        return namaSiswa;
    }

    public String getKelasSiswa() {
        return kelasSiswa;
    }

    public LocalDateTime getWaktuKumpul() {
        return waktuKumpul;
    }

    public String getNamaBerkas() {
        return namaBerkas;
    }

    public String getPathBerkas() {
        return pathBerkas;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getWaktuKumpulFormatted() {
        if (waktuKumpul == null) return "-";
        return waktuKumpul.format(FORMAT_TAMPILAN) + " WIB";
    }
}
