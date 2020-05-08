package it.unimib.disco.gruppoade.gamenow.ui.discover;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.ui.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.ui.RssFeedListAdapter;

public class DiscoverFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private String urlLink = "https://www.eurogamer.it/?format=rss";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;

    private List<PieceOfNews> mFeedModelList;

    private DiscoverViewModel discoverViewModel;
    private RssFeedListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        discoverViewModel =
                ViewModelProviders.of(this).get(DiscoverViewModel.class);
        View root = inflater.inflate(R.layout.fragment_discover, container, false);

        // Recupero il recyclerview dal layout xml
        mRecyclerView = root.findViewById(R.id.recyclerView);

        mFeedModelList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new RssFeedListAdapter(getActivity(), mFeedModelList);
        mRecyclerView.setAdapter(adapter);

        // Swipe per Refresh Manuale
        mSwipeLayout = root.getRootView().findViewById(R.id.swipeRefresh);

        new ProcessInBackground().execute();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ProcessInBackground().execute();
            }
        });

        return root;
    }

    public void parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        // Variabili temporanee per valori xml
        String title = null;
        String link = null;
        String description = null;
        Date pubDate = null;

        // Array di notizie da riempire
        mFeedModelList.clear();

        // mi trovo all'interno della notizia?
        boolean insideItem = false;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            // supporto per namespace xml a false
            factory.setNamespaceAware(false);
            xpp.setInput(inputStream, "UTF_8");

            // tipo dell'evento (inizio doc, fine doc, inizio tag, fine tag, ecc.)
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("title")) {
                        title = xpp.nextText();
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("link")) {
                        link = xpp.nextText();
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("description")) {
                        description = xpp.nextText();
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("pubDate")) {
                        try {
                            DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                            pubDate = formatter.parse(xpp.nextText());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                    if (title != null && link != null && description != null && pubDate != null) {
                        PieceOfNews item = new PieceOfNews(title, description, link, pubDate, extractImageUrl(description));
                        mFeedModelList.add(item);
                    }
                }

                eventType = xpp.next();
            }

        } finally {
            inputStream.close();
        }
    }

    private String extractImageUrl(String description) {
        Document document = Jsoup.parse(description);
        Elements imgs = document.select("img");

        for (Element img : imgs) {
            if (img.hasAttr("src")) {
                return img.attr("src").replace("http:", "https:");
            }
        }

        // no image URL
        return "";
    }

    public class ProcessInBackground extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(urlLink))
                return false;

            try {
                if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "https://" + urlLink;

                // Creo connessione con URL
                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();

                // eseguo il parsing
                parseFeed(inputStream);

                // Parsing XML avvenuto correttamente
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                // Riempo la RecyclerView con le schede notizie
                adapter.notifyDataSetChanged();

                //mRecyclerView.setAdapter(new RssFeedListAdapter(getActivity(), mFeedModelList));

            } else {
                Log.d("RSS URL", "Enter a valid Rss feed url");
            }
        }
    }
}
