package it.unimib.disco.gruppoade.gamenow.models;

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

    public boolean savePieceOfNews(PieceOfNews pon){
        if(!checkSavedPieceOfNews(pon)) {
            Gson gson = new Gson();
            String jsonPieceOfNews = gson.toJson(pon);
            List<String> userDbNews = news;
            userDbNews.add(jsonPieceOfNews);
            FbDatabase.FbDatabase().getUserReference().child("news").setValue(userDbNews);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeSavedPieceOfNews(PieceOfNews pon){
        if(checkSavedPieceOfNews(pon)) {
            Gson gson = new Gson();

            // Siccome i tag locali potrebbero differire da quelli remoti,
            // li rendo uguali (così che l'eliminazione possa avvenire
            // senza errori)
            for(int i = 0; i < news.size(); i++) {
                // Per evitare uno spreco computazionale, eseguo comparazione news
                // solo se i primi 50 caratteri sono uguali
                if (news.get(i).substring(0, 49).equals(gson.toJson(pon).substring(0, 49))) {
                    // Se i primi 50 caratteri sono uguali, confronto il GUID
                    PieceOfNews cloudPonObj = gson.fromJson(news.get(i), PieceOfNews.class);
                    if (cloudPonObj.equals(pon)) {
                        news.remove(i);  // Se il GUID coincide, rimuovo la news
                    }
                }
            }

            FbDatabase.FbDatabase().getUserReference().child("news").setValue(news);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkSavedPieceOfNews(PieceOfNews localPon){
        Gson gson = new Gson();
        String ponToString = gson.toJson(localPon);

        for(String cloudPon : news){
            // Per evitare uno spreco computazionale, eseguo comparazione news
            // solo se i primi 50 caratteri sono uguali
            if(cloudPon.substring(0,49).equals(ponToString.substring(0,49))) {
                // Se i primi 50 caratteri sono uguali, confronto il GUID
                PieceOfNews cloudPonObj = gson.fromJson(cloudPon, PieceOfNews.class);
                if(cloudPonObj.equals(localPon)) {
                    return true;
                }
            }
        }

        return false;
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