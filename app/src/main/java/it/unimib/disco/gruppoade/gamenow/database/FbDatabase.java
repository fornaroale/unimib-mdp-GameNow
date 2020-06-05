package it.unimib.disco.gruppoade.gamenow.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FbDatabase {
    private static FirebaseDatabase database;
    private static DatabaseReference userReference;

    private static FbDatabase db_instance;

    private FbDatabase() {
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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(uid);
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }
}
