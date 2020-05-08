package it.unimib.disco.gruppoade.gamenow.models;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Game {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("storyline")
    @Expose
    private String strotyline;

    @SerializedName("cover")
    @Expose
    private Cover cover;

    @SerializedName("first_release_date")
    @Expose
    private  Integer date;

    @SerializedName("platforms")
    @Expose
    private List<Platform> platforms;

    protected Game(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        summary = in.readString();
        strotyline = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readInt();
        }
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getDate() {
        return date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStrotyline() {
        return strotyline;
    }

    public void setStrotyline(String strotyline) {
        this.strotyline = strotyline;
    }

}
