package it.unimib.disco.gruppoade.gamenow.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.ApiClient;
import it.unimib.disco.gruppoade.gamenow.adapters.IncomingAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private List<Game> mGames = new ArrayList<>();
    private LottieAnimationView lottieAnimationView;
    private RecyclerView recyclerView;
    private String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.search_recyclerview);
        lottieAnimationView = findViewById(R.id.search_animation_view);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        Log.d(TAG, "onCreate: " + query);
        body = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                "search \"" + query.toLowerCase() + "\";\n" +
                "limit 75;";
        retrieveJson(body);
    }


    private void retrieveJson(String body){

        final Gson gson = new Gson();

        Call<List<Game>> call = ApiClient.getInstance().getApi().getGames(body);
        call.enqueue(new Callback<List<Game>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if(response.body() != null){
                    mGames.clear();
                    mGames = response.body();
                    initRecyclerView();
                    lottieAnimationView.setVisibility(GONE);
                }
                if (response.body().isEmpty()){
                    CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator);
                    Snackbar snackbar =  Snackbar.make( coordinatorLayout, "Nessun gioco trovato...", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();
                   }

            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Snackbar.make(recyclerView.getRootView(), t.getMessage(), Snackbar.LENGTH_LONG);
            }
        });

    }
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: Init RecyclerView");
         Collections.sort(mGames, new Comparator<Game>() {
                        @Override
                        public int compare(Game o1, Game o2) {
                            if(o1.getDate() != null && o2.getDate() != null)
                                return Long.valueOf(o2.getDate()).compareTo(Long.valueOf(o1.getDate()));
                            if(o1.getDate() == null && o2.getDate() == null)
                                return 0;
                            if(o1.getDate() == null)
                                return 1;
                            return -1;
                        }
                    });
        IncomingAdapter incomingAdapter = new IncomingAdapter(getApplicationContext(), mGames);
        recyclerView.setAdapter(incomingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

}