package it.unimib.disco.gruppoade.gamenow.ui.comingsoon;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.GameInfoActivity;
import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Platform;

public class ConsoleAdapter extends RecyclerView.Adapter<ConsoleAdapter.ViewHolder>{

    private static final String TAG = "ConsoleAdapter";


    private List<Platform> mPlatforms;
    private Context mContext;
    private Gson gson = new Gson();


    public ConsoleAdapter(List<Platform> mPlatforms, Context mContext) {

        this.mPlatforms = mPlatforms;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_console, parent, false);
        ConsoleAdapter.ViewHolder holder = new ConsoleAdapter.ViewHolder(view);
        final Intent intent = new Intent(mContext, GameInfoActivity.class);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Console Called.");
        Log.d(TAG, "onBindViewHolder: Platforms = " + gson.toJson(mPlatforms));
        holder.consoleAbb.setText(mPlatforms.get(position).getAbbreviation());
    }


    @Override
    public int getItemCount() {
        return mPlatforms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView consoleAbb;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            consoleAbb = itemView.findViewById(R.id.console_abb);
            recyclerView = itemView.findViewById(R.id.card_recyclerview);
        }
    }

}
