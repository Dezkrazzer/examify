# Generics & Collections

**Generics** membuat class dan method dapat bekerja untuk berbagai tipe dengan tetap aman tipe (*type safe*). **Collections Framework** menyediakan struktur data siap pakai.

## Struktur data utama

- `List` (`ArrayList`) - berurutan, boleh duplikat
- `Set` (`HashSet`) - tanpa duplikat
- `Map` (`HashMap`, `LinkedHashMap`) - pasangan kunci-nilai
- `Optional` - wadah untuk nilai yang mungkin tidak ada

```java
List<Soal> daftarSoal = new ArrayList<>();
Map<Siswa, Double> rekap = new HashMap<>();
Set<String> kunciIsian = Set.of("SEMARANG", "KOTA SEMARANG");
```

## Membuat tipe generik sendiri

```java
public interface DataReader<T> {
    List<T> baca(Path path) throws IOException;
}

public class SiswaReader implements DataReader<Siswa> { ... }
```

## equals & hashCode

Objek yang dipakai sebagai key `HashMap` atau isi `HashSet` **wajib** meng-override `equals` dan `hashCode` secara konsisten.

> Di Examify, `Siswa` dibandingkan hanya berdasarkan NIS, sehingga login ulang tidak membuat rekap ganda.

## Stream API

```java
double total = daftarSoal.stream()
        .mapToDouble(Soal::getBobot)
        .sum();
```
