package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.fragments.profile.TagComparator;
import it.unimib.disco.gruppoade.gamenow.models.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class TabSettingsFragment extends Fragment {

    private static final String TAG = "TabSettingFragment";

    private static final int PICK_IMAGE_REQUEST = 234;


    // firebase auth
    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth firebaseAuth;

    // recupero l'utente con i relativi dati dal db
    private User theUser;


    // inserisco variabili
    private List<String> tags;

    // oggetti activity
    private ChipGroup chipGroup;
    private TextView username;
    private TextView email;
    private CardView logout;
    private CardView delteaccount;
    private CircleImageView profilePicture;
    private File localFile;

    public TabSettingsFragment() {
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

        profilePicture = view.findViewById(R.id.profile_image);
        logout = view.findViewById(R.id.cv_logout);
        delteaccount = view.findViewById(R.id.cv_deleteaccount);

        // assegno l'azione di SignOut alla Cardview
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                getActivity().finish();
            }
        });

        delteaccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                // cancello user da db
                FbDatabase.getUserReference().removeValue();

                // cancello la foto profilo
                // creo un riferimento allo storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                // costruisco iol nome del file con lo user Uid
                StorageReference imagesRef = storage.getReference().child("images").child(FbDatabase.getUser().getUid() + ".jpg");



                // Delete the file
                imagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "Foto cancellata");

                        // cancella le credenziale e chiude l'activity
                        deleteAccountCredential();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(TAG, "Errore nel cancellamento foto");

                    }
                });

                // effettuo il logout
                firebaseAuth.signOut();
                delteaccount.setClickable(false);

            }
        });

        // assegno una azione alla foto profilo
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Foto profilo cliccata");
            }
        });

        Log.d(TAG, "Start Profile");

        // recupero l'user
        Log.d(TAG, "Dentro getUserOnDb()");

        // collego un listener all'user su Db
        FbDatabase.getUserReference().addListenerForSingleValueEvent(postListener);

        return view;
    }

    // usato per leggere dati dal DB
    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            theUser = dataSnapshot.getValue(User.class);

            // quando leggo l'user da db, chiamo il motodo che inizializza l'activity
            setUp(getActivity().findViewById(android.R.id.content).getRootView());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

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


        // se ho uno user
        if(theUser != null){

            Log.d(TAG, "theUser: " + theUser.toString());

            // collego activity con oggetti
            chipGroup = view.findViewById(R.id.chipGroup);
            username = view.findViewById((R.id.Username));
            email = view.findViewById((R.id.Email));

            // setto i valori
            username.setText(theUser.getUsername());
            email.setText(theUser.getEmail());

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

                    // aggiunta chips alla chips group
                    chipGroup.addView(chip);
                    chipGroup.setVisibility(view.getVisibility());
                }
            }



        // creo un riferimento allo storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // costruisco iol nome del file con lo user Uid
        StorageReference imagesRef = storage.getReference().child("images").child(FbDatabase.getUser().getUid() + ".jpg");

        localFile = null;

        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // se riesco a scaricare la foto profilo la mostro con picasso
        imagesRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "SUCCESS DOWNLOAD");

                Picasso.get()
                        .load(localFile)
                        .fit()
                        .centerCrop()
                        .into((CircleImageView) profilePicture);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "FAILURE DOWNLOAD");
            }
        });


    }

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

                        // salvo user su DB
                        FbDatabase.getUserReference().setValue(theUser);

                    }
                });
                // mostro la snackbar
                mySnackbar.show();

                Log.d(TAG, "Inizio aggiornamento");

               // myRef = database.getReference(usernameDb);
                Log.d(TAG, "Tag theUser: " + theUser.getTags());
                Log.d(TAG, "New theUser: " + theUser.toString());

                // salvo user su DB
                FbDatabase.getUserReference().setValue(theUser);
            }
        });

        // ritorno la chip creata
        return chip;
    }

    private void deleteAccountCredential(){
        // cancello account
        FbDatabase.getUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");

                            // chiudo l'activity una volta cancellato l'account
                            getActivity().finish();

                        }
                    }
                });
    }



}