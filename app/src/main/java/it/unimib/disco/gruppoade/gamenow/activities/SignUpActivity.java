package it.unimib.disco.gruppoade.gamenow.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class SignUpActivity extends AppCompatActivity {



    private static final String TAG = "SignUpActivity";
    private static final int RC_SIGN_IN = 123;

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String usernameDb;

    // collegamento al db
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    //a Uri object to store file path
    private Uri filePath;
    private ImageView profile_photo;


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
        Button photo_choose_button = findViewById(R.id.btn_photo);
        profile_photo = findViewById(R.id.img_profilepicture);



        // checkbox
        final CheckBox pc = findViewById(R.id.cb_pc);
        final CheckBox xbox = findViewById(R.id.cb_xbox);
        final CheckBox ps4 = findViewById(R.id.cb_ps4);
        final CheckBox nintendo = findViewById(R.id.cb_Switch);


        Log.d(TAG, "Activity partita, userset: ");

        photo_choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "In the click button choose photo");
                showFileChooser();
            }
        });


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


                        // reference to firestore
                        // Create a storage reference
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // build th ename file with the Iid
                        StorageReference imagesRef = storage.getReference().child("images").child(user.getUid() + ".jpg");

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

                        // Get the data from an ImageView as bytes
//                        profile_photo.setDrawingCacheEnabled(true);
//                        profile_photo.buildDrawingCache();
//                        Bitmap bitmap = ((BitmapDrawable) profile_photo.getDrawable()).getBitmap();
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] data = baos.toByteArray();
//
//                        UploadTask uploadTask = imagesRef.putBytes(data);
//                        uploadTask.addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                Log.d(TAG, "Upload FALLITO!!");
//                            }
//                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                Log.d(TAG, "Upload effettuato con SUCCESSO");
//                            }
//                        });





                        // creo User
                        User theUser = new User(user.getDisplayName(), user.getEmail());

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
                        myRef.setValue(theUser);




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
            filePath = data.getData();
            try {

                Picasso.get()
                        .load(filePath)
                        .fit()
                        .centerCrop()
                        .into((ImageView) profile_photo);

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
               // profile_photo.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
