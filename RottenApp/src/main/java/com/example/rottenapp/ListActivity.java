package com.example.rottenapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends android.app.ListActivity implements SearchView.OnQueryTextListener {

    private ArrayList movieList;
    Type typeList = new TypeToken<List<Movie>>(){}.getType();
    private SearchView searchView;
    private final String apikey = "d2uywhtvna2y9fhm4eq4ydzc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra("title"));
        getRequest(getIntent().getStringExtra("URL"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_film));
        searchView.setOnQueryTextListener(this);

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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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

    private void getRequest(String input) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, input, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONArray movies = new JSONArray();
                        try {
                            movies = response.getJSONArray("movies");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        movieList = new ArrayList<Movie>();
                        Gson gson = new Gson();
                        movieList = gson.fromJson(movies.toString(),typeList);
                        ListView results = getListView();
                        results.setAdapter(new MyAdapter(ListActivity.this,movieList));

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        VolleySingleton.getInstance(this).getRequestQueue().add(getRequest);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        searchView.setQuery("", false);
        searchView.setIconified(true);
        String title= s.replace(' ','+');
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey="+apikey+"&q="+title;
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("URL",URL);
        myIntent.putExtra("title",getString(R.string.title_activity_search));
        startActivity(myIntent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
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
            View rootView = inflater.inflate(R.layout.list_fragment, container, false);
            return rootView;
        }
    }



}
