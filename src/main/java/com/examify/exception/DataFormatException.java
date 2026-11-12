package com.examify.exception;

/** Baris pada file data tidak sesuai data contract; menyimpan lokasi kesalahan. */
public class DataFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int nomorBaris;
    private final String isiBaris;

    public DataFormatException(int nomorBaris, String isiBaris, String alasan) {
        this(nomorBaris, isiBaris, alasan, null);
    }

    public DataFormatException(int nomorBaris, String isiBaris, String alasan, Throwable cause) {
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
