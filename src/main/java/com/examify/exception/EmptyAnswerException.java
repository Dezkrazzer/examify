package com.examify.exception;

/** Dilempar ketika jawaban yang disimpan kosong atau hanya spasi. */
public class EmptyAnswerException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int idSoal;

    public EmptyAnswerException(int idSoal) {
        super("Jawaban soal " + idSoal + " masih kosong");
        this.idSoal = idSoal;
    }

    public int getIdSoal() {
        return idSoal;
    }
}
