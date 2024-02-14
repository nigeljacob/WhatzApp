package com.nnjtrading.whatzapp;

public class User {
    private String Name, number, slogan, status, uid, uri;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public User(String name, String number, String slogan, String status, String uid) {
        Name = name;
        this.number = number;
        this.slogan = slogan;
        this.status = status;
        this.uid = uid;
    }
    public User() {

    }
}
