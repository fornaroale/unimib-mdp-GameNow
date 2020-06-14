package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.activities.MainActivity;
import it.unimib.disco.gruppoade.gamenow.adapters.ConsoleAdapter;
import it.unimib.disco.gruppoade.gamenow.adapters.VideoAdapter;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.Platform;
import it.unimib.disco.gruppoade.gamenow.models.Video;


public class GameInfoFragment extends Fragment {

    private static final String TAG = "GameInfoFragment";
    private String url;

    private FirebaseTranslatorOptions options;
    private FirebaseTranslator translator;

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

    public static GameInfoFragment newInstance(String param1, String param2) {
        GameInfoFragment fragment = new GameInfoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Game game = GameInfoFragmentArgs.fromBundle(getArguments()).getGame();

        // Crea un traduttore English-Italiano
        options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.IT)
                .build();
        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        gameDescription = view.findViewById(R.id.gameinfo_desc);
        gameDescriptionText = view.findViewById(R.id.gameinfo_desc_text);
        descDivider = view.findViewById(R.id.gameinfo_desc_divider);
        descSpinner = view.findViewById(R.id.gameinfo_desc_spinner);

        gameTitle = view.findViewById(R.id.gameinfo_title);
        gameScreen = view.findViewById(R.id.gameinfo_screen);

        ratingBar = view.findViewById(R.id.gameinfo_rating);

        storylineDivider = view.findViewById(R.id.gameinfo_storyline_divider);
        gameStoryline = view.findViewById(R.id.gameinfo_storyline);
        gameStorylineText = view.findViewById(R.id.gameinfo_storyline_text);
        storylineSpinner = view.findViewById(R.id.gameinfo_storyline_spinner);

        gameVideoText = view.findViewById(R.id.gameinfo_gameplays);
        videoDivider = view.findViewById(R.id.gameinfo_gameplays_divider);

        gameCover = view.findViewById(R.id.gameinfo_cover);
        platformsRecycler = view.findViewById(R.id.gameinfo_recyclerview);
        videosRecycler = view.findViewById(R.id.gameplays_recyclerview);

        if( game.getSummary() != null) {
            final String desc = game.getSummary();
            translate(desc, gameDescriptionText,descSpinner);

        } else {
            gameDescription.setVisibility(View.GONE);
            descDivider.setVisibility(View.GONE);
            gameDescriptionText.setVisibility(View.GONE);
            descSpinner.setVisibility(View.GONE);
        }
        if(game.getRating() == 0){
            ratingBar.setVisibility(View.GONE);
        } else {
            ratingBar.setRating(setRating(game.getRating()));
        }

        gameTitle.setText(game.getName());
        String storyline = game.getStoryline();
        Log.d(TAG, "onCreate: Storyline = " + storyline);


        mPlatforms = game.getPlatforms();
        mVideos = game.getVideos();

        if (mVideos == null){
            gameVideoText.setVisibility(View.GONE);
            videoDivider.setVisibility(View.GONE);
        }
        //Log.d(TAG, "onCreate: Platforms = " + gson.toJson(mPlatforms));
        String coverBig, url;

        if(game.getCover() != null){
             coverBig = game.getCover().getUrl().replace("t_thumb", "t_cover_big");
             url = "https:" + coverBig;}
        else
            url = "";
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

        // Traduzione
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                if( game.getSummary() != null) {
                                    final String desc = game.getSummary();
                                    translate(desc, gameDescriptionText,descSpinner);

                                } else {
                                    gameDescription.setVisibility(View.GONE);
                                    descDivider.setVisibility(View.GONE);
                                    gameDescriptionText.setVisibility(View.GONE);
                                    descSpinner.setVisibility(View.GONE);
                                }
                                if( storyline != null) {
                                    translate(storyline, gameStorylineText, storylineSpinner);

                                } else {
                                    storylineDivider.setVisibility(View.GONE);
                                    gameStoryline.setVisibility(View.GONE);
                                    gameStorylineText.setVisibility(View.GONE);
                                    storylineSpinner.setVisibility(View.GONE);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                //Snackbar.make(, "Failed Downloading Model", Snackbar.LENGTH_LONG).show();
                            }
                        });
    }

    private void initPlatformsRecyclerView() {
        Log.d(TAG, "initRecyclerView: Init Platforms RecyclerView");
        ConsoleAdapter consoleAdapter = new ConsoleAdapter(mPlatforms,getActivity());
        platformsRecycler.setAdapter(consoleAdapter);
        if(mPlatforms.size() > 4)
            platformsRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        else
            platformsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));

    }

    private void initVideosRecyclerView() {
        Log.d(TAG, "initRecyclerView: Init Videos RecyclerView");
        VideoAdapter videoAdapter = new VideoAdapter(mVideos,this.getLifecycle(),getActivity());
        videosRecycler.setAdapter(videoAdapter);
        videosRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));

    }

    private float setRating(double totRating){
        double rating = Math.floor(totRating * 5) / 100;
        double significance = 0.1;
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