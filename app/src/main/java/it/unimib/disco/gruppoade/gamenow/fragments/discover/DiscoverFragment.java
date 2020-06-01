package it.unimib.disco.gruppoade.gamenow.fragments.discover;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.RssFeedListAdapter;
import it.unimib.disco.gruppoade.gamenow.models.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.NewsProvider;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class DiscoverFragment extends Fragment {

    private View root;
    private static final String TAG = "HomeFragment";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private List<PieceOfNews> mFeedModelList;
    private DiscoverViewModel discoverViewModel;
    private RssFeedListAdapter adapter;

    // Firebase
    private User user;
    private ValueEventListener postListenerFirstUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            Log.d(TAG, "Messaggio onDataChange: " + user.toString());

            // Recupero il recyclerview dal layout xml e imposto l'adapter
            mRecyclerView = root.findViewById(R.id.recyclerView);
            mFeedModelList = new ArrayList<>();
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setHasFixedSize(true);
            adapter = new RssFeedListAdapter(getActivity(), mFeedModelList, user);
            mRecyclerView.setAdapter(adapter);

            new ProcessInBackground().execute(readProvidersCsv());

            mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new ProcessInBackground().execute(readProvidersCsv());
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, databaseError.getMessage());
            throw databaseError.toException();
        }
    };
    private ValueEventListener postListenerUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            Log.d(TAG, "Messaggio onDataChange: " + user.toString());

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, databaseError.getMessage());
            throw databaseError.toException();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        discoverViewModel =
                ViewModelProviders.of(this).get(DiscoverViewModel.class);
        root = inflater.inflate(R.layout.fragment_discover, container, false);

        // Swipe per Refresh Manuale
        mSwipeLayout = root.getRootView().findViewById(R.id.swipeRefresh);

        // Recupero dati database
        FbDatabase db = FbDatabase.FbDatabase();
        FbDatabase.getUserReference().addListenerForSingleValueEvent(postListenerFirstUserData);
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);

        return root;
    }

    private List<NewsProvider> readProvidersCsv() {
        List<NewsProvider> providers = new ArrayList<NewsProvider>();
        InputStream is = getResources().openRawResource(R.raw.providers);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("@@@");
                if (user.getTags() != null) {
                    if (!user.getTags().contains(tokens[0]))
                        providers.add(new NewsProvider(tokens[0], tokens[1], tokens[2], tokens[3]));
                } else {  // non ha nessun tag salvato
                    providers.add(new NewsProvider(tokens[0], tokens[1], tokens[2], tokens[3]));
                }
            }
        } catch (IOException e) {
            Log.e("CSV ERROR LOG ----->>> ", "Error: " + e);
        }

        return providers;
    }

    public boolean checkNewsPresence(String guid, String platform) {
        // Controlla che non vi sia già una news uguale ma con tag diversi,
        // in tal caso aggiunge a quella
        for (PieceOfNews pieceOfNews : mFeedModelList) {
            if (pieceOfNews.getGuid().equals(guid)) {
                // Se è già presente, aggiungo il tag (ammesso che questo non vi sia già)
                String tmpPlatform = pieceOfNews.getProvider().getPlatform();
                if (tmpPlatform.indexOf(platform) == -1)
                    pieceOfNews.getProvider().setPlatform(tmpPlatform + "," + platform);
                Log.d(TAG, pieceOfNews.getProvider().getPlatform());
                return true;
            }
        }
        return false;
    }

    public void parseFeed(InputStream inputStream, NewsProvider provider) throws XmlPullParserException, IOException {
        // Variabili temporanee per valori xml
        String title = null;
        String link = null;
        String description = null;
        String guid = null;
        LocalDateTime pubDate = null;

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
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("guid")) {
                        guid = xpp.nextText();
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("pubDate")) {
                        pubDate = LocalDateTime.parse(xpp.nextText(), DateTimeFormatter.RFC_1123_DATE_TIME);
                    }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                    if (title != null && link != null && description != null && pubDate != null) {
                        if (!checkNewsPresence(guid, provider.getPlatform())) {
                            PieceOfNews item = new PieceOfNews(title, description, link, pubDate, extractImageUrl(description), guid, provider);
                            mFeedModelList.add(item);
                        }
                    }
                }
                eventType = xpp.next();
            }
        } finally {
            inputStream.close();
        }
    }

    public class ProcessInBackground extends AsyncTask<List<NewsProvider>, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(List<NewsProvider>... providers) {

            // Pulisco array di notizie
            mFeedModelList.clear();

            for (NewsProvider provider : providers[0]) {
                URL urlLink = provider.getRssUrl();

                if (TextUtils.isEmpty(urlLink.toString()))
                    return false;

                try {
                    // Creo connessione con URL
                    InputStream inputStream = urlLink.openConnection().getInputStream();

                    // Eseguo il parsing XML -> oggetti PieceOfNews
                    parseFeed(inputStream, provider);
                } catch (IOException e) {
                    Log.e(TAG, "Error [IO EXCEPTION] ", e);
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "Error [XML PULL PARS.] ", e);
                }
            }

            // Parsing XML avvenuto correttamente
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                // Ordino notizie in base alla data di pubblicazione
                Collections.sort(mFeedModelList);
                Collections.reverse(mFeedModelList);

                // Controllo la presenza o meno di informazioni per mostrare un messaggio di stato
                if (mFeedModelList.isEmpty()) {
                    root.findViewById(R.id.recyclerView).setVisibility(View.GONE);
                    root.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                } else {
                    root.findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.empty_view).setVisibility(View.GONE);
                }

                // Riempo la RecyclerView con le schede notizie
                adapter.notifyDataSetChanged();
            } else {
                Log.d("RSS URL", "Enter a valid Rss feed url");
            }
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
}
