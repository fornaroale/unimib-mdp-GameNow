package it.unimib.disco.gruppoade.gamenow;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

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

import it.unimib.disco.gruppoade.gamenow.models.User;

public class ForumActivity extends AppCompatActivity {



    private static final String TAG = "ForumActivity";
    private static final int RC_SIGN_IN = 123;

    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

       /* if (user != null) {
            Log.d(TAG , "Loggato");

        } else {
            Log.d(TAG , "NON LOGGATO");
            createSignInIntent();
        }*/


        Button submit_button = findViewById(R.id.submit);

        // checkbox
        final CheckBox pc = findViewById(R.id.cb_pc);
        final CheckBox xbox = findViewById(R.id.cb_xbox);
        final CheckBox ps4 = findViewById(R.id.cb_ps4);
        final CheckBox nintendo = findViewById(R.id.cb_Switch);


        Log.d(TAG, "Activity partita, userset: ");



        submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "In the click button submit");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        usernameDb = user.getUid();
                        usernameDb = usernameDb.replace(".", "DOT");
                        Log.d(TAG, "usernameDb: " + usernameDb);
                        myRef = database.getReference(usernameDb);


                        myRef.addListenerForSingleValueEvent(postListener);


                        // write data on databse
                        Log.d(TAG, "MyRefKey: " + myRef.getKey());
//                        myRef.setValue("Stringa");


                        // creo User
                        User theUser = new User(user.getDisplayName(), user.getEmail());

                        // setto i tag
                        if(pc.isChecked())
                            theUser.addTagNoDbUpdate("PC");

                        if(xbox.isChecked())
                            theUser.addTagNoDbUpdate("XBOX");

                        if(ps4.isChecked())
                            theUser.addTagNoDbUpdate("PS4");

                        if(nintendo.isChecked())
                            theUser.addTagNoDbUpdate("Nintendo");

                        // salvo user su DB
                        myRef.setValue(theUser);




                        finish();
                    }
                });
    }

    // usato per leggere dati dal DB
    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            Log.d(TAG, "Messaggio onDataChange: " + user.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };


    // crea e lancia la activity di LOGIN e REGISTRAZIONE
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
