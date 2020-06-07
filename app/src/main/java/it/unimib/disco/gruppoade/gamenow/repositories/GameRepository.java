package it.unimib.disco.gruppoade.gamenow.repositories;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.ComingSoonFragment;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.ApiClient;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.ApiInterface;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

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
        final Gson gson = new Gson();

        Call<List<Game>> call = apiInterface.getGames(body);
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if(response.isSuccessful() && response.body() != null){
                    games.postValue(null);
                    games.postValue(response.body());
                    Log.d(TAG, "onResponse: Response Body = "+ gson.toJson(games));
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
