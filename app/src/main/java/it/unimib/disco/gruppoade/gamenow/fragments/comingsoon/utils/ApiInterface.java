package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.models.Game;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {


    @Headers({
            "Accept: application/json",
            "user-key: 69d2df5c33778dcbc96be55072265e7d",
            "Content-Type: text/plain"
    })
    @POST("games")
    Call<List<Game>> getGames(
            @Body String body
    );
}
