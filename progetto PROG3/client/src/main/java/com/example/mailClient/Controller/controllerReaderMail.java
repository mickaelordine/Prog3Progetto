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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

public class controllerReaderMail {
    public Label senderRead;
    public Label dataRead;
    public Label objRead;
    public Label recieverRead;
    public Label textRead;
    public Button button;
    private String UserConnected;
    private mailModel mailListModel;

    private connection Connection;
    private Email email;
    private Stage stage;

    public void initModel(Email email, String userConnected, mailModel mailListModel) throws IOException {
        this.mailListModel = mailListModel;
        this.UserConnected = userConnected;
        this.email = email;
        setReaderParams(email);
    }

    /* settiamo i paramentri della mail da leggere */
    public void setReaderParams(Email email) throws IOException {
        senderRead.setText(email.getSender());
        dataRead.setText(email.getDate());
        objRead.setText(email.getObj());
        recieverRead.setText(Connection.getUsername());
        textRead.setText(email.getText());
    }


    public void setConnection(connection Connection) {
        this.Connection = Connection;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private void whichButtonClicked(ActionEvent event) throws IOException, ClassNotFoundException {
        Button button = (Button) event.getTarget();
        String action = button.getId();
        switch (action) {

            case "replyButton":
                setWriterStage(stage,action);
                //in cui mettiamo gia il nome di chi ci ha mandato la mail
                break;

            case "replyAllButton":
                setWriterStage(stage, action);
                //in cui mettiamo tutti gli utenti come soggetti della mail
                break;

            case "forwardButton":
                setWriterStage(stage,action);
                //con la mail preparata, mancano solo i recievers
                break;

            case "deleteButton":
                mailListModel.removeEmail(mailListModel.getCurrentMail()); //per il locale
                if(Connection.deleteMail(email)){
                    deleteMailSuccess();
                }else{
                    showError();
                }
                break;

            case "closeButton":
                setMainStage(stage);
                break;

        }

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


    public void setWriterStage(Stage stage, String typeOfWriter) throws IOException {
        switch (typeOfWriter){
            case "replyButton":
                FXMLLoader writeLoader1 = new FXMLLoader(ClientMain.class.getResource("writeMail.fxml"));
                Parent root1 = writeLoader1.load(); //carichiamo la pagina del reader come nuova finestra
                controllerWriterMail writeController1 = writeLoader1.getController();
                writeController1.setConnection(Connection);
                writeController1.initModel(this.mailListModel);
                writeController1.setReciverFieldWrite(email.getSender() + "@mia-mail.com"); //settiamo nella mail il valore di chi dovrà essere replyato
                writeController1.setTextFieldWrite("in risposta a : " + email.getObj() + ", In data : " + email.getDate() + "."); //aggiungiamo di base delle informazioni nella mail riguardanti a cosa ci ha risposto

                Scene scene1 = new Scene(root1);
                writeController1.setStage(stage);
                stage.setScene(scene1);

                stage.setMaxWidth(600);
                stage.setMaxHeight(400);
                stage.setMinWidth(600);
                stage.setMinHeight(400);
                stage.show();
                break;

            case "replyAllButton":
                ArrayList<String> allUsers = ClientMain.getAllusersExceptMe(Connection.getUsername());
                FXMLLoader writeLoader2 = new FXMLLoader(ClientMain.class.getResource("writeMail.fxml"));
                Parent root2 = writeLoader2.load(); //carichiamo la pagina del reader come nuova finestra
                controllerWriterMail writeController2 = writeLoader2.getController();
                writeController2.setConnection(Connection);
                writeController2.initModel(this.mailListModel);
                writeController2.usersToReplyAll(allUsers);
                writeController2.setReciverFieldWrite("all...");
                writeController2.setTextFieldWrite("in risposta a : " + email.getObj() + ", In data : " + email.getDate() + "."); //aggiungiamo di base delle informazioni nella mail riguardanti a cosa ci ha risposto


                Scene scene2 = new Scene(root2);
                writeController2.setStage(stage);
                stage.setScene(scene2);

                stage.setMaxWidth(600);
                stage.setMaxHeight(400);
                stage.setMinWidth(600);
                stage.setMinHeight(400);
                stage.show();
                //in cui mettiamo tutti gli utenti come soggetti della mail
                break;

            case "forwardButton":
                FXMLLoader writeLoader3 = new FXMLLoader(ClientMain.class.getResource("writeMail.fxml"));
                Parent root3 = writeLoader3.load(); //carichiamo la pagina del reader come nuova finestra
                controllerWriterMail writeController3 = writeLoader3.getController();
                writeController3.setConnection(Connection);
                writeController3.initModel(this.mailListModel);
                writeController3.setTextFieldWrite("inoltrando la mail : " + "CC : " + email.getObj() + "," + " In data : " + email.getDate() + "," + " Testo : " + email.getText() + "," + " Ricevuta da : " + email.getSender() + "@mia-mail.com."); //aggiungiamo di base delle informazioni nella mail riguardanti a cosa ci ha risposto

                Scene scene3 = new Scene(root3);
                writeController3.setStage(stage);
                stage.setScene(scene3);

                stage.setMaxWidth(600);
                stage.setMaxHeight(400);
                stage.setMinWidth(600);
                stage.setMinHeight(400);
                stage.show();
                //con la mail preparata, mancano solo i recievers
                break;
        }
    }
}
