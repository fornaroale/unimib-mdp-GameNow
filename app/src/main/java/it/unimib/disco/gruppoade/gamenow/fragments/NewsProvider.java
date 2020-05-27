package it.unimib.disco.gruppoade.gamenow.fragments;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class NewsProvider {
    private final static String TAG = "NewsProvider Class";
    private String name;
    private URL homepageUrl;
    private URL rssUrl;

    public NewsProvider(String name, String homepageUrl, String rssUrl) {
        this.name = name;
        try {
            if (!homepageUrl.startsWith("http://") && !homepageUrl.startsWith("https://"))
                homepageUrl = "https://" + homepageUrl;

            this.homepageUrl = new URL(homepageUrl);

            if (!rssUrl.startsWith("http://") && !rssUrl.startsWith("https://"))
                rssUrl = "https://" + rssUrl;

            this.rssUrl = new URL(rssUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error [MALFORMED URL] ", e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(URL homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public URL getRssUrl() {
        return rssUrl;
    }

    public void setRssUrl(URL rssUrl) {
        this.rssUrl = rssUrl;
    }
}
