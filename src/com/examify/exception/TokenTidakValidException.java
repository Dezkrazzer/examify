package com.examify.exception;

/** Dilempar ketika token ujian salah atau percobaan sedang dikunci. */
public class TokenTidakValidException extends Exception {

    private final int sisaPercobaan;

    public TokenTidakValidException(int sisaPercobaan, String message) {
        super(message);
        this.sisaPercobaan = sisaPercobaan;
    }

    public int getSisaPercobaan() {
        return sisaPercobaan;
    }
}
