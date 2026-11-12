<div align="center">

<img src="../src/main/resources/com/examify/img/examifylogo.png" alt="Logo Examify" width="120">

# Examify: Daftar Data

### Kelompok 8

Dokumentasi seluruh data bawaan di folder `data/` dalam bentuk tabel.

![Siswa](https://img.shields.io/badge/Siswa-6-1E88E5?style=flat-square) 

</div>

> [!NOTE]
> File ini dibuat dari isi `siswa.txt`, `guru.txt`, `tim.txt`, `kursus.txt`, dan `bank_soal.txt`. Bila file data diubah, perbarui juga dokumen ini.

---

## Daftar Isi

1. [Ringkasan](#1-ringkasan)
2. [Data Siswa](#2-data-siswa)
3. [Data Guru](#3-data-guru)
4. [Anggota Kelompok](#4-anggota-kelompok)
5. [Daftar Kursus](#5-daftar-kursus)
6. [Daftar Ujian & Token](#6-daftar-ujian--token)
7. [Detail Kursus (Bab & Materi)](#7-detail-kursus-bab--materi)
   - [7.1 Pendidikan Agama](#71-pendidikan-agama)
   - [7.2 Konsep Pemrograman](#72-konsep-pemrograman)
   - [7.3 Sistem Digital](#73-sistem-digital)
   - [7.4 Kalkulus I](#74-kalkulus-i)
   - [7.5 Fisika](#75-fisika)
   - [7.6 Bahasa Inggris I](#76-bahasa-inggris-i)
   - [7.7 Statistika & Probabilitas](#77-statistika--probabilitas)
   - [7.8 Bahasa Indonesia](#78-bahasa-indonesia)
   - [7.9 Kalkulus II](#79-kalkulus-ii)
   - [7.10 Matematika Diskrit I](#710-matematika-diskrit-i)
   - [7.11 Aljabar Linier](#711-aljabar-linier)
   - [7.12 Struktur Data & Algoritma](#712-struktur-data--algoritma)
   - [7.13 Organisasi Sistem Komputer](#713-organisasi-sistem-komputer)
   - [7.14 Pendidikan Kewarganegaraan](#714-pendidikan-kewarganegaraan)
   - [7.15 Bahasa Inggris II](#715-bahasa-inggris-ii)
   - [7.16 Matematika Diskrit II](#716-matematika-diskrit-ii)
   - [7.17 Pemrograman Berorientasi Objek](#717-pemrograman-berorientasi-objek)
   - [7.18 Basis Data](#718-basis-data)
   - [7.19 Sistem Operasi](#719-sistem-operasi)
   - [7.20 Pendidikan Pancasila](#720-pendidikan-pancasila)
   - [7.21 Metode Numerik](#721-metode-numerik)
   - [7.22 Desain & Analisis Algoritma](#722-desain--analisis-algoritma)
   - [7.23 Kelas Uji Coba](#723-kelas-uji-coba)
8. [Bank Soal](#8-bank-soal)
   - [8.1 PAI-01: UTS Pendidikan Agama](#81-pai-01-uts-pendidikan-agama)
   - [8.2 PAI-02: UAS Pendidikan Agama](#82-pai-02-uas-pendidikan-agama)
   - [8.3 KP-01: UTS Konsep Pemrograman](#83-kp-01-uts-konsep-pemrograman)
   - [8.4 KP-02: UAS Konsep Pemrograman](#84-kp-02-uas-konsep-pemrograman)
   - [8.5 SDIG-01: UTS Sistem Digital](#85-sdig-01-uts-sistem-digital)
   - [8.6 SDIG-02: UAS Sistem Digital](#86-sdig-02-uas-sistem-digital)
   - [8.7 KALK-01: UTS Kalkulus I](#87-kalk-01-uts-kalkulus-i)
   - [8.8 KALK-02: UAS Kalkulus I](#88-kalk-02-uas-kalkulus-i)
   - [8.9 FIS-01: UTS Fisika](#89-fis-01-uts-fisika)
   - [8.10 FIS-02: UAS Fisika](#810-fis-02-uas-fisika)
   - [8.11 BING-01: UTS Bahasa Inggris I](#811-bing-01-uts-bahasa-inggris-i)
   - [8.12 BING-02: UAS Bahasa Inggris I](#812-bing-02-uas-bahasa-inggris-i)
   - [8.13 STAT-01: UTS Statistika & Probabilitas](#813-stat-01-uts-statistika--probabilitas)
   - [8.14 STAT-02: UAS Statistika & Probabilitas](#814-stat-02-uas-statistika--probabilitas)
   - [8.15 BIND-01: UTS Bahasa Indonesia](#815-bind-01-uts-bahasa-indonesia)
   - [8.16 BIND-02: UAS Bahasa Indonesia](#816-bind-02-uas-bahasa-indonesia)
   - [8.17 KALB-01: UTS Kalkulus II](#817-kalb-01-uts-kalkulus-ii)
   - [8.18 KALB-02: UAS Kalkulus II](#818-kalb-02-uas-kalkulus-ii)
   - [8.19 MDIS-01: UTS Matematika Diskrit I](#819-mdis-01-uts-matematika-diskrit-i)
   - [8.20 MDIS-02: UAS Matematika Diskrit I](#820-mdis-02-uas-matematika-diskrit-i)
   - [8.21 ALIN-01: UTS Aljabar Linier](#821-alin-01-uts-aljabar-linier)
   - [8.22 ALIN-02: UAS Aljabar Linier](#822-alin-02-uas-aljabar-linier)
   - [8.23 SDA-01: UTS Struktur Data & Algoritma](#823-sda-01-uts-struktur-data--algoritma)
   - [8.24 SDA-02: UAS Struktur Data & Algoritma](#824-sda-02-uas-struktur-data--algoritma)
   - [8.25 OSK-01: UTS Organisasi Sistem Komputer](#825-osk-01-uts-organisasi-sistem-komputer)
   - [8.26 OSK-02: UAS Organisasi Sistem Komputer](#826-osk-02-uas-organisasi-sistem-komputer)
   - [8.27 PKN-01: UTS Pendidikan Kewarganegaraan](#827-pkn-01-uts-pendidikan-kewarganegaraan)
   - [8.28 PKN-02: UAS Pendidikan Kewarganegaraan](#828-pkn-02-uas-pendidikan-kewarganegaraan)
   - [8.29 BINGB-01: UTS Bahasa Inggris II](#829-bingb-01-uts-bahasa-inggris-ii)
   - [8.30 BINGB-02: UAS Bahasa Inggris II](#830-bingb-02-uas-bahasa-inggris-ii)
   - [8.31 MDISB-01: UTS Matematika Diskrit II](#831-mdisb-01-uts-matematika-diskrit-ii)
   - [8.32 MDISB-02: UAS Matematika Diskrit II](#832-mdisb-02-uas-matematika-diskrit-ii)
   - [8.33 PBO-01: UTS Pemrograman Berorientasi Objek](#833-pbo-01-uts-pemrograman-berorientasi-objek)
   - [8.34 PBO-02: UAS Pemrograman Berorientasi Objek](#834-pbo-02-uas-pemrograman-berorientasi-objek)
   - [8.35 BDAT-01: UTS Basis Data](#835-bdat-01-uts-basis-data)
   - [8.36 BDAT-02: UAS Basis Data](#836-bdat-02-uas-basis-data)
   - [8.37 SISOP-01: UTS Sistem Operasi](#837-sisop-01-uts-sistem-operasi)
   - [8.38 SISOP-02: UAS Sistem Operasi](#838-sisop-02-uas-sistem-operasi)
   - [8.39 PANC-01: UTS Pendidikan Pancasila](#839-panc-01-uts-pendidikan-pancasila)
   - [8.40 PANC-02: UAS Pendidikan Pancasila](#840-panc-02-uas-pendidikan-pancasila)
   - [8.41 MNUM-01: UTS Metode Numerik](#841-mnum-01-uts-metode-numerik)
   - [8.42 MNUM-02: UAS Metode Numerik](#842-mnum-02-uas-metode-numerik)
   - [8.43 DAA-01: UTS Desain & Analisis Algoritma](#843-daa-01-uts-desain--analisis-algoritma)
   - [8.44 DAA-02: UAS Desain & Analisis Algoritma](#844-daa-02-uas-desain--analisis-algoritma)
   - [8.45 UJI-01: Uji Coba](#845-uji-01-uji-coba)
9. [Keterangan Format File](#9-keterangan-format-file)

---

## 1. Ringkasan

| Data | Jumlah | File |
|------|-------:|------|
| Siswa | 6 | `siswa.txt` |
| Guru | 7 | `guru.txt` |
| Anggota kelompok | 3 | `tim.txt` |
| Kursus | 23 | `kursus.txt` |
| Bab | 353 | `kursus.txt` |
| Item materi (termasuk ujian) | 354 | `kursus.txt` |
| File bacaan Markdown | 322 | `materi/**/*.md` |
| Ujian | 45 | `bank_soal.txt` |
| Soal | 443 (PG 221, TF 177, IS 45) | `bank_soal.txt` |

## 2. Data Siswa

Sumber: `siswa.txt` (format `NIS|NAMA|KELAS|PIN`). Login siswa memakai **NIS + PIN**.

| No | NIS | Nama | Kelas | PIN |
|:--:|-----|------|:-----:|:---:|
| 1 | 2410001 | Andi Pratama | IF-A | 1234 |
| 2 | 2410002 | Budi Santoso | IF-B | 5678 |
| 3 | 2410003 | Citra Lestari | IF-C | 2468 |
| 4 | 2410004 | Dimas Saputra | IF-D | 1111 |
| 5 | 2410005 | Eka Rahmawati | IF-E | 6767 |
| 6 | 0000001 | Lazuardi Akbar Imani | IF-A | 1111 |

## 3. Data Guru

Sumber: `guru.txt` (format `NIP|NAMA|PIN|BIDANG`). Login guru memakai **NIP + PIN**.

| No | NIP | Nama | PIN | Bidang / Mata Kuliah |
|:--:|-----|------|:---:|----------------------|
| 1 | 19850101 | Dr. Andi Wijaya, S.Kom., M.Kom. | 1234 | Matematika Diskrit I & II |
| 2 | 19850102 | Dr. Ratna Sari Dewi, S.Si., M.Sc. | 1234 | Konsep Pemrograman & Aljabar Linier |
| 3 | 19850103 | Siti Nurhaliza, S.Si., M.Kom. | 1234 | Statistika & Probabilitas, Struktur Data & Algoritma |
| 4 | 19850104 | Budi Hartono, S.T., M.T. | 1234 | Organisasi Sistem Komputer & Sistem Operasi |
| 5 | 19850105 | Dr. Dewi Anggraini, S.Si., M.Kom. | 1234 | Metode Numerik |
| 6 | 19850106 | Agus Setiawan, S.T., M.Eng. | 1234 | Basis Data |
| 7 | 19850107 | Rudi Hermawan, S.Kom., M.Cs. | 1234 | Mata Kuliah Umum & Informatika |

## 4. Anggota Kelompok

Sumber: `tim.txt` (format `NIM|NAMA|FOTO|WEBSITE|GITHUB|INSTAGRAM|LINKEDIN`). Ditampilkan di landing page.

| No | NIM | Nama | Foto | Website | GitHub | Instagram | LinkedIn |
|:--:|-----|------|------|---------|--------|-----------|----------|
| 1 | L0125044 | Gilang Ridho Wicaksana | `tim/anggota1.jpg` | - | [link](https://github.com/geel-sana) | [link](https://instagram.com/g.laaang_) | [link](https://linkedin.com/in/gilang-ridho-wicaksana) |
| 2 | L0125105 | Lazuardi Akbar Imani | `tim/anggota2.jpg` | [link](https://lzuardiai.my.id) | [link](https://github.com/Dezkrazzer) | [link](https://instagram.com/lzuardiai) | [link](https://www.linkedin.com/in/lazuardiakbar/) |
| 3 | L0125125 | Alfian Naufal Rabbani | `tim/anggota3.jpeg` | [link](https://portfolio-website.alfiannr234.workers.dev/) | [link](https://github.com/Alfiannr234) | [link](https://instagram.com/alfiannrabbani) | [link](https://linkedin.com/in/alfian-naufal-rabbani) |

## 5. Daftar Kursus

Sumber: `kursus.txt`. Kode kursus dapat dipakai di tombol **Masuk lewat Kode**.

| No | Kode | Nama Kursus | Pengajar | NIP Pembuat | Bab | Bacaan | Ujian |
|:--:|------|-------------|----------|-------------|:---:|:------:|:-----:|
| 1 | `0953112001` | [Pendidikan Agama](#71-pendidikan-agama) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 2 | `0953124002` | [Konsep Pemrograman](#72-konsep-pemrograman) | Dr. Ratna Sari Dewi, S.Si., M.Sc. | 19850102 | 16 | 14 | 2 |
| 3 | `0953123003` | [Sistem Digital](#73-sistem-digital) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 4 | `0953123004` | [Kalkulus I](#74-kalkulus-i) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 5 | `0953123005` | [Fisika](#75-fisika) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 6 | `0953112006` | [Bahasa Inggris I](#76-bahasa-inggris-i) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 7 | `0953123007` | [Statistika & Probabilitas](#77-statistika--probabilitas) | Siti Nurhaliza, S.Si., M.Kom. | 19850103 | 16 | 14 | 2 |
| 8 | `0953112008` | [Bahasa Indonesia](#78-bahasa-indonesia) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 9 | `0953223009` | [Kalkulus II](#79-kalkulus-ii) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 10 | `0953223010` | [Matematika Diskrit I](#710-matematika-diskrit-i) | Dr. Andi Wijaya, S.Kom., M.Kom. | 19850101 | 16 | 14 | 2 |
| 11 | `0953223011` | [Aljabar Linier](#711-aljabar-linier) | Dr. Ratna Sari Dewi, S.Si., M.Sc. | 19850102 | 16 | 14 | 2 |
| 12 | `0953223012` | [Struktur Data & Algoritma](#712-struktur-data--algoritma) | Siti Nurhaliza, S.Si., M.Kom. | 19850103 | 16 | 14 | 2 |
| 13 | `0953223013` | [Organisasi Sistem Komputer](#713-organisasi-sistem-komputer) | Budi Hartono, S.T., M.T. | 19850104 | 16 | 14 | 2 |
| 14 | `0953212014` | [Pendidikan Kewarganegaraan](#714-pendidikan-kewarganegaraan) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 15 | `0953222015` | [Bahasa Inggris II](#715-bahasa-inggris-ii) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 16 | `0953122016` | [Matematika Diskrit II](#716-matematika-diskrit-ii) | Dr. Andi Wijaya, S.Kom., M.Kom. | 19850101 | 16 | 14 | 2 |
| 17 | `0953123017` | [Pemrograman Berorientasi Objek](#717-pemrograman-berorientasi-objek) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 18 | `0953124018` | [Basis Data](#718-basis-data) | Agus Setiawan, S.T., M.Eng. | 19850106 | 16 | 14 | 2 |
| 19 | `0953123019` | [Sistem Operasi](#719-sistem-operasi) | Budi Hartono, S.T., M.T. | 19850104 | 16 | 14 | 2 |
| 20 | `0953112020` | [Pendidikan Pancasila](#720-pendidikan-pancasila) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 21 | `0953123021` | [Metode Numerik](#721-metode-numerik) | Dr. Dewi Anggraini, S.Si., M.Kom. | 19850105 | 16 | 14 | 2 |
| 22 | `0953123022` | [Desain & Analisis Algoritma](#722-desain--analisis-algoritma) | Rudi Hermawan, S.Kom., M.Cs. | 19850107 | 16 | 14 | 2 |
| 23 | `UJI` | [Kelas Uji Coba](#723-kelas-uji-coba) | Tim Examify | - | 1 | 1 | 1 |

## 6. Daftar Ujian & Token

Sumber: `bank_soal.txt`. Kolom kosong pada file berarti nilai bawaan (`ULANG=false`, `STRICTLY_SAFE=false`).

| No | Kode | Mata Pelajaran | Token | Durasi | KKM | Soal | Total Bobot | Boleh Ulang | Mode Aman |
|:--:|------|----------------|:-----:|:------:|:---:|:----:|:-----------:|:-----------:|:---------:|
| 1 | [PAI-01](#81-pai-01-uts-pendidikan-agama) | UTS Pendidikan Agama | `PAIUT1` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 2 | [PAI-02](#82-pai-02-uas-pendidikan-agama) | UAS Pendidikan Agama | `PAIUA1` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 3 | [KP-01](#83-kp-01-uts-konsep-pemrograman) | UTS Konsep Pemrograman | `KPUT21` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 4 | [KP-02](#84-kp-02-uas-konsep-pemrograman) | UAS Konsep Pemrograman | `KPUA22` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 5 | [SDIG-01](#85-sdig-01-uts-sistem-digital) | UTS Sistem Digital | `SDIUT3` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 6 | [SDIG-02](#86-sdig-02-uas-sistem-digital) | UAS Sistem Digital | `SDIUA3` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 7 | [KALK-01](#87-kalk-01-uts-kalkulus-i) | UTS Kalkulus I | `KALUT4` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 8 | [KALK-02](#88-kalk-02-uas-kalkulus-i) | UAS Kalkulus I | `KALUA4` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 9 | [FIS-01](#89-fis-01-uts-fisika) | UTS Fisika | `FISUT5` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 10 | [FIS-02](#810-fis-02-uas-fisika) | UAS Fisika | `FISUA5` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 11 | [BING-01](#811-bing-01-uts-bahasa-inggris-i) | UTS Bahasa Inggris I | `BINUT6` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 12 | [BING-02](#812-bing-02-uas-bahasa-inggris-i) | UAS Bahasa Inggris I | `BINUA6` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 13 | [STAT-01](#813-stat-01-uts-statistika--probabilitas) | UTS Statistika & Probabilitas | `STAUT7` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 14 | [STAT-02](#814-stat-02-uas-statistika--probabilitas) | UAS Statistika & Probabilitas | `STAUA7` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 15 | [BIND-01](#815-bind-01-uts-bahasa-indonesia) | UTS Bahasa Indonesia | `BINUT8` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 16 | [BIND-02](#816-bind-02-uas-bahasa-indonesia) | UAS Bahasa Indonesia | `BINUA8` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 17 | [KALB-01](#817-kalb-01-uts-kalkulus-ii) | UTS Kalkulus II | `KALUT9` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 18 | [KALB-02](#818-kalb-02-uas-kalkulus-ii) | UAS Kalkulus II | `KALUA9` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 19 | [MDIS-01](#819-mdis-01-uts-matematika-diskrit-i) | UTS Matematika Diskrit I | `MDIUT0` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 20 | [MDIS-02](#820-mdis-02-uas-matematika-diskrit-i) | UAS Matematika Diskrit I | `MDIUA0` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 21 | [ALIN-01](#821-alin-01-uts-aljabar-linier) | UTS Aljabar Linier | `ALIUT1` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 22 | [ALIN-02](#822-alin-02-uas-aljabar-linier) | UAS Aljabar Linier | `ALIUA1` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 23 | [SDA-01](#823-sda-01-uts-struktur-data--algoritma) | UTS Struktur Data & Algoritma | `SDAUT2` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 24 | [SDA-02](#824-sda-02-uas-struktur-data--algoritma) | UAS Struktur Data & Algoritma | `SDAUA2` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 25 | [OSK-01](#825-osk-01-uts-organisasi-sistem-komputer) | UTS Organisasi Sistem Komputer | `OSKUT3` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 26 | [OSK-02](#826-osk-02-uas-organisasi-sistem-komputer) | UAS Organisasi Sistem Komputer | `OSKUA3` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 27 | [PKN-01](#827-pkn-01-uts-pendidikan-kewarganegaraan) | UTS Pendidikan Kewarganegaraan | `PKNUT4` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 28 | [PKN-02](#828-pkn-02-uas-pendidikan-kewarganegaraan) | UAS Pendidikan Kewarganegaraan | `PKNUA4` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 29 | [BINGB-01](#829-bingb-01-uts-bahasa-inggris-ii) | UTS Bahasa Inggris II | `BINUT5` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 30 | [BINGB-02](#830-bingb-02-uas-bahasa-inggris-ii) | UAS Bahasa Inggris II | `BINUA5` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 31 | [MDISB-01](#831-mdisb-01-uts-matematika-diskrit-ii) | UTS Matematika Diskrit II | `MDIUT6` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 32 | [MDISB-02](#832-mdisb-02-uas-matematika-diskrit-ii) | UAS Matematika Diskrit II | `MDIUA6` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 33 | [PBO-01](#833-pbo-01-uts-pemrograman-berorientasi-objek) | UTS Pemrograman Berorientasi Objek | `PBOUT7` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 34 | [PBO-02](#834-pbo-02-uas-pemrograman-berorientasi-objek) | UAS Pemrograman Berorientasi Objek | `PBOUA7` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 35 | [BDAT-01](#835-bdat-01-uts-basis-data) | UTS Basis Data | `BDAUT8` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 36 | [BDAT-02](#836-bdat-02-uas-basis-data) | UAS Basis Data | `BDAUA8` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 37 | [SISOP-01](#837-sisop-01-uts-sistem-operasi) | UTS Sistem Operasi | `SISUT9` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 38 | [SISOP-02](#838-sisop-02-uas-sistem-operasi) | UAS Sistem Operasi | `SISUA9` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 39 | [PANC-01](#839-panc-01-uts-pendidikan-pancasila) | UTS Pendidikan Pancasila | `PANUT0` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 40 | [PANC-02](#840-panc-02-uas-pendidikan-pancasila) | UAS Pendidikan Pancasila | `PANUA0` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 41 | [MNUM-01](#841-mnum-01-uts-metode-numerik) | UTS Metode Numerik | `MNUUT1` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 42 | [MNUM-02](#842-mnum-02-uas-metode-numerik) | UAS Metode Numerik | `MNUUA1` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 43 | [DAA-01](#843-daa-01-uts-desain--analisis-algoritma) | UTS Desain & Analisis Algoritma | `DAAUT2` | 60 mnt | 70 | 10 | 100 | Tidak | Tidak |
| 44 | [DAA-02](#844-daa-02-uas-desain--analisis-algoritma) | UAS Desain & Analisis Algoritma | `DAAUA2` | 90 mnt | 70 | 10 | 100 | Tidak | Ya |
| 45 | [UJI-01](#845-uji-01-uji-coba) | Uji Coba | `UJI123` | 1 mnt | 60 | 3 | 30 | Ya | Ya |

## 7. Detail Kursus (Bab & Materi)

### 7.1 Pendidikan Agama

| Atribut | Nilai |
|---------|-------|
| Kode | `0953112001` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Pembentukan karakter beragama, etika akademik, toleransi, dan nilai moral dalam kehidupan bermasyarakat serta profesi teknologi. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Konsep Ketuhanan dan Keimanan | Bacaan | `pai-m01` | Konsep Ketuhanan dan Keimanan | `materi/agama/materi-01.md` |
| Bab 2: Hakikat Manusia dan Tanggung Jawabnya | Bacaan | `pai-m02` | Hakikat Manusia dan Tanggung Jawabnya | `materi/agama/materi-02.md` |
| Bab 3: Sumber Ajaran dan Nilai Moral Agama | Bacaan | `pai-m03` | Sumber Ajaran dan Nilai Moral Agama | `materi/agama/materi-03.md` |
| Bab 4: Etika dan Akhlak dalam Kehidupan Akademik | Bacaan | `pai-m04` | Etika dan Akhlak dalam Kehidupan Akademik | `materi/agama/materi-04.md` |
| Bab 5: Hak Asasi Manusia dan Demokrasi dalam Perspektif Agama | Bacaan | `pai-m05` | Hak Asasi Manusia dan Demokrasi dalam Perspektif Agama | `materi/agama/materi-05.md` |
| Bab 6: Hukum dan Keadilan Sosial | Bacaan | `pai-m06` | Hukum dan Keadilan Sosial | `materi/agama/materi-06.md` |
| Bab 7: Ilmu Pengetahuan, Teknologi, dan Seni dalam Agama | Bacaan | `pai-m07` | Ilmu Pengetahuan, Teknologi, dan Seni dalam Agama | `materi/agama/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `pai-uts` | Ujian Tengah Semester (UTS) Pendidikan Agama | `PAI-01` |
| Bab 9: Kerukunan Antarumat Beragama dan Toleransi | Bacaan | `pai-m08` | Kerukunan Antarumat Beragama dan Toleransi | `materi/agama/materi-08.md` |
| Bab 10: Masyarakat Madani dan Kesejahteraan Umat | Bacaan | `pai-m09` | Masyarakat Madani dan Kesejahteraan Umat | `materi/agama/materi-09.md` |
| Bab 11: Kebudayaan dan Kearifan Lokal | Bacaan | `pai-m10` | Kebudayaan dan Kearifan Lokal | `materi/agama/materi-10.md` |
| Bab 12: Etika Profesi di Era Digital | Bacaan | `pai-m11` | Etika Profesi di Era Digital | `materi/agama/materi-11.md` |
| Bab 13: Keluarga Sakinah dan Tanggung Jawab Sosial | Bacaan | `pai-m12` | Keluarga Sakinah dan Tanggung Jawab Sosial | `materi/agama/materi-12.md` |
| Bab 14: Kepemimpinan dan Amanah dalam Organisasi | Bacaan | `pai-m13` | Kepemimpinan dan Amanah dalam Organisasi | `materi/agama/materi-13.md` |
| Bab 15: Kontribusi Agama dalam Perdamaian Dunia | Bacaan | `pai-m14` | Kontribusi Agama dalam Perdamaian Dunia | `materi/agama/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `pai-uas` | Ujian Akhir Semester (UAS) Pendidikan Agama | `PAI-02` |

### 7.2 Konsep Pemrograman

| Atribut | Nilai |
|---------|-------|
| Kode | `0953124002` |
| Pengajar | Dr. Ratna Sari Dewi, S.Si., M.Sc. |
| NIP Pembuat | 19850102 (Dr. Ratna Sari Dewi, S.Si., M.Sc.) |
| Deskripsi | Dasar logika algoritma, flowchart, tipe data, struktur kontrol, fungsi, array, pointer, dan pengelolaan berkas dalam pemrograman. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengantar Algoritma dan Pemrograman | Bacaan | `kp-m01` | Pengantar Algoritma dan Pemrograman | `materi/konsep_pemrograman/materi-01.md` |
| Bab 2: Pseudocode dan Diagram Alir (Flowchart) | Bacaan | `kp-m02` | Pseudocode dan Diagram Alir (Flowchart) | `materi/konsep_pemrograman/materi-02.md` |
| Bab 3: Tipe Data, Variabel, dan Konstanta | Bacaan | `kp-m03` | Tipe Data, Variabel, dan Konstanta | `materi/konsep_pemrograman/materi-03.md` |
| Bab 4: Operator Aritmatika, Relasional, dan Logika | Bacaan | `kp-m04` | Operator Aritmatika, Relasional, dan Logika | `materi/konsep_pemrograman/materi-04.md` |
| Bab 5: Input dan Output Standar | Bacaan | `kp-m05` | Input dan Output Standar | `materi/konsep_pemrograman/materi-05.md` |
| Bab 6: Percabangan Kondisional (If-Else dan Switch) | Bacaan | `kp-m06` | Percabangan Kondisional (If-Else dan Switch) | `materi/konsep_pemrograman/materi-06.md` |
| Bab 7: Perulangan (For, While, dan Do-While) | Bacaan | `kp-m07` | Perulangan (For, While, dan Do-While) | `materi/konsep_pemrograman/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `kp-uts` | Ujian Tengah Semester (UTS) Konsep Pemrograman | `KP-01` |
| Bab 9: Perulangan Bersarang dan Kontrol Alur | Bacaan | `kp-m08` | Perulangan Bersarang dan Kontrol Alur | `materi/konsep_pemrograman/materi-08.md` |
| Bab 10: Fungsi, Prosedur, dan Ruang Lingkup Variabel | Bacaan | `kp-m09` | Fungsi, Prosedur, dan Ruang Lingkup Variabel | `materi/konsep_pemrograman/materi-09.md` |
| Bab 11: Rekursi dan Penyelesaian Masalah Berulang | Bacaan | `kp-m10` | Rekursi dan Penyelesaian Masalah Berulang | `materi/konsep_pemrograman/materi-10.md` |
| Bab 12: Array Satu Dimensi dan Multi Dimensi | Bacaan | `kp-m11` | Array Satu Dimensi dan Multi Dimensi | `materi/konsep_pemrograman/materi-11.md` |
| Bab 13: Manipulasi String dan Karakter | Bacaan | `kp-m12` | Manipulasi String dan Karakter | `materi/konsep_pemrograman/materi-12.md` |
| Bab 14: Struct, Tipe Data Bentukan, dan Pointer Dasar | Bacaan | `kp-m13` | Struct, Tipe Data Bentukan, dan Pointer Dasar | `materi/konsep_pemrograman/materi-13.md` |
| Bab 15: Operasi File dan Penanganan Kesalahan Dasar | Bacaan | `kp-m14` | Operasi File dan Penanganan Kesalahan Dasar | `materi/konsep_pemrograman/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `kp-uas` | Ujian Akhir Semester (UAS) Konsep Pemrograman | `KP-02` |

### 7.3 Sistem Digital

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123003` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Sistem bilangan biner, aljabar Boolean, gerbang logika, penyederhanaan Karnaugh Map, rangkaian kombinasional, dan rangkaian sekuensial. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Sistem Bilangan dan Konversi Basis | Bacaan | `sdig-m01` | Sistem Bilangan dan Konversi Basis | `materi/sistem_digital/materi-01.md` |
| Bab 2: Aritmatika Biner dan Komplemen Dua | Bacaan | `sdig-m02` | Aritmatika Biner dan Komplemen Dua | `materi/sistem_digital/materi-02.md` |
| Bab 3: Kode Digital (BCD, Gray, ASCII) | Bacaan | `sdig-m03` | Kode Digital (BCD, Gray, ASCII) | `materi/sistem_digital/materi-03.md` |
| Bab 4: Gerbang Logika Dasar dan Universal | Bacaan | `sdig-m04` | Gerbang Logika Dasar dan Universal | `materi/sistem_digital/materi-04.md` |
| Bab 5: Aljabar Boolean dan Teorema De Morgan | Bacaan | `sdig-m05` | Aljabar Boolean dan Teorema De Morgan | `materi/sistem_digital/materi-05.md` |
| Bab 6: Bentuk Kanonik SOP dan POS | Bacaan | `sdig-m06` | Bentuk Kanonik SOP dan POS | `materi/sistem_digital/materi-06.md` |
| Bab 7: Minimisasi Fungsi dengan Peta Karnaugh (K-Map) | Bacaan | `sdig-m07` | Minimisasi Fungsi dengan Peta Karnaugh (K-Map) | `materi/sistem_digital/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `sdig-uts` | Ujian Tengah Semester (UTS) Sistem Digital | `SDIG-01` |
| Bab 9: Metode Quine-McCluskey | Bacaan | `sdig-m08` | Metode Quine-McCluskey | `materi/sistem_digital/materi-08.md` |
| Bab 10: Rangkaian Kombinasional: Adder dan Subtractor | Bacaan | `sdig-m09` | Rangkaian Kombinasional: Adder dan Subtractor | `materi/sistem_digital/materi-09.md` |
| Bab 11: Decoder, Encoder, dan Multiplexer | Bacaan | `sdig-m10` | Decoder, Encoder, dan Multiplexer | `materi/sistem_digital/materi-10.md` |
| Bab 12: Demultiplexer dan Comparator | Bacaan | `sdig-m11` | Demultiplexer dan Comparator | `materi/sistem_digital/materi-11.md` |
| Bab 13: Pengantar Rangkaian Sekuensial: Latch dan Flip-Flop | Bacaan | `sdig-m12` | Pengantar Rangkaian Sekuensial: Latch dan Flip-Flop | `materi/sistem_digital/materi-12.md` |
| Bab 14: Register dan Shift Register | Bacaan | `sdig-m13` | Register dan Shift Register | `materi/sistem_digital/materi-13.md` |
| Bab 15: Counter Sinkron dan Asinkron | Bacaan | `sdig-m14` | Counter Sinkron dan Asinkron | `materi/sistem_digital/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `sdig-uas` | Ujian Akhir Semester (UAS) Sistem Digital | `SDIG-02` |

### 7.4 Kalkulus I

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123004` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Sistem bilangan real, fungsi, limit, kekontinuan, turunan, aplikasi diferensial, dan integral fungsi satu peubah. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Sistem Bilangan Real dan Pertaksamaan | Bacaan | `kalk-m01` | Sistem Bilangan Real dan Pertaksamaan | `materi/kalkulus1/materi-01.md` |
| Bab 2: Fungsi, Domain, dan Grafik Fungsi | Bacaan | `kalk-m02` | Fungsi, Domain, dan Grafik Fungsi | `materi/kalkulus1/materi-02.md` |
| Bab 3: Fungsi Transenden dan Invers | Bacaan | `kalk-m03` | Fungsi Transenden dan Invers | `materi/kalkulus1/materi-03.md` |
| Bab 4: Konsep Limit dan Teorema Limit | Bacaan | `kalk-m04` | Konsep Limit dan Teorema Limit | `materi/kalkulus1/materi-04.md` |
| Bab 5: Kekontinuan Fungsi pada Suatu Titik dan Interval | Bacaan | `kalk-m05` | Kekontinuan Fungsi pada Suatu Titik dan Interval | `materi/kalkulus1/materi-05.md` |
| Bab 6: Definisi Turunan dan Aturan Pencarian Turunan | Bacaan | `kalk-m06` | Definisi Turunan dan Aturan Pencarian Turunan | `materi/kalkulus1/materi-06.md` |
| Bab 7: Aturan Rantai dan Turunan Implisit | Bacaan | `kalk-m07` | Aturan Rantai dan Turunan Implisit | `materi/kalkulus1/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `kalk-uts` | Ujian Tengah Semester (UTS) Kalkulus I | `KALK-01` |
| Bab 9: Turunan Tingkat Tinggi dan Laju Berkaitan | Bacaan | `kalk-m08` | Turunan Tingkat Tinggi dan Laju Berkaitan | `materi/kalkulus1/materi-08.md` |
| Bab 10: Nilai Ekstrem, Kemonotonan, dan Kecekungan | Bacaan | `kalk-m09` | Nilai Ekstrem, Kemonotonan, dan Kecekungan | `materi/kalkulus1/materi-09.md` |
| Bab 11: Masalah Optimasi Terapan | Bacaan | `kalk-m10` | Masalah Optimasi Terapan | `materi/kalkulus1/materi-10.md` |
| Bab 12: Teorema Nilai Rata-Rata dan Dalil L'Hopital | Bacaan | `kalk-m11` | Teorema Nilai Rata-Rata dan Dalil L'Hopital | `materi/kalkulus1/materi-11.md` |
| Bab 13: Integral Tak Tentu dan Anti-Turunan | Bacaan | `kalk-m12` | Integral Tak Tentu dan Anti-Turunan | `materi/kalkulus1/materi-12.md` |
| Bab 14: Integral Tentu dan Jumlah Riemann | Bacaan | `kalk-m13` | Integral Tentu dan Jumlah Riemann | `materi/kalkulus1/materi-13.md` |
| Bab 15: Teorema Dasar Kalkulus dan Aplikasi Luas Daerah | Bacaan | `kalk-m14` | Teorema Dasar Kalkulus dan Aplikasi Luas Daerah | `materi/kalkulus1/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `kalk-uas` | Ujian Akhir Semester (UAS) Kalkulus I | `KALK-02` |

### 7.5 Fisika

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123005` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Besaran fisika, kinematika, dinamika partikel, usaha dan energi, momentum, kelistrikan, kemagnetan, dan gelombang. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Besaran, Satuan SI, dan Analisis Vektor | Bacaan | `fis-m01` | Besaran, Satuan SI, dan Analisis Vektor | `materi/fisika/materi-01.md` |
| Bab 2: Kinematika Gerak Lurus | Bacaan | `fis-m02` | Kinematika Gerak Lurus | `materi/fisika/materi-02.md` |
| Bab 3: Gerak Parabola dan Gerak Melingkar | Bacaan | `fis-m03` | Gerak Parabola dan Gerak Melingkar | `materi/fisika/materi-03.md` |
| Bab 4: Hukum-Hukum Newton tentang Gerak | Bacaan | `fis-m04` | Hukum-Hukum Newton tentang Gerak | `materi/fisika/materi-04.md` |
| Bab 5: Usaha, Energi Kinetik, dan Energi Potensial | Bacaan | `fis-m05` | Usaha, Energi Kinetik, dan Energi Potensial | `materi/fisika/materi-05.md` |
| Bab 6: Momentum Linear dan Tumbukan | Bacaan | `fis-m06` | Momentum Linear dan Tumbukan | `materi/fisika/materi-06.md` |
| Bab 7: Dinamika Rotasi dan Kesetimbangan Benda Tegar | Bacaan | `fis-m07` | Dinamika Rotasi dan Kesetimbangan Benda Tegar | `materi/fisika/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `fis-uts` | Ujian Tengah Semester (UTS) Fisika | `FIS-01` |
| Bab 9: Mekanika Fluida Statis dan Dinamis | Bacaan | `fis-m08` | Mekanika Fluida Statis dan Dinamis | `materi/fisika/materi-08.md` |
| Bab 10: Getaran Harmonik dan Gelombang Mekanik | Bacaan | `fis-m09` | Getaran Harmonik dan Gelombang Mekanik | `materi/fisika/materi-09.md` |
| Bab 11: Suhu, Kalor, dan Hukum Termodinamika | Bacaan | `fis-m10` | Suhu, Kalor, dan Hukum Termodinamika | `materi/fisika/materi-10.md` |
| Bab 12: Elektrostatika: Hukum Coulomb dan Medan Listrik | Bacaan | `fis-m11` | Elektrostatika: Hukum Coulomb dan Medan Listrik | `materi/fisika/materi-11.md` |
| Bab 13: Potensial Listrik dan Kapasitor | Bacaan | `fis-m12` | Potensial Listrik dan Kapasitor | `materi/fisika/materi-12.md` |
| Bab 14: Rangkaian Arus Searah dan Hukum Kirchhoff | Bacaan | `fis-m13` | Rangkaian Arus Searah dan Hukum Kirchhoff | `materi/fisika/materi-13.md` |
| Bab 15: Medan Magnet dan Induksi Elektromagnetik | Bacaan | `fis-m14` | Medan Magnet dan Induksi Elektromagnetik | `materi/fisika/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `fis-uas` | Ujian Akhir Semester (UAS) Fisika | `FIS-02` |

### 7.6 Bahasa Inggris I

| Atribut | Nilai |
|---------|-------|
| Kode | `0953112006` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Keterampilan membaca teks akademik bahasa Inggris, tata bahasa esensial, kosakata teknologi informasi, dan penulisan paragraf ilmiah. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Parts of Speech and Sentence Structure | Bacaan | `bing-m01` | Parts of Speech and Sentence Structure | `materi/inggris1/materi-01.md` |
| Bab 2: Present Tenses in Academic Context | Bacaan | `bing-m02` | Present Tenses in Academic Context | `materi/inggris1/materi-02.md` |
| Bab 3: Past and Future Tenses for Technical Reporting | Bacaan | `bing-m03` | Past and Future Tenses for Technical Reporting | `materi/inggris1/materi-03.md` |
| Bab 4: Passive Voice in Scientific Writing | Bacaan | `bing-m04` | Passive Voice in Scientific Writing | `materi/inggris1/materi-04.md` |
| Bab 5: Reading Strategies: Skimming and Scanning | Bacaan | `bing-m05` | Reading Strategies: Skimming and Scanning | `materi/inggris1/materi-05.md` |
| Bab 6: Identifying Main Ideas and Supporting Details | Bacaan | `bing-m06` | Identifying Main Ideas and Supporting Details | `materi/inggris1/materi-06.md` |
| Bab 7: Contextual Vocabulary in Computer Science | Bacaan | `bing-m07` | Contextual Vocabulary in Computer Science | `materi/inggris1/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `bing-uts` | Ujian Tengah Semester (UTS) Bahasa Inggris I | `BING-01` |
| Bab 9: Subject-Verb Agreement and Parallel Structure | Bacaan | `bing-m08` | Subject-Verb Agreement and Parallel Structure | `materi/inggris1/materi-08.md` |
| Bab 10: Relative Clauses and Adjective Phrases | Bacaan | `bing-m09` | Relative Clauses and Adjective Phrases | `materi/inggris1/materi-09.md` |
| Bab 11: Modal Auxiliaries and Conditional Sentences | Bacaan | `bing-m10` | Modal Auxiliaries and Conditional Sentences | `materi/inggris1/materi-10.md` |
| Bab 12: Summarizing Technical Articles | Bacaan | `bing-m11` | Summarizing Technical Articles | `materi/inggris1/materi-11.md` |
| Bab 13: Paragraph Organization: Topic and Concluding Sentences | Bacaan | `bing-m12` | Paragraph Organization: Topic and Concluding Sentences | `materi/inggris1/materi-12.md` |
| Bab 14: Academic Listening and Note-Taking | Bacaan | `bing-m13` | Academic Listening and Note-Taking | `materi/inggris1/materi-13.md` |
| Bab 15: Oral Presentation Fundamentals | Bacaan | `bing-m14` | Oral Presentation Fundamentals | `materi/inggris1/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `bing-uas` | Ujian Akhir Semester (UAS) Bahasa Inggris I | `BING-02` |

### 7.7 Statistika & Probabilitas

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123007` |
| Pengajar | Siti Nurhaliza, S.Si., M.Kom. |
| NIP Pembuat | 19850103 (Siti Nurhaliza, S.Si., M.Kom.) |
| Deskripsi | Statistika deskriptif, teori peluang, peubah acak, distribusi peluang diskrit dan kontinu, pendugaan parameter, serta uji hipotesis. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengantar Statistika dan Penyajian Data | Bacaan | `stat-m01` | Pengantar Statistika dan Penyajian Data | `materi/statistika/materi-01.md` |
| Bab 2: Ukuran Pemusatan Data (Mean, Median, Modus) | Bacaan | `stat-m02` | Ukuran Pemusatan Data (Mean, Median, Modus) | `materi/statistika/materi-02.md` |
| Bab 3: Ukuran Penyebaran Data (Variansi dan Simpangan Baku) | Bacaan | `stat-m03` | Ukuran Penyebaran Data (Variansi dan Simpangan Baku) | `materi/statistika/materi-03.md` |
| Bab 4: Konsep Dasar Ruang Sampel dan Kejadian | Bacaan | `stat-m04` | Konsep Dasar Ruang Sampel dan Kejadian | `materi/statistika/materi-04.md` |
| Bab 5: Aturan Peluang, Peluang Bersyarat, dan Teorema Bayes | Bacaan | `stat-m05` | Aturan Peluang, Peluang Bersyarat, dan Teorema Bayes | `materi/statistika/materi-05.md` |
| Bab 6: Peubah Acak Diskrit dan Kontinu | Bacaan | `stat-m06` | Peubah Acak Diskrit dan Kontinu | `materi/statistika/materi-06.md` |
| Bab 7: Nilai Harapan dan Variansi Peubah Acak | Bacaan | `stat-m07` | Nilai Harapan dan Variansi Peubah Acak | `materi/statistika/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `stat-uts` | Ujian Tengah Semester (UTS) Statistika & Probabilitas | `STAT-01` |
| Bab 9: Distribusi Peluang Diskrit (Binomial, Poisson) | Bacaan | `stat-m08` | Distribusi Peluang Diskrit (Binomial, Poisson) | `materi/statistika/materi-08.md` |
| Bab 10: Distribusi Peluang Kontinu dan Distribusi Normal | Bacaan | `stat-m09` | Distribusi Peluang Kontinu dan Distribusi Normal | `materi/statistika/materi-09.md` |
| Bab 11: Distribusi Sampling dan Teorema Limit Pusat | Bacaan | `stat-m10` | Distribusi Sampling dan Teorema Limit Pusat | `materi/statistika/materi-10.md` |
| Bab 12: Pendugaan Titik dan Selang Kepercayaan | Bacaan | `stat-m11` | Pendugaan Titik dan Selang Kepercayaan | `materi/statistika/materi-11.md` |
| Bab 13: Pengujian Hipotesis Satu dan Dua Sampel | Bacaan | `stat-m12` | Pengujian Hipotesis Satu dan Dua Sampel | `materi/statistika/materi-12.md` |
| Bab 14: Analisis Korelasi dan Regresi Linear Sederhana | Bacaan | `stat-m13` | Analisis Korelasi dan Regresi Linear Sederhana | `materi/statistika/materi-13.md` |
| Bab 15: Uji Khi-Kuadrat dan Analisis Variansi Dasar | Bacaan | `stat-m14` | Uji Khi-Kuadrat dan Analisis Variansi Dasar | `materi/statistika/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `stat-uas` | Ujian Akhir Semester (UAS) Statistika & Probabilitas | `STAT-02` |

### 7.8 Bahasa Indonesia

| Atribut | Nilai |
|---------|-------|
| Kode | `0953112008` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Sejarah dan kedudukan bahasa Indonesia, EYD, diksi, kalimat efektif, paragraf ilmiah, kutipan, daftar pustaka, dan penulisan karya ilmiah. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Sejarah, Kedudukan, dan Fungsi Bahasa Indonesia | Bacaan | `bind-m01` | Sejarah, Kedudukan, dan Fungsi Bahasa Indonesia | `materi/bahasaindo/materi-01.md` |
| Bab 2: Ragam Bahasa Ilmiah dan Ejaan Bahasa Indonesia (EYD) | Bacaan | `bind-m02` | Ragam Bahasa Ilmiah dan Ejaan Bahasa Indonesia (EYD) | `materi/bahasaindo/materi-02.md` |
| Bab 3: Pilihan Kata (Diksi) dan Kata Baku | Bacaan | `bind-m03` | Pilihan Kata (Diksi) dan Kata Baku | `materi/bahasaindo/materi-03.md` |
| Bab 4: Kalimat Efektif dalam Penulisan Akademik | Bacaan | `bind-m04` | Kalimat Efektif dalam Penulisan Akademik | `materi/bahasaindo/materi-04.md` |
| Bab 5: Pengembangan Paragraf yang Kohesif dan Koheren | Bacaan | `bind-m05` | Pengembangan Paragraf yang Kohesif dan Koheren | `materi/bahasaindo/materi-05.md` |
| Bab 6: Perencanaan Karangan Ilmiah dan Topik Penelitian | Bacaan | `bind-m06` | Perencanaan Karangan Ilmiah dan Topik Penelitian | `materi/bahasaindo/materi-06.md` |
| Bab 7: Sistematika Karya Tulis Ilmiah | Bacaan | `bind-m07` | Sistematika Karya Tulis Ilmiah | `materi/bahasaindo/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `bind-uts` | Ujian Tengah Semester (UTS) Bahasa Indonesia | `BIND-01` |
| Bab 9: Teknik Kutipan Langsung dan Tidak Langsung | Bacaan | `bind-m08` | Teknik Kutipan Langsung dan Tidak Langsung | `materi/bahasaindo/materi-08.md` |
| Bab 10: Penulisan Catatan Kaki dan Daftar Pustaka | Bacaan | `bind-m09` | Penulisan Catatan Kaki dan Daftar Pustaka | `materi/bahasaindo/materi-09.md` |
| Bab 11: Abstrak, Pendahuluan, dan Tinjauan Pustaka | Bacaan | `bind-m10` | Abstrak, Pendahuluan, dan Tinjauan Pustaka | `materi/bahasaindo/materi-10.md` |
| Bab 12: Penulisan Metodologi dan Pembahasan Hasil | Bacaan | `bind-m11` | Penulisan Metodologi dan Pembahasan Hasil | `materi/bahasaindo/materi-11.md` |
| Bab 13: Simpulan, Saran, dan Penyuntingan Naskah | Bacaan | `bind-m12` | Simpulan, Saran, dan Penyuntingan Naskah | `materi/bahasaindo/materi-12.md` |
| Bab 14: Korespondensi Resmi dan Surat Akademik | Bacaan | `bind-m13` | Korespondensi Resmi dan Surat Akademik | `materi/bahasaindo/materi-13.md` |
| Bab 15: Teknik Presentasi Ilmiah Berbahasa Indonesia | Bacaan | `bind-m14` | Teknik Presentasi Ilmiah Berbahasa Indonesia | `materi/bahasaindo/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `bind-uas` | Ujian Akhir Semester (UAS) Bahasa Indonesia | `BIND-02` |

### 7.9 Kalkulus II

| Atribut | Nilai |
|---------|-------|
| Kode | `0953223009` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Teknik pengintegralan lanjut, bentuk tak tentu, integral tak wajar, barisan dan deret tak hingga, serta fungsi peubah banyak. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Teknik Integrasi Substitusi dan Parsial | Bacaan | `kalb-m01` | Teknik Integrasi Substitusi dan Parsial | `materi/kalkulus2/materi-01.md` |
| Bab 2: Integral Fungsi Trigonometri | Bacaan | `kalb-m02` | Integral Fungsi Trigonometri | `materi/kalkulus2/materi-02.md` |
| Bab 3: Substitusi Trigonometri dan Pecahan Parsial | Bacaan | `kalb-m03` | Substitusi Trigonometri dan Pecahan Parsial | `materi/kalkulus2/materi-03.md` |
| Bab 4: Aplikasi Integral: Volume Benda Putar | Bacaan | `kalb-m04` | Aplikasi Integral: Volume Benda Putar | `materi/kalkulus2/materi-04.md` |
| Bab 5: Panjang Busur dan Luas Permukaan Putar | Bacaan | `kalb-m05` | Panjang Busur dan Luas Permukaan Putar | `materi/kalkulus2/materi-05.md` |
| Bab 6: Bentuk Tak Tentu dan Integral Tak Wajar | Bacaan | `kalb-m06` | Bentuk Tak Tentu dan Integral Tak Wajar | `materi/kalkulus2/materi-06.md` |
| Bab 7: Sistem Koordinat Polar dan Persamaan Parametrik | Bacaan | `kalb-m07` | Sistem Koordinat Polar dan Persamaan Parametrik | `materi/kalkulus2/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `kalb-uts` | Ujian Tengah Semester (UTS) Kalkulus II | `KALB-01` |
| Bab 9: Barisan Tak Hingga dan Kekonvergenan | Bacaan | `kalb-m08` | Barisan Tak Hingga dan Kekonvergenan | `materi/kalkulus2/materi-08.md` |
| Bab 10: Deret Tak Hingga dan Uji Konvergensi | Bacaan | `kalb-m09` | Deret Tak Hingga dan Uji Konvergensi | `materi/kalkulus2/materi-09.md` |
| Bab 11: Deret Pangkat, Deret Taylor, dan Deret Maclaurin | Bacaan | `kalb-m10` | Deret Pangkat, Deret Taylor, dan Deret Maclaurin | `materi/kalkulus2/materi-10.md` |
| Bab 12: Fungsi Dua Peubah atau Lebih dan Limitnya | Bacaan | `kalb-m11` | Fungsi Dua Peubah atau Lebih dan Limitnya | `materi/kalkulus2/materi-11.md` |
| Bab 13: Turunan Parsial dan Keterdiferensialan | Bacaan | `kalb-m12` | Turunan Parsial dan Keterdiferensialan | `materi/kalkulus2/materi-12.md` |
| Bab 14: Aturan Rantai dan Gradien Fungsi Peubah Banyak | Bacaan | `kalb-m13` | Aturan Rantai dan Gradien Fungsi Peubah Banyak | `materi/kalkulus2/materi-13.md` |
| Bab 15: Integral Lipat Dua dan Aplikasinya | Bacaan | `kalb-m14` | Integral Lipat Dua dan Aplikasinya | `materi/kalkulus2/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `kalb-uas` | Ujian Akhir Semester (UAS) Kalkulus II | `KALB-02` |

### 7.10 Matematika Diskrit I

| Atribut | Nilai |
|---------|-------|
| Kode | `0953223010` |
| Pengajar | Dr. Andi Wijaya, S.Kom., M.Kom. |
| NIP Pembuat | 19850101 (Dr. Andi Wijaya, S.Kom., M.Kom.) |
| Deskripsi | Logika proposisional, metode pembuktian, teori himpunan, relasi dan fungsi, induksi matematika, serta kombinatorika dasar. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Logika Proposisional dan Tabel Kebenaran | Bacaan | `mdis-m01` | Logika Proposisional dan Tabel Kebenaran | `materi/matdis1/materi-01.md` |
| Bab 2: Ekuivalensi Logika dan Hukum-Hukum Logika | Bacaan | `mdis-m02` | Ekuivalensi Logika dan Hukum-Hukum Logika | `materi/matdis1/materi-02.md` |
| Bab 3: Logika Predikat dan Kuantor | Bacaan | `mdis-m03` | Logika Predikat dan Kuantor | `materi/matdis1/materi-03.md` |
| Bab 4: Aturan Inferensi dan Metode Pembuktian | Bacaan | `mdis-m04` | Aturan Inferensi dan Metode Pembuktian | `materi/matdis1/materi-04.md` |
| Bab 5: Teori Himpunan dan Operasi Himpunan | Bacaan | `mdis-m05` | Teori Himpunan dan Operasi Himpunan | `materi/matdis1/materi-05.md` |
| Bab 6: Relasi dan Sifat-Sifat Relasi | Bacaan | `mdis-m06` | Relasi dan Sifat-Sifat Relasi | `materi/matdis1/materi-06.md` |
| Bab 7: Relasi Ekuivalensi dan Pengurutan Parsial | Bacaan | `mdis-m07` | Relasi Ekuivalensi dan Pengurutan Parsial | `materi/matdis1/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `mdis-uts` | Ujian Tengah Semester (UTS) Matematika Diskrit I | `MDIS-01` |
| Bab 9: Fungsi, Fungsi Injektif, Surjektif, dan Bijektif | Bacaan | `mdis-m08` | Fungsi, Fungsi Injektif, Surjektif, dan Bijektif | `materi/matdis1/materi-08.md` |
| Bab 10: Barisan, Notasi Sigma, dan Kardinalitas | Bacaan | `mdis-m09` | Barisan, Notasi Sigma, dan Kardinalitas | `materi/matdis1/materi-09.md` |
| Bab 11: Induksi Matematika dan Induksi Kuat | Bacaan | `mdis-m10` | Induksi Matematika dan Induksi Kuat | `materi/matdis1/materi-10.md` |
| Bab 12: Definisi Rekursif dan Algoritma Rekursif | Bacaan | `mdis-m11` | Definisi Rekursif dan Algoritma Rekursif | `materi/matdis1/materi-11.md` |
| Bab 13: Prinsip Dasar Membilang (Aturan Penjumlahan dan Perkalian) | Bacaan | `mdis-m12` | Prinsip Dasar Membilang (Aturan Penjumlahan dan Perkalian) | `materi/matdis1/materi-12.md` |
| Bab 14: Prinsip Sarang Merpati (Pigeonhole Principle) | Bacaan | `mdis-m13` | Prinsip Sarang Merpati (Pigeonhole Principle) | `materi/matdis1/materi-13.md` |
| Bab 15: Permutasi, Kombinasi, dan Koefisien Binomial | Bacaan | `mdis-m14` | Permutasi, Kombinasi, dan Koefisien Binomial | `materi/matdis1/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `mdis-uas` | Ujian Akhir Semester (UAS) Matematika Diskrit I | `MDIS-02` |

### 7.11 Aljabar Linier

| Atribut | Nilai |
|---------|-------|
| Kode | `0953223011` |
| Pengajar | Dr. Ratna Sari Dewi, S.Si., M.Sc. |
| NIP Pembuat | 19850102 (Dr. Ratna Sari Dewi, S.Si., M.Sc.) |
| Deskripsi | Sistem persamaan linier, matriks, determinan, ruang vektor Euclides, ruang vektor umum, nilai eigen, dan transformasi linier. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Sistem Persamaan Linier dan Eliminasi Gauss-Jordan | Bacaan | `alin-m01` | Sistem Persamaan Linier dan Eliminasi Gauss-Jordan | `materi/aljabar_linier/materi-01.md` |
| Bab 2: Matriks dan Operasi Aljabar Matriks | Bacaan | `alin-m02` | Matriks dan Operasi Aljabar Matriks | `materi/aljabar_linier/materi-02.md` |
| Bab 3: Matriks Invers dan Matriks Elementer | Bacaan | `alin-m03` | Matriks Invers dan Matriks Elementer | `materi/aljabar_linier/materi-03.md` |
| Bab 4: Determinan dengan Ekspansi Kofaktor dan Reduksi Baris | Bacaan | `alin-m04` | Determinan dengan Ekspansi Kofaktor dan Reduksi Baris | `materi/aljabar_linier/materi-04.md` |
| Bab 5: Aturan Cramer dan Aplikasi Determinan | Bacaan | `alin-m05` | Aturan Cramer dan Aplikasi Determinan | `materi/aljabar_linier/materi-05.md` |
| Bab 6: Vektor di Ruang 2D dan 3D | Bacaan | `alin-m06` | Vektor di Ruang 2D dan 3D | `materi/aljabar_linier/materi-06.md` |
| Bab 7: Hasil Kali Titik (Dot Product) dan Hasil Kali Silang | Bacaan | `alin-m07` | Hasil Kali Titik (Dot Product) dan Hasil Kali Silang | `materi/aljabar_linier/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `alin-uts` | Ujian Tengah Semester (UTS) Aljabar Linier | `ALIN-01` |
| Bab 9: Ruang Vektor Umum dan Subruang | Bacaan | `alin-m08` | Ruang Vektor Umum dan Subruang | `materi/aljabar_linier/materi-08.md` |
| Bab 10: Kombinasi Linier dan Kebebasan Linier | Bacaan | `alin-m09` | Kombinasi Linier dan Kebebasan Linier | `materi/aljabar_linier/materi-09.md` |
| Bab 11: Basis, Dimensi, dan Rank Matriks | Bacaan | `alin-m10` | Basis, Dimensi, dan Rank Matriks | `materi/aljabar_linier/materi-10.md` |
| Bab 12: Ruang Hasil Kali Dalam dan Proses Gram-Schmidt | Bacaan | `alin-m11` | Ruang Hasil Kali Dalam dan Proses Gram-Schmidt | `materi/aljabar_linier/materi-11.md` |
| Bab 13: Nilai Eigen, Vektor Eigen, dan Diagonalisasi | Bacaan | `alin-m12` | Nilai Eigen, Vektor Eigen, dan Diagonalisasi | `materi/aljabar_linier/materi-12.md` |
| Bab 14: Transformasi Linier, Kernel, dan Jangkauan | Bacaan | `alin-m13` | Transformasi Linier, Kernel, dan Jangkauan | `materi/aljabar_linier/materi-13.md` |
| Bab 15: Matriks Transformasi Linier dan Aplikasi Komputasi | Bacaan | `alin-m14` | Matriks Transformasi Linier dan Aplikasi Komputasi | `materi/aljabar_linier/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `alin-uas` | Ujian Akhir Semester (UAS) Aljabar Linier | `ALIN-02` |

### 7.12 Struktur Data & Algoritma

| Atribut | Nilai |
|---------|-------|
| Kode | `0953223012` |
| Pengajar | Siti Nurhaliza, S.Si., M.Kom. |
| NIP Pembuat | 19850103 (Siti Nurhaliza, S.Si., M.Kom.) |
| Deskripsi | Abstraksi data, kompleksitas algoritma, linked list, stack, queue, tree, binary search tree, heap, hashing, graf, dan pengurutan. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Kontrak Belajar, ADT, dan Kompleksitas Waktu Big-O | Bacaan | `sda-m01` | Kontrak Belajar, ADT, dan Kompleksitas Waktu Big-O | `materi/sda/materi-01.md` |
| Bab 2: Array Dinamis dan Analisis Memori | Bacaan | `sda-m02` | Array Dinamis dan Analisis Memori | `materi/sda/materi-02.md` |
| Bab 3: Single Linked List dan Operasi Dasar | Bacaan | `sda-m03` | Single Linked List dan Operasi Dasar | `materi/sda/materi-03.md` |
| Bab 4: Double Linked List dan Circular Linked List | Bacaan | `sda-m04` | Double Linked List dan Circular Linked List | `materi/sda/materi-04.md` |
| Bab 5: Stack (Tumpukan) dan Evaluasi Ekspresi | Bacaan | `sda-m05` | Stack (Tumpukan) dan Evaluasi Ekspresi | `materi/sda/materi-05.md` |
| Bab 6: Queue (Antrean), Circular Queue, dan Priority Queue | Bacaan | `sda-m06` | Queue (Antrean), Circular Queue, dan Priority Queue | `materi/sda/materi-06.md` |
| Bab 7: Algoritma Pencarian: Linear Search dan Binary Search | Bacaan | `sda-m07` | Algoritma Pencarian: Linear Search dan Binary Search | `materi/sda/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `sda-uts` | Ujian Tengah Semester (UTS) Struktur Data & Algoritma | `SDA-01` |
| Bab 9: Algoritma Pengurutan Dasar: Bubble, Selection, Insertion Sort | Bacaan | `sda-m08` | Algoritma Pengurutan Dasar: Bubble, Selection, Insertion Sort | `materi/sda/materi-08.md` |
| Bab 10: Algoritma Pengurutan Lanjut: Merge Sort dan Quick Sort | Bacaan | `sda-m09` | Algoritma Pengurutan Lanjut: Merge Sort dan Quick Sort | `materi/sda/materi-09.md` |
| Bab 11: Konsep Pohon (Tree) dan Binary Tree Traversal | Bacaan | `sda-m10` | Konsep Pohon (Tree) dan Binary Tree Traversal | `materi/sda/materi-10.md` |
| Bab 12: Binary Search Tree (BST): Pencarian, Penyisipan, Penghapusan | Bacaan | `sda-m11` | Binary Search Tree (BST): Pencarian, Penyisipan, Penghapusan | `materi/sda/materi-11.md` |
| Bab 13: AVL Tree dan Penyeimbangan Pohon | Bacaan | `sda-m12` | AVL Tree dan Penyeimbangan Pohon | `materi/sda/materi-12.md` |
| Bab 14: Heap, Heapsort, dan Hash Table | Bacaan | `sda-m13` | Heap, Heapsort, dan Hash Table | `materi/sda/materi-13.md` |
| Bab 15: Representasi Graf serta Penelusuran BFS dan DFS | Bacaan | `sda-m14` | Representasi Graf serta Penelusuran BFS dan DFS | `materi/sda/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `sda-uas` | Ujian Akhir Semester (UAS) Struktur Data & Algoritma | `SDA-02` |

### 7.13 Organisasi Sistem Komputer

| Atribut | Nilai |
|---------|-------|
| Kode | `0953223013` |
| Pengajar | Budi Hartono, S.T., M.T. |
| NIP Pembuat | 19850104 (Budi Hartono, S.T., M.T.) |
| Deskripsi | Arsitektur Von Neumann, unit pemrosesan pusat (CPU), set instruksi, jalur data, hierarki memori, cache, sistem bus, dan modul I/O. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengantar Organisasi dan Arsitektur Komputer | Bacaan | `osk-m01` | Pengantar Organisasi dan Arsitektur Komputer | `materi/osk/materi-01.md` |
| Bab 2: Evolusi Komputer dan Arsitektur Von Neumann | Bacaan | `osk-m02` | Evolusi Komputer dan Arsitektur Von Neumann | `materi/osk/materi-02.md` |
| Bab 3: Sistem Bus dan Interkoneksi Komponen | Bacaan | `osk-m03` | Sistem Bus dan Interkoneksi Komponen | `materi/osk/materi-03.md` |
| Bab 4: Memori Internal: RAM, ROM, dan Organisasi Sel Memori | Bacaan | `osk-m04` | Memori Internal: RAM, ROM, dan Organisasi Sel Memori | `materi/osk/materi-04.md` |
| Bab 5: Memori Cache dan Prinsip Lokalitas | Bacaan | `osk-m05` | Memori Cache dan Prinsip Lokalitas | `materi/osk/materi-05.md` |
| Bab 6: Memori Eksternal: Magnetic Disk, SSD, dan RAID | Bacaan | `osk-m06` | Memori Eksternal: Magnetic Disk, SSD, dan RAID | `materi/osk/materi-06.md` |
| Bab 7: Modul Input/Output, Interrupt, dan DMA | Bacaan | `osk-m07` | Modul Input/Output, Interrupt, dan DMA | `materi/osk/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `osk-uts` | Ujian Tengah Semester (UTS) Organisasi Sistem Komputer | `OSK-01` |
| Bab 9: Aritmatika Komputer: Integer dan Floating Point IEEE 754 | Bacaan | `osk-m08` | Aritmatika Komputer: Integer dan Floating Point IEEE 754 | `materi/osk/materi-08.md` |
| Bab 10: Karakteristik dan Fungsi Set Instruksi (ISA) | Bacaan | `osk-m09` | Karakteristik dan Fungsi Set Instruksi (ISA) | `materi/osk/materi-09.md` |
| Bab 11: Mode Pengalamatan dan Format Instruksi | Bacaan | `osk-m10` | Mode Pengalamatan dan Format Instruksi | `materi/osk/materi-10.md` |
| Bab 12: Struktur dan Fungsi Prosesor (CPU) serta Siklus Instruksi | Bacaan | `osk-m11` | Struktur dan Fungsi Prosesor (CPU) serta Siklus Instruksi | `materi/osk/materi-11.md` |
| Bab 13: Pipelining Instruksi dan Penanganan Hazard | Bacaan | `osk-m12` | Pipelining Instruksi dan Penanganan Hazard | `materi/osk/materi-12.md` |
| Bab 14: Arsitektur RISC vs CISC | Bacaan | `osk-m13` | Arsitektur RISC vs CISC | `materi/osk/materi-13.md` |
| Bab 15: Unit Kontrol dan Prosesor Multicore | Bacaan | `osk-m14` | Unit Kontrol dan Prosesor Multicore | `materi/osk/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `osk-uas` | Ujian Akhir Semester (UAS) Organisasi Sistem Komputer | `OSK-02` |

### 7.14 Pendidikan Kewarganegaraan

| Atribut | Nilai |
|---------|-------|
| Kode | `0953212014` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Identitas nasional, integrasi bangsa, konstitusi UUD 1945, hak dan kewajiban warga negara, demokrasi, wawasan nusantara, dan ketahanan nasional. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Hakikat dan Pentingnya Pendidikan Kewarganegaraan | Bacaan | `pkn-m01` | Hakikat dan Pentingnya Pendidikan Kewarganegaraan | `materi/pkn/materi-01.md` |
| Bab 2: Identitas Nasional dan Karakter Bangsa Indonesia | Bacaan | `pkn-m02` | Identitas Nasional dan Karakter Bangsa Indonesia | `materi/pkn/materi-02.md` |
| Bab 3: Integrasi Nasional dalam Masyarakat Majemuk | Bacaan | `pkn-m03` | Integrasi Nasional dalam Masyarakat Majemuk | `materi/pkn/materi-03.md` |
| Bab 4: Nilai dan Norma Konstitusional UUD NRI 1945 | Bacaan | `pkn-m04` | Nilai dan Norma Konstitusional UUD NRI 1945 | `materi/pkn/materi-04.md` |
| Bab 5: Harmoni Kewajiban dan Hak Negara serta Warga Negara | Bacaan | `pkn-m05` | Harmoni Kewajiban dan Hak Negara serta Warga Negara | `materi/pkn/materi-05.md` |
| Bab 6: Hakikat, Instrumentasi, dan Praksis Demokrasi Indonesia | Bacaan | `pkn-m06` | Hakikat, Instrumentasi, dan Praksis Demokrasi Indonesia | `materi/pkn/materi-06.md` |
| Bab 7: Penegakan Hukum yang Berkeadilan | Bacaan | `pkn-m07` | Penegakan Hukum yang Berkeadilan | `materi/pkn/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `pkn-uts` | Ujian Tengah Semester (UTS) Pendidikan Kewarganegaraan | `PKN-01` |
| Bab 9: Dinamika Historis Konstitusi di Indonesia | Bacaan | `pkn-m08` | Dinamika Historis Konstitusi di Indonesia | `materi/pkn/materi-08.md` |
| Bab 10: Kewarganegaraan Digital dan Etika Ruang Siber | Bacaan | `pkn-m09` | Kewarganegaraan Digital dan Etika Ruang Siber | `materi/pkn/materi-09.md` |
| Bab 11: Wawasan Nusantara sebagai Geopolitik Indonesia | Bacaan | `pkn-m10` | Wawasan Nusantara sebagai Geopolitik Indonesia | `materi/pkn/materi-10.md` |
| Bab 12: Ketahanan Nasional dan Bela Negara | Bacaan | `pkn-m11` | Ketahanan Nasional dan Bela Negara | `materi/pkn/materi-11.md` |
| Bab 13: Otonomi Daerah dan Tata Kelola Pemerintahan yang Baik | Bacaan | `pkn-m12` | Otonomi Daerah dan Tata Kelola Pemerintahan yang Baik | `materi/pkn/materi-12.md` |
| Bab 14: Pencegahan Korupsi dan Integritas Publik | Bacaan | `pkn-m13` | Pencegahan Korupsi dan Integritas Publik | `materi/pkn/materi-13.md` |
| Bab 15: Peran Generasi Muda dalam Pembangunan Nasional | Bacaan | `pkn-m14` | Peran Generasi Muda dalam Pembangunan Nasional | `materi/pkn/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `pkn-uas` | Ujian Akhir Semester (UAS) Pendidikan Kewarganegaraan | `PKN-02` |

### 7.15 Bahasa Inggris II

| Atribut | Nilai |
|---------|-------|
| Kode | `0953222015` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Penulisan esai akademik, telaah kritis jurnal internasional bidang informatika, korespondensi profesional, dan presentasi ilmiah bahasa Inggris. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Academic Essay Structure and Thesis Statement | Bacaan | `bingb-m01` | Academic Essay Structure and Thesis Statement | `materi/inggris2/materi-01.md` |
| Bab 2: Cause and Effect Essays in Technology | Bacaan | `bingb-m02` | Cause and Effect Essays in Technology | `materi/inggris2/materi-02.md` |
| Bab 3: Comparison and Contrast Writing | Bacaan | `bingb-m03` | Comparison and Contrast Writing | `materi/inggris2/materi-03.md` |
| Bab 4: Argumentative Essays and Critical Thinking | Bacaan | `bingb-m04` | Argumentative Essays and Critical Thinking | `materi/inggris2/materi-04.md` |
| Bab 5: Reading Research Papers: Abstract and Introduction | Bacaan | `bingb-m05` | Reading Research Papers: Abstract and Introduction | `materi/inggris2/materi-05.md` |
| Bab 6: Analyzing Methodology and Experimental Results | Bacaan | `bingb-m06` | Analyzing Methodology and Experimental Results | `materi/inggris2/materi-06.md` |
| Bab 7: Paraphrasing, Quoting, and Avoiding Plagiarism | Bacaan | `bingb-m07` | Paraphrasing, Quoting, and Avoiding Plagiarism | `materi/inggris2/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `bingb-uts` | Ujian Tengah Semester (UTS) Bahasa Inggris II | `BINGB-01` |
| Bab 9: Describing Charts, Graphs, and Data Trends | Bacaan | `bingb-m08` | Describing Charts, Graphs, and Data Trends | `materi/inggris2/materi-08.md` |
| Bab 10: Writing Technical Documentation and Reports | Bacaan | `bingb-m09` | Writing Technical Documentation and Reports | `materi/inggris2/materi-09.md` |
| Bab 11: Professional Email and Cover Letter Writing | Bacaan | `bingb-m10` | Professional Email and Cover Letter Writing | `materi/inggris2/materi-10.md` |
| Bab 12: Curriculum Vitae and Job Interview Preparation | Bacaan | `bingb-m11` | Curriculum Vitae and Job Interview Preparation | `materi/inggris2/materi-11.md` |
| Bab 13: Designing Effective Presentation Slides | Bacaan | `bingb-m12` | Designing Effective Presentation Slides | `materi/inggris2/materi-12.md` |
| Bab 14: Delivering Technical Presentations and Q&A Handling | Bacaan | `bingb-m13` | Delivering Technical Presentations and Q&A Handling | `materi/inggris2/materi-13.md` |
| Bab 15: TOEFL/IELTS Academic Reading and Structure Review | Bacaan | `bingb-m14` | TOEFL/IELTS Academic Reading and Structure Review | `materi/inggris2/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `bingb-uas` | Ujian Akhir Semester (UAS) Bahasa Inggris II | `BINGB-02` |

### 7.16 Matematika Diskrit II

| Atribut | Nilai |
|---------|-------|
| Kode | `0953122016` |
| Pengajar | Dr. Andi Wijaya, S.Kom., M.Kom. |
| NIP Pembuat | 19850101 (Dr. Andi Wijaya, S.Kom., M.Kom.) |
| Deskripsi | Prasyarat: 0953223010. Relasi rekurensi lanjut, fungsi pembangkit, teori graf, pohon merentang, pewarnaan graf, aljabar Boole, dan otomata hingga. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Relasi Rekurensi Linier Homogen dan Non-Homogen | Bacaan | `mdisb-m01` | Relasi Rekurensi Linier Homogen dan Non-Homogen | `materi/matdis2/materi-01.md` |
| Bab 2: Fungsi Pembangkit Biasa dan Eksponensial | Bacaan | `mdisb-m02` | Fungsi Pembangkit Biasa dan Eksponensial | `materi/matdis2/materi-02.md` |
| Bab 3: Prinsip Inklusi-Eksklusi dan Aplikasinya | Bacaan | `mdisb-m03` | Prinsip Inklusi-Eksklusi dan Aplikasinya | `materi/matdis2/materi-03.md` |
| Bab 4: Konsep Dasar Teori Graf dan Terminologi Graf | Bacaan | `mdisb-m04` | Konsep Dasar Teori Graf dan Terminologi Graf | `materi/matdis2/materi-04.md` |
| Bab 5: Representasi Graf dan Isomorfisme Graf | Bacaan | `mdisb-m05` | Representasi Graf dan Isomorfisme Graf | `materi/matdis2/materi-05.md` |
| Bab 6: Keterhubungan, Lintasan Euler, dan Lintasan Hamilton | Bacaan | `mdisb-m06` | Keterhubungan, Lintasan Euler, dan Lintasan Hamilton | `materi/matdis2/materi-06.md` |
| Bab 7: Lintasan Terpendek: Algoritma Dijkstra dan Bellman-Ford | Bacaan | `mdisb-m07` | Lintasan Terpendek: Algoritma Dijkstra dan Bellman-Ford | `materi/matdis2/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `mdisb-uts` | Ujian Tengah Semester (UTS) Matematika Diskrit II | `MDISB-01` |
| Bab 9: Graf Planar dan Pewarnaan Graf (Graph Coloring) | Bacaan | `mdisb-m08` | Graf Planar dan Pewarnaan Graf (Graph Coloring) | `materi/matdis2/materi-08.md` |
| Bab 10: Pohon (Trees), Pohon Berakar, dan Aplikasinya | Bacaan | `mdisb-m09` | Pohon (Trees), Pohon Berakar, dan Aplikasinya | `materi/matdis2/materi-09.md` |
| Bab 11: Pohon Merentang Minimum: Algoritma Prim dan Kruskal | Bacaan | `mdisb-m10` | Pohon Merentang Minimum: Algoritma Prim dan Kruskal | `materi/matdis2/materi-10.md` |
| Bab 12: Jaringan Transportasi dan Aliran Maksimum | Bacaan | `mdisb-m11` | Jaringan Transportasi dan Aliran Maksimum | `materi/matdis2/materi-11.md` |
| Bab 13: Poset, Lattice, dan Struktur Aljabar Diskrit | Bacaan | `mdisb-m12` | Poset, Lattice, dan Struktur Aljabar Diskrit | `materi/matdis2/materi-12.md` |
| Bab 14: Teori Bilangan Modul dan Kriptografi Kunci Publik | Bacaan | `mdisb-m13` | Teori Bilangan Modul dan Kriptografi Kunci Publik | `materi/matdis2/materi-13.md` |
| Bab 15: Pengantar Bahasa Formal dan Finite State Automata | Bacaan | `mdisb-m14` | Pengantar Bahasa Formal dan Finite State Automata | `materi/matdis2/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `mdisb-uas` | Ujian Akhir Semester (UAS) Matematika Diskrit II | `MDISB-02` |

### 7.17 Pemrograman Berorientasi Objek

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123017` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Prasyarat: 0953223012. Paradigma OOP di Java: class, object, encapsulation, inheritance, polymorphism, abstraction, generic, exception, GUI, dan thread. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengenalan Pemrograman Berorientasi Objek | Bacaan | `pbo-m01` | Pengenalan Pemrograman Berorientasi Objek | `materi/pbo/materi-01.md` |
| Bab 2: Class, Object, dan Konstruktor | Bacaan | `pbo-m02` | Class, Object, dan Konstruktor | `materi/pbo/materi-02.md` |
| Bab 3: Tipe Data, Variabel, dan Method | Bacaan | `pbo-m03` | Tipe Data, Variabel, dan Method | `materi/pbo/materi-03.md` |
| Bab 4: Encapsulation dan Access Modifier | Bacaan | `pbo-m04` | Encapsulation dan Access Modifier | `materi/pbo/materi-04.md` |
| Bab 5: Relasi Antar Kelas: Association, Aggregation, Composition | Bacaan | `pbo-m05` | Relasi Antar Kelas: Association, Aggregation, Composition | `materi/pbo/materi-05.md` |
| Bab 6: Inheritance dengan extends dan super | Bacaan | `pbo-m06` | Inheritance dengan extends dan super | `materi/pbo/materi-06.md` |
| Bab 7: Polymorphism: Overloading dan Overriding | Bacaan | `pbo-m07` | Polymorphism: Overloading dan Overriding | `materi/pbo/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `pbo-uts` | Ujian Tengah Semester (UTS) Pemrograman Berorientasi Objek | `PBO-01` |
| Bab 9: Abstract Class dan Interface | Bacaan | `pbo-m08` | Abstract Class dan Interface | `materi/pbo/materi-08.md` |
| Bab 10: Manipulasi String dan StringBuilder | Bacaan | `pbo-m09` | Manipulasi String dan StringBuilder | `materi/pbo/materi-09.md` |
| Bab 11: Generics dan Java Collections Framework | Bacaan | `pbo-m10` | Generics dan Java Collections Framework | `materi/pbo/materi-10.md` |
| Bab 12: Exception Handling dan Assertions | Bacaan | `pbo-m11` | Exception Handling dan Assertions | `materi/pbo/materi-11.md` |
| Bab 13: Operasi Input/Output dan File Streams | Bacaan | `pbo-m12` | Operasi Input/Output dan File Streams | `materi/pbo/materi-12.md` |
| Bab 14: Pemrograman Antarmuka Grafis dengan JavaFX | Bacaan | `pbo-m13` | Pemrograman Antarmuka Grafis dengan JavaFX | `materi/pbo/materi-13.md` |
| Bab 15: Multithreading dan Sinkronisasi | Bacaan | `pbo-m14` | Multithreading dan Sinkronisasi | `materi/pbo/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `pbo-uas` | Ujian Akhir Semester (UAS) Pemrograman Berorientasi Objek | `PBO-02` |

### 7.18 Basis Data

| Atribut | Nilai |
|---------|-------|
| Kode | `0953124018` |
| Pengajar | Agus Setiawan, S.T., M.Eng. |
| NIP Pembuat | 19850106 (Agus Setiawan, S.T., M.Eng.) |
| Deskripsi | Arsitektur sistem basis data, pemodelan Entity-Relationship (ERD), model relasional, aljabar relasional, SQL (DDL, DML, DCL), normalisasi, dan transaksi. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengantar Sistem Basis Data dan Arsitektur DBMS | Bacaan | `bdat-m01` | Pengantar Sistem Basis Data dan Arsitektur DBMS | `materi/basis_data/materi-01.md` |
| Bab 2: Model Data Entity-Relationship (ERD) | Bacaan | `bdat-m02` | Model Data Entity-Relationship (ERD) | `materi/basis_data/materi-02.md` |
| Bab 3: Enhanced Entity-Relationship (EER) dan Kardinalitas | Bacaan | `bdat-m03` | Enhanced Entity-Relationship (EER) dan Kardinalitas | `materi/basis_data/materi-03.md` |
| Bab 4: Model Data Relasional dan Integritas Referensial | Bacaan | `bdat-m04` | Model Data Relasional dan Integritas Referensial | `materi/basis_data/materi-04.md` |
| Bab 5: Transformasi ERD ke Skema Relasional | Bacaan | `bdat-m05` | Transformasi ERD ke Skema Relasional | `materi/basis_data/materi-05.md` |
| Bab 6: Aljabar Relasional dan Kalkulus Relasional | Bacaan | `bdat-m06` | Aljabar Relasional dan Kalkulus Relasional | `materi/basis_data/materi-06.md` |
| Bab 7: SQL Dasar: Data Definition Language (DDL) | Bacaan | `bdat-m07` | SQL Dasar: Data Definition Language (DDL) | `materi/basis_data/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `bdat-uts` | Ujian Tengah Semester (UTS) Basis Data | `BDAT-01` |
| Bab 9: SQL Manipulasi Data: SELECT, INSERT, UPDATE, DELETE | Bacaan | `bdat-m08` | SQL Manipulasi Data: SELECT, INSERT, UPDATE, DELETE | `materi/basis_data/materi-08.md` |
| Bab 10: Fungsi Agregasi, GROUP BY, dan Subquery | Bacaan | `bdat-m09` | Fungsi Agregasi, GROUP BY, dan Subquery | `materi/basis_data/materi-09.md` |
| Bab 11: Operasi JOIN Multi-Tabel dan View | Bacaan | `bdat-m10` | Operasi JOIN Multi-Tabel dan View | `materi/basis_data/materi-10.md` |
| Bab 12: Ketergantungan Fungsional (Functional Dependency) | Bacaan | `bdat-m11` | Ketergantungan Fungsional (Functional Dependency) | `materi/basis_data/materi-11.md` |
| Bab 13: Normalisasi Basis Data (1NF, 2NF, 3NF, BCNF) | Bacaan | `bdat-m12` | Normalisasi Basis Data (1NF, 2NF, 3NF, BCNF) | `materi/basis_data/materi-12.md` |
| Bab 14: Indeks, Penyimpanan Fisik, dan Optimasi Query | Bacaan | `bdat-m13` | Indeks, Penyimpanan Fisik, dan Optimasi Query | `materi/basis_data/materi-13.md` |
| Bab 15: Manajemen Transaksi, Properti ACID, dan Concurrency Control | Bacaan | `bdat-m14` | Manajemen Transaksi, Properti ACID, dan Concurrency Control | `materi/basis_data/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `bdat-uas` | Ujian Akhir Semester (UAS) Basis Data | `BDAT-02` |

### 7.19 Sistem Operasi

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123019` |
| Pengajar | Budi Hartono, S.T., M.T. |
| NIP Pembuat | 19850104 (Budi Hartono, S.T., M.T.) |
| Deskripsi | Prasyarat: 0953223013. Struktur kernel, manajemen proses, thread, penjadwalan CPU, sinkronisasi, deadlock, memori virtual, dan sistem berkas. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengantar, Sejarah, dan Struktur Sistem Operasi | Bacaan | `sisop-m01` | Pengantar, Sejarah, dan Struktur Sistem Operasi | `materi/sistem_operasi/materi-01.md` |
| Bab 2: System Calls dan Arsitektur Kernel | Bacaan | `sisop-m02` | System Calls dan Arsitektur Kernel | `materi/sistem_operasi/materi-02.md` |
| Bab 3: Konsep Proses, PCB, dan Komunikasi Antarproses (IPC) | Bacaan | `sisop-m03` | Konsep Proses, PCB, dan Komunikasi Antarproses (IPC) | `materi/sistem_operasi/materi-03.md` |
| Bab 4: Thread dan Pemrograman Multithreading pada OS | Bacaan | `sisop-m04` | Thread dan Pemrograman Multithreading pada OS | `materi/sistem_operasi/materi-04.md` |
| Bab 5: Algoritma Penjadwalan CPU (FCFS, SJF, Round Robin) | Bacaan | `sisop-m05` | Algoritma Penjadwalan CPU (FCFS, SJF, Round Robin) | `materi/sistem_operasi/materi-05.md` |
| Bab 6: Sinkronisasi Proses dan Masalah Critical Section | Bacaan | `sisop-m06` | Sinkronisasi Proses dan Masalah Critical Section | `materi/sistem_operasi/materi-06.md` |
| Bab 7: Semaphore, Mutex, dan Monitor | Bacaan | `sisop-m07` | Semaphore, Mutex, dan Monitor | `materi/sistem_operasi/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `sisop-uts` | Ujian Tengah Semester (UTS) Sistem Operasi | `SISOP-01` |
| Bab 9: Deadlock: Karakteristik, Pencegahan, dan Algoritma Banker | Bacaan | `sisop-m08` | Deadlock: Karakteristik, Pencegahan, dan Algoritma Banker | `materi/sistem_operasi/materi-08.md` |
| Bab 10: Manajemen Memori Utama, Paging, dan Segmentasi | Bacaan | `sisop-m09` | Manajemen Memori Utama, Paging, dan Segmentasi | `materi/sistem_operasi/materi-09.md` |
| Bab 11: Memori Virtual, Demand Paging, dan Page Replacement | Bacaan | `sisop-m10` | Memori Virtual, Demand Paging, dan Page Replacement | `materi/sistem_operasi/materi-10.md` |
| Bab 12: Antarmuka dan Implementasi Sistem Berkas (File System) | Bacaan | `sisop-m11` | Antarmuka dan Implementasi Sistem Berkas (File System) | `materi/sistem_operasi/materi-11.md` |
| Bab 13: Manajemen Penyimpanan Massal dan Penjadwalan Disk | Bacaan | `sisop-m12` | Manajemen Penyimpanan Massal dan Penjadwalan Disk | `materi/sistem_operasi/materi-12.md` |
| Bab 14: Sistem Input/Output dan Device Driver | Bacaan | `sisop-m13` | Sistem Input/Output dan Device Driver | `materi/sistem_operasi/materi-13.md` |
| Bab 15: Keamanan dan Proteksi pada Sistem Operasi | Bacaan | `sisop-m14` | Keamanan dan Proteksi pada Sistem Operasi | `materi/sistem_operasi/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `sisop-uas` | Ujian Akhir Semester (UAS) Sistem Operasi | `SISOP-02` |

### 7.20 Pendidikan Pancasila

| Atribut | Nilai |
|---------|-------|
| Kode | `0953112020` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Landasan historis, kultural, yuridis, dan filosofis Pancasila sebagai dasar negara, ideologi nasional, sistem filsafat, etika, dan pengembangan ilmu. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengantar dan Landasan Pendidikan Pancasila | Bacaan | `panc-m01` | Pengantar dan Landasan Pendidikan Pancasila | `materi/pancasila/materi-01.md` |
| Bab 2: Pancasila dalam Arus Sejarah Perjuangan Bangsa | Bacaan | `panc-m02` | Pancasila dalam Arus Sejarah Perjuangan Bangsa | `materi/pancasila/materi-02.md` |
| Bab 3: Perumusan dan Pengesahan Pancasila sebagai Dasar Negara | Bacaan | `panc-m03` | Perumusan dan Pengesahan Pancasila sebagai Dasar Negara | `materi/pancasila/materi-03.md` |
| Bab 4: Pancasila sebagai Dasar Negara Republik Indonesia | Bacaan | `panc-m04` | Pancasila sebagai Dasar Negara Republik Indonesia | `materi/pancasila/materi-04.md` |
| Bab 5: Implementasi Pancasila dalam Perundang-undangan | Bacaan | `panc-m05` | Implementasi Pancasila dalam Perundang-undangan | `materi/pancasila/materi-05.md` |
| Bab 6: Pancasila sebagai Ideologi Nasional dan Ideologi Terbuka | Bacaan | `panc-m06` | Pancasila sebagai Ideologi Nasional dan Ideologi Terbuka | `materi/pancasila/materi-06.md` |
| Bab 7: Perbandingan Ideologi Pancasila dengan Ideologi Dunia | Bacaan | `panc-m07` | Perbandingan Ideologi Pancasila dengan Ideologi Dunia | `materi/pancasila/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `panc-uts` | Ujian Tengah Semester (UTS) Pendidikan Pancasila | `PANC-01` |
| Bab 9: Pancasila sebagai Sistem Filsafat | Bacaan | `panc-m08` | Pancasila sebagai Sistem Filsafat | `materi/pancasila/materi-08.md` |
| Bab 10: Hakikat Sila-Sila Pancasila secara Ontologis dan Epistemologis | Bacaan | `panc-m09` | Hakikat Sila-Sila Pancasila secara Ontologis dan Epistemologis | `materi/pancasila/materi-09.md` |
| Bab 11: Pancasila sebagai Sistem Etika | Bacaan | `panc-m10` | Pancasila sebagai Sistem Etika | `materi/pancasila/materi-10.md` |
| Bab 12: Etika Kehidupan Berbangsa dan Bernegara | Bacaan | `panc-m11` | Etika Kehidupan Berbangsa dan Bernegara | `materi/pancasila/materi-11.md` |
| Bab 13: Pancasila sebagai Dasar Nilai Pengembangan Ilmu | Bacaan | `panc-m12` | Pancasila sebagai Dasar Nilai Pengembangan Ilmu | `materi/pancasila/materi-12.md` |
| Bab 14: Tantangan Aktualisasi Pancasila di Era Globalisasi | Bacaan | `panc-m13` | Tantangan Aktualisasi Pancasila di Era Globalisasi | `materi/pancasila/materi-13.md` |
| Bab 15: Pengamalan Nilai Pancasila dalam Profesi Teknologi Informasi | Bacaan | `panc-m14` | Pengamalan Nilai Pancasila dalam Profesi Teknologi Informasi | `materi/pancasila/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `panc-uas` | Ujian Akhir Semester (UAS) Pendidikan Pancasila | `PANC-02` |

### 7.21 Metode Numerik

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123021` |
| Pengajar | Dr. Dewi Anggraini, S.Si., M.Kom. |
| NIP Pembuat | 19850105 (Dr. Dewi Anggraini, S.Si., M.Kom.) |
| Deskripsi | Prasyarat: 0953223009; 0953223011. Analisis galat, akar persamaan nirlinier, SPL numerik, interpolasi, regresi, diferensiasi, dan integrasi numerik. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Pengantar Metode Numerik dan Analisis Galat (Error) | Bacaan | `mnum-m01` | Pengantar Metode Numerik dan Analisis Galat (Error) | `materi/metode_numerik/materi-01.md` |
| Bab 2: Representasi Bilangan Titik Kambang dan Perambatan Galat | Bacaan | `mnum-m02` | Representasi Bilangan Titik Kambang dan Perambatan Galat | `materi/metode_numerik/materi-02.md` |
| Bab 3: Akar Persamaan Nirlinier: Metode Bagi Dua dan Regula Falsi | Bacaan | `mnum-m03` | Akar Persamaan Nirlinier: Metode Bagi Dua dan Regula Falsi | `materi/metode_numerik/materi-03.md` |
| Bab 4: Metode Terbuka: Iterasi Titik Tetap, Newton-Raphson, dan Secant | Bacaan | `mnum-m04` | Metode Terbuka: Iterasi Titik Tetap, Newton-Raphson, dan Secant | `materi/metode_numerik/materi-04.md` |
| Bab 5: Sistem Persamaan Linier: Eliminasi Gauss dan Pivoting | Bacaan | `mnum-m05` | Sistem Persamaan Linier: Eliminasi Gauss dan Pivoting | `materi/metode_numerik/materi-05.md` |
| Bab 6: Dekomposisi LU dan Matriks Tridiagonal | Bacaan | `mnum-m06` | Dekomposisi LU dan Matriks Tridiagonal | `materi/metode_numerik/materi-06.md` |
| Bab 7: Metode Iteratif SPL: Jacobi dan Gauss-Seidel | Bacaan | `mnum-m07` | Metode Iteratif SPL: Jacobi dan Gauss-Seidel | `materi/metode_numerik/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `mnum-uts` | Ujian Tengah Semester (UTS) Metode Numerik | `MNUM-01` |
| Bab 9: Interpolasi Polinomial: Metode Lagrange | Bacaan | `mnum-m08` | Interpolasi Polinomial: Metode Lagrange | `materi/metode_numerik/materi-08.md` |
| Bab 10: Interpolasi Beda Terbagi Newton dan Spline | Bacaan | `mnum-m09` | Interpolasi Beda Terbagi Newton dan Spline | `materi/metode_numerik/materi-09.md` |
| Bab 11: Regresi Kuadrat Terkecil (Least Squares Regression) | Bacaan | `mnum-m10` | Regresi Kuadrat Terkecil (Least Squares Regression) | `materi/metode_numerik/materi-10.md` |
| Bab 12: Turunan Numerik: Selisih Maju, Mundur, dan Pusat | Bacaan | `mnum-m11` | Turunan Numerik: Selisih Maju, Mundur, dan Pusat | `materi/metode_numerik/materi-11.md` |
| Bab 13: Integrasi Numerik: Aturan Trapesium dan Aturan Simpson | Bacaan | `mnum-m12` | Integrasi Numerik: Aturan Trapesium dan Aturan Simpson | `materi/metode_numerik/materi-12.md` |
| Bab 14: Integrasi Romberg dan Kuadratur Gauss | Bacaan | `mnum-m13` | Integrasi Romberg dan Kuadratur Gauss | `materi/metode_numerik/materi-13.md` |
| Bab 15: Solusi Numerik Persamaan Diferensial Biasa: Euler dan Runge-Kutta | Bacaan | `mnum-m14` | Solusi Numerik Persamaan Diferensial Biasa: Euler dan Runge-Kutta | `materi/metode_numerik/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `mnum-uas` | Ujian Akhir Semester (UAS) Metode Numerik | `MNUM-02` |

### 7.22 Desain & Analisis Algoritma

| Atribut | Nilai |
|---------|-------|
| Kode | `0953123022` |
| Pengajar | Rudi Hermawan, S.Kom., M.Cs. |
| NIP Pembuat | 19850107 (Rudi Hermawan, S.Kom., M.Cs.) |
| Deskripsi | Prasyarat: 0953223012. Analisis asimtotik, relasi rekurensi, brute force, divide and conquer, decrease and conquer, greedy, dynamic programming, dan NP. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Bab 1: Dasar Analisis Efisiensi Algoritma dan Notasi Asimtotik | Bacaan | `daa-m01` | Dasar Analisis Efisiensi Algoritma dan Notasi Asimtotik | `materi/daa/materi-01.md` |
| Bab 2: Analisis Algoritma Non-Rekursif dan Rekursif (Master Theorem) | Bacaan | `daa-m02` | Analisis Algoritma Non-Rekursif dan Rekursif (Master Theorem) | `materi/daa/materi-02.md` |
| Bab 3: Strategi Brute Force dan Exhaustive Search | Bacaan | `daa-m03` | Strategi Brute Force dan Exhaustive Search | `materi/daa/materi-03.md` |
| Bab 4: Strategi Decrease and Conquer (Insertion Sort, Topological Sort) | Bacaan | `daa-m04` | Strategi Decrease and Conquer (Insertion Sort, Topological Sort) | `materi/daa/materi-04.md` |
| Bab 5: Strategi Divide and Conquer (Merge Sort, Quick Sort, Karatsuba) | Bacaan | `daa-m05` | Strategi Divide and Conquer (Merge Sort, Quick Sort, Karatsuba) | `materi/daa/materi-05.md` |
| Bab 6: Transform and Conquer (Presorting, Gaussian Elimination, Heap) | Bacaan | `daa-m06` | Transform and Conquer (Presorting, Gaussian Elimination, Heap) | `materi/daa/materi-06.md` |
| Bab 7: Space and Time Trade-Offs (Hashing, Horspool, Boyer-Moore) | Bacaan | `daa-m07` | Space and Time Trade-Offs (Hashing, Horspool, Boyer-Moore) | `materi/daa/materi-07.md` |
| Bab 8: Ujian Tengah Semester (UTS) | Ujian | `daa-uts` | Ujian Tengah Semester (UTS) Desain & Analisis Algoritma | `DAA-01` |
| Bab 9: Algoritma Greedy: Coin Change, Knapsack Fraksional, Huffman | Bacaan | `daa-m08` | Algoritma Greedy: Coin Change, Knapsack Fraksional, Huffman | `materi/daa/materi-08.md` |
| Bab 10: Algoritma Greedy pada Graf: Prim, Kruskal, dan Dijkstra | Bacaan | `daa-m09` | Algoritma Greedy pada Graf: Prim, Kruskal, dan Dijkstra | `materi/daa/materi-09.md` |
| Bab 11: Dynamic Programming Dasar: Fibonacci, Coin Row, dan 0/1 Knapsack | Bacaan | `daa-m10` | Dynamic Programming Dasar: Fibonacci, Coin Row, dan 0/1 Knapsack | `materi/daa/materi-10.md` |
| Bab 12: Dynamic Programming Lanjut: LCS dan Floyd-Warshall | Bacaan | `daa-m11` | Dynamic Programming Lanjut: LCS dan Floyd-Warshall | `materi/daa/materi-11.md` |
| Bab 13: Strategi Backtracking: N-Queens, Subset Sum, dan Hamiltonian | Bacaan | `daa-m12` | Strategi Backtracking: N-Queens, Subset Sum, dan Hamiltonian | `materi/daa/materi-12.md` |
| Bab 14: Strategi Branch and Bound: Assignment dan Traveling Salesperson | Bacaan | `daa-m13` | Strategi Branch and Bound: Assignment dan Traveling Salesperson | `materi/daa/materi-13.md` |
| Bab 15: Kelas Kompleksitas P, NP, NP-Complete, dan Algoritma Aproksimasi | Bacaan | `daa-m14` | Kelas Kompleksitas P, NP, NP-Complete, dan Algoritma Aproksimasi | `materi/daa/materi-14.md` |
| Bab 16: Ujian Akhir Semester (UAS) | Ujian | `daa-uas` | Ujian Akhir Semester (UAS) Desain & Analisis Algoritma | `DAA-02` |

### 7.23 Kelas Uji Coba

| Atribut | Nilai |
|---------|-------|
| Kode | `UJI` |
| Pengajar | Tim Examify |
| Deskripsi | Ujian singkat 1 menit untuk mencoba fitur auto-submit. Boleh diulang. |

| Bab | Jenis | ID Materi | Judul | Sumber / Kode Ujian |
|-----|:-----:|-----------|-------|---------------------|
| Mulai di Sini | Bacaan | `uji-01` | Panduan Mengikuti Ujian | `materi/uji/01-panduan.md` |
| Mulai di Sini | Ujian | `uji-coba` | Ujian Coba 1 Menit | `UJI-01` |

## 8. Bank Soal

Keterangan jenis soal: **PG** = Pilihan Ganda, **TF** = Benar/Salah (T = Benar, F = Salah), **IS** = Isian Singkat (beberapa kunci dipisah `;`).

### 8.1 PAI-01: UTS Pendidikan Agama

**Token:** `PAIUT1` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Pendidikan Agama adalah ... | A. Konsep Ketuhanan dan Keimanan<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Konsep Ketuhanan dan Keimanan) | 10 |
| 2 | TF | Materi Hakikat Manusia dan Tanggung Jawabnya merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Pendidikan Agama. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Pendidikan Agama adalah ... | A. Desain Kompiler<br>B. Sumber Ajaran dan Nilai Moral Agama<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Sumber Ajaran dan Nilai Moral Agama) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Pendidikan Agama membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Etika dan Akhlak dalam Kehidupan Akademik<br>D. Komputasi Awan | **C** (Etika dan Akhlak dalam Kehidupan Akademik) | 10 |
| 5 | TF | Konsep Hak Asasi Manusia dan Demokrasi dalam Perspektif Agama dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Pendidikan Agama sebelum UTS adalah ... | A. Kriptografi<br>B. Hukum dan Keadilan Sosial<br>C. Bioinformatika<br>D. Robotika | **B** (Hukum dan Keadilan Sosial) | 10 |
| 7 | TF | Topik Ilmu Pengetahuan, Teknologi, dan Seni dalam Agama adalah materi penutup paruh pertama semester pada Pendidikan Agama. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Pendidikan Agama? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Konsep Ketuhanan dan Keimanan diperlukan untuk memahami bab selanjutnya pada Pendidikan Agama. | Benar / Salah | **Benar** | 10 |

### 8.2 PAI-02: UAS Pendidikan Agama

**Token:** `PAIUA1` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Pendidikan Agama mencakup ... | A. Kerukunan Antarumat Beragama dan Toleransi<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Kerukunan Antarumat Beragama dan Toleransi) | 10 |
| 2 | TF | Topik Masyarakat Madani dan Kesejahteraan Umat dipelajari setelah pelaksanaan Ujian Tengah Semester pada Pendidikan Agama. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Pendidikan Agama adalah ... | A. Sistem Tertanam<br>B. Kebudayaan dan Kearifan Lokal<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Kebudayaan dan Kearifan Lokal) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Pendidikan Agama membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Etika Profesi di Era Digital<br>D. Komputasi Grid | **C** (Etika Profesi di Era Digital) | 10 |
| 5 | TF | Materi Keluarga Sakinah dan Tanggung Jawab Sosial termasuk dalam cakupan Ujian Akhir Semester (UAS) Pendidikan Agama. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Pendidikan Agama adalah ... | A. Teknologi Blockchain<br>B. Kepemimpinan dan Amanah dalam Organisasi<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Kepemimpinan dan Amanah dalam Organisasi) | 10 |
| 7 | TF | Materi Kontribusi Agama dalam Perdamaian Dunia merupakan topik penutup sebelum pelaksanaan UAS Pendidikan Agama. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Pendidikan Agama adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Pendidikan Agama mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.3 KP-01: UTS Konsep Pemrograman

**Token:** `KPUT21` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Konsep Pemrograman adalah ... | A. Pengantar Algoritma dan Pemrograman<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengantar Algoritma dan Pemrograman) | 10 |
| 2 | TF | Materi Pseudocode dan Diagram Alir (Flowchart) merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Konsep Pemrograman. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Konsep Pemrograman adalah ... | A. Desain Kompiler<br>B. Tipe Data, Variabel, dan Konstanta<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Tipe Data, Variabel, dan Konstanta) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Konsep Pemrograman membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Operator Aritmatika, Relasional, dan Logika<br>D. Komputasi Awan | **C** (Operator Aritmatika, Relasional, dan Logika) | 10 |
| 5 | TF | Konsep Input dan Output Standar dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Konsep Pemrograman sebelum UTS adalah ... | A. Kriptografi<br>B. Percabangan Kondisional (If-Else dan Switch)<br>C. Bioinformatika<br>D. Robotika | **B** (Percabangan Kondisional (If-Else dan Switch)) | 10 |
| 7 | TF | Topik Perulangan (For, While, dan Do-While) adalah materi penutup paruh pertama semester pada Konsep Pemrograman. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Konsep Pemrograman? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengantar Algoritma dan Pemrograman diperlukan untuk memahami bab selanjutnya pada Konsep Pemrograman. | Benar / Salah | **Benar** | 10 |

### 8.4 KP-02: UAS Konsep Pemrograman

**Token:** `KPUA22` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Konsep Pemrograman mencakup ... | A. Perulangan Bersarang dan Kontrol Alur<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Perulangan Bersarang dan Kontrol Alur) | 10 |
| 2 | TF | Topik Fungsi, Prosedur, dan Ruang Lingkup Variabel dipelajari setelah pelaksanaan Ujian Tengah Semester pada Konsep Pemrograman. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Konsep Pemrograman adalah ... | A. Sistem Tertanam<br>B. Rekursi dan Penyelesaian Masalah Berulang<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Rekursi dan Penyelesaian Masalah Berulang) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Konsep Pemrograman membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Array Satu Dimensi dan Multi Dimensi<br>D. Komputasi Grid | **C** (Array Satu Dimensi dan Multi Dimensi) | 10 |
| 5 | TF | Materi Manipulasi String dan Karakter termasuk dalam cakupan Ujian Akhir Semester (UAS) Konsep Pemrograman. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Konsep Pemrograman adalah ... | A. Teknologi Blockchain<br>B. Struct, Tipe Data Bentukan, dan Pointer Dasar<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Struct, Tipe Data Bentukan, dan Pointer Dasar) | 10 |
| 7 | TF | Materi Operasi File dan Penanganan Kesalahan Dasar merupakan topik penutup sebelum pelaksanaan UAS Konsep Pemrograman. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Konsep Pemrograman adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Konsep Pemrograman mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.5 SDIG-01: UTS Sistem Digital

**Token:** `SDIUT3` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Sistem Digital adalah ... | A. Sistem Bilangan dan Konversi Basis<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Sistem Bilangan dan Konversi Basis) | 10 |
| 2 | TF | Materi Aritmatika Biner dan Komplemen Dua merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Sistem Digital. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Sistem Digital adalah ... | A. Desain Kompiler<br>B. Kode Digital (BCD, Gray, ASCII)<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Kode Digital (BCD, Gray, ASCII)) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Sistem Digital membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Gerbang Logika Dasar dan Universal<br>D. Komputasi Awan | **C** (Gerbang Logika Dasar dan Universal) | 10 |
| 5 | TF | Konsep Aljabar Boolean dan Teorema De Morgan dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Sistem Digital sebelum UTS adalah ... | A. Kriptografi<br>B. Bentuk Kanonik SOP dan POS<br>C. Bioinformatika<br>D. Robotika | **B** (Bentuk Kanonik SOP dan POS) | 10 |
| 7 | TF | Topik Minimisasi Fungsi dengan Peta Karnaugh (K-Map) adalah materi penutup paruh pertama semester pada Sistem Digital. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Sistem Digital? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Sistem Bilangan dan Konversi Basis diperlukan untuk memahami bab selanjutnya pada Sistem Digital. | Benar / Salah | **Benar** | 10 |

### 8.6 SDIG-02: UAS Sistem Digital

**Token:** `SDIUA3` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Sistem Digital mencakup ... | A. Metode Quine-McCluskey<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Metode Quine-McCluskey) | 10 |
| 2 | TF | Topik Rangkaian Kombinasional: Adder dan Subtractor dipelajari setelah pelaksanaan Ujian Tengah Semester pada Sistem Digital. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Sistem Digital adalah ... | A. Sistem Tertanam<br>B. Decoder, Encoder, dan Multiplexer<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Decoder, Encoder, dan Multiplexer) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Sistem Digital membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Demultiplexer dan Comparator<br>D. Komputasi Grid | **C** (Demultiplexer dan Comparator) | 10 |
| 5 | TF | Materi Pengantar Rangkaian Sekuensial: Latch dan Flip-Flop termasuk dalam cakupan Ujian Akhir Semester (UAS) Sistem Digital. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Sistem Digital adalah ... | A. Teknologi Blockchain<br>B. Register dan Shift Register<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Register dan Shift Register) | 10 |
| 7 | TF | Materi Counter Sinkron dan Asinkron merupakan topik penutup sebelum pelaksanaan UAS Sistem Digital. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Sistem Digital adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Sistem Digital mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.7 KALK-01: UTS Kalkulus I

**Token:** `KALUT4` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Kalkulus I adalah ... | A. Sistem Bilangan Real dan Pertaksamaan<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Sistem Bilangan Real dan Pertaksamaan) | 10 |
| 2 | TF | Materi Fungsi, Domain, dan Grafik Fungsi merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Kalkulus I. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Kalkulus I adalah ... | A. Desain Kompiler<br>B. Fungsi Transenden dan Invers<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Fungsi Transenden dan Invers) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Kalkulus I membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Konsep Limit dan Teorema Limit<br>D. Komputasi Awan | **C** (Konsep Limit dan Teorema Limit) | 10 |
| 5 | TF | Konsep Kekontinuan Fungsi pada Suatu Titik dan Interval dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Kalkulus I sebelum UTS adalah ... | A. Kriptografi<br>B. Definisi Turunan dan Aturan Pencarian Turunan<br>C. Bioinformatika<br>D. Robotika | **B** (Definisi Turunan dan Aturan Pencarian Turunan) | 10 |
| 7 | TF | Topik Aturan Rantai dan Turunan Implisit adalah materi penutup paruh pertama semester pada Kalkulus I. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Kalkulus I? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Sistem Bilangan Real dan Pertaksamaan diperlukan untuk memahami bab selanjutnya pada Kalkulus I. | Benar / Salah | **Benar** | 10 |

### 8.8 KALK-02: UAS Kalkulus I

**Token:** `KALUA4` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Kalkulus I mencakup ... | A. Turunan Tingkat Tinggi dan Laju Berkaitan<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Turunan Tingkat Tinggi dan Laju Berkaitan) | 10 |
| 2 | TF | Topik Nilai Ekstrem, Kemonotonan, dan Kecekungan dipelajari setelah pelaksanaan Ujian Tengah Semester pada Kalkulus I. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Kalkulus I adalah ... | A. Sistem Tertanam<br>B. Masalah Optimasi Terapan<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Masalah Optimasi Terapan) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Kalkulus I membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Teorema Nilai Rata-Rata dan Dalil L'Hopital<br>D. Komputasi Grid | **C** (Teorema Nilai Rata-Rata dan Dalil L'Hopital) | 10 |
| 5 | TF | Materi Integral Tak Tentu dan Anti-Turunan termasuk dalam cakupan Ujian Akhir Semester (UAS) Kalkulus I. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Kalkulus I adalah ... | A. Teknologi Blockchain<br>B. Integral Tentu dan Jumlah Riemann<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Integral Tentu dan Jumlah Riemann) | 10 |
| 7 | TF | Materi Teorema Dasar Kalkulus dan Aplikasi Luas Daerah merupakan topik penutup sebelum pelaksanaan UAS Kalkulus I. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Kalkulus I adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Kalkulus I mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.9 FIS-01: UTS Fisika

**Token:** `FISUT5` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Fisika adalah ... | A. Besaran, Satuan SI, dan Analisis Vektor<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Besaran, Satuan SI, dan Analisis Vektor) | 10 |
| 2 | TF | Materi Kinematika Gerak Lurus merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Fisika. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Fisika adalah ... | A. Desain Kompiler<br>B. Gerak Parabola dan Gerak Melingkar<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Gerak Parabola dan Gerak Melingkar) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Fisika membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Hukum-Hukum Newton tentang Gerak<br>D. Komputasi Awan | **C** (Hukum-Hukum Newton tentang Gerak) | 10 |
| 5 | TF | Konsep Usaha, Energi Kinetik, dan Energi Potensial dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Fisika sebelum UTS adalah ... | A. Kriptografi<br>B. Momentum Linear dan Tumbukan<br>C. Bioinformatika<br>D. Robotika | **B** (Momentum Linear dan Tumbukan) | 10 |
| 7 | TF | Topik Dinamika Rotasi dan Kesetimbangan Benda Tegar adalah materi penutup paruh pertama semester pada Fisika. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Fisika? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Besaran, Satuan SI, dan Analisis Vektor diperlukan untuk memahami bab selanjutnya pada Fisika. | Benar / Salah | **Benar** | 10 |

### 8.10 FIS-02: UAS Fisika

**Token:** `FISUA5` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Fisika mencakup ... | A. Mekanika Fluida Statis dan Dinamis<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Mekanika Fluida Statis dan Dinamis) | 10 |
| 2 | TF | Topik Getaran Harmonik dan Gelombang Mekanik dipelajari setelah pelaksanaan Ujian Tengah Semester pada Fisika. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Fisika adalah ... | A. Sistem Tertanam<br>B. Suhu, Kalor, dan Hukum Termodinamika<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Suhu, Kalor, dan Hukum Termodinamika) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Fisika membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Elektrostatika: Hukum Coulomb dan Medan Listrik<br>D. Komputasi Grid | **C** (Elektrostatika: Hukum Coulomb dan Medan Listrik) | 10 |
| 5 | TF | Materi Potensial Listrik dan Kapasitor termasuk dalam cakupan Ujian Akhir Semester (UAS) Fisika. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Fisika adalah ... | A. Teknologi Blockchain<br>B. Rangkaian Arus Searah dan Hukum Kirchhoff<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Rangkaian Arus Searah dan Hukum Kirchhoff) | 10 |
| 7 | TF | Materi Medan Magnet dan Induksi Elektromagnetik merupakan topik penutup sebelum pelaksanaan UAS Fisika. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Fisika adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Fisika mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.11 BING-01: UTS Bahasa Inggris I

**Token:** `BINUT6` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Bahasa Inggris I adalah ... | A. Parts of Speech and Sentence Structure<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Parts of Speech and Sentence Structure) | 10 |
| 2 | TF | Materi Present Tenses in Academic Context merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Bahasa Inggris I. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Bahasa Inggris I adalah ... | A. Desain Kompiler<br>B. Past and Future Tenses for Technical Reporting<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Past and Future Tenses for Technical Reporting) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Bahasa Inggris I membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Passive Voice in Scientific Writing<br>D. Komputasi Awan | **C** (Passive Voice in Scientific Writing) | 10 |
| 5 | TF | Konsep Reading Strategies: Skimming and Scanning dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Bahasa Inggris I sebelum UTS adalah ... | A. Kriptografi<br>B. Identifying Main Ideas and Supporting Details<br>C. Bioinformatika<br>D. Robotika | **B** (Identifying Main Ideas and Supporting Details) | 10 |
| 7 | TF | Topik Contextual Vocabulary in Computer Science adalah materi penutup paruh pertama semester pada Bahasa Inggris I. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Bahasa Inggris I? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Parts of Speech and Sentence Structure diperlukan untuk memahami bab selanjutnya pada Bahasa Inggris I. | Benar / Salah | **Benar** | 10 |

### 8.12 BING-02: UAS Bahasa Inggris I

**Token:** `BINUA6` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Bahasa Inggris I mencakup ... | A. Subject-Verb Agreement and Parallel Structure<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Subject-Verb Agreement and Parallel Structure) | 10 |
| 2 | TF | Topik Relative Clauses and Adjective Phrases dipelajari setelah pelaksanaan Ujian Tengah Semester pada Bahasa Inggris I. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Bahasa Inggris I adalah ... | A. Sistem Tertanam<br>B. Modal Auxiliaries and Conditional Sentences<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Modal Auxiliaries and Conditional Sentences) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Bahasa Inggris I membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Summarizing Technical Articles<br>D. Komputasi Grid | **C** (Summarizing Technical Articles) | 10 |
| 5 | TF | Materi Paragraph Organization: Topic and Concluding Sentences termasuk dalam cakupan Ujian Akhir Semester (UAS) Bahasa Inggris I. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Bahasa Inggris I adalah ... | A. Teknologi Blockchain<br>B. Academic Listening and Note-Taking<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Academic Listening and Note-Taking) | 10 |
| 7 | TF | Materi Oral Presentation Fundamentals merupakan topik penutup sebelum pelaksanaan UAS Bahasa Inggris I. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Bahasa Inggris I adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Bahasa Inggris I mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.13 STAT-01: UTS Statistika & Probabilitas

**Token:** `STAUT7` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Statistika & Probabilitas adalah ... | A. Pengantar Statistika dan Penyajian Data<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengantar Statistika dan Penyajian Data) | 10 |
| 2 | TF | Materi Ukuran Pemusatan Data (Mean, Median, Modus) merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Statistika & Probabilitas. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Statistika & Probabilitas adalah ... | A. Desain Kompiler<br>B. Ukuran Penyebaran Data (Variansi dan Simpangan Baku)<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Ukuran Penyebaran Data (Variansi dan Simpangan Baku)) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Statistika & Probabilitas membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Konsep Dasar Ruang Sampel dan Kejadian<br>D. Komputasi Awan | **C** (Konsep Dasar Ruang Sampel dan Kejadian) | 10 |
| 5 | TF | Konsep Aturan Peluang, Peluang Bersyarat, dan Teorema Bayes dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Statistika & Probabilitas sebelum UTS adalah ... | A. Kriptografi<br>B. Peubah Acak Diskrit dan Kontinu<br>C. Bioinformatika<br>D. Robotika | **B** (Peubah Acak Diskrit dan Kontinu) | 10 |
| 7 | TF | Topik Nilai Harapan dan Variansi Peubah Acak adalah materi penutup paruh pertama semester pada Statistika & Probabilitas. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Statistika & Probabilitas? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengantar Statistika dan Penyajian Data diperlukan untuk memahami bab selanjutnya pada Statistika & Probabilitas. | Benar / Salah | **Benar** | 10 |

### 8.14 STAT-02: UAS Statistika & Probabilitas

**Token:** `STAUA7` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Statistika & Probabilitas mencakup ... | A. Distribusi Peluang Diskrit (Binomial, Poisson)<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Distribusi Peluang Diskrit (Binomial, Poisson)) | 10 |
| 2 | TF | Topik Distribusi Peluang Kontinu dan Distribusi Normal dipelajari setelah pelaksanaan Ujian Tengah Semester pada Statistika & Probabilitas. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Statistika & Probabilitas adalah ... | A. Sistem Tertanam<br>B. Distribusi Sampling dan Teorema Limit Pusat<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Distribusi Sampling dan Teorema Limit Pusat) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Statistika & Probabilitas membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Pendugaan Titik dan Selang Kepercayaan<br>D. Komputasi Grid | **C** (Pendugaan Titik dan Selang Kepercayaan) | 10 |
| 5 | TF | Materi Pengujian Hipotesis Satu dan Dua Sampel termasuk dalam cakupan Ujian Akhir Semester (UAS) Statistika & Probabilitas. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Statistika & Probabilitas adalah ... | A. Teknologi Blockchain<br>B. Analisis Korelasi dan Regresi Linear Sederhana<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Analisis Korelasi dan Regresi Linear Sederhana) | 10 |
| 7 | TF | Materi Uji Khi-Kuadrat dan Analisis Variansi Dasar merupakan topik penutup sebelum pelaksanaan UAS Statistika & Probabilitas. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Statistika & Probabilitas adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Statistika & Probabilitas mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.15 BIND-01: UTS Bahasa Indonesia

**Token:** `BINUT8` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Bahasa Indonesia adalah ... | A. Sejarah, Kedudukan, dan Fungsi Bahasa Indonesia<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Sejarah, Kedudukan, dan Fungsi Bahasa Indonesia) | 10 |
| 2 | TF | Materi Ragam Bahasa Ilmiah dan Ejaan Bahasa Indonesia (EYD) merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Bahasa Indonesia. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Bahasa Indonesia adalah ... | A. Desain Kompiler<br>B. Pilihan Kata (Diksi) dan Kata Baku<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Pilihan Kata (Diksi) dan Kata Baku) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Bahasa Indonesia membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Kalimat Efektif dalam Penulisan Akademik<br>D. Komputasi Awan | **C** (Kalimat Efektif dalam Penulisan Akademik) | 10 |
| 5 | TF | Konsep Pengembangan Paragraf yang Kohesif dan Koheren dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Bahasa Indonesia sebelum UTS adalah ... | A. Kriptografi<br>B. Perencanaan Karangan Ilmiah dan Topik Penelitian<br>C. Bioinformatika<br>D. Robotika | **B** (Perencanaan Karangan Ilmiah dan Topik Penelitian) | 10 |
| 7 | TF | Topik Sistematika Karya Tulis Ilmiah adalah materi penutup paruh pertama semester pada Bahasa Indonesia. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Bahasa Indonesia? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Sejarah, Kedudukan, dan Fungsi Bahasa Indonesia diperlukan untuk memahami bab selanjutnya pada Bahasa Indonesia. | Benar / Salah | **Benar** | 10 |

### 8.16 BIND-02: UAS Bahasa Indonesia

**Token:** `BINUA8` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Bahasa Indonesia mencakup ... | A. Teknik Kutipan Langsung dan Tidak Langsung<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Teknik Kutipan Langsung dan Tidak Langsung) | 10 |
| 2 | TF | Topik Penulisan Catatan Kaki dan Daftar Pustaka dipelajari setelah pelaksanaan Ujian Tengah Semester pada Bahasa Indonesia. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Bahasa Indonesia adalah ... | A. Sistem Tertanam<br>B. Abstrak, Pendahuluan, dan Tinjauan Pustaka<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Abstrak, Pendahuluan, dan Tinjauan Pustaka) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Bahasa Indonesia membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Penulisan Metodologi dan Pembahasan Hasil<br>D. Komputasi Grid | **C** (Penulisan Metodologi dan Pembahasan Hasil) | 10 |
| 5 | TF | Materi Simpulan, Saran, dan Penyuntingan Naskah termasuk dalam cakupan Ujian Akhir Semester (UAS) Bahasa Indonesia. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Bahasa Indonesia adalah ... | A. Teknologi Blockchain<br>B. Korespondensi Resmi dan Surat Akademik<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Korespondensi Resmi dan Surat Akademik) | 10 |
| 7 | TF | Materi Teknik Presentasi Ilmiah Berbahasa Indonesia merupakan topik penutup sebelum pelaksanaan UAS Bahasa Indonesia. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Bahasa Indonesia adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Bahasa Indonesia mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.17 KALB-01: UTS Kalkulus II

**Token:** `KALUT9` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Kalkulus II adalah ... | A. Teknik Integrasi Substitusi dan Parsial<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Teknik Integrasi Substitusi dan Parsial) | 10 |
| 2 | TF | Materi Integral Fungsi Trigonometri merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Kalkulus II. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Kalkulus II adalah ... | A. Desain Kompiler<br>B. Substitusi Trigonometri dan Pecahan Parsial<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Substitusi Trigonometri dan Pecahan Parsial) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Kalkulus II membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Aplikasi Integral: Volume Benda Putar<br>D. Komputasi Awan | **C** (Aplikasi Integral: Volume Benda Putar) | 10 |
| 5 | TF | Konsep Panjang Busur dan Luas Permukaan Putar dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Kalkulus II sebelum UTS adalah ... | A. Kriptografi<br>B. Bentuk Tak Tentu dan Integral Tak Wajar<br>C. Bioinformatika<br>D. Robotika | **B** (Bentuk Tak Tentu dan Integral Tak Wajar) | 10 |
| 7 | TF | Topik Sistem Koordinat Polar dan Persamaan Parametrik adalah materi penutup paruh pertama semester pada Kalkulus II. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Kalkulus II? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Teknik Integrasi Substitusi dan Parsial diperlukan untuk memahami bab selanjutnya pada Kalkulus II. | Benar / Salah | **Benar** | 10 |

### 8.18 KALB-02: UAS Kalkulus II

**Token:** `KALUA9` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Kalkulus II mencakup ... | A. Barisan Tak Hingga dan Kekonvergenan<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Barisan Tak Hingga dan Kekonvergenan) | 10 |
| 2 | TF | Topik Deret Tak Hingga dan Uji Konvergensi dipelajari setelah pelaksanaan Ujian Tengah Semester pada Kalkulus II. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Kalkulus II adalah ... | A. Sistem Tertanam<br>B. Deret Pangkat, Deret Taylor, dan Deret Maclaurin<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Deret Pangkat, Deret Taylor, dan Deret Maclaurin) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Kalkulus II membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Fungsi Dua Peubah atau Lebih dan Limitnya<br>D. Komputasi Grid | **C** (Fungsi Dua Peubah atau Lebih dan Limitnya) | 10 |
| 5 | TF | Materi Turunan Parsial dan Keterdiferensialan termasuk dalam cakupan Ujian Akhir Semester (UAS) Kalkulus II. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Kalkulus II adalah ... | A. Teknologi Blockchain<br>B. Aturan Rantai dan Gradien Fungsi Peubah Banyak<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Aturan Rantai dan Gradien Fungsi Peubah Banyak) | 10 |
| 7 | TF | Materi Integral Lipat Dua dan Aplikasinya merupakan topik penutup sebelum pelaksanaan UAS Kalkulus II. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Kalkulus II adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Kalkulus II mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.19 MDIS-01: UTS Matematika Diskrit I

**Token:** `MDIUT0` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Matematika Diskrit I adalah ... | A. Logika Proposisional dan Tabel Kebenaran<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Logika Proposisional dan Tabel Kebenaran) | 10 |
| 2 | TF | Materi Ekuivalensi Logika dan Hukum-Hukum Logika merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Matematika Diskrit I. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Matematika Diskrit I adalah ... | A. Desain Kompiler<br>B. Logika Predikat dan Kuantor<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Logika Predikat dan Kuantor) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Matematika Diskrit I membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Aturan Inferensi dan Metode Pembuktian<br>D. Komputasi Awan | **C** (Aturan Inferensi dan Metode Pembuktian) | 10 |
| 5 | TF | Konsep Teori Himpunan dan Operasi Himpunan dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Matematika Diskrit I sebelum UTS adalah ... | A. Kriptografi<br>B. Relasi dan Sifat-Sifat Relasi<br>C. Bioinformatika<br>D. Robotika | **B** (Relasi dan Sifat-Sifat Relasi) | 10 |
| 7 | TF | Topik Relasi Ekuivalensi dan Pengurutan Parsial adalah materi penutup paruh pertama semester pada Matematika Diskrit I. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Matematika Diskrit I? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Logika Proposisional dan Tabel Kebenaran diperlukan untuk memahami bab selanjutnya pada Matematika Diskrit I. | Benar / Salah | **Benar** | 10 |

### 8.20 MDIS-02: UAS Matematika Diskrit I

**Token:** `MDIUA0` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Matematika Diskrit I mencakup ... | A. Fungsi, Fungsi Injektif, Surjektif, dan Bijektif<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Fungsi, Fungsi Injektif, Surjektif, dan Bijektif) | 10 |
| 2 | TF | Topik Barisan, Notasi Sigma, dan Kardinalitas dipelajari setelah pelaksanaan Ujian Tengah Semester pada Matematika Diskrit I. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Matematika Diskrit I adalah ... | A. Sistem Tertanam<br>B. Induksi Matematika dan Induksi Kuat<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Induksi Matematika dan Induksi Kuat) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Matematika Diskrit I membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Definisi Rekursif dan Algoritma Rekursif<br>D. Komputasi Grid | **C** (Definisi Rekursif dan Algoritma Rekursif) | 10 |
| 5 | TF | Materi Prinsip Dasar Membilang (Aturan Penjumlahan dan Perkalian) termasuk dalam cakupan Ujian Akhir Semester (UAS) Matematika Diskrit I. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Matematika Diskrit I adalah ... | A. Teknologi Blockchain<br>B. Prinsip Sarang Merpati (Pigeonhole Principle)<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Prinsip Sarang Merpati (Pigeonhole Principle)) | 10 |
| 7 | TF | Materi Permutasi, Kombinasi, dan Koefisien Binomial merupakan topik penutup sebelum pelaksanaan UAS Matematika Diskrit I. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Matematika Diskrit I adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Matematika Diskrit I mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.21 ALIN-01: UTS Aljabar Linier

**Token:** `ALIUT1` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Aljabar Linier adalah ... | A. Sistem Persamaan Linier dan Eliminasi Gauss-Jordan<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Sistem Persamaan Linier dan Eliminasi Gauss-Jordan) | 10 |
| 2 | TF | Materi Matriks dan Operasi Aljabar Matriks merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Aljabar Linier. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Aljabar Linier adalah ... | A. Desain Kompiler<br>B. Matriks Invers dan Matriks Elementer<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Matriks Invers dan Matriks Elementer) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Aljabar Linier membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Determinan dengan Ekspansi Kofaktor dan Reduksi Baris<br>D. Komputasi Awan | **C** (Determinan dengan Ekspansi Kofaktor dan Reduksi Baris) | 10 |
| 5 | TF | Konsep Aturan Cramer dan Aplikasi Determinan dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Aljabar Linier sebelum UTS adalah ... | A. Kriptografi<br>B. Vektor di Ruang 2D dan 3D<br>C. Bioinformatika<br>D. Robotika | **B** (Vektor di Ruang 2D dan 3D) | 10 |
| 7 | TF | Topik Hasil Kali Titik (Dot Product) dan Hasil Kali Silang adalah materi penutup paruh pertama semester pada Aljabar Linier. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Aljabar Linier? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Sistem Persamaan Linier dan Eliminasi Gauss-Jordan diperlukan untuk memahami bab selanjutnya pada Aljabar Linier. | Benar / Salah | **Benar** | 10 |

### 8.22 ALIN-02: UAS Aljabar Linier

**Token:** `ALIUA1` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Aljabar Linier mencakup ... | A. Ruang Vektor Umum dan Subruang<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Ruang Vektor Umum dan Subruang) | 10 |
| 2 | TF | Topik Kombinasi Linier dan Kebebasan Linier dipelajari setelah pelaksanaan Ujian Tengah Semester pada Aljabar Linier. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Aljabar Linier adalah ... | A. Sistem Tertanam<br>B. Basis, Dimensi, dan Rank Matriks<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Basis, Dimensi, dan Rank Matriks) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Aljabar Linier membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Ruang Hasil Kali Dalam dan Proses Gram-Schmidt<br>D. Komputasi Grid | **C** (Ruang Hasil Kali Dalam dan Proses Gram-Schmidt) | 10 |
| 5 | TF | Materi Nilai Eigen, Vektor Eigen, dan Diagonalisasi termasuk dalam cakupan Ujian Akhir Semester (UAS) Aljabar Linier. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Aljabar Linier adalah ... | A. Teknologi Blockchain<br>B. Transformasi Linier, Kernel, dan Jangkauan<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Transformasi Linier, Kernel, dan Jangkauan) | 10 |
| 7 | TF | Materi Matriks Transformasi Linier dan Aplikasi Komputasi merupakan topik penutup sebelum pelaksanaan UAS Aljabar Linier. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Aljabar Linier adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Aljabar Linier mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.23 SDA-01: UTS Struktur Data & Algoritma

**Token:** `SDAUT2` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Struktur Data & Algoritma adalah ... | A. Kontrak Belajar, ADT, dan Kompleksitas Waktu Big-O<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Kontrak Belajar, ADT, dan Kompleksitas Waktu Big-O) | 10 |
| 2 | TF | Materi Array Dinamis dan Analisis Memori merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Struktur Data & Algoritma. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Struktur Data & Algoritma adalah ... | A. Desain Kompiler<br>B. Single Linked List dan Operasi Dasar<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Single Linked List dan Operasi Dasar) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Struktur Data & Algoritma membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Double Linked List dan Circular Linked List<br>D. Komputasi Awan | **C** (Double Linked List dan Circular Linked List) | 10 |
| 5 | TF | Konsep Stack (Tumpukan) dan Evaluasi Ekspresi dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Struktur Data & Algoritma sebelum UTS adalah ... | A. Kriptografi<br>B. Queue (Antrean), Circular Queue, dan Priority Queue<br>C. Bioinformatika<br>D. Robotika | **B** (Queue (Antrean), Circular Queue, dan Priority Queue) | 10 |
| 7 | TF | Topik Algoritma Pencarian: Linear Search dan Binary Search adalah materi penutup paruh pertama semester pada Struktur Data & Algoritma. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Struktur Data & Algoritma? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Kontrak Belajar, ADT, dan Kompleksitas Waktu Big-O diperlukan untuk memahami bab selanjutnya pada Struktur Data & Algoritma. | Benar / Salah | **Benar** | 10 |

### 8.24 SDA-02: UAS Struktur Data & Algoritma

**Token:** `SDAUA2` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Struktur Data & Algoritma mencakup ... | A. Algoritma Pengurutan Dasar: Bubble, Selection, Insertion Sort<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Algoritma Pengurutan Dasar: Bubble, Selection, Insertion Sort) | 10 |
| 2 | TF | Topik Algoritma Pengurutan Lanjut: Merge Sort dan Quick Sort dipelajari setelah pelaksanaan Ujian Tengah Semester pada Struktur Data & Algoritma. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Struktur Data & Algoritma adalah ... | A. Sistem Tertanam<br>B. Konsep Pohon (Tree) dan Binary Tree Traversal<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Konsep Pohon (Tree) dan Binary Tree Traversal) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Struktur Data & Algoritma membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Binary Search Tree (BST): Pencarian, Penyisipan, Penghapusan<br>D. Komputasi Grid | **C** (Binary Search Tree (BST): Pencarian, Penyisipan, Penghapusan) | 10 |
| 5 | TF | Materi AVL Tree dan Penyeimbangan Pohon termasuk dalam cakupan Ujian Akhir Semester (UAS) Struktur Data & Algoritma. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Struktur Data & Algoritma adalah ... | A. Teknologi Blockchain<br>B. Heap, Heapsort, dan Hash Table<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Heap, Heapsort, dan Hash Table) | 10 |
| 7 | TF | Materi Representasi Graf serta Penelusuran BFS dan DFS merupakan topik penutup sebelum pelaksanaan UAS Struktur Data & Algoritma. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Struktur Data & Algoritma adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Struktur Data & Algoritma mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.25 OSK-01: UTS Organisasi Sistem Komputer

**Token:** `OSKUT3` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Organisasi Sistem Komputer adalah ... | A. Pengantar Organisasi dan Arsitektur Komputer<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengantar Organisasi dan Arsitektur Komputer) | 10 |
| 2 | TF | Materi Evolusi Komputer dan Arsitektur Von Neumann merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Organisasi Sistem Komputer. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Organisasi Sistem Komputer adalah ... | A. Desain Kompiler<br>B. Sistem Bus dan Interkoneksi Komponen<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Sistem Bus dan Interkoneksi Komponen) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Organisasi Sistem Komputer membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Memori Internal: RAM, ROM, dan Organisasi Sel Memori<br>D. Komputasi Awan | **C** (Memori Internal: RAM, ROM, dan Organisasi Sel Memori) | 10 |
| 5 | TF | Konsep Memori Cache dan Prinsip Lokalitas dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Organisasi Sistem Komputer sebelum UTS adalah ... | A. Kriptografi<br>B. Memori Eksternal: Magnetic Disk, SSD, dan RAID<br>C. Bioinformatika<br>D. Robotika | **B** (Memori Eksternal: Magnetic Disk, SSD, dan RAID) | 10 |
| 7 | TF | Topik Modul Input/Output, Interrupt, dan DMA adalah materi penutup paruh pertama semester pada Organisasi Sistem Komputer. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Organisasi Sistem Komputer? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengantar Organisasi dan Arsitektur Komputer diperlukan untuk memahami bab selanjutnya pada Organisasi Sistem Komputer. | Benar / Salah | **Benar** | 10 |

### 8.26 OSK-02: UAS Organisasi Sistem Komputer

**Token:** `OSKUA3` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Organisasi Sistem Komputer mencakup ... | A. Aritmatika Komputer: Integer dan Floating Point IEEE 754<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Aritmatika Komputer: Integer dan Floating Point IEEE 754) | 10 |
| 2 | TF | Topik Karakteristik dan Fungsi Set Instruksi (ISA) dipelajari setelah pelaksanaan Ujian Tengah Semester pada Organisasi Sistem Komputer. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Organisasi Sistem Komputer adalah ... | A. Sistem Tertanam<br>B. Mode Pengalamatan dan Format Instruksi<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Mode Pengalamatan dan Format Instruksi) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Organisasi Sistem Komputer membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Struktur dan Fungsi Prosesor (CPU) serta Siklus Instruksi<br>D. Komputasi Grid | **C** (Struktur dan Fungsi Prosesor (CPU) serta Siklus Instruksi) | 10 |
| 5 | TF | Materi Pipelining Instruksi dan Penanganan Hazard termasuk dalam cakupan Ujian Akhir Semester (UAS) Organisasi Sistem Komputer. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Organisasi Sistem Komputer adalah ... | A. Teknologi Blockchain<br>B. Arsitektur RISC vs CISC<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Arsitektur RISC vs CISC) | 10 |
| 7 | TF | Materi Unit Kontrol dan Prosesor Multicore merupakan topik penutup sebelum pelaksanaan UAS Organisasi Sistem Komputer. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Organisasi Sistem Komputer adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Organisasi Sistem Komputer mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.27 PKN-01: UTS Pendidikan Kewarganegaraan

**Token:** `PKNUT4` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Pendidikan Kewarganegaraan adalah ... | A. Hakikat dan Pentingnya Pendidikan Kewarganegaraan<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Hakikat dan Pentingnya Pendidikan Kewarganegaraan) | 10 |
| 2 | TF | Materi Identitas Nasional dan Karakter Bangsa Indonesia merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Pendidikan Kewarganegaraan. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Pendidikan Kewarganegaraan adalah ... | A. Desain Kompiler<br>B. Integrasi Nasional dalam Masyarakat Majemuk<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Integrasi Nasional dalam Masyarakat Majemuk) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Pendidikan Kewarganegaraan membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Nilai dan Norma Konstitusional UUD NRI 1945<br>D. Komputasi Awan | **C** (Nilai dan Norma Konstitusional UUD NRI 1945) | 10 |
| 5 | TF | Konsep Harmoni Kewajiban dan Hak Negara serta Warga Negara dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Pendidikan Kewarganegaraan sebelum UTS adalah ... | A. Kriptografi<br>B. Hakikat, Instrumentasi, dan Praksis Demokrasi Indonesia<br>C. Bioinformatika<br>D. Robotika | **B** (Hakikat, Instrumentasi, dan Praksis Demokrasi Indonesia) | 10 |
| 7 | TF | Topik Penegakan Hukum yang Berkeadilan adalah materi penutup paruh pertama semester pada Pendidikan Kewarganegaraan. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Pendidikan Kewarganegaraan? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Hakikat dan Pentingnya Pendidikan Kewarganegaraan diperlukan untuk memahami bab selanjutnya pada Pendidikan Kewarganegaraan. | Benar / Salah | **Benar** | 10 |

### 8.28 PKN-02: UAS Pendidikan Kewarganegaraan

**Token:** `PKNUA4` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Pendidikan Kewarganegaraan mencakup ... | A. Dinamika Historis Konstitusi di Indonesia<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Dinamika Historis Konstitusi di Indonesia) | 10 |
| 2 | TF | Topik Kewarganegaraan Digital dan Etika Ruang Siber dipelajari setelah pelaksanaan Ujian Tengah Semester pada Pendidikan Kewarganegaraan. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Pendidikan Kewarganegaraan adalah ... | A. Sistem Tertanam<br>B. Wawasan Nusantara sebagai Geopolitik Indonesia<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Wawasan Nusantara sebagai Geopolitik Indonesia) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Pendidikan Kewarganegaraan membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Ketahanan Nasional dan Bela Negara<br>D. Komputasi Grid | **C** (Ketahanan Nasional dan Bela Negara) | 10 |
| 5 | TF | Materi Otonomi Daerah dan Tata Kelola Pemerintahan yang Baik termasuk dalam cakupan Ujian Akhir Semester (UAS) Pendidikan Kewarganegaraan. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Pendidikan Kewarganegaraan adalah ... | A. Teknologi Blockchain<br>B. Pencegahan Korupsi dan Integritas Publik<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Pencegahan Korupsi dan Integritas Publik) | 10 |
| 7 | TF | Materi Peran Generasi Muda dalam Pembangunan Nasional merupakan topik penutup sebelum pelaksanaan UAS Pendidikan Kewarganegaraan. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Pendidikan Kewarganegaraan adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Pendidikan Kewarganegaraan mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.29 BINGB-01: UTS Bahasa Inggris II

**Token:** `BINUT5` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Bahasa Inggris II adalah ... | A. Academic Essay Structure and Thesis Statement<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Academic Essay Structure and Thesis Statement) | 10 |
| 2 | TF | Materi Cause and Effect Essays in Technology merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Bahasa Inggris II. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Bahasa Inggris II adalah ... | A. Desain Kompiler<br>B. Comparison and Contrast Writing<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Comparison and Contrast Writing) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Bahasa Inggris II membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Argumentative Essays and Critical Thinking<br>D. Komputasi Awan | **C** (Argumentative Essays and Critical Thinking) | 10 |
| 5 | TF | Konsep Reading Research Papers: Abstract and Introduction dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Bahasa Inggris II sebelum UTS adalah ... | A. Kriptografi<br>B. Analyzing Methodology and Experimental Results<br>C. Bioinformatika<br>D. Robotika | **B** (Analyzing Methodology and Experimental Results) | 10 |
| 7 | TF | Topik Paraphrasing, Quoting, and Avoiding Plagiarism adalah materi penutup paruh pertama semester pada Bahasa Inggris II. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Bahasa Inggris II? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Academic Essay Structure and Thesis Statement diperlukan untuk memahami bab selanjutnya pada Bahasa Inggris II. | Benar / Salah | **Benar** | 10 |

### 8.30 BINGB-02: UAS Bahasa Inggris II

**Token:** `BINUA5` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Bahasa Inggris II mencakup ... | A. Describing Charts, Graphs, and Data Trends<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Describing Charts, Graphs, and Data Trends) | 10 |
| 2 | TF | Topik Writing Technical Documentation and Reports dipelajari setelah pelaksanaan Ujian Tengah Semester pada Bahasa Inggris II. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Bahasa Inggris II adalah ... | A. Sistem Tertanam<br>B. Professional Email and Cover Letter Writing<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Professional Email and Cover Letter Writing) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Bahasa Inggris II membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Curriculum Vitae and Job Interview Preparation<br>D. Komputasi Grid | **C** (Curriculum Vitae and Job Interview Preparation) | 10 |
| 5 | TF | Materi Designing Effective Presentation Slides termasuk dalam cakupan Ujian Akhir Semester (UAS) Bahasa Inggris II. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Bahasa Inggris II adalah ... | A. Teknologi Blockchain<br>B. Delivering Technical Presentations and Q&A Handling<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Delivering Technical Presentations and Q&A Handling) | 10 |
| 7 | TF | Materi TOEFL/IELTS Academic Reading and Structure Review merupakan topik penutup sebelum pelaksanaan UAS Bahasa Inggris II. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Bahasa Inggris II adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Bahasa Inggris II mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.31 MDISB-01: UTS Matematika Diskrit II

**Token:** `MDIUT6` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Matematika Diskrit II adalah ... | A. Relasi Rekurensi Linier Homogen dan Non-Homogen<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Relasi Rekurensi Linier Homogen dan Non-Homogen) | 10 |
| 2 | TF | Materi Fungsi Pembangkit Biasa dan Eksponensial merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Matematika Diskrit II. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Matematika Diskrit II adalah ... | A. Desain Kompiler<br>B. Prinsip Inklusi-Eksklusi dan Aplikasinya<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Prinsip Inklusi-Eksklusi dan Aplikasinya) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Matematika Diskrit II membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Konsep Dasar Teori Graf dan Terminologi Graf<br>D. Komputasi Awan | **C** (Konsep Dasar Teori Graf dan Terminologi Graf) | 10 |
| 5 | TF | Konsep Representasi Graf dan Isomorfisme Graf dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Matematika Diskrit II sebelum UTS adalah ... | A. Kriptografi<br>B. Keterhubungan, Lintasan Euler, dan Lintasan Hamilton<br>C. Bioinformatika<br>D. Robotika | **B** (Keterhubungan, Lintasan Euler, dan Lintasan Hamilton) | 10 |
| 7 | TF | Topik Lintasan Terpendek: Algoritma Dijkstra dan Bellman-Ford adalah materi penutup paruh pertama semester pada Matematika Diskrit II. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Matematika Diskrit II? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Relasi Rekurensi Linier Homogen dan Non-Homogen diperlukan untuk memahami bab selanjutnya pada Matematika Diskrit II. | Benar / Salah | **Benar** | 10 |

### 8.32 MDISB-02: UAS Matematika Diskrit II

**Token:** `MDIUA6` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Matematika Diskrit II mencakup ... | A. Graf Planar dan Pewarnaan Graf (Graph Coloring)<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Graf Planar dan Pewarnaan Graf (Graph Coloring)) | 10 |
| 2 | TF | Topik Pohon (Trees), Pohon Berakar, dan Aplikasinya dipelajari setelah pelaksanaan Ujian Tengah Semester pada Matematika Diskrit II. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Matematika Diskrit II adalah ... | A. Sistem Tertanam<br>B. Pohon Merentang Minimum: Algoritma Prim dan Kruskal<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Pohon Merentang Minimum: Algoritma Prim dan Kruskal) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Matematika Diskrit II membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Jaringan Transportasi dan Aliran Maksimum<br>D. Komputasi Grid | **C** (Jaringan Transportasi dan Aliran Maksimum) | 10 |
| 5 | TF | Materi Poset, Lattice, dan Struktur Aljabar Diskrit termasuk dalam cakupan Ujian Akhir Semester (UAS) Matematika Diskrit II. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Matematika Diskrit II adalah ... | A. Teknologi Blockchain<br>B. Teori Bilangan Modul dan Kriptografi Kunci Publik<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Teori Bilangan Modul dan Kriptografi Kunci Publik) | 10 |
| 7 | TF | Materi Pengantar Bahasa Formal dan Finite State Automata merupakan topik penutup sebelum pelaksanaan UAS Matematika Diskrit II. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Matematika Diskrit II adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Matematika Diskrit II mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.33 PBO-01: UTS Pemrograman Berorientasi Objek

**Token:** `PBOUT7` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Pemrograman Berorientasi Objek adalah ... | A. Pengenalan Pemrograman Berorientasi Objek<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengenalan Pemrograman Berorientasi Objek) | 10 |
| 2 | TF | Materi Class, Object, dan Konstruktor merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Pemrograman Berorientasi Objek. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Pemrograman Berorientasi Objek adalah ... | A. Desain Kompiler<br>B. Tipe Data, Variabel, dan Method<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Tipe Data, Variabel, dan Method) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Pemrograman Berorientasi Objek membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Encapsulation dan Access Modifier<br>D. Komputasi Awan | **C** (Encapsulation dan Access Modifier) | 10 |
| 5 | TF | Konsep Relasi Antar Kelas: Association, Aggregation, Composition dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Pemrograman Berorientasi Objek sebelum UTS adalah ... | A. Kriptografi<br>B. Inheritance dengan extends dan super<br>C. Bioinformatika<br>D. Robotika | **B** (Inheritance dengan extends dan super) | 10 |
| 7 | TF | Topik Polymorphism: Overloading dan Overriding adalah materi penutup paruh pertama semester pada Pemrograman Berorientasi Objek. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Pemrograman Berorientasi Objek? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengenalan Pemrograman Berorientasi Objek diperlukan untuk memahami bab selanjutnya pada Pemrograman Berorientasi Objek. | Benar / Salah | **Benar** | 10 |

### 8.34 PBO-02: UAS Pemrograman Berorientasi Objek

**Token:** `PBOUA7` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Pemrograman Berorientasi Objek mencakup ... | A. Abstract Class dan Interface<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Abstract Class dan Interface) | 10 |
| 2 | TF | Topik Manipulasi String dan StringBuilder dipelajari setelah pelaksanaan Ujian Tengah Semester pada Pemrograman Berorientasi Objek. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Pemrograman Berorientasi Objek adalah ... | A. Sistem Tertanam<br>B. Generics dan Java Collections Framework<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Generics dan Java Collections Framework) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Pemrograman Berorientasi Objek membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Exception Handling dan Assertions<br>D. Komputasi Grid | **C** (Exception Handling dan Assertions) | 10 |
| 5 | TF | Materi Operasi Input/Output dan File Streams termasuk dalam cakupan Ujian Akhir Semester (UAS) Pemrograman Berorientasi Objek. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Pemrograman Berorientasi Objek adalah ... | A. Teknologi Blockchain<br>B. Pemrograman Antarmuka Grafis dengan JavaFX<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Pemrograman Antarmuka Grafis dengan JavaFX) | 10 |
| 7 | TF | Materi Multithreading dan Sinkronisasi merupakan topik penutup sebelum pelaksanaan UAS Pemrograman Berorientasi Objek. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Pemrograman Berorientasi Objek adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Pemrograman Berorientasi Objek mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.35 BDAT-01: UTS Basis Data

**Token:** `BDAUT8` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Basis Data adalah ... | A. Pengantar Sistem Basis Data dan Arsitektur DBMS<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengantar Sistem Basis Data dan Arsitektur DBMS) | 10 |
| 2 | TF | Materi Model Data Entity-Relationship (ERD) merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Basis Data. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Basis Data adalah ... | A. Desain Kompiler<br>B. Enhanced Entity-Relationship (EER) dan Kardinalitas<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Enhanced Entity-Relationship (EER) dan Kardinalitas) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Basis Data membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Model Data Relasional dan Integritas Referensial<br>D. Komputasi Awan | **C** (Model Data Relasional dan Integritas Referensial) | 10 |
| 5 | TF | Konsep Transformasi ERD ke Skema Relasional dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Basis Data sebelum UTS adalah ... | A. Kriptografi<br>B. Aljabar Relasional dan Kalkulus Relasional<br>C. Bioinformatika<br>D. Robotika | **B** (Aljabar Relasional dan Kalkulus Relasional) | 10 |
| 7 | TF | Topik SQL Dasar: Data Definition Language (DDL) adalah materi penutup paruh pertama semester pada Basis Data. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Basis Data? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengantar Sistem Basis Data dan Arsitektur DBMS diperlukan untuk memahami bab selanjutnya pada Basis Data. | Benar / Salah | **Benar** | 10 |

### 8.36 BDAT-02: UAS Basis Data

**Token:** `BDAUA8` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Basis Data mencakup ... | A. SQL Manipulasi Data: SELECT, INSERT, UPDATE, DELETE<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (SQL Manipulasi Data: SELECT, INSERT, UPDATE, DELETE) | 10 |
| 2 | TF | Topik Fungsi Agregasi, GROUP BY, dan Subquery dipelajari setelah pelaksanaan Ujian Tengah Semester pada Basis Data. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Basis Data adalah ... | A. Sistem Tertanam<br>B. Operasi JOIN Multi-Tabel dan View<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Operasi JOIN Multi-Tabel dan View) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Basis Data membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Ketergantungan Fungsional (Functional Dependency)<br>D. Komputasi Grid | **C** (Ketergantungan Fungsional (Functional Dependency)) | 10 |
| 5 | TF | Materi Normalisasi Basis Data (1NF, 2NF, 3NF, BCNF) termasuk dalam cakupan Ujian Akhir Semester (UAS) Basis Data. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Basis Data adalah ... | A. Teknologi Blockchain<br>B. Indeks, Penyimpanan Fisik, dan Optimasi Query<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Indeks, Penyimpanan Fisik, dan Optimasi Query) | 10 |
| 7 | TF | Materi Manajemen Transaksi, Properti ACID, dan Concurrency Control merupakan topik penutup sebelum pelaksanaan UAS Basis Data. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Basis Data adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Basis Data mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.37 SISOP-01: UTS Sistem Operasi

**Token:** `SISUT9` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Sistem Operasi adalah ... | A. Pengantar, Sejarah, dan Struktur Sistem Operasi<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengantar, Sejarah, dan Struktur Sistem Operasi) | 10 |
| 2 | TF | Materi System Calls dan Arsitektur Kernel merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Sistem Operasi. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Sistem Operasi adalah ... | A. Desain Kompiler<br>B. Konsep Proses, PCB, dan Komunikasi Antarproses (IPC)<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Konsep Proses, PCB, dan Komunikasi Antarproses (IPC)) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Sistem Operasi membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Thread dan Pemrograman Multithreading pada OS<br>D. Komputasi Awan | **C** (Thread dan Pemrograman Multithreading pada OS) | 10 |
| 5 | TF | Konsep Algoritma Penjadwalan CPU (FCFS, SJF, Round Robin) dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Sistem Operasi sebelum UTS adalah ... | A. Kriptografi<br>B. Sinkronisasi Proses dan Masalah Critical Section<br>C. Bioinformatika<br>D. Robotika | **B** (Sinkronisasi Proses dan Masalah Critical Section) | 10 |
| 7 | TF | Topik Semaphore, Mutex, dan Monitor adalah materi penutup paruh pertama semester pada Sistem Operasi. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Sistem Operasi? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengantar, Sejarah, dan Struktur Sistem Operasi diperlukan untuk memahami bab selanjutnya pada Sistem Operasi. | Benar / Salah | **Benar** | 10 |

### 8.38 SISOP-02: UAS Sistem Operasi

**Token:** `SISUA9` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Sistem Operasi mencakup ... | A. Deadlock: Karakteristik, Pencegahan, dan Algoritma Banker<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Deadlock: Karakteristik, Pencegahan, dan Algoritma Banker) | 10 |
| 2 | TF | Topik Manajemen Memori Utama, Paging, dan Segmentasi dipelajari setelah pelaksanaan Ujian Tengah Semester pada Sistem Operasi. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Sistem Operasi adalah ... | A. Sistem Tertanam<br>B. Memori Virtual, Demand Paging, dan Page Replacement<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Memori Virtual, Demand Paging, dan Page Replacement) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Sistem Operasi membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Antarmuka dan Implementasi Sistem Berkas (File System)<br>D. Komputasi Grid | **C** (Antarmuka dan Implementasi Sistem Berkas (File System)) | 10 |
| 5 | TF | Materi Manajemen Penyimpanan Massal dan Penjadwalan Disk termasuk dalam cakupan Ujian Akhir Semester (UAS) Sistem Operasi. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Sistem Operasi adalah ... | A. Teknologi Blockchain<br>B. Sistem Input/Output dan Device Driver<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Sistem Input/Output dan Device Driver) | 10 |
| 7 | TF | Materi Keamanan dan Proteksi pada Sistem Operasi merupakan topik penutup sebelum pelaksanaan UAS Sistem Operasi. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Sistem Operasi adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Sistem Operasi mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.39 PANC-01: UTS Pendidikan Pancasila

**Token:** `PANUT0` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Pendidikan Pancasila adalah ... | A. Pengantar dan Landasan Pendidikan Pancasila<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengantar dan Landasan Pendidikan Pancasila) | 10 |
| 2 | TF | Materi Pancasila dalam Arus Sejarah Perjuangan Bangsa merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Pendidikan Pancasila. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Pendidikan Pancasila adalah ... | A. Desain Kompiler<br>B. Perumusan dan Pengesahan Pancasila sebagai Dasar Negara<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Perumusan dan Pengesahan Pancasila sebagai Dasar Negara) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Pendidikan Pancasila membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Pancasila sebagai Dasar Negara Republik Indonesia<br>D. Komputasi Awan | **C** (Pancasila sebagai Dasar Negara Republik Indonesia) | 10 |
| 5 | TF | Konsep Implementasi Pancasila dalam Perundang-undangan dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Pendidikan Pancasila sebelum UTS adalah ... | A. Kriptografi<br>B. Pancasila sebagai Ideologi Nasional dan Ideologi Terbuka<br>C. Bioinformatika<br>D. Robotika | **B** (Pancasila sebagai Ideologi Nasional dan Ideologi Terbuka) | 10 |
| 7 | TF | Topik Perbandingan Ideologi Pancasila dengan Ideologi Dunia adalah materi penutup paruh pertama semester pada Pendidikan Pancasila. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Pendidikan Pancasila? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengantar dan Landasan Pendidikan Pancasila diperlukan untuk memahami bab selanjutnya pada Pendidikan Pancasila. | Benar / Salah | **Benar** | 10 |

### 8.40 PANC-02: UAS Pendidikan Pancasila

**Token:** `PANUA0` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Pendidikan Pancasila mencakup ... | A. Pancasila sebagai Sistem Filsafat<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Pancasila sebagai Sistem Filsafat) | 10 |
| 2 | TF | Topik Hakikat Sila-Sila Pancasila secara Ontologis dan Epistemologis dipelajari setelah pelaksanaan Ujian Tengah Semester pada Pendidikan Pancasila. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Pendidikan Pancasila adalah ... | A. Sistem Tertanam<br>B. Pancasila sebagai Sistem Etika<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Pancasila sebagai Sistem Etika) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Pendidikan Pancasila membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Etika Kehidupan Berbangsa dan Bernegara<br>D. Komputasi Grid | **C** (Etika Kehidupan Berbangsa dan Bernegara) | 10 |
| 5 | TF | Materi Pancasila sebagai Dasar Nilai Pengembangan Ilmu termasuk dalam cakupan Ujian Akhir Semester (UAS) Pendidikan Pancasila. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Pendidikan Pancasila adalah ... | A. Teknologi Blockchain<br>B. Tantangan Aktualisasi Pancasila di Era Globalisasi<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Tantangan Aktualisasi Pancasila di Era Globalisasi) | 10 |
| 7 | TF | Materi Pengamalan Nilai Pancasila dalam Profesi Teknologi Informasi merupakan topik penutup sebelum pelaksanaan UAS Pendidikan Pancasila. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Pendidikan Pancasila adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Pendidikan Pancasila mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.41 MNUM-01: UTS Metode Numerik

**Token:** `MNUUT1` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Metode Numerik adalah ... | A. Pengantar Metode Numerik dan Analisis Galat (Error)<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Pengantar Metode Numerik dan Analisis Galat (Error)) | 10 |
| 2 | TF | Materi Representasi Bilangan Titik Kambang dan Perambatan Galat merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Metode Numerik. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Metode Numerik adalah ... | A. Desain Kompiler<br>B. Akar Persamaan Nirlinier: Metode Bagi Dua dan Regula Falsi<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Akar Persamaan Nirlinier: Metode Bagi Dua dan Regula Falsi) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Metode Numerik membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Metode Terbuka: Iterasi Titik Tetap, Newton-Raphson, dan Secant<br>D. Komputasi Awan | **C** (Metode Terbuka: Iterasi Titik Tetap, Newton-Raphson, dan Secant) | 10 |
| 5 | TF | Konsep Sistem Persamaan Linier: Eliminasi Gauss dan Pivoting dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Metode Numerik sebelum UTS adalah ... | A. Kriptografi<br>B. Dekomposisi LU dan Matriks Tridiagonal<br>C. Bioinformatika<br>D. Robotika | **B** (Dekomposisi LU dan Matriks Tridiagonal) | 10 |
| 7 | TF | Topik Metode Iteratif SPL: Jacobi dan Gauss-Seidel adalah materi penutup paruh pertama semester pada Metode Numerik. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Metode Numerik? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Pengantar Metode Numerik dan Analisis Galat (Error) diperlukan untuk memahami bab selanjutnya pada Metode Numerik. | Benar / Salah | **Benar** | 10 |

### 8.42 MNUM-02: UAS Metode Numerik

**Token:** `MNUUA1` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Metode Numerik mencakup ... | A. Interpolasi Polinomial: Metode Lagrange<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Interpolasi Polinomial: Metode Lagrange) | 10 |
| 2 | TF | Topik Interpolasi Beda Terbagi Newton dan Spline dipelajari setelah pelaksanaan Ujian Tengah Semester pada Metode Numerik. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Metode Numerik adalah ... | A. Sistem Tertanam<br>B. Regresi Kuadrat Terkecil (Least Squares Regression)<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Regresi Kuadrat Terkecil (Least Squares Regression)) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Metode Numerik membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Turunan Numerik: Selisih Maju, Mundur, dan Pusat<br>D. Komputasi Grid | **C** (Turunan Numerik: Selisih Maju, Mundur, dan Pusat) | 10 |
| 5 | TF | Materi Integrasi Numerik: Aturan Trapesium dan Aturan Simpson termasuk dalam cakupan Ujian Akhir Semester (UAS) Metode Numerik. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Metode Numerik adalah ... | A. Teknologi Blockchain<br>B. Integrasi Romberg dan Kuadratur Gauss<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Integrasi Romberg dan Kuadratur Gauss) | 10 |
| 7 | TF | Materi Solusi Numerik Persamaan Diferensial Biasa: Euler dan Runge-Kutta merupakan topik penutup sebelum pelaksanaan UAS Metode Numerik. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Metode Numerik adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Metode Numerik mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.43 DAA-01: UTS Desain & Analisis Algoritma

**Token:** `DAAUT2` · **Durasi:** 60 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Tidak

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Topik pembuka yang dipelajari pada mata kuliah Desain & Analisis Algoritma adalah ... | A. Dasar Analisis Efisiensi Algoritma dan Notasi Asimtotik<br>B. Rekayasa Jaringan<br>C. Komputasi Kuantum<br>D. Kriptografi Lanjut | **A** (Dasar Analisis Efisiensi Algoritma dan Notasi Asimtotik) | 10 |
| 2 | TF | Materi Analisis Algoritma Non-Rekursif dan Rekursif (Master Theorem) merupakan bagian dari cakupan evaluasi paruh pertama (UTS) pada Desain & Analisis Algoritma. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Pokok bahasan ketiga yang menjadi fondasi pada mata kuliah Desain & Analisis Algoritma adalah ... | A. Desain Kompiler<br>B. Strategi Brute Force dan Exhaustive Search<br>C. Pemrograman Mikrokontroler<br>D. Jaringan Nirkabel | **B** (Strategi Brute Force dan Exhaustive Search) | 10 |
| 4 | PG | Materi pertemuan keempat pada mata kuliah Desain & Analisis Algoritma membahas tentang ... | A. Pemrosesan Citra<br>B. Sistem Pakar<br>C. Strategi Decrease and Conquer (Insertion Sort, Topological Sort)<br>D. Komputasi Awan | **C** (Strategi Decrease and Conquer (Insertion Sort, Topological Sort)) | 10 |
| 5 | TF | Konsep Strategi Divide and Conquer (Merge Sort, Quick Sort, Karatsuba) dipelajari sebelum pelaksanaan Ujian Tengah Semester. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan keenam dalam silabus Desain & Analisis Algoritma sebelum UTS adalah ... | A. Kriptografi<br>B. Transform and Conquer (Presorting, Gaussian Elimination, Heap)<br>C. Bioinformatika<br>D. Robotika | **B** (Transform and Conquer (Presorting, Gaussian Elimination, Heap)) | 10 |
| 7 | TF | Topik Space and Time Trade-Offs (Hashing, Horspool, Boyer-Moore) adalah materi penutup paruh pertama semester pada Desain & Analisis Algoritma. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Tengah Semester adalah ... | - | **UTS** | 10 |
| 9 | PG | Berapa jumlah bab materi yang diujikan pada UTS Desain & Analisis Algoritma? | A. 5 bab<br>B. 6 bab<br>C. 7 bab<br>D. 10 bab | **C** (7 bab) | 10 |
| 10 | TF | Penguasaan konsep dasar Dasar Analisis Efisiensi Algoritma dan Notasi Asimtotik diperlukan untuk memahami bab selanjutnya pada Desain & Analisis Algoritma. | Benar / Salah | **Benar** | 10 |

### 8.44 DAA-02: UAS Desain & Analisis Algoritma

**Token:** `DAAUA2` · **Durasi:** 90 menit · **KKM:** 70 · **Boleh ulang:** Tidak · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Pokok bahasan awal paruh kedua semester pada mata kuliah Desain & Analisis Algoritma mencakup ... | A. Algoritma Greedy: Coin Change, Knapsack Fraksional, Huffman<br>B. Arsitektur Cloud<br>C. Biomedis<br>D. Robotika Industri | **A** (Algoritma Greedy: Coin Change, Knapsack Fraksional, Huffman) | 10 |
| 2 | TF | Topik Algoritma Greedy pada Graf: Prim, Kruskal, dan Dijkstra dipelajari setelah pelaksanaan Ujian Tengah Semester pada Desain & Analisis Algoritma. | Benar / Salah | **Benar** | 10 |
| 3 | PG | Salah satu materi lanjutan yang diujikan pada UAS Desain & Analisis Algoritma adalah ... | A. Sistem Tertanam<br>B. Dynamic Programming Dasar: Fibonacci, Coin Row, dan 0/1 Knapsack<br>C. Realitas Virtual<br>D. Pemrosesan Sinyal | **B** (Dynamic Programming Dasar: Fibonacci, Coin Row, dan 0/1 Knapsack) | 10 |
| 4 | PG | Topik kesebelas pada mata kuliah Desain & Analisis Algoritma membahas mengenai ... | A. Forensik Digital<br>B. Game Engine<br>C. Dynamic Programming Lanjut: LCS dan Floyd-Warshall<br>D. Komputasi Grid | **C** (Dynamic Programming Lanjut: LCS dan Floyd-Warshall) | 10 |
| 5 | TF | Materi Strategi Backtracking: N-Queens, Subset Sum, dan Hamiltonian termasuk dalam cakupan Ujian Akhir Semester (UAS) Desain & Analisis Algoritma. | Benar / Salah | **Benar** | 10 |
| 6 | PG | Pokok bahasan menjelang akhir semester pada Desain & Analisis Algoritma adalah ... | A. Teknologi Blockchain<br>B. Strategi Branch and Bound: Assignment dan Traveling Salesperson<br>C. Visi Komputer<br>D. Jaringan Sensor | **B** (Strategi Branch and Bound: Assignment dan Traveling Salesperson) | 10 |
| 7 | TF | Materi Kelas Kompleksitas P, NP, NP-Complete, dan Algoritma Aproksimasi merupakan topik penutup sebelum pelaksanaan UAS Desain & Analisis Algoritma. | Benar / Salah | **Benar** | 10 |
| 8 | IS | Singkatan dari Ujian Akhir Semester adalah ... | - | **UAS** | 10 |
| 9 | PG | Total keseluruhan bab dalam satu semester pada mata kuliah Desain & Analisis Algoritma adalah ... | A. 12 bab<br>B. 14 bab<br>C. 16 bab<br>D. 18 bab | **C** (16 bab) | 10 |
| 10 | TF | Evaluasi akhir Desain & Analisis Algoritma mengintegrasikan pemahaman konsep dari paruh pertama dan kedua perkuliahan. | Benar / Salah | **Benar** | 10 |

### 8.45 UJI-01: Uji Coba

**Token:** `UJI123` · **Durasi:** 1 menit · **KKM:** 60 · **Boleh ulang:** Ya · **Mode Aman:** Ya

| No | Jenis | Pertanyaan | Opsi | Kunci | Bobot |
|:--:|:-----:|------------|------|-------|:-----:|
| 1 | PG | Ibu kota Indonesia saat ini adalah ... | A. Jakarta<br>B. Bandung<br>C. Surabaya | **A** (Jakarta) | 10 |
| 2 | TF | Satu jam sama dengan 60 menit. | Benar / Salah | **Benar** | 10 |
| 3 | IS | Warna langit pada siang hari yang cerah adalah ... | - | **BIRU** | 10 |

## 9. Keterangan Format File

| File | Format | Keterangan |
|------|--------|------------|
| `siswa.txt` | `NIS\|NAMA\|KELAS\|PIN` | Satu baris = satu siswa |
| `guru.txt` | `NIP\|NAMA\|PIN\|BIDANG` | Satu baris = satu guru |
| `tim.txt` | `NIM\|NAMA\|FOTO\|WEBSITE\|GITHUB\|INSTAGRAM\|LINKEDIN` | Foto relatif dari `data/`; sosial media boleh kosong |
| `kursus.txt` | Blok `[KURSUS]` → header `KUNCI=NILAI` → `[BAB] judul` → baris materi → `[END]` | Materi: `TEKS\|id\|judul\|file.md`, `LINK\|id\|judul\|url`, `PDF\|id\|judul\|file.pdf`, `UJIAN\|id\|judul\|kodeUjian` |
| `bank_soal.txt` | Blok `[UJIAN]` → header → `[SOAL]` → baris soal → `[END]` | `PG\|id\|tanya\|opsi;...\|kunci\|bobot`, `TF\|id\|tanya\|T/F\|bobot`, `IS\|id\|tanya\|jwb;...\|bobot` |
| `materi/<kursus>/*.md` | Markdown | Isi bacaan untuk materi `TEKS` |

Baris kosong dan baris berawalan `#` diabaikan pada semua file.
