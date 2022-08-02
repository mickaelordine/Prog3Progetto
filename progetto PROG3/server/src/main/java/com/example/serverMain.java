package com.example;

import com.example.controller.controllerServer;
import com.example.logServer.Model;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class serverMain extends Application {
    private final String nomeHost = "localhost";
    private final int port = 8192;





    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("mainServer.fxml"));
        Parent root = mainLoader.load();

        controllerServer controller = mainLoader.getController();
        Model serverModel = new Model();
        serverModel.addLog("Server started!");
        controller.initConnection(serverModel);

        Scene scene = new Scene(root, 600, 350);

        stage.setTitle("Server");
        stage.setScene(scene);
        stage.setMinHeight(100);
        stage.setMinWidth(300);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
