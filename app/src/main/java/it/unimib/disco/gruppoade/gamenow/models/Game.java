package it.unimib.disco.gruppoade.gamenow.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

public class Game implements Parcelable {

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
    private Integer date;

    @SerializedName("platforms")
    @Expose
    private List<Platform> platforms;


    protected Game(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        rating = in.readDouble();
        videos = in.createTypedArrayList(Video.CREATOR);
        name = in.readString();
        summary = in.readString();
        storyline = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readInt();
        }
        platforms = in.createTypedArrayList(Platform.CREATOR);
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public List<Platform> getPlatforms() {
        return platforms;
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

    public String getSummary() {
        return summary;
    }

    public String getStoryline() {
        return storyline;
    }

    public double getRating() {
        return rating;
    }

    public List<Video> getVideos() {
        return videos;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeDouble(rating);
        dest.writeTypedList(videos);
        dest.writeString(name);
        dest.writeString(summary);
        dest.writeString(storyline);
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(date);
        }
        dest.writeTypedList(platforms);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", rating=" + rating +
                ", videos=" + videos +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", storyline='" + storyline + '\'' +
                ", cover=" + cover +
                ", date=" + date +
                ", platforms=" + platforms +
                '}';
    }
}
