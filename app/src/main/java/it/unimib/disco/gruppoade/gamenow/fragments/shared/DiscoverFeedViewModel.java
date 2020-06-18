package it.unimib.disco.gruppoade.gamenow.fragments.shared;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.repositories.NewsRepository;

public class DiscoverFeedViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<PieceOfNews>> news;
    private boolean usingFeed;
    private static final String TAG = "DiscoverFeedViewModel";

    public DiscoverFeedViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<PieceOfNews>> getNews(User user) {
        if (news == null) {
            news = new MutableLiveData<>();
        }
        NewsRepository.getInstance(getApplication().getResources()).getNews(news, user, usingFeed);
        return news;
    }

    public void cleanNews(){
        news = null;
    }

    public void setFeedUse(boolean usingFeed){
        this.usingFeed=usingFeed;
    }
}