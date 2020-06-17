package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.activities.MainActivity;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.fragments.profile.TagComparator;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class TabSettingsFragment extends Fragment {

    private static final String TAG = "TabSettingFragment";

    private static final int PICK_IMAGE_REQUEST = 234;


    // recupero l'utente con i relativi dati dal db
    private User theUser;


    // inserisco variabili
    private List<String> tags;
    //a Uri object to store file path
    private Uri filePath;

    // oggetti activity
    private EditText usernameET;
    private ChipGroup chipGroup;
    private TextView username;
    private TextView email;
    private CardView logout;
    private CardView deleteaccount;
    private CardView cv_infoaccount;
    private CircleImageView profilePicture;
    private File localFile;
    private boolean userDeleted;

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

        return inflater.inflate(R.layout.fragment_tab_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePicture = view.findViewById(R.id.profile_image_change);
        logout = view.findViewById(R.id.cv_logout);
        deleteaccount = view.findViewById(R.id.cv_deleteaccount);
        cv_infoaccount = view.findViewById(R.id.cv_infoaccount);
        usernameET = view.findViewById(R.id.Username);

        setHasOptionsMenu(true);

        userDeleted = false;
       // usernameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawableRight, 0);



        // assegno l'azione di SignOut alla Cardview
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // associo azione delete
        deleteaccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Eliminazione account")
                        .setMessage("Sei sicuro di voler eliminare il tuo account?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(TAG, "DELETE -> Cancello utente.");
                                deleteAccount();

                                Log.d(TAG, "DELETE -> Sign out.");
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                firebaseAuth.signOut();

                                Log.d(TAG, "DELETE -> Start mainActivity intent.");
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);

                                Log.d(TAG, "DELETE -> Finish sulla vecchia mainActivity.");
                                getActivity().finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        // assegno una azione alla foto profilo
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d(TAG, "Foto profilo cliccata");
                showFileChooser();
            }
        });

        Log.d(TAG, "Start Profile");

        // recupero l'user
        Log.d(TAG, "Dentro getUserOnDb()");

        // collego un listener all'user su Db
        FbDatabase.getUserReference().addListenerForSingleValueEvent(postListener);

    }

    private void deleteAccount(){
        userDeleted = true;

        String uid = FbDatabase.getUserAuth().getUid();

        // Cancello foto profilo utente (se presente)
        StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images").child(uid);
        Log.d(TAG, "Sto cancellando l'immagine.");
        imagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Ho cancellato l'immagine.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Non sono riuscito a cancellare l'immagine. Errore: " + exception);
            }
        });

        // Cancello credenziali autenticazione
        Log.d(TAG, "Sto cancellando le credenziali di auth.");
        FirebaseUser FBAuthUserToDel = FbDatabase.getUserAuth();
        FBAuthUserToDel
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });

        // Cancello dati utente database
        Log.d(TAG, "Sto cancellando i dati del realtime database.");
        FbDatabase.getUserReference().removeValue();
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(!userDeleted) {
                theUser = dataSnapshot.getValue(User.class);

                // quando leggo l'user da db, chiamo il motodo che inizializza l'activity
                setUp(getActivity().findViewById(android.R.id.content).getRootView());
            }
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
            //username.setText(theUser.getUsername());
            setEditUser();
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
        StorageReference imagesRef = storage.getReference().child("images").child(FbDatabase.getUserAuth().getUid());

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

    private void uploadNewPhoto(){
        if(filePath != null){
            // reference to firestore
            // Create a storage reference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // build th ename file with the Iid
            StorageReference imagesRef = storage.getReference().child("images").child(FbDatabase.getUserAuth().getUid());

            // upload file on firestore
            UploadTask uploadTask = imagesRef.putFile(filePath);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {

            // prendo il persocroso della foto
            filePath = data.getData();

            // la stampo usando picasso
            if(filePath != null){
                Picasso.get()
                        .load(filePath)
                        .fit()
                        .centerCrop()
                        .into((CircleImageView) profilePicture);

                // aggiorno la foto sul database
                uploadNewPhoto();

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditUser(){
        // setto l'username
        username.setText(theUser.getUsername());

        // Rimuovo il chack
        usernameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Rimuovo il chack
                usernameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                    usernameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_username_check_24, 0);
                else
                    usernameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_24, 0);
            }
        });

        usernameET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(usernameET.getCompoundDrawables()[DRAWABLE_RIGHT] != null)
                        if(event.getRawX() >= (usernameET.getRight() - usernameET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            Log.d(TAG, "Premuto check Right");

                            // Rimuovo il chack
                            usernameET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                            if(!usernameET.getText().toString().isEmpty())
                                updateUsername(usernameET.getText());
                            else
                                usernameET.setText(theUser.getUsername());

                            return true;
                        }
                }
                return false;
            }
        });
    }

    private void updateUsername(Editable newUSername){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(String.valueOf(newUSername))
                .build();

        FbDatabase.getUserAuth().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // success!
                        }
                    }
                });

        // set the name in the database
        FbDatabase.getUserReference().child("username").setValue(newUSername.toString());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}