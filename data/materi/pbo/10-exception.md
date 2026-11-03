# Exceptions & Assertions

**Exception** adalah kejadian tidak normal yang mengganggu alur program. Java memaksa kita menangani kondisi tersebut secara eksplisit.

## Hierarki singkat

- `Throwable`
  - `Error` - masalah serius JVM, jangan ditangkap
  - `Exception` - **checked**, wajib ditangani atau dideklarasikan
    - `RuntimeException` - **unchecked**, biasanya bug program

## try - catch - finally

```java
try {
    ujianService.selesaikanPaksa(hasil);
} catch (IOException e) {
    System.err.println("Rekap gagal ditulis: " + e.getMessage());
} finally {
    Platform.exit();   // selalu dijalankan
}
```

## try-with-resources

Resource seperti file ditutup otomatis:

```java
try (BufferedReader br = Files.newBufferedReader(path)) {
    return br.readLine();
}
```

## Exception buatan sendiri

```java
public class JawabanKosongException extends Exception {
    public JawabanKosongException(int idSoal) {
        super("Jawaban soal " + idSoal + " masih kosong");
    }
}
```

## Assertion

`assert` memeriksa asumsi internal program. Assertion **nonaktif** secara bawaan dan diaktifkan dengan opsi JVM `-ea`.

```java
assert skor >= 0 && skor <= 100 : "Skor di luar rentang";
```

> Jangan memakai assertion untuk memvalidasi input pengguna. Gunakan exception.
