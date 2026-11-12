@SuppressWarnings("requires-automatic") // JNA belum menyediakan module-info resmi
module com.examify {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.sun.jna;          // Mode Aman: akses API native Windows
    requires com.sun.jna.platform; // Mode Aman: User32/Kernel32 (hook keyboard)

    opens com.examify.controller to javafx.fxml; // refleksi @FXML
    opens com.examify.view to javafx.fxml;       // komponen kustom (AnimatedBackground, IconView) di FXML
    exports com.examify.app to javafx.graphics;   // subclass Application dijalankan oleh launcher JavaFX
}
