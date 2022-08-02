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
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class controllerMain {

    /* block mail */
    @FXML
    private Label textId;
    @FXML
    private Label objId;
    @FXML
    private Label senderId;
    @FXML
    private Label dateId;
    /* block mail */

    @FXML
    private ListView<Email> emailListView;

    private connection Connection;
    private mailModel mailListModel;
    private Stage stage;
    public boolean flag = true; //usata per dire al thread quando smettere di eseguire il codice di richiesta per le mail in arrivo

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage(){
        return this.stage;
    }

    public void setConnection(connection Connection) {
        this.Connection = Connection;
    }

    public void initModel(mailModel mailListModel) throws IOException, ClassNotFoundException {
        if (this.mailListModel != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.mailListModel = mailListModel;

        ArrayList<Email> mailTodisplay = new ArrayList<>();
        mailTodisplay.addAll(Connection.loadMails()); //mi salvo le mail scaricate dal server in una List<>

        //aggiungo alla listview tutte le emial scaricate dal server
        this.mailListModel.setEmailListView(mailTodisplay);
        emailListView.setItems(this.mailListModel.getEmailList());

        emailListView.setCellFactory(mailList -> new ListCell<>() {
            @Override
            public void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText("DA:   " + email.getSender() + "\n" + "CC:   " + email.getObj()  + "\n" + "             " + email.getDate() + "\n");
                }
            }
        });

        emailListView.setOnMouseClicked(mouseEvent -> {
            Email selectedMail = emailListView.getSelectionModel().getSelectedItem(); //ci prendiamo la mail corrispondente
            mailListModel.setCurrentMail(selectedMail); //settiamo momentaneamente i valori della mail a current mail nel modello
            try {
                setReaderStage(stage, selectedMail);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        new Thread(() -> {
            while (flag) {
                try {
                    int emailsListView = mailListModel.getEmailList().size();
                    int emailsServerFile = Connection.howManyMails();

                    if (emailsServerFile > emailsListView) {
                        Platform.runLater(() -> {
                            try {
                                NewEmailShowPopUp();
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    Thread.sleep(10000);
                } catch (SocketException se) {
                    try {
                        mailListModel.loadData(Connection.loadMails());
                    } catch (IOException | ClassNotFoundException e) {
                        Platform.runLater(() -> {
                            try {
                                showError();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        });
                        e.printStackTrace();
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
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

    public void NewEmailShowPopUp() throws IOException, InterruptedException {
        // create sample content
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("newMessagePopUp.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Nuovo Messaggio!!");
        stage.setScene(new Scene(root1));
        stage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished( event -> stage.close() );
        delay.play();
    }


    @FXML
    private void whichButtonClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        Button button = (Button) event.getTarget();
        String action = button.getId();
        switch (action) {
            case "sendMailButton":
                setWriterStage(getStage());
                break;
            case "refreshButton":
                ArrayList<Email> mailTodisplay = Connection.loadMails(); //mi salvo le mail scaricate dal server in una List<>
                //aggiungo alla listview tutte le emial scaricate dal server
                this.mailListModel.setEmailListView(mailTodisplay);
                emailListView.setItems(this.mailListModel.getEmailList());
                break;
            case "logOutButton":
                Connection.closeConnection();
                setLoginStage(stage);
                break;
        }


    }




    //settiamo la view per leggere la mail col reader
    public void setReaderStage(Stage stage, Email selectedEmail) throws IOException {
        FXMLLoader readLoader = new FXMLLoader(ClientMain.class.getResource("readMail.fxml"));
        Parent root = readLoader.load(); //carichiamo la pagina del reader come nuova finestra
        controllerReaderMail readController = readLoader.getController();
        readController.setConnection(Connection);
        readController.initModel(selectedEmail, Connection.getUsername(), this.mailListModel);

        Scene scene = new Scene(root);
        readController.setStage(stage);
        stage.setScene(scene);

        stage.setMaxWidth(600);
        stage.setMaxHeight(400);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        flag = false;
        stage.show();
    }

    //settiamo la view per scrivere la mail col writerMail
    public void setWriterStage(Stage stage) throws IOException {
        FXMLLoader writeLoader = new FXMLLoader(ClientMain.class.getResource("writeMail.fxml"));
        Parent root = writeLoader.load(); //carichiamo la pagina del reader come nuova finestra
        controllerWriterMail writeController = writeLoader.getController();
        writeController.setConnection(Connection);
        writeController.initModel(this.mailListModel);

        Scene scene = new Scene(root);
        writeController.setStage(stage);
        stage.setScene(scene);

        stage.setMaxWidth(600);
        stage.setMaxHeight(400);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        flag = false;
        stage.show();
    }

    public void setLoginStage(Stage stage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(ClientMain.class.getResource("login.fxml"));
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
        flag = false;
        stage.show();
    }



}
