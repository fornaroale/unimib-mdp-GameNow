package it.unimib.disco.gruppoade.gamenow.repositories;

import android.content.res.Resources;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.repositories.utils.RssDownloader;

public class NewsRepository {

    private static NewsRepository instance;
    private static Resources resources;

    private NewsRepository(Resources resources) {
        this.resources = resources;
    }

    public static synchronized NewsRepository getInstance(Resources resources) {
        if (instance == null) {
            instance = new NewsRepository(resources);
        }
        return instance;
    }

    public void getNews(MutableLiveData<ArrayList<PieceOfNews>> news, User user, boolean usingFeed){
        RssDownloader runnable = new RssDownloader(resources, news, user, usingFeed);
        new Thread(runnable).start();
    }
}