package it.unimib.disco.gruppoade.gamenow;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {

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
        tag.toLowerCase();
        tags.add(tag);
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
        tmpString.toLowerCase();
        tags.remove(tmpString);
    }
}