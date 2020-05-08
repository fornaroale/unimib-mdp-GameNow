package it.unimib.disco.gruppoade.gamenow.ui.comingsoon;

public class Constants {

    private final String API_KEY;
    private final String BASE_URL;

    public String getAPIKey() {
        return API_KEY;
    }

    public String getBASE_URL() {
        return BASE_URL;
    }

    public Constants() {
        API_KEY  = "ab4f92a787f1b14954b8d3fb288dc8afbf1ed68d";
        BASE_URL = "https://api-v3.igdb.com/";
    }
}
