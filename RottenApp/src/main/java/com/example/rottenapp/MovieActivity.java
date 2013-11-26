package com.example.rottenapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MovieActivity extends ActionBarActivity {

    Movie currentMovie;
    ImageView ivPoster, ivCritics, ivAudience;
    TextView tvTitle, tvYear, tvCritics, tvAudience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_main);
        Intent i = getIntent();
        currentMovie = (Movie) i.getParcelableExtra("movie");
        setTitle("Movie");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        ivCritics = (ImageView) findViewById(R.id.ivCritics);
        ivAudience = (ImageView) findViewById(R.id.ivAudience);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvYear = (TextView) findViewById(R.id.tvYear);
        tvCritics = (TextView) findViewById(R.id.tvCritics);
        tvAudience = (TextView) findViewById(R.id.tvAudience);
        tvTitle.setText(currentMovie.getTitle());
        tvYear.setText("("+currentMovie.getYear()+")");
        tvCritics.setText(currentMovie.getRatings().getCritics_score()+"% of critics liked it");
        tvAudience.setText(currentMovie.getRatings().getAudience_score()+"% of users liked it");
        representRatings();
        new FillMovieTask().execute(currentMovie.getId());
    }

    public void representRatings () {
        String critics = currentMovie.getRatings().getCritics_rating();
        if (critics.equals("Certified Fresh")) {
            ivCritics.setImageResource(R.drawable.fresh);
        } else if (critics.equals("Fresh")) {
            ivCritics.setImageResource(R.drawable.ic_launcher);
        } else if (critics.equals("Rotten")) {
            ivCritics.setImageResource(R.drawable.rotten);
        }

        String audience = currentMovie.getRatings().getAudience_rating();
        if (audience.equals("Upright")) {
            ivAudience.setImageResource(R.drawable.bucket);
        } else if (audience.equals("Spilled")) {
            ivAudience.setImageResource(R.drawable.spilled);
        }
    }

    private class FillMovieTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            InputStream in = null;
            String imageURL = currentMovie.getPosters().getDetailed();
            try {
                in = new java.net.URL(imageURL).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap image = BitmapFactory.decodeStream(in);
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ivPoster.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.movie_fragment_main, container, false);
            return rootView;
        }
    }

}
