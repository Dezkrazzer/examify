package com.examify.model;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/** Clock yang dapat dimajukan manual untuk menguji batas waktu tanpa menunggu. */
public class WaktuTerkendali extends Clock {

    private Instant sekarang = Instant.parse("2026-11-03T07:00:00Z");

    public void maju(Duration durasi) {
        sekarang = sekarang.plus(durasi);
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.of("Asia/Jakarta");
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return sekarang;
    }
}
