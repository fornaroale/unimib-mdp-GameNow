package it.unimib.disco.gruppoade.gamenow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Platform;
import it.unimib.disco.gruppoade.gamenow.ui.comingsoon.ConsoleAdapter;


public class GameInfoActivity extends AppCompatActivity {

    private static final String TAG = "GameInfoActivity";

    private TextView gameDescription, gameTitle, gameStoryline;
    private TextView  gameDescriptionText, gameStorylineText;
    private ImageView gameCover;
    private RecyclerView platformsRecycler;
    private View descDivider, storylineDivider;

    private List<Platform> mPlatforms;

    private  String url;
    private Gson gson = new Gson();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);

        gameDescription = findViewById(R.id.gameinfo_desc);
        gameDescriptionText = findViewById(R.id.gameinfo_desc_text);
        descDivider = findViewById(R.id.gameinfo_desc_divider);

        gameTitle = findViewById(R.id.gameinfo_title);

        storylineDivider = findViewById(R.id.gameinfo_storyline_divider);
        gameStoryline = findViewById(R.id.gameinfo_storyline);
        gameStorylineText = findViewById(R.id.gameinfo_storyline_text);

        gameCover = findViewById(R.id.gameinfo_cover);
        platformsRecycler = findViewById(R.id.gameinfo_recyclerview);

        Intent intent = getIntent();
        if( intent.getStringExtra("desc") != null) {
            gameDescriptionText.setText(intent.getStringExtra("desc"));
        } else {
            gameDescription.setVisibility(View.GONE);
            descDivider.setVisibility(View.GONE);
            gameDescriptionText.setVisibility(View.GONE);
        }

        gameTitle.setText(intent.getStringExtra("title"));
        String storyline = intent.getStringExtra("storyline");
        Log.d(TAG, "onCreate: Storyline = " + storyline);
        if( storyline != null) {
            gameStorylineText.setText(intent.getStringExtra("storyline"));
        } else {
            storylineDivider.setVisibility(View.GONE);
            gameStoryline.setVisibility(View.GONE);
            gameStorylineText.setVisibility(View.GONE);
        }

        mPlatforms = intent.getParcelableArrayListExtra("platforms");
        Log.d(TAG, "onCreate: Platforms = " + gson.toJson(mPlatforms));

        url = intent.getStringExtra("imageUrl");
        Log.d(TAG, "onCreate: URl + " + url);
        if (!url.isEmpty()) {
            Picasso.with(this).load(url).placeholder(R.drawable.img).into(gameCover);
        } else {
            gameCover.setImageResource(R.drawable.cover_na);

        }

        initPlatformsRecyclerView();

    }

    private void initPlatformsRecyclerView() {
        Log.d(TAG, "initRecyclerView: Init Platforms RecyclerView");
        ConsoleAdapter consoleAdapter = new ConsoleAdapter(mPlatforms,this);
        platformsRecycler.setAdapter(consoleAdapter);
        platformsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

    }
}
