package it.unimib.disco.gruppoade.gamenow.models;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;

@IgnoreExtraProperties
public class User {

    private String username;
    private String email;
    private List<String> tags;
    private List<String> news;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        tags = new ArrayList<>();
        news = new ArrayList<>();
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        tags = new ArrayList<>();
        news = new ArrayList<>();
    }

    @PropertyName("tags")
    public List<String> getTags() {
        return tags;
    }

    @PropertyName("news")
    public List<String> getNews() {
        return news;
    }

    @PropertyName("username")
    public String getUsername() {
        return username;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    public boolean savePieceOfNews(List<PieceOfNews> locallySavedNews, PieceOfNews pon){
        if(!checkSavedPieceOfNews(locallySavedNews, pon)) {
            locallySavedNews.add(pon);
            Gson gson = new Gson();
            String jsonPieceOfNews = gson.toJson(pon);
            List<String> userDbNews = getNews();
            userDbNews.add(jsonPieceOfNews);
            FbDatabase.FbDatabase().getUserReference().child("news").setValue(userDbNews);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeSavedPieceOfNews(List<PieceOfNews> locallySavedNews, PieceOfNews pon){
        if(checkSavedPieceOfNews(locallySavedNews, pon)) {
            locallySavedNews.remove(pon);
            List<String> newsToUpload = new ArrayList<>();
            Gson gson = new Gson();

            for (PieceOfNews oldPon : locallySavedNews) {
                newsToUpload.add(gson.toJson(oldPon));
            }

            // Per sicurezza, tolgo anche dall'array locale:
            news.remove(gson.toJson(pon));

            FbDatabase.FbDatabase().getUserReference().child("news").setValue(newsToUpload);

            return true;
        } else {
            return false;
        }
    }

    public boolean checkSavedPieceOfNews(List<PieceOfNews> locallySavedNews, PieceOfNews PON){
        Boolean savedNews = false;
        for(PieceOfNews pon : locallySavedNews){
            if(pon.equals(PON)) {
                savedNews = true;
            }
        }
        return savedNews;
    }

    public void addTag(String tag) {
        tags.add(tag.toUpperCase());
        FbDatabase.FbDatabase().getUserReference().child("tags").setValue(getTags());
    }

    public void addTagNoDbUpdate(String tag) {
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
        tags.remove(tmpString.toUpperCase());
        FbDatabase.FbDatabase().getUserReference().child("tags").setValue(getTags());
    }

    public void removeTagNoDbUpdate(String tmpString) {
        tags.remove(tmpString.toUpperCase());
    }
}