package com.nnjtrading.whatzapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class LoadDataOffline extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
