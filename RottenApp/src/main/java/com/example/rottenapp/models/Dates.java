package com.example.rottenapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Alberto Polidura on 24/12/13.
 */
public class Dates implements Parcelable {
    public Date theater,dvd;

    @Override
    public int describeContents() {
        return 0;
    }

    public Date getTheater() {
        return theater;
    }

    public Date getDvd() {
        return dvd;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeLong(theater.getTime());
        } catch (NullPointerException e) {
            dest.writeLong(0);
        }
        try {
            dest.writeLong(dvd.getTime());
        } catch (NullPointerException e) {
            dest.writeLong(0);
        }

    }

    public static final Parcelable.Creator<Dates> CREATOR = new Parcelable.Creator<Dates>() {
        public Dates createFromParcel(Parcel in) {
            return new Dates(in);
        }

        public Dates[] newArray(int size) {
            return new Dates[size];
        }
    };

    private Dates(Parcel in) {
        theater = new Date(in.readLong());
        dvd = new Date(in.readLong());
    }
}
