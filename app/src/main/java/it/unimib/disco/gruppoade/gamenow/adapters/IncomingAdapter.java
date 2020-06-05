package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.activities.GameInfoActivity;
import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.Platform;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class IncomingAdapter extends RecyclerView.Adapter<IncomingAdapter.ViewHolder>{

    private static final String TAG = "Adapter";

    final Gson gson = new Gson();

    private Context mContext;
    private List<Game> mGames;

    public IncomingAdapter(Context mContext, List<Game> mResults) {
        this.mContext = mContext;
        this.mGames = mResults;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carditem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Game game = mGames.get(position);
        final Intent intent = new Intent(mContext, GameInfoActivity.class);
        if (game.getDate() != null) {
            holder.itemTitle.setText(constructTitle(game.getName(), game.getDate()));
        } else {
            holder.itemTitle.setText(game.getName());
        }

        final String coverBig, url;
        Log.d(TAG, "onBindViewHolder: Game = " + gson.toJson(game));

        if(game.getCover() != null) {
            coverBig = game.getCover().getUrl().replace("t_thumb", "t_cover_big");
            url = "https:" + coverBig;
            Picasso.with(mContext).load(url).into(holder.imageView);

        } else {
            holder.imageView.setImageResource(R.drawable.cover_na);
            url = "";
        }

        //Console Recycler
        final List<Platform> mPlatforms;
        mPlatforms = mGames.get(position).getPlatforms();
        ConsoleAdapter consoleAdapter = new ConsoleAdapter(mPlatforms,mContext);
        RecyclerView recyclerView = holder.recyclerView;
        recyclerView.setAdapter(consoleAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //extras
                intent.putExtra("desc", game.getSummary());
                intent.putExtra("title", game.getName());
                intent.putExtra("imageUrl",url);
                intent.putParcelableArrayListExtra("platforms", (ArrayList<? extends Parcelable>) game.getPlatforms());
                intent.putParcelableArrayListExtra("videos", (ArrayList<? extends Parcelable>) game.getVideos());
                intent.putExtra("storyline", game.getStoryline());
                intent.putExtra("rating", game.getRating());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                //start activity
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mGames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemTitle;
        ImageView imageView;
        CardView cardView;
        RecyclerView recyclerView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.incoming_image);
            itemTitle = itemView.findViewById(R.id.incoming_title);
            cardView = itemView.findViewById(R.id.incoming_cardview);
            recyclerView = itemView.findViewById(R.id.card_recyclerview);
        }
    }

    private String constructTitle(String name, Integer date){
        long dateInMillis = date * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate = sdf.format(dateInMillis);
        if(name.length() > 35) {
            return name.substring(0, 35) + "... - " + stringDate;
        }
        return name + " - " + stringDate;
    }

}
