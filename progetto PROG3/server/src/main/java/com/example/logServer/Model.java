package com.example.logServer;


import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/*
* rappresentiamo la lista del log e gli utenti attualmente collegati
* */

public class Model {
    private final List<String> list = new ArrayList<>();
    private final ObservableList<String> logList = FXCollections.observableArrayList(list);


    private final ArrayList<String> usersConnected = new ArrayList<>();
    public ArrayList<String> getUsersConnected() {
        return usersConnected;
    }

    public ObservableList<String> getLogList() {
        return logList;
    }
    public synchronized void addLog(String line) {
        logList.add(line);
    }

}
