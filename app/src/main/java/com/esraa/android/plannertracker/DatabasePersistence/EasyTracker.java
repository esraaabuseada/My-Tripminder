package com.esraa.android.plannertracker.DatabasePersistence;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class EasyTracker extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
