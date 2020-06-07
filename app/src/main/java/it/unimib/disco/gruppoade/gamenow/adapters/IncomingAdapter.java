package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
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
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Game;

public class IncomingAdapter extends RecyclerView.Adapter<IncomingAdapter.ViewHolder>{

    public interface OnItemClickListener{
        void onItemClick(Game game);
    }

    private static final String TAG = "Adapter";

    final Gson gson = new Gson();

    private Context mContext;
    private List<Game> mGames;
    private OnItemClickListener onItemClickListener;

    public IncomingAdapter(Context mContext, List<Game> mResults, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.mGames = mResults;
        this.onItemClickListener = onItemClickListener;
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
        holder.bind(game,this.onItemClickListener);

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

        public void bind(final Game game, final OnItemClickListener onItemClickListener){

            if (game.getDate() != null) {
                itemTitle.setText(constructTitle(game.getName(), game.getDate()));
            } else {
                itemTitle.setText(game.getName());
            }

            final String coverBig, url;
            Log.d(TAG, "onBindViewHolder: Game = " + gson.toJson(game));

            if(game.getCover() != null) {
                coverBig = game.getCover().getUrl().replace("t_thumb", "t_cover_big");
                url = "https:" + coverBig;
                Picasso.get()
                        .load(url)
                        .into(imageView);

            } else {
                imageView.setImageResource(R.drawable.cover_na);
                url = "";
            }

            //Console Recycler
            ConsoleAdapter consoleAdapter = new ConsoleAdapter(game.getPlatforms(),mContext);
            recyclerView.setAdapter(consoleAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));



            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(game);
                }
            });




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
