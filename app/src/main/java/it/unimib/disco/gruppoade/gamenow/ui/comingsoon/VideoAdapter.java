package it.unimib.disco.gruppoade.gamenow.ui.comingsoon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;


import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private static final String TAG = "VideoAdapter";

    private List<Video> mVideos;
    private Lifecycle mLifecycle;

    public VideoAdapter(List<Video> mVideos, Lifecycle mLifecycle) {
        this.mVideos = mVideos;
        this.mLifecycle = mLifecycle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_videos, parent, false);
        VideoAdapter.ViewHolder holder = new VideoAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
       holder.webView.loadUrl("https://www.youtube.com/embed/"+mVideos.get(position).getVideoId());
    }

    @Override
    public int getItemCount() {
        if(!(mVideos == null))
            return mVideos.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        WebView webView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.youtube_player);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setJavaScriptEnabled(true);
        }
    }
}

