package com.example.controller;

import com.example.connection.Server;
import com.example.logServer.Model;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class controllerServer {
    private Model serverModel;
    @FXML
    private ListView<String> listView;
    private final static int NUM_THREADS = 10;
    private ServerSocket s;
    private ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

    /*
    * inizializza modello e richiama il metodo per la connesione client-server
    * */
    public void initConnection(Model serverModel) throws IOException {
        if (this.serverModel != null)
            throw new IllegalStateException("Model can only be initialized once");
        this.serverModel = serverModel;

        listView.setItems(serverModel.getLogList());

        this.clientHandlerConnection();

    }

    /*
    * sincronizzazione client server attraverso l'uso di thread e executor per i thread
    * */
    private void clientHandlerConnection(){
        new Thread(() -> {
            try {
                s = new ServerSocket(9999); //creaiamo un serversocket con la porta 9999
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    Socket socket = s.accept();
                    System.out.println("accetatta la connesione di un client");
                    executor.execute(new Server(socket,this.serverModel)); //inizilizziamo un server con il modello e il socket
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}




