package it.unimib.disco.gruppoade.gamenow.repositories.utils;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.NewsProvider;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.repositories.ProvidersRepository;

public class RssDownloader implements Runnable {
    private Resources resources;
    private MutableLiveData<ArrayList<PieceOfNews>> news;
    private final static String TAG = "RssDownloaderRunnable";

    public RssDownloader(Resources res, MutableLiveData<ArrayList<PieceOfNews>> news){
        this.resources = res;
        this.news = news;
    }

    @Override
    public void run() {
        ProvidersRepository.getInstance(resources);
        List<NewsProvider> providers = ProvidersRepository.loadProviders();

        // Array temporaneo per news
        HashMap<String, PieceOfNews> newsFromProviders = new HashMap<>();
        for (NewsProvider provider : providers) {
            URL urlLink = provider.getRssUrl();

            if (!TextUtils.isEmpty(urlLink.toString())) {
                try {
                    // Creo connessione con URL
                    InputStream inputStream = urlLink.openConnection().getInputStream();

                    // Eseguo il parsing XML -> oggetti PieceOfNews
                    parseFeed(inputStream, provider, newsFromProviders);

                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error [IO EXCEPTION] ", e);
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "Error [XML PULL PARS.] ", e);
                }
            }
        }

        // Convert to arraylist
        ArrayList<PieceOfNews> arrayListToPost = new ArrayList<>(newsFromProviders.values());

        // Ordino notizie in base alla data di pubblicazione
        Collections.sort(arrayListToPost);
        Collections.reverse(arrayListToPost);

        // Aggiorno l'oggetto LiveData news
        news.postValue(arrayListToPost);
    }

    private void parseFeed(InputStream inputStream, NewsProvider provider, HashMap<String, PieceOfNews> newsFromProviders)
            throws XmlPullParserException, IOException {
        // Variabili temporanee per valori xml
        String title = null;
        String link = null;
        String description = null;
        String guid = null;
        LocalDateTime pubDate = null;
        String contentEncoded = null;

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
                        if(!guid.contains("https")){
                            guid = guid.replace("http","https");
                        }
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("pubDate")) {
                        pubDate = LocalDateTime.parse(xpp.nextText(), DateTimeFormatter.RFC_1123_DATE_TIME);
                    } else if (insideItem && xpp.getName().equalsIgnoreCase("content:encoded")) {
                        contentEncoded = xpp.nextText();
                    }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                    if (title != null && link != null && description != null && pubDate != null) {
                        if (newsFromProviders.containsKey(guid)) {
                            String providertoAdd = provider.getPlatform();

                            NewsProvider alreadyPresentArticleProvider = Objects.requireNonNull(newsFromProviders.get(guid)).getProvider();
                            String alreadyPresentPlatforms = alreadyPresentArticleProvider.getPlatform();

                            if(!alreadyPresentPlatforms.contains(providertoAdd)) {
                                alreadyPresentPlatforms = alreadyPresentPlatforms + "," + providertoAdd;
                                alreadyPresentArticleProvider.setPlatform(alreadyPresentPlatforms);
                            }
                        } else {
                            String imgUrl = extractImageUrl(description);
                            String contentImgUrl = null;
                            if (imgUrl.isEmpty() && contentEncoded != null) {
                                contentImgUrl = extractImageUrl(contentEncoded);
                            }
                            PieceOfNews item;

                            if (contentImgUrl != null) {
                                item = new PieceOfNews(title, description, link, pubDate, contentImgUrl, guid, new NewsProvider(provider));
                            } else {
                                item = new PieceOfNews(title, description, link, pubDate, imgUrl, guid, new NewsProvider(provider));
                            }

                            newsFromProviders.put(guid, item);
                        }
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
}
