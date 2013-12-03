package com.example.rottenapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    private final String apikey = "d2uywhtvna2y9fhm4eq4ydzc";
    private ArrayList boxList, upcomingList;
    Type typeList = new TypeToken<List <Movie>>(){}.getType();
    ListView boxView, upcomingView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        boxView = (ListView) findViewById(R.id.listBoxOffice);
        boxView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(view.getContext(), MovieActivity.class);
                // save id
                Movie m = (Movie)boxList.get(i);
                myIntent.putExtra("movie",m);

                startActivity(myIntent);
            }
        });
        upcomingView = (ListView) findViewById(R.id.listUpcoming);
        upcomingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(view.getContext(), MovieActivity.class);
                // save id
                Movie m = (Movie)upcomingList.get(i);
                myIntent.putExtra("movie",m);

                startActivity(myIntent);
            }
        });

        loadData();
    }

    private void loadData() {
        String cachedEntries = null;
        Gson gson = new Gson();

        try {
            cachedEntries = (String) InternalStorage.readObject(MainActivity.this, "boxlist");
            boxList = gson.fromJson(cachedEntries,typeList);
            boxView.setAdapter(new MyAdapter(this,boxList));
        } catch (IOException e) {
            refreshBoxOffice();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            cachedEntries = (String) InternalStorage.readObject(MainActivity.this, "upcominglist");
            upcomingList = gson.fromJson(cachedEntries,typeList);
            upcomingView.setAdapter(new MyAdapter(this,upcomingList));
        } catch (IOException e) {
            refreshUpcoming();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void refreshUpcoming() {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?apikey="+apikey+"&page_limit=4";
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
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

                        upcomingList = new ArrayList<Movie>();
                        Gson gson = new Gson();
                        upcomingList = gson.fromJson(movies.toString(),typeList);
                        upcomingView.setAdapter(new MyAdapter(MainActivity.this,upcomingList));
                        try {
                            InternalStorage.writeObject(MainActivity.this, "upcominglist", movies.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    public void refreshBoxOffice() {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey="+apikey+"&country=ES&limit=4";
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
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
                        boxList = new ArrayList<Movie>();
                        Gson gson = new Gson();
                        boxList = gson.fromJson(movies.toString(),typeList);
                        boxView.setAdapter(new MyAdapter(MainActivity.this,boxList));
                        try {
                            InternalStorage.writeObject(MainActivity.this, "boxlist", movies.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    public boolean openFullBox(View v) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey="+apikey+"&country=ES";
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("URL",URL);
        myIntent.putExtra("title",getString(R.string.box_office));
        startActivity(myIntent);
        return false;
    }

    public boolean openFullUpcoming(View v) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?apikey="+apikey+"&country=ES";
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("URL",URL);
        myIntent.putExtra("title",getString(R.string.upcoming));
        startActivity(myIntent);
        return false;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
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
            View rootView = inflater.inflate(R.layout.main_fragment, container, false);
            return rootView;
        }
    }

}
