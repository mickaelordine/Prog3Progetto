package com.example.mailClient.Connection;

import com.example.common.Email;
import com.example.mailClient.Model.mailModel;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class connection {
    private final String nomeHost = "localhost";
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
            socket.close();
        } catch (IOException e) {
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
            System.out.println(e.getMessage() + "connection with server gone wrong");
    }
    }

    /*
     * loading mails in the arraylist and sorting by date with bubbleSort
     * */
    public synchronized ArrayList<Email> loadMails() throws IOException, ClassNotFoundException{
        ArrayList<Email> emails = new ArrayList<>();
        Email temp;
        try{
            connect();
            out.println("loadingMails");
            emails = (ArrayList<Email>) inStream.readObject(); //leggiamo la risposta del server
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while reading object");
            e.printStackTrace();
        }
        return emails;
    }

    /*
    * loading mails in the arraylist and sorting by date with bubbleSort
    * */
    public synchronized ArrayList<Email> reloadMails() throws IOException, ClassNotFoundException{
        connect();
        out.println("reloadMails");
        ArrayList<Email> emails = new ArrayList<>();
        Email temp;
        try {
            emails = (ArrayList<Email>) inStream.readObject(); //leggiamo la risposta del server
            for(int i = 0; i< emails.size()-1; i++){ //sorting emails with bubblesort
                for(int j = 0; j<emails.size()-i-1; j++){
                    if(emails.get(j).getDate().compareTo(emails.get(j+1).getDate())>0){
                        temp = emails.get(j);
                        emails.set(emails.indexOf(emails.get(j)), emails.get(j+1));
                        emails.set(emails.indexOf(emails.get(j+1)), temp);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while reading object");
            e.printStackTrace();
        }
        return emails;
    }

    /*
    * deliting mails in the model
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
    * used to display the correct size of mails
    * */
    public int howManyMails() throws IOException, InterruptedException {
        connect();
        out.println("howManyMails");
        Integer number = 0;
        try {
            number = (Integer) inStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException eofException) {
            System.out.println("EOF");
            eofException.printStackTrace();
        }

        socket.close();
        return number;
    }

    /*
     * used to send mails and return bool to check if everything goes right
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
