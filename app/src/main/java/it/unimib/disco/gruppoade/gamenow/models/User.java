package it.unimib.disco.gruppoade.gamenow.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {

    private static final String TAG = "User";

    private String username;
    private String email;
    private List<String> tags;
    //private List<PieceOfNews> savedNews;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        tags = new ArrayList<>();
        //savedNews = new ArrayList<>();
        //savedNews.add(new PieceOfNews("titolo", "desc", "link", LocalDateTime.now(), "img", "guid", new NewsProvider("PS4", "nome", "https://google.com", "rssUrl")));
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

//    public List<PieceOfNews> getSavedNews() {
//        return savedNews;
//    }

//    public void savePieceOfNews(PieceOfNews pon){
//        savedNews.add(pon);
//        List<String> preparedNews = prepareNewsForSet(getSavedNews());
//        FbDatabase.FbDatabase().getUserReference().child("news").setValue(preparedNews);
//    }
//
//    public void removeSavedPieceOfNews(PieceOfNews pon){
//        savedNews.remove(pon);
//        List<String> preparedNews = prepareNewsForSet(getSavedNews());
//        FbDatabase.FbDatabase().getUserReference().child("news").setValue(preparedNews);
//    }
//
//    public List<String> prepareNewsForSet(List<PieceOfNews> tmpNews){
//        List<String> preparedNews = new ArrayList<>();
//
//        for(PieceOfNews tmpPieceOfNews : tmpNews){
//            String temp = "";
//            temp.concat(tmpPieceOfNews.getTitle() + "@@@");
//            temp.concat(tmpPieceOfNews.getDesc() + "@@@");
//            temp.concat(tmpPieceOfNews.getLink() + "@@@");
//            temp.concat(tmpPieceOfNews.getPubDate() + "@@@");
//            temp.concat(tmpPieceOfNews.getImage() + "@@@");
//            temp.concat(tmpPieceOfNews.getGuid() + "@@@");
//            temp.concat(tmpPieceOfNews.getProvider().getPlatform() + "@@@");
//            temp.concat(tmpPieceOfNews.getProvider().getName() + "@@@");
//            temp.concat(tmpPieceOfNews.getProvider().getHomepageUrl() + "@@@");
//            temp.concat(tmpPieceOfNews.getProvider().getRssUrl().toString());
//            preparedNews.add(temp);
//        }
//
//        return preparedNews;
//    }

    public void addTag(String tag) {
        if (tags != null)
            tags.add(tag.toUpperCase());
        else
            tags = new ArrayList<>();
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