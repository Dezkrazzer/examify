# Pengenalan Pemrograman Berorientasi Objek

**Pemrograman Berorientasi Objek** (PBO / OOP) adalah cara menyusun program sebagai kumpulan *objek* yang saling bekerja sama. Setiap objek menyimpan **data** (atribut) dan **perilaku** (method).

## Mengapa OOP?

- Kode lebih mudah dipahami karena meniru dunia nyata
- Mudah dipakai ulang lewat pewarisan (*inheritance*)
- Perubahan pada satu bagian tidak merusak bagian lain
- Cocok untuk aplikasi besar yang dikerjakan banyak orang

## Empat pilar OOP

1. **Encapsulation** - menyembunyikan detail internal objek
2. **Inheritance** - kelas baru mewarisi sifat kelas lain
3. **Polymorphism** - satu perintah, banyak bentuk perilaku
4. **Abstraction** - fokus pada *apa* yang dilakukan, bukan *bagaimana*

> Aplikasi Examify yang sedang Anda pakai dibangun dengan keempat pilar ini. Di setiap bab, perhatikan bagian *Contoh di Examify*.

## Contoh di Examify

Setiap soal ujian adalah objek. Soal pilihan ganda, isian, dan benar/salah punya cara penilaian berbeda, tetapi semuanya dinilai dengan perintah yang sama:

```java
for (Soal soal : ujian.getDaftarSoal()) {
    boolean benar = soal.evaluasiJawaban(jawaban);
}
```
