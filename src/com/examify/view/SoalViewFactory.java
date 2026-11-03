package com.examify.view;

import com.examify.model.Soal;
import com.examify.model.SoalIsian;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.SoalTrueFalse;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/** Membangun kontrol input sesuai jenis soal, sehingga model tetap bebas dari JavaFX. */
public final class SoalViewFactory {

    private static final double JARAK_OPSI = 10;

    private SoalViewFactory() {
    }

    /**
     * @param jawabanTersimpan jawaban sebelumnya untuk dipulihkan saat navigasi
     * @param saatBerubah      dipanggil setiap jawaban berubah; string kosong berarti dikosongkan
     */
    public static Node buatInput(Soal soal, Optional<String> jawabanTersimpan, Consumer<String> saatBerubah) {
        if (soal instanceof SoalPilihanGanda pg) {
            return buatPilihan(opsiPilihanGanda(pg), jawabanTersimpan, saatBerubah);
        }
        if (soal instanceof SoalTrueFalse) {
            return buatPilihan(opsiTrueFalse(), jawabanTersimpan, saatBerubah);
        }
        if (soal instanceof SoalIsian) {
            return buatIsian(jawabanTersimpan, saatBerubah);
        }
        throw new IllegalArgumentException("Jenis soal belum didukung: " + soal.getJenis());
    }

    /** @return map nilai jawaban -> label yang ditampilkan */
    private static Map<String, String> opsiPilihanGanda(SoalPilihanGanda soal) {
        Map<String, String> opsi = new LinkedHashMap<>();
        for (int i = 0; i < soal.getOpsi().size(); i++) {
            opsi.put(String.valueOf(SoalPilihanGanda.hurufOpsi(i)), soal.getLabelOpsi(i));
        }
        return opsi;
    }

    private static Map<String, String> opsiTrueFalse() {
        Map<String, String> opsi = new LinkedHashMap<>();
        opsi.put(SoalTrueFalse.LABEL_BENAR, "Benar");
        opsi.put(SoalTrueFalse.LABEL_SALAH, "Salah");
        return opsi;
    }

    private static Node buatPilihan(Map<String, String> opsi, Optional<String> jawabanTersimpan,
                                    Consumer<String> saatBerubah) {
        ToggleGroup grup = new ToggleGroup();
        VBox wadah = new VBox(JARAK_OPSI);
        opsi.forEach((nilai, label) -> {
            RadioButton radio = new RadioButton(label);
            radio.setUserData(nilai);
            radio.setToggleGroup(grup);
            radio.setWrapText(true);
            radio.getStyleClass().add("opsi-jawaban");
            radio.setMaxWidth(Double.MAX_VALUE);
            radio.setSelected(jawabanTersimpan.map(nilai::equalsIgnoreCase).orElse(false));
            wadah.getChildren().add(radio);
        });
        grup.selectedToggleProperty().addListener((obs, lama, baru) -> saatBerubah.accept(nilaiDari(baru)));
        return wadah;
    }

    private static String nilaiDari(Toggle toggle) {
        return toggle == null ? "" : (String) toggle.getUserData();
    }

    private static Node buatIsian(Optional<String> jawabanTersimpan, Consumer<String> saatBerubah) {
        TextField field = new TextField(jawabanTersimpan.orElse(""));
        field.setPromptText("Ketik jawaban di sini");
        field.getStyleClass().add("isian-jawaban");
        field.textProperty().addListener((obs, lama, baru) -> saatBerubah.accept(baru));
        return field;
    }
}
