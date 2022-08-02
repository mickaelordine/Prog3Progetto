module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    //requires org.controlsfx.controls;

    opens com.example to javafx.fxml;
    exports com.example;

    opens com.example.common to javafx.fxml;
    exports com.example.common;

    opens com.example.mailClient.Controller to javafx.fxml;
    exports com.example.mailClient.Controller to javafx.fxml;


}