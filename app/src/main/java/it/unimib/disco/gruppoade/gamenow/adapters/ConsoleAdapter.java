package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.Platform;

public class ConsoleAdapter extends RecyclerView.Adapter<ConsoleAdapter.ViewHolder>{

    private List<Platform> mPlatforms;

    public ConsoleAdapter(List<Platform> mPlatforms) {

        this.mPlatforms = mPlatforms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_console, parent, false);
        ConsoleAdapter.ViewHolder holder = new ConsoleAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.consoleAbb.setText(mPlatforms.get(position).getAbbreviation());
        if(mPlatforms.get(position).getAbbreviation() != null && mPlatforms.get(position).getAbbreviation().equals("XONE"))
            holder.consoleAbb.setText("XBOX");
    }


    @Override
    public int getItemCount() {
        if(mPlatforms != null)
            return mPlatforms.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView consoleAbb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            consoleAbb = itemView.findViewById(R.id.console_abb);
        }
    }

}
