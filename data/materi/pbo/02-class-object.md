# Class & Object

**Class** adalah cetak biru (blueprint), sedangkan **object** adalah benda nyata yang dibuat dari cetak biru tersebut. Satu class dapat menghasilkan banyak object.

## Anatomi sebuah class

```java
public class Siswa {
    private final String nis;   // atribut
    private final String nama;

    public Siswa(String nis, String nama) {   // konstruktor
        this.nis = nis;
        this.nama = nama;
    }

    public String getNama() {   // method
        return nama;
    }
}
```

## Membuat object

Object dibuat dengan kata kunci `new`, yang memanggil konstruktor:

```java
Siswa andi = new Siswa("2410001", "Andi Pratama");
Siswa budi = new Siswa("2410002", "Budi Santoso");
```

- `andi` dan `budi` adalah dua object berbeda dari class yang sama
- Keduanya punya atribut sendiri-sendiri

## Konstruktor

- Namanya sama dengan nama class dan tidak punya tipe kembalian
- Dipakai untuk memastikan object selalu dalam keadaan valid
- Boleh lebih dari satu (*overloading*)

## Contoh di Examify

Saat aplikasi dibuka, setiap baris di `siswa.txt` diubah menjadi object `Siswa`, dan setiap blok di `bank_soal.txt` menjadi object `Ujian` yang berisi banyak object `Soal`.
