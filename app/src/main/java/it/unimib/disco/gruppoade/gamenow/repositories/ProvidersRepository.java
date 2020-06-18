package it.unimib.disco.gruppoade.gamenow.repositories;

import android.content.res.Resources;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.models.NewsProvider;
import it.unimib.disco.gruppoade.gamenow.models.User;

public final class ProvidersRepository {

    private static ProvidersRepository instance = null;
    private static ArrayList<NewsProvider> providers;
    private static User user;
    private static boolean usingFeed;
    private static Resources resources;

    private ProvidersRepository(Resources res, User user, boolean usingFeed) {
        providers = new ArrayList<>();
        this.user=user;
        this.usingFeed=usingFeed;
        this.resources = res;
    }

    public static ProvidersRepository getInstance(Resources res, User user, boolean usingFeed) {
        if(instance==null)
            synchronized(ProvidersRepository.class) {
                if( instance == null )
                    instance = new ProvidersRepository(res, user, usingFeed);
            }
        return instance;
    }

    public static List<NewsProvider> loadProviders(){
        providers = new ArrayList<>();
        InputStream is = resources.openRawResource(R.raw.providers);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                if(!line.isEmpty()) {
                    String[] tokens = line.split("@@@");
                    if (user.getTags() != null) {
                        if(usingFeed){
                            if (user.getTags().contains(tokens[0])) {
                                providers.add(new NewsProvider(tokens[0], tokens[1], tokens[2], tokens[3]));
                            }
                        } else {
                            if (!user.getTags().contains(tokens[0])) {
                                providers.add(new NewsProvider(tokens[0], tokens[1], tokens[2], tokens[3]));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.e("CSV ERROR LOG ----->>> ", "Error: " + e);
        }

        return providers;
    }
}
