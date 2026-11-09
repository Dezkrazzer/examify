package com.examify.thread;

/** Callback dari {@link ExamTimer}; kedua method dipanggil di FX Application Thread. */
public interface TimerListener {

    void onTick(int sisaDetik);

    void onWaktuHabis();
}
