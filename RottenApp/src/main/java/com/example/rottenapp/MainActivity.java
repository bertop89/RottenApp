package com.example.rottenapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends ListActivity {

    private ArrayList movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void onListItemClick(ListView parent, View v, int position, long id) {

        Intent myIntent = new Intent(v.getContext(), MovieActivity.class);
        // save id
        Movie m = (Movie)movieList.get(position);
        myIntent.putExtra("movie",m);

        startActivity(myIntent);
    }

    public void searchTitle(View v) {
        EditText title = (EditText) findViewById(R.id.editText);
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
        String input = title.getText().toString();
        new GetData().execute(input);
    }




    private class GetData extends AsyncTask<String, Void, ArrayList> {

        private final String apikey = "d2uywhtvna2y9fhm4eq4ydzc";

        @Override
        protected ArrayList doInBackground(String... data) {
            String title= data[0].replace(' ','+');
            String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey="+apikey+"&q="+title;
            String jsonReturnText = "";

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
            JSONArray movies = new JSONArray();
            try {
                JSONObject oData = new JSONObject(jsonReturnText);
                movies = oData.getJSONArray("movies");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            movieList = new ArrayList<Movie>();
            Gson gson = new Gson();
            try {
                for(int i=0;i<movies.length();i++) {
                    JSONObject movie=movies.getJSONObject(i);
                    Movie currentMovie = gson.fromJson(movie.toString(),Movie.class);
                    movieList.add(currentMovie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }


        protected void onPostExecute(ArrayList result) {
            ListView results = getListView();
            results.setAdapter(new MyAdapter(MainActivity.this,movieList));
        }
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }



}
