package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.fragments.profile.TagComparator;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class TabSettingsFragment extends Fragment {

    // firebase

    // firebase auth
    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth firebaseAuth;

    // firebase db
    FirebaseDatabase database;
    DatabaseReference myRef;

    private static final String TAG = "TabSettingFragment";

    // recupero l'utente con i relativi dati dal db
    private User theUser = null;

    // inserisco variabili
    private List<String> tags = null;

    // oggetti activity
    private ChipGroup chipGroup;
    private TextView username;
    private TextView email;
    private TextView platform;

    public TabSettingsFragment() {
        //theUser = getUserOnDb();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_tab_settings, container, false);

        Log.d(TAG, "Start Profile");

        // recupero l'user
        Log.d(TAG, "Dentro getUserOnDb()");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String usernameDb = user.getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(usernameDb);
        myRef.addListenerForSingleValueEvent(postListener);

        return view;
    }

    // usato per leggere dati dal DB
    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            theUser = dataSnapshot.getValue(User.class);
            Log.d(TAG, "Messaggio onDataChange: " + theUser.toString());

            setUp(getActivity().findViewById(android.R.id.content).getRootView());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    private String findPlatform(List<String> tags) {
        String tempPlatform = "";

        for (String temp : tags) {
            temp.toLowerCase();

            if (temp.equalsIgnoreCase("xbox") ||
                    temp.equalsIgnoreCase("pc") ||
                    temp.equalsIgnoreCase("ps4") ||
                    temp.equalsIgnoreCase("switcg")) {
                tempPlatform += temp + " ";
            }
        }

        return tempPlatform;
    }


    private void sortedAdd(String element, List<String> tags) {
        // aggiungo l'elemento all'oggetto user
        theUser.addTagNoDbUpdate(element);

        // sorting
        Collections.sort(tags, new TagComparator());
    }

    private String formatText(String text) {
        String result = text;

        // Formatto le stringhe con la prima lettera maiuscola e le successive in minuscolo
        result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
        return result;
    }

    private void setUp(View view) {

        //theUser = getUserOnDb();

        // se non è vuoto
        if(theUser != null){

            Log.d(TAG, "theUser: " + theUser.toString());

            // collego activity con oggetti
            chipGroup = view.findViewById(R.id.chipGroup);
            username = view.findViewById((R.id.Username));
            email = view.findViewById((R.id.Email));
            platform = view.findViewById((R.id.Platform));

            // setto i valori
            username.setText(theUser.getUsername());
            email.setText(theUser.getEmail());

            // setto piattaforma usando un metodo
            // che analizza i tag e trova le piattaforme
            if (theUser.getTags() != null)
                platform.setText(findPlatform(theUser.getTags()));

            // riempio i tag
            tags = theUser.getTags();
        }



        // popolo con i tags
        if (tags != null)
            for (String text : tags) {

                // se il nome esiste
                if (text != null) {
                    text = formatText(text);

                    // creo la chip
                    Chip chip = creaChip(text, view);

                    // aggiunta chips alla chipsvgroup
                    chipGroup.addView(chip);
                    chipGroup.setVisibility(view.getVisibility());
                }
            }


    }

//    User getUserOnDb(){
//        User tempUser = null;
//
//        Log.d(TAG, "Dentro getUserOnDb()");
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        String usernameDb = user.getUid();
//        myRef = database.getReference(usernameDb);
//        myRef.addListenerForSingleValueEvent(postListener);
//
//
//
//
//
//        return tempUser;
//    }

    private Chip creaChip(String text, View tempView) {

        Log.d(TAG, "Creo chip, text: " + text);

        // creo la chips e la setto
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        // creo delle variabili final
        // mi servono per poterle usare nell'azione del tasto UNDO
        final String tmpString = text;
        final View tmpView = tempView;
        final Chip tmpChip = chip;

        // associo alla x la rimozione
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //rimuovo l'elemento dal chipGroup
                chipGroup.removeView(v);

                // rimuovo l'elemento dall'oggetto User
                theUser.removeTagNoDbUpdate(tmpString);
                Log.d(TAG, "rimozione tag da theUser: " + theUser.toString());

                // riottengo i tag
                tags = theUser.getTags();


                // Creo la snackbar
                Snackbar mySnackbar = Snackbar.make(chipGroup, "Tag eliminato: " + tmpString, Snackbar.LENGTH_SHORT);

                // associo la funzione al tasto UNDO
                mySnackbar.setAction("Undo", new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {

                        // richiamo la stessa funzione (ricorsione(?))
                        creaChip(tmpString, tmpView);

                        // aggiungo nuovamente la chips al chipsGroup
                        chipGroup.addView(tmpChip);
                        chipGroup.setVisibility(tmpView.getVisibility());

                        // aggiungo l'elemento ad user
                        sortedAdd(tmpString, tags);

                        // salvo l'user su db
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        String usernameDb = user.getUid();

                        myRef = database.getReference(usernameDb);

                        // aggiorno le piattaforme
                        platform.setText(findPlatform(theUser.getTags()));
                        // salvo user su DB
                        myRef.setValue(theUser);

                    }
                });
                // mostro la snackbar
                mySnackbar.show();

                Log.d(TAG, "Inizio aggiornamento");
                // salvo l'user su db
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String usernameDb = user.getUid();

                Log.d(TAG, "UsernameDb: " + usernameDb);
                myRef = database.getReference(usernameDb);
                Log.d(TAG, "Tag theUser: " + theUser.getTags());
                Log.d(TAG, "New theUser: " + theUser.toString());

                // aggiorno le piattaforme
                platform.setText(findPlatform(theUser.getTags()));

                // salvo user su DB
                myRef.setValue(theUser);
            }
        });

        // ritorno la chip creata
        return chip;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        theUser = getUserOnDb();
//    }
}