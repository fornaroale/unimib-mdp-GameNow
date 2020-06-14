package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
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

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.FeedModelViewHolder> {

    private final FragmentActivity mContext;
    private List<PieceOfNews> mNewsFeedModels;
    private User user;
    private boolean forSavedNews;

    public NewsListAdapter(Context mContext, List<PieceOfNews> newsFeedModels, User user, boolean forSavedNews) {
        this.mContext = (FragmentActivity) mContext;
        this.mNewsFeedModels = newsFeedModels;
        this.user = user;
        this.forSavedNews = forSavedNews;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_singlenews_card, parent, false);
        return new FeedModelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeedModelViewHolder holder, int position) {
        PieceOfNews rssFeedModel = mNewsFeedModels.get(position);

        // Immagine
        String imgUrl = rssFeedModel.getImage();
        if(imgUrl.isEmpty()) {
            Picasso.get()
                    .load(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into((ImageView) holder.rssFeedView.findViewById(R.id.newsImage));
        } else {
            Picasso.get()
                    .load(imgUrl)
                    .fit()
                    .centerCrop()
                    .into((ImageView) holder.rssFeedView.findViewById(R.id.newsImage));
        }

        // Titolo
        ((TextView) holder.rssFeedView.findViewById(R.id.newsTitle)).setText(rssFeedModel.getTitle());

        // Provider della notizia
        ((TextView) holder.rssFeedView.findViewById(R.id.newsProvider)).setText(rssFeedModel.getProvider().getName());

        // Descrizione
        String plainDesc = Html.fromHtml(rssFeedModel.getDesc().replaceAll("<img.+/(img)*>", "")).toString();
        ((TextView) holder.rssFeedView.findViewById(R.id.newsDesc)).setText(plainDesc);

        // Data di pubblicazione
        LocalDateTime osDateTime = rssFeedModel.getPubDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy, HH:mm");
        ((TextView) holder.rssFeedView.findViewById(R.id.newsPubDate)).setText(dtf.format(osDateTime));

        // Configurazione link
        holder.rssFeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(view.getResources().getColor(R.color.colorPrimary));
                builder.setShowTitle(true);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(mNewsFeedModels.get(holder.getAdapterPosition()).getLink()));
            }
        });

        // ToggleButton bookmark
        final ToggleButton favButton = holder.rssFeedView.findViewById(R.id.saveNewsImg);
        if(!forSavedNews) {
            if (user.checkSavedPieceOfNews(rssFeedModel)) {
                favButton.setChecked(true);
            } else {
                favButton.setChecked(false);
            }
        } else {
            favButton.setChecked(true);
        }
        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(favButton.isPressed()) {
                    final PieceOfNews ponClicked = mNewsFeedModels.get(holder.getAdapterPosition());
                    if (isChecked) {
                        if(user.savePieceOfNews(ponClicked)) {
                            Snackbar.make(buttonView, R.string.fav_news_added, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action_undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            user.removeSavedPieceOfNews(ponClicked);
                                        }
                                    })
                                    .setAnchorView(R.id.nav_view)
                                    .show();
                        }
                    } else {
                        if(user.removeSavedPieceOfNews(ponClicked)) {
                            Snackbar.make(buttonView, R.string.fav_news_removed, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action_undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            user.savePieceOfNews(ponClicked);
                                        }
                                    })
                                    .setAnchorView(R.id.nav_view)
                                    .show();
                        }
                    }
                }
            }
        });

        // News' tags chips
        ChipGroup chipGroup = holder.rssFeedView.findViewById(R.id.newsTagsChipGroup);
        chipGroup.removeAllViews();
        for (String newsTag : rssFeedModel.getProvider().getPlatform().split(",")) {
            final Chip chip = (Chip) LayoutInflater.from(mContext).inflate(R.layout.layout_chip_tag, chipGroup, false);
            chip.setText(newsTag);
            chipGroup.addView(chip);
            if(!forSavedNews) {
                setNewsTagsIcon(chip, holder);
                chip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String chipTagText = chip.getText().toString();
                        if (user.getTags().contains(chipTagText)) {
                            user.removeTag(chipTagText);
                            chip.setChipIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.heart));
                            Snackbar.make(view, R.string.fav_tag_removed, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action_undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            user.addTag(chipTagText);
                                        }
                                    })
                                    .setAnchorView(R.id.nav_view)
                                    .show();
                        } else {
                            user.addTag(chipTagText);
                            chip.setChipIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.heart_pressed));
                            Snackbar.make(view, R.string.fav_tag_added, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.action_undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            user.removeTag(chipTagText);
                                        }
                                    })
                                    .setAnchorView(R.id.nav_view)
                                    .show();
                        }
                    }
                });
            }
        }
    }

    private void setNewsTagsIcon(Chip chip, FeedModelViewHolder holder) {
        if (user.getTags().contains(chip.getText().toString())) {
            chip.setChipIcon(ContextCompat.getDrawable(holder.rssFeedView.getContext(), R.drawable.heart_pressed));
        } else {
            chip.setChipIcon(ContextCompat.getDrawable(holder.rssFeedView.getContext(), R.drawable.heart));
        }
    }

    @Override
    public int getItemCount() {
        return mNewsFeedModels.size();
    }

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;

        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
        }
    }
}