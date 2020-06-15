package it.unimib.disco.gruppoade.gamenow.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;


import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.activities.FullScreenActivity;
import it.unimib.disco.gruppoade.gamenow.models.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private static final String TAG = "VideoAdapter";

    private List<Video> mVideos;
    private Lifecycle mLifecycle;
    private Activity mContext;


    public VideoAdapter(List<Video> mVideos, Lifecycle mLifecycle, Activity mContext) {
        this.mVideos = mVideos;
        this.mLifecycle = mLifecycle;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_videos, parent, false);
        mLifecycle.addObserver(youTubePlayerView);

        return new ViewHolder(youTubePlayerView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.cueVideo(mVideos.get(position).getVideoId());
        holder.youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                holder.youTubePlayer.pause();
                Intent intent = new Intent(mContext, FullScreenActivity.class);
                intent.putExtra("currentSecond", holder.tracker.getCurrentSecond());
                intent.putExtra("videoId", holder.tracker.getVideoId());
                mContext.startActivity(intent);
                holder.youTubePlayerView.enterFullScreen();
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                holder.youTubePlayerView.exitFullScreen();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(!(mVideos == null))
            return mVideos.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private YouTubePlayerView youTubePlayerView;
        private YouTubePlayer youTubePlayer;
        private YouTubePlayerTracker tracker;
        private String currentVideoId;

        ViewHolder(YouTubePlayerView playerView) {
            super(playerView);
            youTubePlayerView = playerView;
            tracker = new YouTubePlayerTracker();
            PlayerUiController pc = youTubePlayerView.getPlayerUiController();
            pc.showFullscreenButton(false);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayer.addListener(tracker);
                    youTubePlayer.cueVideo(currentVideoId, 0);
                    pc.showFullscreenButton(true);
                }
            });
        }

        void cueVideo(String videoId) {
            currentVideoId = videoId;

            if(youTubePlayer == null)
                return;

            youTubePlayer.cueVideo(videoId, 0);
        }
    }

}


