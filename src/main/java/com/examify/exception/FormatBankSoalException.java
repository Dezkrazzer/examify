package com.examify.exception;

/** Kesalahan format pada bank_soal.txt. */
public class FormatBankSoalException extends FormatDataException {

    public FormatBankSoalException(int nomorBaris, String isiBaris, String alasan) {
        super(nomorBaris, isiBaris, alasan);
    }

    public FormatBankSoalException(int nomorBaris, String isiBaris, String alasan, Throwable cause) {
        super(nomorBaris, isiBaris, alasan, cause);
    }
}
