package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.ConsoleAdapter;
import it.unimib.disco.gruppoade.gamenow.adapters.VideoAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.Platform;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.models.Video;


public class GameInfoFragment extends Fragment {

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
    private ToggleButton toggleButton;
    private User user;

    private List<Platform> mPlatforms;
    private List<Video> mVideos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Game game = GameInfoFragmentArgs.fromBundle(requireArguments()).getGame();
        ValueEventListener postListenerUserData = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (Objects.requireNonNull(user).checkSavedGame(game))
                    toggleButton.setChecked(true);
                else
                    toggleButton.setChecked(false);
                toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (toggleButton.isPressed()) {
                        if (isChecked) {
                            if (user.saveGame(game)) {
                                Snackbar.make(buttonView, R.string.gioco_aggiunto, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.action_undo, v -> user.removeGame(game))
                                        .setAnchorView(R.id.nav_view)
                                        .show();
                            }
                        } else {
                            if (user.removeGame(game)) {
                                Snackbar.make(buttonView, R.string.gioco_rimosso, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.action_undo, v -> user.saveGame(game))
                                        .setAnchorView(R.id.nav_view)
                                        .show();
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);


        // Crea un traduttore English-Italiano

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

        toggleButton = view.findViewById(R.id.gameinfo_save_game);


        if(Locale.getDefault().getLanguage().equals("it"))
            buildIT(game);
        else
            buildEN(game);

        buildCommon(game);


    }

    private void buildEN(Game game){
        descSpinner.setVisibility(View.GONE);
        storylineSpinner.setVisibility(View.GONE);
        if( game.getSummary() != null) {
            gameDescriptionText.setText(game.getSummary());

        } else {
            gameDescription.setVisibility(View.GONE);
            descDivider.setVisibility(View.GONE);
            gameDescriptionText.setVisibility(View.GONE);
            descSpinner.setVisibility(View.GONE);
        }

        if( game.getStoryline() != null) {
            gameStorylineText.setText(game.getStoryline());

        } else {
            storylineDivider.setVisibility(View.GONE);
            gameStoryline.setVisibility(View.GONE);
            gameStorylineText.setVisibility(View.GONE);
            storylineSpinner.setVisibility(View.GONE);
        }

    }

    private void buildIT(Game game){

        if( game.getSummary() == null) {
            gameDescription.setVisibility(View.GONE);
            descDivider.setVisibility(View.GONE);
            gameDescriptionText.setVisibility(View.GONE);
            descSpinner.setVisibility(View.GONE);
        }

        if( game.getStoryline() == null) {
            storylineDivider.setVisibility(View.GONE);
            gameStoryline.setVisibility(View.GONE);
            gameStorylineText.setVisibility(View.GONE);
            storylineSpinner.setVisibility(View.GONE);
        }

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.IT)
                .build();
        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        String storyline = game.getStoryline();

        // Traduzione
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        v -> {
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
                        })
                .addOnFailureListener(
                        e -> {
                            e.printStackTrace();
                            Snackbar.make(requireView(), "Failed Downloading Model", Snackbar.LENGTH_LONG)
                                    .setAnchorView(R.id.nav_view)
                                    .show();
                        });

    }

    private void buildCommon(Game game){
        gameTitle.setText(game.getName());

        if(game.getRating() == 0){
            ratingBar.setVisibility(View.GONE);
        } else {
            ratingBar.setRating(setRating(game.getRating()));
        }

        mPlatforms = game.getPlatforms();
        mVideos = game.getVideos();

        if (mVideos == null){
            gameVideoText.setVisibility(View.GONE);
            videoDivider.setVisibility(View.GONE);
        }

        String coverBig, url;

        if(game.getCover() != null){
            coverBig = game.getCover().getUrl().replace("t_thumb", "t_cover_big");
            url = "https:" + coverBig;}
        else
            url = "";
        if (!url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.img)
                    .into(gameCover);
        } else {
            gameCover.setImageResource(R.drawable.cover_na);

        }

        Bitmap bitmap = ((BitmapDrawable) gameCover.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(palette -> {
            vibrantSwatch = Objects.requireNonNull(palette).getVibrantSwatch();
            mutedSwatch = palette.getMutedSwatch();
            if(vibrantSwatch != null)
                gameScreen.setBackgroundColor(vibrantSwatch.getRgb());
            else if (mutedSwatch != null)
                gameScreen.setBackgroundColor(mutedSwatch.getRgb());
        });
        if(mPlatforms!= null)
            initPlatformsRecyclerView();
        if(mVideos == null){
            gameVideoText.setVisibility(View.GONE);
            videoDivider.setVisibility(View.GONE);
        } else
            initVideosRecyclerView();
    }

    private void initPlatformsRecyclerView() {
        ConsoleAdapter consoleAdapter = new ConsoleAdapter(mPlatforms);
        platformsRecycler.setAdapter(consoleAdapter);
        if(mPlatforms.size() > 4)
            platformsRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        else
            platformsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));

    }

    private void initVideosRecyclerView() {
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
                        translatedText -> {
                            v.setText(translatedText);
                            spinner.setVisibility(View.GONE);
                        })
                .addOnFailureListener(
                        e -> {
                            v.setText(R.string.errore_traduzione);
                            e.printStackTrace();
                        });
    }



}