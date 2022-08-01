package com.example.mailClient.Model;
import com.example.common.Email;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.ParseException;
import java.util.ArrayList;

/*
* il modello conoterrà tutti quei valori che devono essere manipolati dal controller e rirappresentati sulla view una volta aggiornati
* - nome utente
* - mail list
* */
public class mailModel {

    private final ObservableList<Email> emailListView = FXCollections.observableArrayList();
    private Email currentMail = new Email();
    private String Username;

    public mailModel() throws ParseException {
    }

    public void setCurrentMail(Email email){
        this.currentMail = email;
    }

    public Email getCurrentMail(){
        return currentMail;
    }



    public void setUsername(String name){
        this.Username = name;
    }

    public String getUsername(){
        return this.Username;
    }

    public ObservableList<Email> getEmailList() {
        return emailListView;
    }

    public ObservableList<Email> setEmailListView(ArrayList<Email> emails) {
        emailListView.clear(); //pulisco
        emailListView.addAll(emails); //inserisco
        return emailListView;
    }

    public void removeEmail(Email newEmail) {
        emailListView.removeIf(email -> email.getDate().equals(newEmail.getDate()));
    }

    public void loadData(ArrayList<Email> mails) {
        emailListView.clear();
        emailListView.addAll(mails);
    }


}
