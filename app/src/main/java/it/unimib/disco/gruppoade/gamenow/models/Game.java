package it.unimib.disco.gruppoade.gamenow.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Game {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("total_rating")
    @Expose
    private double rating;

    @SerializedName("videos")
    @Expose
    private List<Video> videos;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("storyline")
    @Expose
    private String storyline;

    @SerializedName("cover")
    @Expose
    private Cover cover;

    @SerializedName("first_release_date")
    @Expose
    private  Integer date;

    @SerializedName("platforms")
    @Expose
    private List<Platform> platforms;

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

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
