package it.unimib.disco.gruppoade.gamenow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.ForumActivity;
import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.data.User;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "FireBase UI Activity";


    public static final String EXTRA_MESSAGE = "nameDb";

    // firebase auth
    FirebaseAuth.AuthStateListener mAuthListener;
    TextView tv;
    private FirebaseAuth firebaseAuth;

    // firebase db
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_discover, R.id.navigation_comingsoon, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        tv = findViewById(R.id.textview_accountState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        // se non ho i file di preset
        /*if(true){
            // lancio la activity che mi fa compilare la pagina di preset
            Intent intent = new Intent(this, ForumActivity.class);

            Log.d(TAG , "chiamo:  startActivity(intent)");
            startActivity(intent);
        }*/


        if (user != null) {
           Log.d(TAG , "Loggato");

        } else {
            Log.d(TAG , "NON LOGGATO");
            createSignInIntent();
        }


    }

    public void createSignInIntent() {
        Log.d(TAG , "Dentro: createSignInIntent()");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "Nome utente: " + user.getDisplayName());

                // QUI LANCIO FORUM se serve


                String usernameDb = user.getUid();
                Log.d(TAG, "usernameDb: " + usernameDb);
                myRef = database.getReference(usernameDb);


                // read from database
                // TODO sistemare problema che viene effettuata la richiesta al db solamente dopo l'if
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if(user == null){
                            // lancio la activity che mi fa compilare la pagina di preset
                            Intent intent = new Intent(getApplicationContext(), ForumActivity.class);

                            Log.d(TAG , "chiamo:  startActivity(intent)");
                            startActivity(intent);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });





            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }



}
