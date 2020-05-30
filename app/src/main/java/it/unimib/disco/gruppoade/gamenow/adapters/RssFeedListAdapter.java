package it.unimib.disco.gruppoade.gamenow.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;

public class RssFeedListAdapter extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder> {

    private final FragmentActivity mContext;
    private List<PieceOfNews> mRssFeedModels;
    private final static String TAG = "RssFeedListAdapter";

    private final ArrayList<String> USER_TAGS = new ArrayList<>();

    public RssFeedListAdapter(Context mContext, List<PieceOfNews> rssFeedModels) {
        this.mContext = (FragmentActivity) mContext;
        mRssFeedModels = rssFeedModels;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_singlenews_card, parent, false);
        return new FeedModelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeedModelViewHolder holder, int position) {
        final PieceOfNews rssFeedModel = mRssFeedModels.get(position);

        // Immagine
        String imgUrl = rssFeedModel.getImage();
        if (!imgUrl.isEmpty())
            Picasso.get()
                    .load(imgUrl)
                    .fit()
                    .centerCrop()
                    .into((ImageView) holder.rssFeedView.findViewById(R.id.newsImage));

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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rssFeedModel.getLink()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });

        // ToggleButton bookmark
        ToggleButton favButton = holder.rssFeedView.findViewById(R.id.saveNewsImg);
        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // TODO: Salvare news
                    Snackbar snackbar = Snackbar.make(buttonView, "News salvata!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    // TODO: Salvare news
                    Snackbar snackbar = Snackbar.make(buttonView, "News rimossa da 'News salvate'", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        // News' tags chips
        String[] platforms = rssFeedModel.getProvider().getPlatform().split(",");
        ChipGroup chipGroup = holder.rssFeedView.findViewById(R.id.newsTagsChipGroup);
        chipGroup.removeAllViews();
        for (String platform : platforms) {
            final Chip chip = (Chip) LayoutInflater.from(mContext).inflate(R.layout.chip_tag_layout, chipGroup, false);
            chip.setText(platform);
            chipGroup.addView(chip);
            setNewsTagsIcon(chip, holder);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String chipTagText = chip.getText().toString();

                    if (USER_TAGS.contains(chipTagText)) { // TODO: tag già presente nella lista tag utente
                        USER_TAGS.remove(chipTagText);
                        chip.setChipIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.heart));

                        Snackbar snackbar = Snackbar.make(view, "Tag rimosso dai tag utente!", Snackbar.LENGTH_LONG);
                        snackbar.show();

                        notifyItemChanged(holder.getAdapterPosition());
                        notifyItemRangeChanged(0, holder.getAdapterPosition());
                    } else { // TODO: tag non presente nella lista tag utente
                        USER_TAGS.add(chipTagText);
                        chip.setChipIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.heart_pressed));

                        Snackbar snackbar = Snackbar.make(view, "Tag aggiunto ai tag utente!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            });
        }
    }

    private void setNewsTagsIcon(Chip chip, FeedModelViewHolder holder) {
        if (USER_TAGS.contains(chip.getText().toString())) {
            chip.setChipIcon(ContextCompat.getDrawable(holder.rssFeedView.getContext(), R.drawable.heart_pressed));
        } else {
            chip.setChipIcon(ContextCompat.getDrawable(holder.rssFeedView.getContext(), R.drawable.heart));
        }
    }

    @Override
    public int getItemCount() {
        return mRssFeedModels.size();
    }

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;

        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
        }
    }
}