package com.example.rottenapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto Polidura on 10/12/13.
 */
public class Links implements Parcelable {
    public String self, alternate, cast, clips, reviews, similar;

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAlternate() {
        return alternate;
    }

    public String getReviews() {
        return reviews;
    }

    public String getSimilar() {
        return similar;
    }

    public String getCast() {
        return cast;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(self);
        parcel.writeString(alternate);
        parcel.writeString(cast);
        parcel.writeString(clips);
        parcel.writeString(reviews);
        parcel.writeString(similar);
    }

    public static final Parcelable.Creator<Links> CREATOR = new Parcelable.Creator<Links>() {
        public Links createFromParcel(Parcel in) {
            return new Links(in);
        }

        public Links[] newArray(int size) {
            return new Links[size];
        }
    };

    private Links(Parcel in) {
        self = in.readString();
        alternate = in.readString();
        cast = in.readString();
        clips = in.readString();
        reviews = in.readString();
        similar = in.readString();
    }
}
