package com.examify.exception;

/** Baris pada file data tidak sesuai data contract; menyimpan lokasi kesalahan. */
public class FormatDataException extends Exception {

    private final int nomorBaris;
    private final String isiBaris;

    public FormatDataException(int nomorBaris, String isiBaris, String alasan) {
        this(nomorBaris, isiBaris, alasan, null);
    }

    public FormatDataException(int nomorBaris, String isiBaris, String alasan, Throwable cause) {
        super(String.format("Baris %d tidak valid -> %s%n    isi: \"%s\"", nomorBaris, alasan, isiBaris), cause);
        this.nomorBaris = nomorBaris;
        this.isiBaris = isiBaris;
    }

    public int getNomorBaris() {
        return nomorBaris;
    }

    public String getIsiBaris() {
        return isiBaris;
    }
}
