package it.unimib.disco.gruppoade.gamenow.handlers;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;

public class FirebaseHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FbDatabase.FbDatabase();
    }
}
