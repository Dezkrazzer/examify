# Java Variables

Variabel adalah wadah bernama untuk menyimpan nilai. Java adalah bahasa *strongly typed*: setiap variabel wajib punya tipe.

## Delapan tipe primitif

- `byte` - bilangan bulat 8 bit (-128 s.d. 127)
- `short` - bilangan bulat 16 bit
- `int` - bilangan bulat 32 bit, paling sering dipakai
- `long` - bilangan bulat 64 bit
- `float` - pecahan presisi tunggal
- `double` - pecahan presisi ganda
- `char` - satu karakter Unicode, misalnya `'A'`
- `boolean` - `true` atau `false`

## Jenis variabel berdasarkan letaknya

1. **Instance variable** - milik setiap object
2. **Static variable** - milik class, dipakai bersama semua object
3. **Local variable** - hanya hidup di dalam method
4. **Parameter** - nilai yang dikirim ke method

```java
public class UjianTimer {
    private static final String NAMA_THREAD = "Examify-Timer"; // static
    private volatile int sisaWaktu;                            // instance

    private int hitungSisaDetik() {
        long sisaNanos = deadlineNanos - System.nanoTime();    // local
        return (int) Math.max(0, sisaNanos / 1_000_000_000L);
    }
}
```

> `final` membuat variabel tidak dapat diubah setelah diberi nilai. Gunakan sebanyak mungkin agar kode lebih aman.

## Contoh di Examify

- `double skor` menyimpan nilai akhir
- `char kunciJawaban` menyimpan kunci soal pilihan ganda
- `short durasiMenit` cukup untuk durasi 1-180 menit
- `byte MAKS_OPSI = 5` untuk jumlah opsi A-E
