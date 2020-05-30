package it.unimib.disco.gruppoade.gamenow.models;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {

    private static final String TAG = "User";

    private String username;
    private String email;
    private List<String> tags;
    private List<PieceOfNews> savedNews;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        tags = new ArrayList<>();
        savedNews = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<PieceOfNews> getSavedNews() {
        return savedNews;
    }

    public void addTag(String tag) {
        tags.add(tag.toUpperCase());
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