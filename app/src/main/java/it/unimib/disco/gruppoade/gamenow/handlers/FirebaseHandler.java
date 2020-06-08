package it.unimib.disco.gruppoade.gamenow.handlers;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.activities.SignUpActivity;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class FirebaseHandler extends Application {

    private AppCompatActivity signActivity;
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "FirebaseHandler";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FbDatabase.FbDatabase();
    }
}
