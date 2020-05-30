package it.unimib.disco.gruppoade.gamenow;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;



@IgnoreExtraProperties
public class User {

    private static final String TAG = "User";

    public String username;
    public String email;
    public List<String> tags;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        tags = new ArrayList<>();
    }

    public void addTag(String tag){

        tags.add(tag.toUpperCase());
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", tags=" + tags +
                '}';
    }

    public void removeTag(String tmpString) {
        Log.d(TAG, "Rimozione Tag");

        Log.d(TAG, "Tag da rimuovere: " + tmpString);
        Log.d(TAG, "Elenco tag: " + tags);

        tags.remove(tmpString.toUpperCase());

    }
}