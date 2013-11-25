package com.example.rottenapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alberto on 23/11/13.
 */
public class Movie implements Parcelable{

    private String id,title,year,mpaa_rating,runtime,critics_consensus,synopsis;
    private Ratings ratings;
    private Posters posters;





    public Movie() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public Posters getPosters() {
        return posters;
    }

    public Ratings getRatings() {
        return ratings;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(year);
        parcel.writeValue(posters);
        parcel.writeValue(ratings);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        year = in.readString();
        posters = (Posters)in.readValue(Posters.class.getClassLoader());
        ratings = (Ratings)in.readValue(Ratings.class.getClassLoader());
    }
}
