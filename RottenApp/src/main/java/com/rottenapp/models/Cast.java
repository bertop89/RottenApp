package com.rottenapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Alberto Polidura on 13/12/13.
 */
public class Cast implements Parcelable{
    private String name, id;
    private ArrayList<String> characters = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public ArrayList<String> getCharacters() {
        return characters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeStringList(characters);
    }

    public static final Parcelable.Creator<Cast> CREATOR = new Parcelable.Creator<Cast>() {
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    private Cast(Parcel in) {
        name = in.readString();
        id = in.readString();
        in.readStringList(characters);
    }
}
