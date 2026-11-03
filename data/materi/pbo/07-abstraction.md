# Abstraction

**Abstraction** menyembunyikan detail implementasi dan hanya menampilkan apa yang perlu diketahui pemakai. Java menyediakan dua alat: **abstract class** dan **interface**.

## Abstract class

- Tidak dapat dibuat object-nya secara langsung
- Boleh punya atribut, konstruktor, dan method konkret
- Method `abstract` wajib diimplementasikan subclass

```java
public abstract class Soal {
    public abstract boolean evaluasiJawaban(String input);
}
```

## Interface

- Kontrak murni: *apa* yang bisa dilakukan
- Satu class boleh mengimplementasikan banyak interface
- Sejak Java 8 boleh punya method `default`

```java
public interface Gradable {
    double NILAI_MAKSIMUM = 100.0;
    double hitungNilaiAkhir();

    default String getPredikat() {
        return hitungNilaiAkhir() >= 90 ? "A" : "B";
    }
}
```

## Kapan memakai yang mana?

- **Abstract class** bila ada state dan perilaku bersama
- **Interface** bila hanya ingin menetapkan kemampuan

## Contoh di Examify

- `Soal` dan `Materi` adalah abstract class
- `Gradable`, `TimerListener`, dan `DataReader<T>` adalah interface
