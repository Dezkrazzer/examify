# Multithreading

**Thread** adalah jalur eksekusi independen. Dengan beberapa thread, program dapat mengerjakan beberapa hal sekaligus, misalnya menghitung mundur waktu ujian sambil siswa menjawab soal.

## Membuat thread

```java
public class UjianTimer implements Runnable {
    @Override
    public void run() {
        while (berjalan && sisaWaktu > 0) {
            Thread.sleep(1000);
            // hitung ulang sisa waktu
        }
    }
}

Thread t = new Thread(new UjianTimer(...), "Examify-Timer");
t.setDaemon(true);
t.start();
```

## Masalah umum dan solusinya

- **Visibilitas** - gunakan `volatile` untuk flag yang dibaca thread lain
- **Race condition** - gunakan `synchronized` untuk bagian kritis
- **UI dari thread lain** - JavaFX melarangnya; gunakan `Platform.runLater`

```java
Platform.runLater(() -> lblTimer.setText("Sisa 04:59"));
```

## Contoh di Examify

- `HasilUjian.finalisasi()` bersifat `synchronized` dan idempoten, sehingga klik *Selesai* bersamaan dengan waktu habis tidak membuat rekap ganda
- Sisa waktu dihitung dari `System.nanoTime()` agar tidak meleset
