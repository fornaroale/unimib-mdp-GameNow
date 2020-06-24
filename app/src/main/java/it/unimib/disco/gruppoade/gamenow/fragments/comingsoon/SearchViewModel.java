package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.repositories.GameRepository;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<List<Game>> mGames;

    public LiveData<List<Game>> getGames(String body) {
        if(mGames == null){
            mGames = new MutableLiveData<>();
            GameRepository.getInstance().getGames(mGames, body);
        }
        return mGames;
    }

    public void resetGames() {
        mGames = null;
    }


    @NotNull
    @Override
    public String toString() {
        return "ComingSoonViewModel{" +
                "mGames=" + mGames +
                '}';
    }
}