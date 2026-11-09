package com.examify.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** Mengisi kolom tabel lewat lambda, sehingga model tidak perlu dibuka untuk refleksi. */
final class TableUtil {

    private static final double PADDING_SEL = 24;

    private TableUtil() {
    }

    static <T> void kolom(TableColumn<T, String> kolom, Function<T, String> nilai) {
        kolom.setCellValueFactory(data -> new ReadOnlyStringWrapper(nilai.apply(data.getValue())));
    }

    /**
     * Baris tabel bisa diklik untuk dibuka/ditutup. Saat tertutup, teks panjang di kolom yang
     * diberikan dipotong dengan "..."; saat terbuka, teks tampil utuh (dibungkus ke baris berikutnya).
     */
    @SafeVarargs
    static <T> void barisBisaDibuka(TableView<T> tabel, TableColumn<T, String>... kolomTeks) {
        Set<T> terbuka = Collections.newSetFromMap(new IdentityHashMap<>());
        tabel.setRowFactory(tv -> {
            TableRow<T> baris = new TableRow<>();
            baris.setCursor(Cursor.HAND);
            baris.setOnMouseClicked(e -> {
                T item = baris.getItem();
                if (item == null || e.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                if (!terbuka.remove(item)) {
                    terbuka.add(item);
                }
                tabel.refresh();
            });
            return baris;
        });
        for (TableColumn<T, String> kolom : List.of(kolomTeks)) {
            kolom.setCellFactory(k -> new SelBisaDibuka<>(terbuka));
        }
    }

    private static final class SelBisaDibuka<T> extends TableCell<T, String> {

        private final Set<T> terbuka;
        private final Label label = new Label();

        SelBisaDibuka(Set<T> terbuka) {
            this.terbuka = terbuka;
            label.setTextOverrun(OverrunStyle.ELLIPSIS);
            label.setMinHeight(Region.USE_PREF_SIZE);
            label.getStyleClass().add("teks-sel");
            tableColumnProperty().addListener((obs, lama, kolom) -> {
                label.maxWidthProperty().unbind();
                if (kolom != null) {
                    label.maxWidthProperty().bind(kolom.widthProperty().subtract(PADDING_SEL));
                }
            });
        }

        @Override
        protected void updateItem(String teks, boolean kosong) {
            super.updateItem(teks, kosong);
            setText(null);
            if (kosong || teks == null) {
                setGraphic(null);
                return;
            }
            T item = getTableRow() == null ? null : getTableRow().getItem();
            label.setText(teks);
            label.setWrapText(item != null && terbuka.contains(item));
            setGraphic(label);
        }
    }
}
