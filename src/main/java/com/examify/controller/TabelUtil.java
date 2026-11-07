package com.examify.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;

import java.util.function.Function;

/** Mengisi kolom tabel lewat lambda, sehingga model tidak perlu dibuka untuk refleksi. */
final class TabelUtil {

    private TabelUtil() {
    }

    static <T> void kolom(TableColumn<T, String> kolom, Function<T, String> nilai) {
        kolom.setCellValueFactory(data -> new ReadOnlyStringWrapper(nilai.apply(data.getValue())));
    }
}
