# Inheritance (Pewarisan)

**Inheritance** memungkinkan sebuah class (*subclass*) mewarisi atribut dan method dari class lain (*superclass*) dengan kata kunci `extends`.

```java
public abstract class Soal {
    private final int idSoal;
    private final String pertanyaan;

    protected Soal(int idSoal, String pertanyaan) {
        this.idSoal = idSoal;
        this.pertanyaan = pertanyaan;
    }
}

public class SoalPilihanGanda extends Soal {
    private final List<String> opsi;

    public SoalPilihanGanda(int id, String tanya, List<String> opsi) {
        super(id, tanya);   // memanggil konstruktor superclass
        this.opsi = opsi;
    }
}
```

## Hal penting

- Java hanya mendukung **single inheritance** untuk class
- `super(...)` memanggil konstruktor superclass dan harus di baris pertama
- Modifier `protected` dapat diakses oleh subclass
- Semua class di Java secara otomatis mewarisi `Object`

## Hubungan "is-a"

Gunakan inheritance hanya bila hubungannya benar-benar *adalah sebuah*: soal pilihan ganda **adalah** soal. Bila hubungannya *memiliki*, gunakan komposisi (atribut).

## Contoh di Examify

- `SoalPilihanGanda`, `SoalIsian`, `SoalTrueFalse` mewarisi `Soal`
- `MateriTeks`, `MateriTautan`, `MateriPdf`, `MateriUjian` mewarisi `Materi`
- `FormatBankSoalException` mewarisi `FormatDataException`
