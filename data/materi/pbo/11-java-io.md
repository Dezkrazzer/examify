# Java I/O

Java I/O dipakai untuk membaca dan menulis data dari file, jaringan, atau konsol. Examify menyimpan semua datanya di file teks UTF-8.

## Membaca file baris per baris

```java
try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    String baris;
    while ((baris = br.readLine()) != null) {
        // proses satu baris
    }
}
```

## Menulis (append)

```java
try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
    bw.write(baris);
    bw.newLine();
}
```

## Class penting di java.nio.file

- `Path` - lokasi file
- `Files.readString`, `Files.writeString`
- `Files.exists`, `Files.size`, `Files.createDirectories`

## File yang dipakai Examify

1. `data/siswa.txt` - akun siswa
2. `data/bank_soal.txt` - ujian dan soal
3. `data/kursus.txt` - kursus, bab, dan materi
4. `output/rekap_nilai.txt` - riwayat nilai (append-only)
5. `output/progres_belajar.txt` - materi yang sudah dibaca
6. `output/sertifikat/*.txt` - e-sertifikat

> Selalu tentukan charset (`UTF_8`) agar file terbaca sama di semua komputer.
