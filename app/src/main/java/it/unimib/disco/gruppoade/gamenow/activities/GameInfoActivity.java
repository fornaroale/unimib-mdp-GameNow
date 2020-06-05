package it.unimib.disco.gruppoade.gamenow.activities;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Platform;
import it.unimib.disco.gruppoade.gamenow.models.Video;
import it.unimib.disco.gruppoade.gamenow.adapters.ConsoleAdapter;
import it.unimib.disco.gruppoade.gamenow.adapters.VideoAdapter;


public class GameInfoActivity extends AppCompatActivity {

    private static final String TAG = "GameInfoActivity";

    private TextView gameDescription, gameTitle, gameStoryline;
    private TextView  gameDescriptionText, gameStorylineText, gameVideoText;
    private ImageView gameCover, gameScreen;
    private RecyclerView platformsRecycler;
    private RecyclerView videosRecycler;
    private View descDivider, storylineDivider, videoDivider;
    private RatingBar ratingBar;
    private ProgressBar descSpinner, storylineSpinner;
    private Palette.Swatch vibrantSwatch, mutedSwatch;

    private List<Platform> mPlatforms;
    private List<Video> mVideos;

    private FirebaseTranslator translator = new MainActivity().enItTranslator;

    private  String url;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);

        gameDescription = findViewById(R.id.gameinfo_desc);
        gameDescriptionText = findViewById(R.id.gameinfo_desc_text);
        descDivider = findViewById(R.id.gameinfo_desc_divider);
        descSpinner = findViewById(R.id.gameinfo_desc_spinner);

        gameTitle = findViewById(R.id.gameinfo_title);
        gameScreen = findViewById(R.id.gameinfo_screen);

        ratingBar = findViewById(R.id.gameinfo_rating);

        storylineDivider = findViewById(R.id.gameinfo_storyline_divider);
        gameStoryline = findViewById(R.id.gameinfo_storyline);
        gameStorylineText = findViewById(R.id.gameinfo_storyline_text);
        storylineSpinner = findViewById(R.id.gameinfo_storyline_spinner);

        gameVideoText = findViewById(R.id.gameinfo_gameplays);
        videoDivider = findViewById(R.id.gameinfo_gameplays_divider);

        gameCover = findViewById(R.id.gameinfo_cover);
        platformsRecycler = findViewById(R.id.gameinfo_recyclerview);
        videosRecycler = findViewById(R.id.gameplays_recyclerview);



        Intent intent = getIntent();
        if( intent.getStringExtra("desc") != null) {
            final String desc = intent.getStringExtra("desc");
            translate(desc, gameDescriptionText,descSpinner);

        } else {
            gameDescription.setVisibility(View.GONE);
            descDivider.setVisibility(View.GONE);
            gameDescriptionText.setVisibility(View.GONE);
            descSpinner.setVisibility(View.GONE);
        }
        if(intent.getDoubleExtra("rating", 0) == 0){
            ratingBar.setVisibility(View.GONE);
        } else {
            ratingBar.setRating(setRating(intent.getDoubleExtra("rating", 0)));
        }

        gameTitle.setText(intent.getStringExtra("title"));
        String storyline = intent.getStringExtra("storyline");
        Log.d(TAG, "onCreate: Storyline = " + storyline);
        if( storyline != null) {
            translate(storyline, gameStorylineText, storylineSpinner);

        } else {
            storylineDivider.setVisibility(View.GONE);
            gameStoryline.setVisibility(View.GONE);
            gameStorylineText.setVisibility(View.GONE);
            storylineSpinner.setVisibility(View.GONE);
        }

        mPlatforms = intent.getParcelableArrayListExtra("platforms");
        mVideos = intent.getParcelableArrayListExtra("videos");

        if (mVideos == null){
            gameVideoText.setVisibility(View.GONE);
            videoDivider.setVisibility(View.GONE);
        }
        Log.d(TAG, "onCreate: Platforms = " + gson.toJson(mPlatforms));

        url = intent.getStringExtra("imageUrl");
        Log.d(TAG, "onCreate: URl + " + url);
        if (!url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.img)
                    .into(gameCover);
        } else {
            gameCover.setImageResource(R.drawable.cover_na);

        }

        Bitmap bitmap = ((BitmapDrawable) gameCover.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                vibrantSwatch = palette.getVibrantSwatch();
                mutedSwatch = palette.getMutedSwatch();
                if(vibrantSwatch != null)
                    gameScreen.setBackgroundColor(vibrantSwatch.getRgb());
                else
                    gameScreen.setBackgroundColor(mutedSwatch.getRgb());

            }
        });

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
        if(mPlatforms.size() > 4)
            platformsRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        else
            platformsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

    }

    private void initVideosRecyclerView() {
        Log.d(TAG, "initRecyclerView: Init Videos RecyclerView");
        VideoAdapter videoAdapter = new VideoAdapter(mVideos,this.getLifecycle());
        videosRecycler.setAdapter(videoAdapter);
        videosRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

    }

    private float setRating(double totRating){
        double rating = Math.floor(totRating * 5) / 100;
        double significance = 0.5;
        return (float) ((float)((int)(rating/significance) * significance) + significance);
    }

    private void translate (String textToTranslate, final TextView v, final View spinner){
        translator.translate(textToTranslate)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                v.setText(translatedText);
                                spinner.setVisibility(View.GONE);
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
