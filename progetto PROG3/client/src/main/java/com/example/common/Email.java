package com.example.common;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Email implements Serializable {
    Random rand = new Random(); //uso il random float per generare un codice univoco alle mail per essere selezionate ed eleminarle correttamente
    private Float ID;
    private String sender;
    private String receivers;
    private String obj;
    private String text;
    private String date;


    public Email() throws ParseException {
        this.ID = rand.nextFloat();
        this.sender = "";
        this.receivers = "";
        this.obj = "";
        this.text = "";
        this.date = "";
    }

    public Email(String sender, String receivers, String obj, String text, String date) {
        this.sender = sender;
        this.receivers = receivers;
        this.obj = obj;
        this.text = text;
        this.date = date;
    }

    public Float getID() {
        return ID;
    }

    public void setID(Float ID) {
        this.ID = ID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
