# Polymorphism

**Polymorphism** berarti *banyak bentuk*: satu referensi atau satu nama method dapat berperilaku berbeda tergantung objeknya.

## Dua jenis polymorphism

1. **Overriding** (runtime) - subclass mengganti perilaku method superclass
2. **Overloading** (compile time) - beberapa method bernama sama dengan parameter berbeda

## Overriding

```java
public class SoalTrueFalse extends Soal {
    @Override
    public boolean evaluasiJawaban(String input) {
        return normalisasi(input).equals("BENAR") == (kunci == 'T');
    }
}
```

Saat dipanggil lewat referensi bertipe `Soal`, Java memilih versi milik objek sebenarnya (*dynamic dispatch*):

```java
Soal soal = new SoalTrueFalse(1, "2 adalah prima", 10, 'T');
soal.evaluasiJawaban("benar");   // memanggil versi SoalTrueFalse
```

## Overloading

```java
public HasilUjian(Siswa siswa, Ujian ujian) { ... }
public HasilUjian(Siswa siswa, Ujian ujian, Clock clock) { ... }
```

## Contoh di Examify

Daftar materi di halaman kursus memanggil `materi.getKeterangan()`. Hasilnya berbeda untuk bacaan, tautan, PDF, dan ujian, tanpa satu pun `if` untuk membedakan jenisnya.
