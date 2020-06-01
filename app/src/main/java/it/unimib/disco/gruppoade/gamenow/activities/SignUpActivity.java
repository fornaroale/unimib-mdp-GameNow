package it.unimib.disco.gruppoade.gamenow.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class SignUpActivity extends AppCompatActivity {



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
}
