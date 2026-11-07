# Examify

Aplikasi desktop **E-Learning & Computer-Based Test (CBT)** berbasis JavaFX dengan arsitektur
MVC + Service Layer + Repository (I/O). Data disimpan di file teks UTF-8, tanpa database.

```
Landing Page ──► Login ──► Kursus Saya ──► Detail Kursus ──► Bab ──► Materi (bacaan / tautan / PDF)
                              │                                  └─► Ujian ──► Token ──► CBT Room ──► Hasil
                              └─► Profil & Nilai (statistik, grafik, riwayat, sertifikat)
```

Fitur utama:

- **Landing page** dengan statistik, katalog kursus, dan pencarian sebelum login
- **E-learning**: kursus → bab → materi. Materi bisa berupa bacaan Markdown, tautan web, PDF lokal, atau ujian
- **Progres belajar** per kursus yang tersimpan permanen
- **CBT**: token ujian, timer berbasis thread, simpan otomatis, auto-submit saat waktu habis
- **Profil**: rata-rata nilai, grafik nilai terbaik, riwayat ujian, unduh e-sertifikat
- **Tema terang/gelap** bergaya glassmorphism dengan animasi

## Kebutuhan

- JDK 17 atau lebih baru (`java -version`)
- Tidak perlu memasang Maven: proyek ini membawa **Maven Wrapper** (`mvnw` / `mvnw.cmd`) yang
  mengunduh Maven dan JavaFX secara otomatis saat pertama kali dijalankan.

## Menjalankan

Dari folder proyek:

```bash
./mvnw javafx:run          # Git Bash / macOS / Linux
mvnw.cmd javafx:run        # Command Prompt / PowerShell
```

| Perintah              | Fungsi                                        |
|-----------------------|-----------------------------------------------|
| `./mvnw compile`      | Kompilasi kode                                |
| `./mvnw test`         | Menjalankan seluruh unit test JUnit 5         |
| `./mvnw package`      | Membuat `target/examify-1.0.0.jar`            |
| `./mvnw clean`        | Menghapus hasil build (`target/`)             |

### Dari VS Code

Pasang **Extension Pack for Java**. VS Code otomatis membaca `pom.xml`, mengunduh JavaFX, dan
memakai layout Maven standar (`src/main/java`, `src/test/java`). Jalankan lewat menu *Run and Debug* →
**Examify** (konfigurasi di `.vscode/launch.json`, sudah menyertakan `-ea` agar assertion aktif).

## Akun, kursus & token contoh

| NIS     | PIN  | Nama          |
|---------|------|---------------|
| 2410001 | 1234 | Andi Pratama  |
| 2410002 | 5678 | Budi Santoso  |
| 2410003 | 2468 | Citra Lestari |

| Kursus | Ujian            | Token  | Durasi | Boleh ulang |
|--------|------------------|--------|--------|-------------|
| PBO    | PBO-01 (UTS)     | PBO7UT | 30 mnt | tidak       |
| PBO    | PBO-02 (UAS)     | PBO9UA | 30 mnt | tidak       |
| MTK    | MTK-01           | MTK7X2 | 30 mnt | tidak       |
| BIN    | BIN-01           | BIN9K4 | 20 mnt | tidak       |
| IPA    | IPA-01           | IPA3Q8 | 25 mnt | tidak       |
| UJI    | UJI-01           | UJI123 | 1 mnt  | ya          |

Kursus **PBO** berisi bab sesuai materi Java Core & OOP (Class & Object hingga Multithreading)
lengkap dengan UTS dan UAS.

> Ingin mengulang dari awal? Hapus folder `output/`. Riwayat nilai, progres belajar, dan
> sertifikat akan dibuat ulang saat aplikasi dipakai.

## Struktur folder

```
examify/
├── pom.xml                  konfigurasi Maven (layout standar)
├── mvnw, mvnw.cmd           Maven Wrapper
├── data/                    data runtime (input): siswa, guru, bank_soal, kursus, materi/
├── output/                  data runtime (hasil): rekap_nilai, progres_belajar, sertifikat/
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── module-info.java
    │   │   └── com/examify/
    │   │       ├── app/         MainApp, SceneRouter (shell + tema), Layar, AppSession
    │   │       ├── controller/  satu controller per layar FXML
    │   │       ├── view/        komponen UI: AnimasiUtil, LatarAnimasi, IkonView, MarkdownView, ...
    │   │       ├── service/     logika bisnis (Auth, Ujian, Rekap, Kursus, ProgresBelajar, ...)
    │   │       ├── model/       entitas domain, record, enum
    │   │       ├── io/          reader & writer file teks
    │   │       ├── thread/      UjianTimer (Runnable) & TimerListener
    │   │       ├── exception/   exception kustom (checked)
    │   │       └── utils/       AppConfig, StringUtil, FormatUtil, MarkdownParser, ...
    │   └── resources/com/examify/
    │       ├── fxml/            tampilan setiap layar
    │       ├── css/             theme.css (terang) & dark.css (gelap)
    │       ├── font/            Poppins (SIL Open Font License)
    │       └── img/             examifylogo.png (logo & ikon jendela)
    └── test/java/com/examify/   unit test JUnit 5
```

`data/` dan `output/` sengaja berada di luar `src/`: keduanya dibaca dan ditulis saat aplikasi
berjalan, bukan bagian dari kode sumber.

## Format data

### `data/bank_soal.txt`

Blok `[UJIAN]` (header `KUNCI=NILAI`), `[SOAL]`, dan `[END]`:

```
[UJIAN]
KODE=PBO-01
MAPEL=Pemrograman Berorientasi Objek
TOKEN=PBO7UT
DURASI=30
KKM=70
ULANG=false        # opsional: true = boleh dikerjakan ulang (bawaan false)
[SOAL]
PG|id|pertanyaan|opsi1;opsi2;...;opsiN|kunci(A-E)|bobot
TF|id|pernyataan|kunci(T/F)|bobot
IS|id|pertanyaan|jawaban1;jawaban2;...|bobot
[END]
```

### `data/kursus.txt`

```
[KURSUS]
KODE=PBO
NAMA=Pemrograman Berorientasi Objek
PENGAJAR=Dr. Rina Kartika, M.Kom.
DESKRIPSI=Konsep OOP dan Java Core.
[BAB] Class & Object
TEKS|pbo-02|Class, Object, dan Konstruktor|materi/pbo/02-class-object.md
LINK|pbo-02b|Oracle Tutorial: Classes|https://docs.oracle.com/javase/tutorial/java/javaOO/classes.html
PDF|pbo-07b|Ringkasan Empat Pilar OOP|materi/pbo/ringkasan-pilar-oop.pdf
UJIAN|pbo-uts|Ujian Tengah Semester|PBO-01
[END]
```

Path `TEKS` dan `PDF` relatif terhadap folder `data/`. Bacaan mendukung Markdown: `#` heading,
paragraf, daftar `-` / `1.`, blok kode <code>```</code>, kutipan `>`, garis `---`,
serta `**tebal**`, `*miring*`, dan `` `kode` ``.

### Lainnya

- `data/siswa.txt` berformat `NIS|NAMA|KELAS|PIN`.
- Baris kosong dan baris berawalan `#` diabaikan di semua file. Karakter `|` dan `;` tidak boleh
  dipakai di dalam teks. Bila file rusak, aplikasi menampilkan nomor baris dan isi baris yang salah.

## Tentang styling

Tampilan memakai **CSS JavaFX** (properti berawalan `-fx-`). Semua warna didefinisikan sebagai
variabel di `.root` pada `theme.css`, dan `dark.css` cukup menimpa variabel tersebut untuk mode gelap.

- **Glassmorphism**: CSS JavaFX tidak punya `backdrop-filter`, jadi efeknya dibuat dari gumpalan
  warna yang di-blur (`LatarAnimasi`, `GaussianBlur`) di belakang kartu semi-transparan (`.kaca`).
- **Animasi**: CSS JavaFX 21 belum mendukung `transition`, jadi gerakan dibuat dengan
  `javafx.animation` di `AnimasiUtil`: halaman memudar masuk, kartu muncul berurutan, kartu terangkat
  saat hover, angka menghitung naik, kartu melayang, getar saat PIN salah, dan timer berdenyut.

## Catatan

- PIN dan token disimpan sebagai teks biasa (sesuai konteks praktikum).
- Spesifikasi teknis awal ada di dokumen *Draft Teknis & Blueprint Implementasi*.
