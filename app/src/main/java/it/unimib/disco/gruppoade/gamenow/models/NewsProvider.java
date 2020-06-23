package it.unimib.disco.gruppoade.gamenow.models;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public class NewsProvider implements Cloneable {
    private final static String TAG = "NewsProvider Class";
    private String name;
    private URL homepageUrl;
    private URL rssUrl;
    private String platform;

    public NewsProvider(String platform, String name, String homepageUrl, String rssUrl) {
        this.name = name;
        this.platform = platform;

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

    public NewsProvider(NewsProvider copy) {
        this.name = copy.getName();
        this.homepageUrl = copy.getHomepageUrl();
        this.rssUrl = copy.getRssUrl();
        this.platform = copy.getPlatform();
    }

    public String getName() {
        return name;
    }

    public URL getRssUrl() {
        return rssUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public URL getHomepageUrl() {
        return homepageUrl;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @NotNull
    @Override
    public String toString() {
        return "NewsProvider{" +
                "platform='" + platform + '\'' +
                '}';
    }
}
