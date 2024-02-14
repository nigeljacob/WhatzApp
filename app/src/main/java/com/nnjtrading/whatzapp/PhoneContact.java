package com.nnjtrading.whatzapp;

public class PhoneContact {

    private String Name, number;

    public String getName() {
        return Name;
    }

    public String getNumber() {
        return number;
    }

    public PhoneContact(String name, String number) {
        Name = name;
        this.number = number;
    }
}
