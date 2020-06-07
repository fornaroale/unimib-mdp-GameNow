package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.repositories.GameRepository;

public class ComingSoonViewModel extends ViewModel {

    private static final String TAG = "ComingSoonViewModel";

    private MutableLiveData<List<Game>> mGames;

    public LiveData<List<Game>> getGames(String body) {
        if(mGames == null){
            mGames = new MutableLiveData<>();
            GameRepository.getInstance().getGames(mGames, body);
        }
        return mGames;
    }
}