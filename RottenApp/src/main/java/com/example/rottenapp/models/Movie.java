package com.example.rottenapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by alberto on 23/11/13.
 */
public class Movie implements Parcelable{

    private String id,title,year,mpaa_rating,runtime,critics_consensus,synopsis;
    private Ratings ratings;
    private Posters posters;
    private Links links;
    private ArrayList<Cast> abridged_cast;


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

    public String getSynopsis() {
        return synopsis;
    }

    public String getMpaa_rating() {
        return mpaa_rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public Links getLinks() {
        return links;
    }

    public ArrayList<Cast> getAbridged_cast() {
        return abridged_cast;
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
        parcel.writeString(synopsis);
        parcel.writeString(mpaa_rating);
        parcel.writeString(runtime);
        parcel.writeValue(posters);
        parcel.writeValue(ratings);
        parcel.writeValue(links);
        parcel.writeValue(abridged_cast);
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
        synopsis = in.readString();
        mpaa_rating = in.readString();
        runtime = in.readString();
        posters = (Posters)in.readValue(Posters.class.getClassLoader());
        ratings = (Ratings)in.readValue(Ratings.class.getClassLoader());
        links = (Links)in.readValue(Links.class.getClassLoader());
        abridged_cast = (ArrayList)in.readValue(Cast.class.getClassLoader());
    }
}
