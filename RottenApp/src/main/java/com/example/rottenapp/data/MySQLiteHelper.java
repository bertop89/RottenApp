package com.example.rottenapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.rottenapp.models.Movie;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Alberto Polidura on 6/12/13.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rotten.db";
    private static final int DATABASE_VERSION = 4;


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_FAVOURITE_TABLE = "CREATE TABLE favourite_movies ( id INTEGER PRIMARY KEY, json TEXT)";
        // create movies table
        sqLiteDatabase.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favourite_movies");
        onCreate(sqLiteDatabase);
    }

    public void addMovie(Movie movie){


        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put("id", Integer.parseInt(movie.getId())); // get title
        values.put("json", gson.toJson(movie)); // get author

        // 3. insert
        db.insert("favourite_movies", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void deleteMovie(Movie movie) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("favourite_movies", //table name
                "id = ?",  // selections
                new String[] { String.valueOf(movie.getId()) }); //selections args

        // 3. close
        db.close();
    }

    public boolean checkMovie(Movie movie) {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.rawQuery("SELECT * FROM favourite_movies WHERE id=?", new String[]{movie.getId()});

        // 3. check cursor result
        /* record exist *//* record not exist */
        return cursor.getCount() > 0;
    }

    public ArrayList<Movie> getMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        // 1. build the query
        String query = "SELECT * FROM favourite_movies";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Movie movie = null;
        Gson gson = new Gson();
        if (cursor.moveToFirst()) {
            do {
                movie = gson.fromJson(cursor.getString(1),Movie.class);
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", movies.toString());

        // return books
        return movies;
    }
}