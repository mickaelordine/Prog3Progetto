package com.example.logServer;

import com.example.common.Email;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;


/*
* inserire in un blocco di 4 alla volta Sender,CC,text,data componendo così una mail.
* le mail contenute in inbox.txt sono esclusivamente quelle ricevute, non quelle inviate
* */
public class FileManager {
    public static final String path = "src/main/files/";
    public static ArrayList<Email> StaticEmails = new ArrayList<>(); //qui ci salviamo le mail che sono arrivate
    public static ArrayList<Email> StaticEmailsOfMickael = new ArrayList<>(); //qui ci salviamo le mail che sono arrivate
    public static ArrayList<Email> StaticEmailsOfEmanuele = new ArrayList<>(); //qui ci salviamo le mail che sono arrivate
    public static ArrayList<Email> StaticEmailsOfGabriele = new ArrayList<>(); //qui ci salviamo le mail che sono arrivate

    public synchronized static ArrayList<Email> downloadMails(String user) throws IOException, ParseException { //deleteMails -> 0 NONE 1-> called by deletemail
        StaticEmails = checkUser(user); //ci preniamo l'arraylist corrispondende allo user
        StaticEmails.clear();
        File file = new File(path + user + "/inbox.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<Email> blockOfMessageToDisplay = new ArrayList<>();
        String line;
            while((line = br.readLine()) != null){
                    Email messageToDisplay = new Email(); //essendo il metodo statico, necessitiamo che l'oggetto venga costruito all'interno del ciclo, se no l'ultima lettura avrà valore per tutti gli altri
                    /*  1-> data, 2-> da, 3-> obj, 4-> Txt  */
                    messageToDisplay.setDate(br.readLine());
                    messageToDisplay.setSender(br.readLine());
                    messageToDisplay.setObj(br.readLine());
                    messageToDisplay.setText(br.readLine());

                    //abbiamo il blocco della mail COMPLETA
                    blockOfMessageToDisplay.add(messageToDisplay);
                    StaticEmails.add(messageToDisplay);
                }
        br.close();
        return blockOfMessageToDisplay;
    }


    /* insert the email in the inbox.txt of the receiver of the mail */
    public synchronized static void createEmail(Email email) throws IOException {
        File file = new File(path + email.getReceivers() + "/inbox.txt");
        System.out.println(email.getReceivers());
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file,true));
        PrintWriter pw = new PrintWriter(bufferedWriter);

        pw.println(); //linea che serve per la corretta lettura dei file
        pw.println(email.getDate());
        pw.println(email.getSender());
        pw.println(email.getObj());
        pw.println(email.getText());


        System.out.println("Data Successfully appended into file");

        pw.flush();
        bufferedWriter.close();
        pw.close();
    }

    public static ArrayList<Email> checkUser(String user){
        if(user.equals("Emanuele@mia-mail.com"))
            return StaticEmailsOfEmanuele;
        if(user.equals("Mickael@mia-mail.com"))
            return StaticEmailsOfMickael;
        else
            return StaticEmailsOfGabriele;
    }

    /* delete email in inboxTxt
    * copy all the mails except the one we want to eliminate, then recopy all the mails in the inboxTxt*/
    public synchronized static boolean deleteEmail(Email email, String user) throws IOException, ParseException {
        StaticEmails = checkUser(user); //ci preniamo l'arraylist corrispondende allo user
        File file = new File(path + user + "/inbox.txt");
        File tempFile = new File(path + user + "/temp.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        PrintWriter pw = new PrintWriter(bufferedWriter);
        boolean created,deleted;
        created = tempFile.createNewFile();
        if(created) System.out.println("file creato correttamente");
            System.out.println(StaticEmails.size());
            for(int i = 0; i<StaticEmails.size(); i++){
                if(StaticEmails.get(i).getDate().equals(email.getDate())){
                    System.out.println("skippato");; //skippiamo la mail
                }else{
                    pw.println(); //creo lo spazio vuoto separatore
                    pw.println(StaticEmails.get(i).getDate());
                    pw.println(StaticEmails.get(i).getSender());
                    pw.println(StaticEmails.get(i).getObj());
                    pw.println(StaticEmails.get(i).getText());
                }
            }
            pw.flush();
            bufferedWriter.close();
            pw.close();
            StaticEmails.clear();
            boolean ok;
            deleted = file.delete();
            ok = tempFile.renameTo(new File(path + user + "/inbox.txt"));
        return ok && deleted;
    }
}
