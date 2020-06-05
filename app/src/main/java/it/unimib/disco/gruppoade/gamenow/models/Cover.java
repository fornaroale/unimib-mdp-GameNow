package it.unimib.disco.gruppoade.gamenow.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cover {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("url")
    @Expose
    private String url;

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}