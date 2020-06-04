package it.unimib.disco.gruppoade.gamenow.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class SignUpActivity extends AppCompatActivity {



    private static final String TAG = "SignUpActivity";
    private static final int RC_SIGN_IN = 123;

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    // user
    User theUser;

    //a Uri object to store file path
    private Uri filePath;
    private ImageView profile_photo;

    // activity obj
    private CheckBox pc;
    private CheckBox xbox;
    private CheckBox ps4;
    private CheckBox nintendo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        // actyivity obj
        Button submit_button = findViewById(R.id.submit);
        Button photo_choose_button = findViewById(R.id.btn_photo);
        profile_photo = findViewById(R.id.img_profilepicture);

        // checkbox
        pc = findViewById(R.id.cb_pc);
        xbox = findViewById(R.id.cb_xbox);
        ps4 = findViewById(R.id.cb_ps4);
        nintendo = findViewById(R.id.cb_Switch);


        Log.d(TAG, "Activity partita");

        // quando premo il pulsante per scelgiere la foto, lancio showFileChooser()
        photo_choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "In the click button choose photo");
                showFileChooser();
            }
        });


        // quando premo il pulsante per inviare i dati, effettuo le operazioni di salvataggio
        submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "In the click button submit");

                        // reference to firestore
                        // Create a storage reference
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // build th ename file with the Iid
                        StorageReference imagesRef = storage.getReference().child("images").child(FbDatabase.getUser().getUid() + ".jpg");

                        // upload file on firestore
                        UploadTask uploadTask = imagesRef.putFile(filePath);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d(TAG, "Upload FALLITO!!");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "Upload effettuato con SUCCESSO");
                            }
                        });


                        // creo User
                        theUser = new User(FbDatabase.getUser().getDisplayName(), FbDatabase.getUser().getEmail());

                        // setto i tag
                        if(pc.isChecked())
                            theUser.addTagNoDbUpdate("PC");

                        if(xbox.isChecked())
                            theUser.addTagNoDbUpdate("XBOX ONE/X");

                        if(ps4.isChecked())
                            theUser.addTagNoDbUpdate("PS4/5");

                        if(nintendo.isChecked())
                            theUser.addTagNoDbUpdate("Nintendo");

                        // salvo user su DB
                        FbDatabase.getUserReference().setValue(theUser);

                        // chiudo l'activity
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
            Picasso.get()
                    .load(filePath)
                    .fit()
                    .centerCrop()
                    .into((ImageView) profile_photo);

        }
    }


}
