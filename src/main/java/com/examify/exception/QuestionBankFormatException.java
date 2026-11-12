package com.examify.exception;

/** Kesalahan format pada bank_soal.txt. */
public class QuestionBankFormatException extends DataFormatException {

    private static final long serialVersionUID = 1L;

    public QuestionBankFormatException(int nomorBaris, String isiBaris, String alasan) {
        super(nomorBaris, isiBaris, alasan);
    }

    public QuestionBankFormatException(int nomorBaris, String isiBaris, String alasan, Throwable cause) {
        super(nomorBaris, isiBaris, alasan, cause);
    }
}
