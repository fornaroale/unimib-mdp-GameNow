package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.repositories.GameRepository;

public class ComingSoonViewModel extends ViewModel {

    private MutableLiveData<List<Game>> mGames;
    private int offset = 0;
    private boolean isLoading;


    public LiveData<List<Game>> getGames(String body) {
        if(mGames == null){
            mGames = new MutableLiveData<>();
            GameRepository.getInstance().getGames(mGames, body);
        }
        return mGames;
    }

    public MutableLiveData<List<Game>> getmGamesLiveData(){
        return mGames;
    }

    public void getMoreGames(String body) {
        GameRepository.getInstance().getGames(mGames, body);
    }

    public LiveData<List<Game>> changeConsole(String body) {
        GameRepository.getInstance().getGames(mGames, body);
        return mGames;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @NotNull
    @Override
    public String toString() {
        return "ComingSoonViewModel{" +
                "mGames=" + mGames +
                ", offset=" + offset +
                ", isLoading=" + isLoading +
                '}';
    }
}