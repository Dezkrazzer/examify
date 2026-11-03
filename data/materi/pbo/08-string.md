# String

`String` adalah objek *immutable* yang berisi rangkaian karakter. Setiap operasi yang "mengubah" string sebenarnya menghasilkan string baru.

## Method yang sering dipakai

- `trim()` - membuang spasi di awal dan akhir
- `toUpperCase()` / `toLowerCase()`
- `split("\\|", -1)` - memecah berdasarkan pemisah
- `replaceAll(regex, pengganti)`
- `substring`, `indexOf`, `charAt`, `length`
- `equals` dan `equalsIgnoreCase` - **jangan** membandingkan string dengan `==`
- `String.format` dan `String.join`

## StringBuilder

Untuk menyusun string panjang di dalam perulangan, gunakan `StringBuilder` agar tidak membuat banyak objek sementara:

```java
StringBuilder sb = new StringBuilder();
sb.append("+").append("=".repeat(64)).append("+\n");
String garis = sb.toString();
```

## Contoh di Examify

Satu baris bank soal diproses seperti ini:

```java
String[] field = "PG|1|12 x 3 = ...|30;36;38|B|10".split("\\|", -1);
String kunci = field[4].trim().toUpperCase();   // "B"
```

Jawaban isian dinormalisasi sebelum dinilai, sehingga `" kota semarang. "` dianggap sama dengan `KOTA SEMARANG`.
