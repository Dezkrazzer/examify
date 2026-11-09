package com.examify.exception;

/** Kesalahan format pada bank_soal.txt. */
public class QuestionBankFormatException extends DataFormatException {

    public QuestionBankFormatException(int nomorBaris, String isiBaris, String alasan) {
        super(nomorBaris, isiBaris, alasan);
    }

    public QuestionBankFormatException(int nomorBaris, String isiBaris, String alasan, Throwable cause) {
        super(nomorBaris, isiBaris, alasan, cause);
    }
}
