package com.example.rottenapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alberto on 25/11/13.
 */
public class Ratings implements Parcelable{
    private String critics_rating, critics_score, audience_rating, audience_score;

    public String getCritics_rating() {
        return critics_rating;
    }

    public String getCritics_score() {
        return critics_score;
    }

    public String getAudience_rating() {
        return audience_rating;
    }

    public String getAudience_score() {
        return audience_score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(critics_rating);
        parcel.writeString(critics_score);
        parcel.writeString(audience_rating);
        parcel.writeString(audience_score);
    }

    public static final Parcelable.Creator<Ratings> CREATOR = new Parcelable.Creator<Ratings>() {
        public Ratings createFromParcel(Parcel in) {
            return new Ratings(in);
        }

        public Ratings[] newArray(int size) {
            return new Ratings[size];
        }
    };

    private Ratings(Parcel in) {
        critics_rating = in.readString();
        critics_score = in.readString();
        audience_rating = in.readString();
        audience_score = in.readString();
    }
}
