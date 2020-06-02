package it.unimib.disco.gruppoade.gamenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class FullscreenActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private String currentVideoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        youTubePlayerView = findViewById(R.id.player_fullscreen);
        getLifecycle().addObserver(youTubePlayerView);
        Intent intent = getIntent();
        currentVideoId = intent.getStringExtra("videoID");
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(currentVideoId,0);
            }
        });
    }
}

