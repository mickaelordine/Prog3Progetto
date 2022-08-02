package com.example.connection;

import com.example.controller.controllerServer;
import com.example.common.Email;
import com.example.logServer.FileManager;
import com.example.logServer.Model;
import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{
    private String connnectedUser;
    private ServerSocket ss;
    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private controllerServer cs;
    Email email;
    Model serverModel;
    private PrintWriter out;

    public ServerSocket getSs() {
        return ss;
    }

    public Socket getS() {
        return s;
    }

    public Server(Socket socket, Model serverModel) throws IOException {
        this.serverModel = serverModel;
        this.s = socket;
        try {
            this.os = new ObjectOutputStream(s.getOutputStream());
            this.is = new ObjectInputStream(s.getInputStream());
            out = new PrintWriter(s.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
            Platform.runLater(() -> serverModel.addLog("errore mentre inizializzavo i br e bw"));
        }

    }

    public synchronized ArrayList<Email> refreshMail() throws IOException, ParseException {
        ArrayList<Email> listOfMails = new ArrayList<>();
        listOfMails.addAll(FileManager.downloadMails(connnectedUser));
        return listOfMails;
    }

    public synchronized void sendMail(Email email) throws IOException {
        FileManager.createEmail(email);
        Platform.runLater(() -> serverModel.addLog(  connnectedUser + " sent a mail to " + email.getReceivers()));
    }

    public synchronized void deleteMail(Email email) throws IOException, ParseException {
        if(FileManager.deleteEmail(email, connnectedUser))
            Platform.runLater(() -> serverModel.addLog(  connnectedUser + " deleted the mail with IDdate :  " + email.getDate()));
        else
            Platform.runLater(() -> serverModel.addLog(  connnectedUser + " error while deleting the mail with IDdate :  " + email.getDate()));

    }

    public synchronized void logOut(){
        System.out.println("logout");
    }

    public synchronized int howMuchMails() throws IOException, ParseException {
        int num = FileManager.howManyMails(connnectedUser);
        if(num >= 0){
            System.out.println();
        }else{
            Platform.runLater(() -> serverModel.addLog(  connnectedUser + " error downloading the size of mails :  " + num));
        }
        return num;
    }



    /*
     * richieste effettuate dall'utente (remove mail, send mail, reload mail, number of email and logout)
     * */
    @Override
    public void run() {
            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String username = in.readLine() + "@mia-mail.com";
                connnectedUser = username;
                serverModel.getUsersConnected().add(username); //aggiungo lo user all'arraylist degli utenti connessi nel modello
                //Platform.runLater(() -> serverModel.addLog(username));
            }catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> serverModel.addLog("error while reading username"));
            }
            String request;
            try {
                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    request = in.readLine();
                    try{
                        clientHanler(in,request);
                    }catch (NullPointerException | ClassNotFoundException | ParseException e){
                        e.printStackTrace();
                    }
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> serverModel.addLog("error while managing request"));
            }
        }


        public void clientHanler(BufferedReader in, String action) throws IOException, ClassNotFoundException, ParseException { //in: per ricevere info dal server, action: l'azione da exc
            switch (action) {
                case "removeMail":
                    email = ((Email) is.readObject());
                    deleteMail(email);
                    break;

                case "sendMail":
                    email = ((Email) is.readObject());
                    sendMail(email);
                    break;

                case "loadingMails":
                    try {
                        ArrayList<Email> mailToDisplay = refreshMail();
                        os.writeObject(mailToDisplay); //gli mando le mail al client
                    }catch (IOException e){
                        e.printStackTrace();
                        Platform.runLater(() -> serverModel.addLog("error while loadingMails to display"));
                    }
                    break;

                case "howManyMails":
                    int result = howMuchMails();
                    try{
                        os.writeObject(result);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;

                case "logout":
                    logOut();
                    break;

                default:
                    System.out.println("error in request param");
            }

        }

}

