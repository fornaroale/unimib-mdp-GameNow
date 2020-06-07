package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class SavedNewsListAdapter extends RecyclerView.Adapter<SavedNewsListAdapter.SavedNewsModelViewHolder> {

    private final FragmentActivity mContext;
    private List<PieceOfNews> mSavedNewsModels;
    private User user;

    public SavedNewsListAdapter(Context mContext, List<PieceOfNews> savedNewsModels, User user) {
        this.mContext = (FragmentActivity) mContext;
        this.mSavedNewsModels = savedNewsModels;
        this.user = user;
    }

    @Override
    public SavedNewsModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_singlenews_card, parent, false);
        return new SavedNewsModelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SavedNewsModelViewHolder holder, int position) {
        final PieceOfNews savedNewsModel = mSavedNewsModels.get(position);

        // Immagine
        String imgUrl = savedNewsModel.getImage();
        if(imgUrl.isEmpty()) {
            Picasso.get()
                    .load(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into((ImageView) holder.savedNewsView.findViewById(R.id.newsImage));
        } else {
            Picasso.get()
                    .load(imgUrl)
                    .fit()
                    .centerCrop()
                    .into((ImageView) holder.savedNewsView.findViewById(R.id.newsImage));
        }

        // Titolo
        ((TextView) holder.savedNewsView.findViewById(R.id.newsTitle)).setText(savedNewsModel.getTitle());

        // Provider della notizia
        ((TextView) holder.savedNewsView.findViewById(R.id.newsProvider)).setText(savedNewsModel.getProvider().getName());

        // Descrizione
        String plainDesc = Html.fromHtml(savedNewsModel.getDesc().replaceAll("<img.+/(img)*>", "")).toString();
        ((TextView) holder.savedNewsView.findViewById(R.id.newsDesc)).setText(plainDesc);

        // Data di pubblicazione
        LocalDateTime osDateTime = savedNewsModel.getPubDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy, HH:mm");
        ((TextView) holder.savedNewsView.findViewById(R.id.newsPubDate)).setText(dtf.format(osDateTime));

        // Configurazione link
        holder.savedNewsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSavedNewsModels.get(holder.getAdapterPosition()).getLink()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });

        // ToggleButton bookmark
        final ToggleButton favButton = holder.savedNewsView.findViewById(R.id.saveNewsImg);
        favButton.setChecked(true);
        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(favButton.isPressed()) {
                    final PieceOfNews ponToRemove = mSavedNewsModels.get(holder.getAdapterPosition());
                    if (user.removeSavedPieceOfNews(ponToRemove)) {
                        Snackbar.make(holder.savedNewsView, R.string.fav_news_removed, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action_undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        user.savePieceOfNews(ponToRemove);
                                    }
                                })
                                .show();
                    }
                }
            }
        });

        // News' tags chips
        ChipGroup chipGroup = holder.savedNewsView.findViewById(R.id.newsTagsChipGroup);
        chipGroup.removeAllViews();
        for (String newsTag : savedNewsModel.getProvider().getPlatform().split(",")) {
            final Chip chip = (Chip) LayoutInflater.from(mContext).inflate(R.layout.layout_chip_tag, chipGroup, false);
            chip.setText(newsTag);
            chip.setChipIcon(null);
            chipGroup.addView(chip);
        }
    }

    @Override
    public int getItemCount() {
        return mSavedNewsModels.size();
    }

    public static class SavedNewsModelViewHolder extends RecyclerView.ViewHolder {
        private View savedNewsView;

        public SavedNewsModelViewHolder(View v) {
            super(v);
            savedNewsView = v;
        }
    }
}