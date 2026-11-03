package com.examify.app;

/** main() terpisah agar fat-jar dapat dijalankan tanpa module path JavaFX. */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        MainApp.main(args);
    }
}
