# Encapsulation (Enkapsulasi)

**Encapsulation** berarti membungkus data dan menyembunyikannya dari luar. Akses hanya diberikan lewat method yang sudah dikontrol.

## Access modifier

- `private` - hanya di dalam class itu sendiri
- *(default)* - satu package
- `protected` - satu package dan subclass
- `public` - dari mana saja

## Getter tanpa setter

```java
public class Siswa {
    private final String pin;   // tidak ada getter!

    public boolean verifikasiPin(String input) {
        return input != null && input.trim().equals(pin);
    }
}
```

PIN tidak pernah keluar dari object. Pihak luar hanya bisa *bertanya* apakah PIN cocok.

> Prinsip *tell, don't ask*: minta object melakukan sesuatu, jangan ambil datanya lalu diolah di luar.

## Contoh di Examify

Kunci jawaban disimpan `private final` dan hanya dapat dibuka setelah ujian selesai:

```java
public String bukaKunci(StatusUjian status) {
    if (status != StatusUjian.SELESAI)
        throw new IllegalStateException("Ujian belum selesai");
    return String.valueOf(kunciJawaban);
}
```
