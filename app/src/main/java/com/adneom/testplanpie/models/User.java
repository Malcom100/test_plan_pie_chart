package com.adneom.testplanpie.models;

import java.util.Date;

/**
 * Created by gtshilombowanticale on 04-08-16.
 */
public class User {

    private int id;
    private String email;
    private java.util.Date date;

    public User(String email, java.util.Date date){
        //setId(id);
        setEmail(email);
        setDate(date);
    }

    public User(){
        setId(-1);
        setEmail("test-1@gmail.com");
        setDate(new Date());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() {
        return ""+getId()+" "+getEmail()+" "+" -- ";
    }
}
