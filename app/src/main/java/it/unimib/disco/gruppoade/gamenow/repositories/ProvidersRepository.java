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
    private static Resources resources;

    private ProvidersRepository(Resources res) {
        providers = new ArrayList<>();
        this.resources = res;
    }

    public static ProvidersRepository getInstance(Resources res) {
        if(instance==null)
            synchronized(ProvidersRepository.class) {
                if( instance == null )
                    instance = new ProvidersRepository(res);
            }
        return instance;
    }

    public static List<NewsProvider> loadProviders(User user, boolean usingFeed){
        Log.d("UTENTE", "CSV " + Integer.toHexString(System.identityHashCode(user)) + " - " + user);

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
                            providers.add(new NewsProvider(tokens[0], tokens[1], tokens[2], tokens[3]));
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
