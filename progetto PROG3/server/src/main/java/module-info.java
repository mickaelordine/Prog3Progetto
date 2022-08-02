module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example to javafx.fxml;
    exports com.example;

    opens com.example.controller to javafx.fxml;
    exports com.example.controller;

    opens com.example.connection to javafx.fxml;
    exports com.example.connection;

    opens com.example.logServer to javafx.fxml;
    exports com.example.logServer;
}