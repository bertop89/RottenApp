package com.example.rottenapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Alberto Polidura on 29/11/13.
 */
public class Role implements Parcelable {

    private String name, id;
    private ArrayList<String> characters;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getCharacters() {
        return characters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeStringList(characters);
    }

    public static final Parcelable.Creator<Role> CREATOR = new Parcelable.Creator<Role>() {
        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        public Role[] newArray(int size) {
            return new Role[size];
        }
    };

    private Role(Parcel in) {
        name = in.readString();
        id = in.readString();
        in.readStringList(characters);
    }
}
