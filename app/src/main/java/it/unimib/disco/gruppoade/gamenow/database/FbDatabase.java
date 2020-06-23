package it.unimib.disco.gruppoade.gamenow.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public final class FbDatabase {
    private static FirebaseDatabase database;
    private static DatabaseReference userReference;
    private static boolean userDeleting;

    private static FbDatabase db_instance;

    private FbDatabase() {
        userDeleting = false;
        database = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setUserReference();
        }
    }

    public static FbDatabase FbDatabase() {
        // Per esser sicuri che venga creata una sola istanza:
        synchronized (FbDatabase.class) {
            if (db_instance == null)
                db_instance = new FbDatabase();
        }
        return db_instance;
    }

    public static DatabaseReference getUserReference() {
        return userReference;
    }

    public static void setUserReference(){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userReference = database.getReference("users/" + uid);
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static FirebaseUser getUserAuth() {return FirebaseAuth.getInstance().getCurrentUser();}

    public static void setUserDeletingTrue(){
        userDeleting = true;
    }

    public static void setUserDeletingFalse(){
        userDeleting = false;
    }

    public static boolean getUserDeleting(){
        return userDeleting;
    }
}