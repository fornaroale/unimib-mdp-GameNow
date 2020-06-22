package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class IncomingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int GAME_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;


    public interface OnItemClickListener {
        void onItemClick(Game game);
    }

    private static final String TAG = "Adapter";

    private Context mContext;
    private List<Game> mGames;
    private OnItemClickListener onItemClickListener;
    private User user;


    public IncomingAdapter(Context mContext, List<Game> mResults, OnItemClickListener onItemClickListener, User user) {
        this.mContext = mContext;
        this.mGames = mResults;
        this.onItemClickListener = onItemClickListener;
        this.user = user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == GAME_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carditem, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_games, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Game game = mGames.get(position);
        if (holder instanceof ViewHolder)
            ((ViewHolder) holder).bind(game, this.onItemClickListener);
        else if (holder instanceof LoadingViewHolder)
            ((LoadingViewHolder) holder).loadingGame.setIndeterminate(true);

    }

    @Override
    public int getItemCount() {
        if (mGames != null)
            return mGames.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mGames.get(position) == null)
            return LOADING_VIEW_TYPE;
        else
            return GAME_VIEW_TYPE;
    }

    public void setData(List<Game> gameList) {
        if (gameList != null) {
            this.mGames = gameList;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitle;
        ImageView imageView;
        CardView cardView;
        RecyclerView recyclerView;
        ToggleButton toggleButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.incoming_image);
            itemTitle = itemView.findViewById(R.id.incoming_title);
            cardView = itemView.findViewById(R.id.incoming_cardview);
            recyclerView = itemView.findViewById(R.id.card_recyclerview);
            toggleButton = itemView.findViewById(R.id.incoming_save_game);
        }

        public void bind(final Game game, final OnItemClickListener onItemClickListener) {

            if (game.getDate() != null)
                itemTitle.setText(constructTitle(game.getName(), game.getDate()));
            else if (game.getName() == null)
                itemTitle.setText("N/A");
            else
                itemTitle.setText(game.getName());

            final String coverBig, url;

            if (game.getCover() != null) {
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
            ConsoleAdapter consoleAdapter = new ConsoleAdapter(game.getPlatforms());
            recyclerView.setAdapter(consoleAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(game);
                }
            });
            if (game != null) {
                if (user.checkSavedGame(game))
                    toggleButton.setChecked(true);
                else
                    toggleButton.setChecked(false);
                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (toggleButton.isPressed()) {
                            final Game clickedGame = mGames.get(getAdapterPosition());
                            if (isChecked) {
                                if (user.saveGame(clickedGame)) {
                                    Snackbar.make(buttonView, R.string.gioco_aggiunto, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.action_undo, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    user.removeGame(clickedGame);
                                                }
                                            })
                                            .setAnchorView(R.id.nav_view)
                                            .show();
                                }
                            } else {
                                if (user.removeGame(clickedGame)) {
                                    Snackbar.make(buttonView, R.string.gioco_rimosso, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.action_undo, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    user.saveGame(clickedGame);
                                                }
                                            })
                                            .setAnchorView(R.id.nav_view)
                                            .show();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

        public class LoadingViewHolder extends RecyclerView.ViewHolder {

            ProgressBar loadingGame;

            public LoadingViewHolder(@NonNull View itemView) {
                super(itemView);
                loadingGame = itemView.findViewById(R.id.loading_game);
            }
        }

        private String constructTitle(String name, Integer date) {
            long dateInMillis = date * 1000L;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String stringDate = sdf.format(dateInMillis);
            if (name.length() > 35) {
                return name.substring(0, 35) + "... - " + stringDate;
            }
            return name + " - " + stringDate;
        }
}
