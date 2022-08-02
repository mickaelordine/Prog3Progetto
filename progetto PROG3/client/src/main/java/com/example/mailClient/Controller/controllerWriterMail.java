package com.example.mailClient.Controller;

import com.example.ClientMain;
import com.example.common.Email;
import com.example.mailClient.Connection.connection;
import com.example.mailClient.Model.mailModel;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class controllerWriterMail {
    public TextField reciverFieldWrite;
    public TextField objFieldWrite;
    public TextArea textFieldWrite;

    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");

    private mailModel mailListModel;
    private connection Connection;
    private Stage stage;
    private PrintWriter out;
    private Socket s;
    private BufferedReader in;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private ArrayList<String> allUsers = new ArrayList<>();

    public void setReciverFieldWrite(String reciverFieldWrite) {
        this.reciverFieldWrite.setText(reciverFieldWrite);
    }

    public void setObjFieldWrite(String objFieldWrite) {
        this.objFieldWrite.setText(objFieldWrite);
    }

    public void setTextFieldWrite(String textFieldWrite) {
        this.textFieldWrite.setText(textFieldWrite);
    }

    public void setConnection(connection Connection) {
        this.Connection = Connection;
    }

    public void initModel(mailModel mailListModel){
        this.mailListModel = mailListModel;

    }

    @FXML
    private void sendMail(ActionEvent event) throws IOException, InterruptedException, ParseException {
        boolean sent = false;
        if(allUsers.size() > 1) { //se non abbiamo inizializzato l'arraylist conetenete tutti gli users ne mandiamo una al destinatario
            for (String allUser : allUsers) { //se no mandiamo la mail a tutti gli utenti ppresenti nel servizio della postaElettronica
                Email emailToSend = new Email();
                /* setto la mail da inviare */
                String text = textFieldWrite.getText();
                text = text.replaceAll("\n","_ ");
                emailToSend.setText(text);
                System.out.println(emailToSend.getText());
                emailToSend.setObj(objFieldWrite.getText());
                emailToSend.setReceivers(allUser);
                emailToSend.setSender(Connection.getUsername());
                emailToSend.setDate(format.format(new Date()));
                /* setto la mail da inviare */
                sent = Connection.sendMail(emailToSend);
            }
            if(sent)
                sendMailSuccess();
            else
                showError();
        }else{
            Email emailToSend = new Email();
            /* setto la mail da inviare */
            String text = textFieldWrite.getText();
            text = text.replaceAll("\n","_ ");
            emailToSend.setText(text);
            emailToSend.setObj(objFieldWrite.getText());
            emailToSend.setReceivers(reciverFieldWrite.getText());
            emailToSend.setSender(Connection.getUsername());
            emailToSend.setDate(format.format(new Date()));
            /* setto la mail da inviare */
            if(Connection.sendMail(emailToSend)){
                sendMailSuccess();
            }else{
                showError();
            }
        }
    }

    /*closing writer stage */
    @FXML
    private void closeWriter(ActionEvent event) throws IOException, InterruptedException, ClassNotFoundException {
        setMainStage(stage);
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


    public void sendMailSuccess() throws IOException, InterruptedException {
        // create sample content
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("popUpSuccess.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Success!!");
        stage.setScene(new Scene(root1));
        stage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished( event -> stage.close() );
        delay.play();
    }

    public void deleteMailSuccess() throws IOException {
        // create sample content
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("deleteMailSuccess.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Error!!");
        stage.setScene(new Scene(root1));
        stage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished( event -> stage.close() );
        delay.play();
    }

    public void showError() throws IOException {
        // create sample content
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("popUpFailed.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Error!!");
        stage.setScene(new Scene(root1));
        stage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished( event -> stage.close() );
        delay.play();
    }


    /* usata in caso in cui ci sia il richiamo di replyAll */
    public void usersToReplyAll(ArrayList<String> allUsers) {
        this.allUsers = allUsers;
    }
}
