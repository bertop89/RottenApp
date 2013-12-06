package com.example.rottenapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by alberto on 6/12/13.
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
        if (cursor.getCount()>0)
        {
            return true;
            /* record exist */
        }
        else
        {
            return false;
            /* record not exist */
        }
    }
}
