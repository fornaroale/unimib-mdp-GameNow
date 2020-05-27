package it.unimib.disco.gruppoade.gamenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class ForumActivity extends AppCompatActivity {









    private static final String TAG = "ForumActivity";
    private static final int RC_SIGN_IN = 123;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String usernameDb;
    // login
    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth firebaseAuth;

    // collegamento al db
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        // login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(TAG , "Loggato");

        } else {
            Log.d(TAG , "NON LOGGATO");
            createSignInIntent();
        }


        Button submit_button = findViewById(R.id.submit);




        Log.d(TAG, String.valueOf("Activity partita, userset: "));

        if(user.isEmailVerified())
            Log.d(TAG, "mailVerificata: " + user.getEmail());


        usernameDb = "prego@gmail.com";
        //usernameDb = user.getEmail();
        usernameDb = usernameDb.replace(".", "DOT");
        Log.d(TAG, "usernameDb: " + usernameDb);
        myRef = database.getReference(usernameDb);


                submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "In the click button submit");

                        // write data on databse
                        Log.d(TAG, "MyRefKey: " + myRef.getKey());
                        myRef.setValue("PS4, XBOX");

                        finish();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "Dentro ONStart");

       /* myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Messaggio onDataChange: " + text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.");
            }
        });*/

        Log.d(TAG, "Fuori ONStart");
    }

    private String creaUserDB(FirebaseUser user) {
        String temp = null;

        temp = user.getDisplayName().replace(" ", "");
        temp += user.getEmail().replace(".", "");

        Log.d(TAG, "Dentro creaUserDB: " + temp);


        return temp;
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                //new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        //new AuthUI.IdpConfig.FacebookBuilder().build(),
        //new AuthUI.IdpConfig.TwitterBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.app_logo)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }
}
