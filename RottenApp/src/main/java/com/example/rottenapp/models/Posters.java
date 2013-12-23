package com.example.rottenapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto Polidura on 25/11/13.
 */
public class Posters implements Parcelable {
    private String thumbnail, profile, detailed, original;

    public String getThumbnail() {
        return thumbnail;
    }

    public String getProfile() {
        return profile;
    }

    public String getDetailed() {
        return detailed;
    }

    public String getOriginal() {
        return original;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(thumbnail);
        parcel.writeString(profile);
        parcel.writeString(detailed);
        parcel.writeString(original);
    }

    public static final Parcelable.Creator<Posters> CREATOR = new Parcelable.Creator<Posters>() {
        public Posters createFromParcel(Parcel in) {
            return new Posters(in);
        }

        public Posters[] newArray(int size) {
            return new Posters[size];
        }
    };

    private Posters(Parcel in) {
        thumbnail = in.readString();
        profile = in.readString();
        detailed = in.readString();
        original = in.readString();
    }
}
