package it.unimib.disco.gruppoade.gamenow;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.models.Platform;
import it.unimib.disco.gruppoade.gamenow.models.Video;
import it.unimib.disco.gruppoade.gamenow.ui.comingsoon.ConsoleAdapter;
import it.unimib.disco.gruppoade.gamenow.ui.comingsoon.VideoAdapter;


public class GameInfoActivity extends AppCompatActivity {

    private static final String TAG = "GameInfoActivity";

    private TextView gameDescription, gameTitle, gameStoryline;
    private TextView  gameDescriptionText, gameStorylineText, gameVideoText;
    private ImageView gameCover;
    private RecyclerView platformsRecycler;
    private RecyclerView videosRecycler;
    private View descDivider, storylineDivider, videoDivider;
    private RatingBar ratingBar;

    private ViewGroup views;

    private List<Platform> mPlatforms;
    private List<Video> mVideos;

    private  String url;
    private Gson gson = new Gson();

    // Create an English-Italian translator:
    private FirebaseTranslatorOptions options =
            new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    .setTargetLanguage(FirebaseTranslateLanguage.IT)
                    .build();
    private FirebaseTranslator enItTranslator =
            FirebaseNaturalLanguage.getInstance().getTranslator(options);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);

        gameDescription = findViewById(R.id.gameinfo_desc);
        gameDescriptionText = findViewById(R.id.gameinfo_desc_text);
        descDivider = findViewById(R.id.gameinfo_desc_divider);

        gameTitle = findViewById(R.id.gameinfo_title);

        ratingBar = findViewById(R.id.gameinfo_rating);

        storylineDivider = findViewById(R.id.gameinfo_storyline_divider);
        gameStoryline = findViewById(R.id.gameinfo_storyline);
        gameStorylineText = findViewById(R.id.gameinfo_storyline_text);

        gameVideoText = findViewById(R.id.gameinfo_gameplays);
        videoDivider = findViewById(R.id.gameinfo_gameplays_divider);

        gameCover = findViewById(R.id.gameinfo_cover);
        platformsRecycler = findViewById(R.id.gameinfo_recyclerview);
        videosRecycler = findViewById(R.id.gameplays_recyclerview);

        views = findViewById(R.id.gameinfo_parent);

        Log.d(TAG, "onCreate: View Group " + views);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        enItTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Log.d(TAG, "onSuccess: Downloaded Model");

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });


        Intent intent = getIntent();
        if( intent.getStringExtra("desc") != null) {
            final String desc = intent.getStringExtra("desc");
            translate(desc, gameDescriptionText);

        } else {
            gameDescription.setVisibility(View.GONE);
            descDivider.setVisibility(View.GONE);
            gameDescriptionText.setVisibility(View.GONE);
        }

        if(intent.getStringExtra("rating") == null){
            ratingBar.setVisibility(View.GONE);
        } else {
            ratingBar.setRating((float)setRating(Float.valueOf(intent.getStringExtra("rating"))));
        }

        gameTitle.setText(intent.getStringExtra("title"));
        String storyline = intent.getStringExtra("storyline");
        Log.d(TAG, "onCreate: Storyline = " + storyline);
        if( storyline != null) {
            translate(storyline, gameStorylineText);

        } else {
            storylineDivider.setVisibility(View.GONE);
            gameStoryline.setVisibility(View.GONE);
            gameStorylineText.setVisibility(View.GONE);
        }

        mPlatforms = intent.getParcelableArrayListExtra("platforms");
        mVideos = intent.getParcelableArrayListExtra("videos");
        Log.d(TAG, "onCreate: Platforms = " + gson.toJson(mPlatforms));

        url = intent.getStringExtra("imageUrl");
        Log.d(TAG, "onCreate: URl + " + url);
        if (!url.isEmpty()) {
            Picasso.with(this).load(url).placeholder(R.drawable.img).into(gameCover);
        } else {
            gameCover.setImageResource(R.drawable.cover_na);

        }

        initPlatformsRecyclerView();
        if(mVideos == null){
            gameVideoText.setVisibility(View.GONE);
            videoDivider.setVisibility(View.GONE);
        } else
            initVideosRecyclerView();

    }

    private void initPlatformsRecyclerView() {
        Log.d(TAG, "initRecyclerView: Init Platforms RecyclerView");
        ConsoleAdapter consoleAdapter = new ConsoleAdapter(mPlatforms,this);
        platformsRecycler.setAdapter(consoleAdapter);
        platformsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

    }

    private void initVideosRecyclerView() {
        Log.d(TAG, "initRecyclerView: Init Videos RecyclerView");
        VideoAdapter videoAdapter = new VideoAdapter(mVideos,this.getLifecycle(), this, this, views);
        videosRecycler.setAdapter(videoAdapter);
        videosRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

    }

    private double setRating(double totRating){
        double rating = Math.floor(totRating * 5) / 100;
        double significance = 0.5;
        return ((int)(rating/significance) * significance) + significance;
    }

    private void translate (String textToTranslate, final TextView v){
        enItTranslator.translate(textToTranslate)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                v.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
    }

}
