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

    String id,title,year;
    ImageView ivPoster;
    TextView tvTitle, tvYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_main);
        Intent i = getIntent();
        Movie movieObject = (Movie) i.getParcelableExtra("movie");
        id = movieObject.getId();
        title = movieObject.getTitle();
        year = movieObject.getYear();
        setTitle("Movie");
        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvYear = (TextView) findViewById(R.id.tvYear);
        tvTitle.setText(title);
        tvYear.setText("("+year+")");
        new FillMovieTask().execute(id);
    }

    private class FillMovieTask extends AsyncTask<String, Void, Bitmap> {

        private final String apikey = "d2uywhtvna2y9fhm4eq4ydzc";

        @Override
        protected Bitmap doInBackground(String... strings) {
            String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/"+strings[0]+".json?apikey="+apikey;
            String jsonReturnText = "";
            InputStream in = null;

            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 10000; // 10 second timeout for connecting to site
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 30000; // 30 second timeout for obtaining results
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpGet httpget = new HttpGet(URL);

            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity r_entity = response.getEntity();
                jsonReturnText = EntityUtils.toString(r_entity);
            } catch (Exception e) {
                jsonReturnText = e.getMessage();
            }

            try {
                JSONObject oData = new JSONObject(jsonReturnText);
                JSONObject posters = oData.getJSONObject("posters");
                String imageURL = posters.getString("detailed");
                try {
                    in = new java.net.URL(imageURL).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
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
