package it.unimib.disco.gruppoade.gamenow.ui.comingsoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;


import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.FullscreenActivity;
import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Video;
import it.unimib.disco.gruppoade.gamenow.utils.FullscreenHelper;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private static final String TAG = "VideoAdapter";

    private List<Video> mVideos;
    private Lifecycle mLifecycle;
    private Context mContext;
    private Activity activity;
    private ViewGroup viewGroup;

    public VideoAdapter(List<Video> mVideos, Lifecycle mLifecycle, Context mContext, Activity activity, ViewGroup viewGroup) {
        this.mVideos = mVideos;
        this.mLifecycle = mLifecycle;
        this.mContext = mContext;
        this.activity = activity;
        this.viewGroup = (ViewGroup) viewGroup.getChildAt(0);
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
        final Intent intent = new Intent(mContext, FullscreenActivity.class);
        intent.putExtra("videoID", mVideos.get(position).getVideoId());

        final RecyclerView.LayoutParams rp = (RecyclerView.LayoutParams) holder.youTubePlayerView.getLayoutParams();
        final RecyclerView recyclerView = activity.findViewById(R.id.gameplays_recyclerview);
        final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();

        holder.youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                holder.fullscreenHelper.enterFullScreen();
                rp.setMargins(0,0,0,0);
                holder.youTubePlayerView.setLayoutParams(rp);
                lp.setMargins(0,0,0,0);
                recyclerView.setLayoutParams(lp);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                holder.fullscreenHelper.exitFullScreen();
                rp.setMargins(8,8,8,24);
                holder.youTubePlayerView.setLayoutParams(rp);
                lp.setMargins(8,8,8,24);
                recyclerView.setLayoutParams(lp);
            }
        });

//        holder.playerUiController.setFullScreenButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.startActivity(intent);
//                holder.youTubePlayerView.toggleFullScreen();
//            }
//        });
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
        private String currentVideoId;
        private PlayerUiController playerUiController;
        private FullscreenHelper fullscreenHelper;
        private View[] views;


        ViewHolder(YouTubePlayerView playerView) {
            super(playerView);
            youTubePlayerView = playerView;
            /*for (int i = 0; i<viewGroup.getChildCount(); i++){
                views[i] = viewGroup.getChildAt(i);
            }*/
            fullscreenHelper = new FullscreenHelper(activity, viewGroup.getChildAt(0));
            playerUiController = youTubePlayerView.getPlayerUiController();
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayer.cueVideo(currentVideoId, 0);
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


