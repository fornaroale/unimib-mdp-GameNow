package it.unimib.disco.gruppoade.gamenow.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;


import java.util.List;

import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.ApiClient;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.ApiInterface;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.Constants;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameRepository {
    //Classe Singleton

    private static final String TAG = "GameRepository";

    private static GameRepository instance;
    private ApiInterface apiInterface;

    private GameRepository(){

        apiInterface = ApiClient.getInstance().getApi();
    }

    public static synchronized GameRepository getInstance(){
        if (instance == null)
            instance = new GameRepository();
        return instance;
    }

    public void getGames(final MutableLiveData<List<Game>> games, String body){

        Call<List<Game>> call = apiInterface.getGames(body);
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(games.getValue() != null) {
                        List<Game> currentGameList = games.getValue();
                        currentGameList.remove(currentGameList.size() - 1);
                        currentGameList.addAll(response.body());
                        games.postValue(currentGameList);
                    } else
                        games.postValue(response.body());
                    Constants.loadingSentinel = false;
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
               // Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: Error " + t.getLocalizedMessage());
            }
        });
    }

}
