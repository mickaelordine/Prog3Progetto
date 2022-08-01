package com.example.mailClient.Controller;

import com.example.ClientMain;

import com.example.mailClient.Connection.connection;
import com.example.mailClient.Model.mailModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;

public class controllerLogin {
    private mailModel mailListModel;
    private connection Connection;
    private Stage stage;

    public void setConnection(connection Connection) {
        this.Connection = Connection;
    }

    @FXML
    private void login(ActionEvent event) throws IOException, InterruptedException {
        Button button = (Button) event.getTarget();
        String username = button.getId();
        mailListModel.setUsername(username); //aggiungiamo il nostro username sul modello
        Connection.setUsername(username);
        System.out.println("Welcome " + username);
        try {
            setMainStage(stage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void setMailListModel(mailModel mailListModel) {
        this.mailListModel = mailListModel;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }



    public void setMainStage(Stage stage) throws IOException, ClassNotFoundException {
        BorderPane root = new BorderPane();
        FXMLLoader menuLoader = new FXMLLoader(ClientMain.class.getResource("main.fxml"));
        root.setLeft(menuLoader.load());
        controllerMain mainController = menuLoader.getController();
        mainController.setConnection(Connection);
        mainController.initModel(mailListModel);


        Scene scene = new Scene(root);

        mainController.setStage(stage);

        stage.setMaxWidth(600);
        stage.setMaxHeight(400);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
