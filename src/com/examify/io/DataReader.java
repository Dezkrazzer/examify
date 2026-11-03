package com.examify.io;

import com.examify.exception.FormatDataException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Kontrak generik pembaca file data: setiap implementasi mengubah satu file teks
 * menjadi daftar objek model bertipe {@code T}.
 */
public interface DataReader<T> {

    List<T> baca(Path path) throws IOException, FormatDataException;
}
