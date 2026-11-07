package com.examify.service;







import com.examify.io.KursusWriter;



import com.examify.model.Kursus;



import com.examify.model.Materi;



import com.examify.model.MateriQuiz;



import com.examify.model.MateriTeks;



import com.examify.model.MateriUjian;



import com.examify.model.Ujian;







import java.io.IOException;



import java.nio.charset.StandardCharsets;



import java.nio.file.Files;



import java.nio.file.Path;



import java.util.Comparator;



import java.util.LinkedHashMap;



import java.util.List;



import java.util.Map;



import java.util.Optional;







/** Katalog kursus e-learning beserta akses ke isi materi dan ujian di dalamnya. */



public class KursusService {







    private final Map<String, Kursus> katalog = new LinkedHashMap<>();



    private final UjianService ujianService;



    private final KursusWriter writer;



    private final Path fileKursus;







    public KursusService(List<Kursus> daftarKursus, UjianService ujianService) {



        this(daftarKursus, ujianService, null, null);



    }







    public KursusService(List<Kursus> daftarKursus, UjianService ujianService,



                         KursusWriter writer, Path fileKursus) {



        this.ujianService = ujianService;



        this.writer = writer;



        this.fileKursus = fileKursus;



        daftarKursus.forEach(kursus -> katalog.put(kursus.getKode(), kursus));



        pastikanReferensiUjianValid();



    }







    private void pastikanReferensiUjianValid() {



        for (Kursus kursus : katalog.values()) {



            for (Materi materi : kursus.getSemuaMateri()) {



                if (materi instanceof MateriUjian mu && ujianService.cariUjian(mu.getKodeUjian()).isEmpty()) {



                    throw new IllegalStateException("Kursus " + kursus.getKode() + " merujuk ujian tidak dikenal: "



                            + mu.getKodeUjian());



                }



                if (materi instanceof MateriQuiz mq && ujianService.cariUjian(mq.getKodeUjian()).isEmpty()) {



                    throw new IllegalStateException("Kursus " + kursus.getKode() + " merujuk kuis tidak dikenal: "



                            + mq.getKodeUjian());



                }



            }



        }



    }







    public List<Kursus> getDaftarKursus() {



        return List.copyOf(katalog.values());



    }







    public Optional<Kursus> cariKursus(String kode) {



        if (kode == null) return Optional.empty();



        return Optional.ofNullable(katalog.get(kode.trim()));



    }







    /** Kursus yang cocok dengan kata kunci, diurutkan berdasarkan nama. */



    public List<Kursus> cari(String kataKunci) {



        return katalog.values().stream()



                .filter(kursus -> kursus.cocokDengan(kataKunci))



                .sorted(Comparator.comparing(Kursus::getNama))



                .toList();



    }







    public synchronized void tambahKursus(Kursus kursus) throws IOException {



        if (katalog.containsKey(kursus.getKode())) {



            throw new IllegalArgumentException("Kode kursus sudah digunakan: " + kursus.getKode());



        }



        katalog.put(kursus.getKode(), kursus);



        simpanKeFile();



    }







    public synchronized void perbaruiKursus(Kursus kursus) throws IOException {



        katalog.put(kursus.getKode(), kursus);



        simpanKeFile();



    }







    public synchronized Kursus ubahKodeKursus(Kursus kursusLama, String kodeBaru) throws IOException {
        if (kodeBaru == null || kodeBaru.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode kursus tidak boleh kosong");
        }
        String kodeBersih = kodeBaru.trim().toUpperCase();
        if (!kodeBersih.equals(kursusLama.getKode()) && katalog.containsKey(kodeBersih)) {
            throw new IllegalArgumentException("Kode kursus sudah digunakan: " + kodeBersih);
        }
        katalog.remove(kursusLama.getKode());
        Kursus kursusBaru = kursusLama.denganKode(kodeBersih);
        katalog.put(kodeBersih, kursusBaru);
        simpanKeFile();
        return kursusBaru;
    }







    public synchronized void simpanKeFile() throws IOException {



        if (writer != null && fileKursus != null) {



            writer.tulis(fileKursus, getDaftarKursus());



        }



    }







    public String bacaTeks(MateriTeks materi) throws IOException {



        return Files.readString(materi.getFile(), StandardCharsets.UTF_8);



    }







    public void simpanTeksMateri(MateriTeks materi, String isiKonten) throws IOException {



        Path file = materi.getFile();



        if (file.getParent() != null) {



            Files.createDirectories(file.getParent());



        }



        Files.writeString(file, isiKonten, StandardCharsets.UTF_8);



    }







    public MateriTeks buatMateriTeksBaru(Kursus kursus, String judul, String isiKonten) throws IOException {



        String idMateri = kursus.getKode().toLowerCase() + "-m" + (System.currentTimeMillis() % 100000);



        Path folderKursus = com.examify.utils.AppConfig.DIR_DATA.resolve("materi").resolve(kursus.getKode().toLowerCase());



        Files.createDirectories(folderKursus);



        Path fileTarget = folderKursus.resolve(idMateri + ".md");



        Files.writeString(fileTarget, isiKonten, StandardCharsets.UTF_8);



        return new MateriTeks(idMateri, judul, fileTarget);



    }







    public Ujian ujianDari(MateriUjian materi) {



        return ujianService.cariUjian(materi.getKodeUjian()).orElseThrow();



    }







    public Ujian ujianDari(MateriQuiz materi) {



        return ujianService.cariUjian(materi.getKodeUjian()).orElseThrow();



    }







    public int getJumlahKursus() {



        return katalog.size();



    }







    public int getJumlahMateri() {



        return getJumlahMateriSemuaKursus();



    }







    public int getJumlahMateriSemuaKursus() {



        return katalog.values().stream()



                .mapToInt(Kursus::getJumlahMateri)



                .sum();



    }



}



