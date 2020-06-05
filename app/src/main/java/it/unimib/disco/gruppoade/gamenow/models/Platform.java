package it.unimib.disco.gruppoade.gamenow.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Platform implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("abbreviation")
    @Expose
    private String abbreviation;

    protected Platform(Parcel in) {
        id = in.readInt();
        abbreviation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(abbreviation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Platform> CREATOR = new Creator<Platform>() {
        @Override
        public Platform createFromParcel(Parcel in) {
            return new Platform(in);
        }

        @Override
        public Platform[] newArray(int size) {
            return new Platform[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
