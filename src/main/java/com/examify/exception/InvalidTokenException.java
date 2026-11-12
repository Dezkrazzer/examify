package com.examify.exception;

/** Dilempar ketika token ujian salah atau percobaan sedang dikunci. */
public class InvalidTokenException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int sisaPercobaan;

    public InvalidTokenException(int sisaPercobaan, String message) {
        super(message);
        this.sisaPercobaan = sisaPercobaan;
    }

    public int getSisaPercobaan() {
        return sisaPercobaan;
    }
}
