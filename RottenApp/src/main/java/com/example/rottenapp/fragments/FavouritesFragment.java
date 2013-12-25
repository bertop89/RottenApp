package com.example.rottenapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rottenapp.models.Movie;
import com.example.rottenapp.data.MySQLiteHelper;
import com.example.rottenapp.R;
import com.example.rottenapp.activities.MovieActivity;
import com.example.rottenapp.adapters.MovieAdapter;

import java.util.ArrayList;

/**
* A placeholder fragment containing a simple view.
*/
public class FavouritesFragment extends Fragment {

    ListView favouritesView;
    TextView tvEmpty;
    ArrayList<Movie>  movies;
    MySQLiteHelper db;

    public FavouritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_layout, container, false);
        favouritesView = (ListView) rootView.findViewById(R.id.lvMainList);
        favouritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(view.getContext(), MovieActivity.class);
                // save id
                Movie m = movies.get(i);
                myIntent.putExtra("movie",m);

                startActivity(myIntent);
            }
        });
        tvEmpty = (TextView)rootView.findViewById(android.R.id.empty);
        tvEmpty.setText(R.string.empty_favourites);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db = new MySQLiteHelper(activity);

    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadCursorTask().execute();
    }



    private class LoadCursorTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            movies =  db.getMovies();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            favouritesView.setAdapter(new MovieAdapter(getActivity(),movies,2));
            favouritesView.setEmptyView(tvEmpty);
            super.onPostExecute(aVoid);
        }
    }
}
