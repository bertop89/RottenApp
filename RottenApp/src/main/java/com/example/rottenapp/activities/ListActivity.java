package com.example.rottenapp.activities;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.rottenapp.data.Global;
import com.example.rottenapp.models.Movie;
import com.example.rottenapp.R;
import com.example.rottenapp.helpers.VolleySingleton;
import com.example.rottenapp.adapters.MovieAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity implements SearchView.OnQueryTextListener {

    private String apikey;
    private int type;
    private ArrayList movieList;
    Type typeList = new TypeToken<List<Movie>>(){}.getType();
    private SearchView searchView;
    private ListView listView;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final Global global = (Global)getApplicationContext();
        apikey = global.getApikey();
        setTitle(getIntent().getStringExtra("title"));
        type = getIntent().getIntExtra("type",2);
        getRequest(getIntent().getStringExtra("URL"));

        listView = (ListView) findViewById(R.id.lvMainList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), MovieActivity.class);
                // save id
                Movie m = (Movie)movieList.get(position);
                myIntent.putExtra("movie",m);

                startActivity(myIntent);
            }
        });
        loading = (ProgressBar) findViewById(R.id.progress);
        loading.setVisibility(View.VISIBLE);
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
                Intent upIntent = new Intent(this, MainActivity.class);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                        movieList = gson.fromJson(movies.toString(),typeList);
                        loading.setVisibility(View.GONE);
                        listView.setAdapter(new MovieAdapter(ListActivity.this, movieList,type));

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
