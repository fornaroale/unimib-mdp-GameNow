package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.FeedModelViewHolder> {

    public class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitolo, tvProvider, tvDescrizione, tvData;
        private ImageView ivNewsImage;
        private ToggleButton tbFav;
        private ChipGroup cgNewsTags;

        public FeedModelViewHolder(View view) {
            super(view);

            tvTitolo = (TextView) view.findViewById(R.id.newsTitle);
            tvProvider = (TextView) view.findViewById(R.id.newsProvider);
            tvDescrizione = (TextView) view.findViewById(R.id.newsDesc);
            tvData = (TextView) view.findViewById(R.id.newsPubDate);
            ivNewsImage = (ImageView) view.findViewById(R.id.newsImage);
            tbFav = (ToggleButton) view.findViewById(R.id.saveNewsImg);
            cgNewsTags = (ChipGroup) view.findViewById(R.id.newsTagsChipGroup);
        }

        public void bind(PieceOfNews pieceOfNews, OnItemClickListener onItemClickListener){
            tvTitolo.setText(pieceOfNews.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(pieceOfNews);
                }
            });

            // Immagine
            String imgUrl = pieceOfNews.getImage();
            if(imgUrl.isEmpty()) {
                Picasso.get()
                        .load(R.drawable.image_not_available)
                        .fit()
                        .centerCrop()
                        .into(ivNewsImage);
            } else {
                Picasso.get()
                        .load(imgUrl)
                        .fit()
                        .centerCrop()
                        .into(ivNewsImage);
            }

            // Titolo
            tvTitolo.setText(pieceOfNews.getTitle());

            // Provider della notizia
            tvProvider.setText(pieceOfNews.getProvider().getName());

            // Descrizione
            String plainDesc = Html.fromHtml(pieceOfNews.getDesc().replaceAll("<img.+/(img)*>", "")).toString();
            tvDescrizione.setText(plainDesc);

            // Data di pubblicazione
            LocalDateTime osDateTime = pieceOfNews.getPubDate();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy, HH:mm");
            tvData.setText(dtf.format(osDateTime));

            // ToggleButton bookmark
            if(!forSavedNews) {
                if (user.checkSavedPieceOfNews(pieceOfNews)) {
                    tbFav.setChecked(true);
                } else {
                    tbFav.setChecked(false);
                }
            } else {
                tbFav.setChecked(true);
            }
            tbFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(tbFav.isPressed()) {
                        final PieceOfNews ponClicked = pieceOfNews;
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
            cgNewsTags.removeAllViews();
            for (String newsTag : pieceOfNews.getProvider().getPlatform().split(",")) {
                final Chip chip = (Chip) LayoutInflater.from(mContext).inflate(R.layout.layout_chip_tag, cgNewsTags, false);
                chip.setText(newsTag);
                cgNewsTags.addView(chip);
                if(!forSavedNews) {
                    setNewsTagsIcon(chip, itemView);
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
    }

    private final FragmentActivity mContext;
    private LayoutInflater layoutInflater;
    private List<PieceOfNews> newsList;
    private User user;
    private boolean forSavedNews;

    private OnItemClickListener onItemClickListener;

    @NotNull
    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = this.layoutInflater.inflate(R.layout.layout_singlenews_card, parent, false);
        return new FeedModelViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        if (newsList != null)
            return newsList.size();
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(PieceOfNews pieceOfNews);
    }

    public NewsListAdapter(Context mContext, List<PieceOfNews> newsFeedModels, User user, boolean forSavedNews, OnItemClickListener onItemClickListener) {
        this.mContext = (FragmentActivity) mContext;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.newsList = newsFeedModels;
        this.user = user;
        this.forSavedNews = forSavedNews;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        holder.bind(newsList.get(position), this.onItemClickListener);
    }

    private void setNewsTagsIcon(Chip chip, View holder) {
        if (user.getTags().contains(chip.getText().toString())) {
            chip.setChipIcon(ContextCompat.getDrawable(holder.getContext(), R.drawable.heart_pressed));
        } else {
            chip.setChipIcon(ContextCompat.getDrawable(holder.getContext(), R.drawable.heart));
        }
    }
}