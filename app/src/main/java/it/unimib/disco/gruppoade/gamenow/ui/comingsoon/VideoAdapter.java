package it.unimib.disco.gruppoade.gamenow.ui.comingsoon;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;


import java.util.List;
import java.util.Map;

import it.unimib.disco.gruppoade.gamenow.FullscreenActivity;
import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private static final String TAG = "VideoAdapter";

    private List<Video> mVideos;
    private Context mContext;
    private Gson gson = new Gson();

    public VideoAdapter(List<Video> mVideos, Context mContext) {
        this.mVideos = mVideos;
        this.mContext = mContext;
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
        //holder.webView.loadUrl("https://www.youtube.com/embed/"+mVideos.get(position).getVideoId());
        String url = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+ mVideos.get(position).getVideoId() +"\" frameborder=\"0\" allowfullscreen></iframe>";
        holder.webView.loadData(url, "text/html", "utf-8");
        final Intent intent = new Intent(mContext, FullscreenActivity.class);
        intent.putExtra("url", url);
        holder.fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mContext.startActivity(intent);
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

        WebView webView;
        View fullscreen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullscreen = itemView.findViewById(R.id.webview_fullscreen);
            webView = itemView.findViewById(R.id.youtube_player);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setJavaScriptEnabled(true);
        }
    }
}


