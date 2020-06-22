package it.unimib.disco.gruppoade.gamenow.fragments.shared;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.repositories.NewsRepository;

public class NewsViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<PieceOfNews>> news;

    public NewsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<PieceOfNews>> getNews() {
        if (news == null) {
            news = new MutableLiveData<>();
            NewsRepository.getInstance(getApplication().getResources()).getNews(news);
        }

        return news;
    }

    public void refreshNews(){
        NewsRepository.getInstance(getApplication().getResources()).getNews(news);
    }
}