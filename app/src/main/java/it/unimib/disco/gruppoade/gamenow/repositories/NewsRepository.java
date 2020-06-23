package it.unimib.disco.gruppoade.gamenow.repositories;

import android.content.res.Resources;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.repositories.utils.RssDownloader;

public class NewsRepository {

    private static NewsRepository instance;
    private static Resources resources;

    private NewsRepository(Resources resources) {
        NewsRepository.resources = resources;
    }

    public static synchronized NewsRepository getInstance(Resources resources) {
        if (instance == null) {
            instance = new NewsRepository(resources);
        }
        return instance;
    }

    public void getNews(MutableLiveData<ArrayList<PieceOfNews>> news){
        RssDownloader runnable = new RssDownloader(resources, news);
        new Thread(runnable).start();
    }
}