package it.unimib.disco.gruppoade.gamenow.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FbDatabase {
    private static FirebaseDatabase database;
    private static DatabaseReference userReference;

    private static FbDatabase db_instance;

    private FbDatabase() {
        database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(uid);
    }

    public static FbDatabase FbDatabase() {
        // To ensure only one instance is created
        synchronized (FbDatabase.class) {
            if (db_instance == null)
                db_instance = new FbDatabase();
        }
        return db_instance;
    }

    public static DatabaseReference getUserReference() {
        return userReference;
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }
}
