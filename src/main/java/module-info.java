module com.examify {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.examify.controller to javafx.fxml; // refleksi @FXML
    opens com.examify.view to javafx.fxml;       // komponen kustom (LatarAnimasi, IkonView) di FXML
    exports com.examify.app;                     // subclass Application
}
