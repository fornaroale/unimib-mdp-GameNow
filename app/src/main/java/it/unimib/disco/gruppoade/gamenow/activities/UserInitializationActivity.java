package it.unimib.disco.gruppoade.gamenow.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.NewsProvider;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.repositories.ProvidersRepository;

public class UserInitializationActivity extends AppCompatActivity {



    private static final String TAG = "SignUpActivity";
    private static final int RC_SIGN_IN = 123;

    private static final String FOTO = "foto";

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    // user
    User theUser;

    // variabile che controlla se almeno una checkbox è stata premuta
    private int numCKset;

    //a Uri object to store file path
    private Uri filePath;
    private ImageView profile_photo;

    // lista di tag
    private List<String> tagList;

    // tag selezionati da utente
    private  List<String> tagSelected;

    // activity obj
    private LinearLayout container_cb;

    // button
    private Button submit_button;
    private Button photo_choose_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_initialization);



        tagList = readTagsCsv();
        tagSelected = new ArrayList<>();
        numCKset = 0;


        // actyivity obj
        submit_button = findViewById(R.id.submit);
        photo_choose_button = findViewById(R.id.btn_photo);
        profile_photo = findViewById(R.id.img_profilepicture);

        // setto il bottone come non premibile
        submit_button.setEnabled(false);

        // container checkbox
        container_cb = findViewById(R.id.container_cb);

        // id associata alla checkbox
        int id = 0;

        // creazione dinamica cjeckbox
        for(String tag : tagList){

            final CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText(tag);
            cb.setId(id);
            id++;
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(cb.isChecked()){
                        tagSelected.add((String) cb.getText());
                        numCKset++;
                        activateDeactivateButton();
                    }
                    else{
                        tagSelected.remove((String) cb.getText());
                        numCKset--;
                        activateDeactivateButton();
                    }
                }
            });
            container_cb.addView(cb);
        }

        // quando premo il pulsante per scelgiere la foto, lancio showFileChooser()
        photo_choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


        // quando premo il pulsante per inviare i dati, effettuo le operazioni di salvataggio
        submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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



                        // creo User
                        theUser = new User(FbDatabase.getUserAuth().getDisplayName(), FbDatabase.getUserAuth().getEmail());

                        // setto i tag
                        for(String tag : tagSelected){
                            theUser.addTagNoDbUpdate(tag);
                        }

                        // salvo user su DB
                        FbDatabase.getUserReference().setValue(theUser);

                        finish();
                    }
                });


    }


    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // prendo il persocroso della foto
            filePath = data.getData();

            // la stampo usando picasso
            if(filePath != null){
                Picasso.get()
                        .load(filePath)
                        .fit()
                        .centerCrop()
                        .into((ImageView) profile_photo);

                profile_photo.setVisibility(View.VISIBLE);
            }
        }
    }

    private void activateDeactivateButton(){
        if(numCKset>0)
            submit_button.setEnabled(true);
        else
            submit_button.setEnabled(false);
    }

    private List<String> readTagsCsv() {
        ProvidersRepository.getInstance(getResources());
        List<NewsProvider> providers = ProvidersRepository.loadProviders();
        List<String> providersTags = new ArrayList<>();

        for(NewsProvider provider : providers){
            String providerPlatform = provider.getPlatform();
            if(!providersTags.contains(providerPlatform))
                providersTags.add(providerPlatform);
        }

        return providersTags;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

}
