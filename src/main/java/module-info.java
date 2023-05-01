module com.example.menuapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.menuapp to javafx.fxml;
    exports com.example.menuapp;
}