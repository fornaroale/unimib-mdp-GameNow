package it.unimib.disco.gruppoade.gamenow.fragments.feed;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.auth.data.model.Resource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.activities.SignUpActivity;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.NewsProvider;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.repositories.NewsRepository;

public class FeedViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<PieceOfNews>> news;
    private User user;
    private boolean usingFeed;
    private static final String TAG = "FeedViewModel";

    public FeedViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<PieceOfNews>> getNews() {
        if (news == null) {
            news = new MutableLiveData<>();
            NewsRepository.getInstance(getApplication().getResources()).getNews(news, user, usingFeed);
        }
        return news;
    }

    public void setFeedUse(boolean usingFeed){
        this.usingFeed=usingFeed;
    }

    public void setUser(User user){
        this.user=user;
    }
}