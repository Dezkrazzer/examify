# Java GUI dengan JavaFX

**JavaFX** adalah toolkit modern untuk membuat aplikasi desktop Java. Tampilan disusun sebagai pohon *node* yang disebut **scene graph**.

## Bagian utama

- `Stage` - jendela aplikasi
- `Scene` - isi jendela
- `Node` - elemen tampilan: `Label`, `Button`, `VBox`, `TableView`, ...

## FXML dan Controller

Tampilan ditulis di file FXML, sedangkan logikanya di class controller (pola **MVC**):

```xml
<Button text="Masuk" onAction="#handleMasuk"/>
```

```java
public class LoginController {
    @FXML private TextField tfNis;

    @FXML
    private void handleMasuk() { ... }
}
```

## Styling dengan CSS

JavaFX memakai CSS dengan awalan `-fx-`:

```css
.tombol-utama {
    -fx-background-color: linear-gradient(to right, #1aa3e8, #7c5cff);
    -fx-background-radius: 12;
}
```

## Animasi

Animasi dibuat dengan class di `javafx.animation`, misalnya `FadeTransition`, `TranslateTransition`, dan `Timeline`.

> Semua perubahan tampilan wajib dilakukan di **FX Application Thread**.
