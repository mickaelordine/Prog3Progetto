package com.example;

import com.example.mailClient.Connection.connection;
import com.example.mailClient.Controller.controllerLogin;
import com.example.mailClient.Model.mailModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class ClientMain extends Application {
    mailModel mailListModel = new mailModel();
    connection Connection = new connection(mailListModel);
    public static ArrayList<String> allusers = new ArrayList<>();

    public ClientMain() throws ParseException {
    }

    /* usata per quando usiamo reply all */
    public static ArrayList<String> getAllusersExceptMe(String username) {
        System.out.println(username);
        if(username.equals("Mickael")){ //sono mickael
            allusers.add("Gabriele@mia-mail.com");
            allusers.add("Emanuele@mia-mail.com");
        }else if(username.equals("Gabriele")){ //sono gabriele
            allusers.add("Emanuele@mia-mail.com");
            allusers.add("Mickael@mia-mail.com");
        }else{ //sono emanuele
            allusers.add("Mickael@mia-mail.com");
            allusers.add("Gabriele@mia-mail.com");
        }
        return allusers;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loginLoader.load();
        controllerLogin loginController = loginLoader.getController();
        loginController.setMailListModel(mailListModel);
        loginController.setConnection(Connection);


        Scene scene = new Scene(root);
        loginController.setStage(stage);

        stage.setTitle("Login");
        stage.setMaxHeight(400);
        stage.setMaxWidth(600);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setScene(scene);
        stage.show();


        stage.setOnCloseRequest(t -> {
            Connection.closeConnection();

            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}

