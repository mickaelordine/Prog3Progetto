package com.example.mailClient.Connection;

import com.example.common.Email;
import com.example.mailClient.Model.mailModel;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class connection {
    private final int port = 9999;
    private String username;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private mailModel mailListModel;

    public connection(mailModel mailListModel) { //int port,
        this.mailListModel = mailListModel;
    }

    /*
    * logging out
    * */
    public synchronized void closeConnection() {
        try {
            connect();
            out.println("logout");
            Thread.sleep(200);
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) throws IOException {
            this.username = username;
    }

    public final String getUsername() throws IOException {

        return username;
    }

    /*
    * connecting to Server
    * */
    public synchronized void connect() throws IOException{
        socket = new Socket("localhost", port);
        try{
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(username);
            Thread.sleep(200); //permette al server di processare sequenzialmente e correttamente i byte che vengono inviati
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
    }
    }

    /*
     * loading mails in the arraylist and sorting by date with bubbleSort
     * */
    public synchronized ArrayList<Email> loadMails() throws IOException, ClassNotFoundException{
        ArrayList<Email> emails = new ArrayList<>();
        try{
            connect();
            out.println("loadingMails");
            emails = (ArrayList<Email>) inStream.readObject(); //leggiamo la risposta del server
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return emails;
    }

    /*
    * deleting mails in the model
    * */
    public synchronized boolean deleteMail(Email email){
        try {
            connect();
            out.println("removeMail");
            Thread.sleep(300);
            outStream.writeObject(email);
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
    * used to display the correct size of mails, used for NewMessagePopUp
    * */
    public int howManyMails() throws IOException, InterruptedException {
        connect();
        out.println("howManyMails");
        Integer number = 0;
        try {
            number = (Integer) inStream.readObject();
        } catch (EOFException | ClassNotFoundException eofException) {
            eofException.printStackTrace();
        }
        socket.close();
        return number;
    }

    /*
     * used to send mails and return bool to check if everything goes right and for show the correct popUp
     * */
    public synchronized boolean sendMail(Email mail) {
        try {
            connect();
            out.println("sendMail");
            Thread.sleep(300);
            outStream.writeObject(mail);
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


}
