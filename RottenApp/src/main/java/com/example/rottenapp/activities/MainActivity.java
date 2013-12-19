package com.example.rottenapp.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.commonsware.cwac.merge.MergeAdapter;
import com.example.rottenapp.data.Global;
import com.example.rottenapp.fragments.FavouritesFragment;
import com.example.rottenapp.helpers.InternalStorage;
import com.example.rottenapp.models.Movie;
import com.example.rottenapp.models.NavDrawerItem;
import com.example.rottenapp.adapters.NavDrawerListAdapter;
import com.example.rottenapp.R;
import com.example.rottenapp.helpers.VolleySingleton;
import com.example.rottenapp.adapters.MovieAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    private static String apikey;
    private SearchView searchView;
    private String[] mDrawerArray;
    private TypedArray navMenuIcons;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    private ArrayList<NavDrawerItem> navDrawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        loadDrawer();
        final Global global = (Global)getApplicationContext();
        apikey = global.getApikey();
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new PlaceholderFragment();
                break;
            case 1:
                fragment = new FavouritesFragment();
                break;
        default:
            break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            getActionBar().setTitle(mDrawerArray[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void loadDrawer() {
        //Load Navigation Drawer
        mDrawerArray = getResources().getStringArray(R.array.drawer_list);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        //Add Items to the Drawer
        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem(mDrawerArray[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(mDrawerArray[1], navMenuIcons.getResourceId(1, -1)));

        // Set the adapter for the list view
        mDrawerList.setAdapter(new NavDrawerListAdapter(getApplicationContext(),navDrawerItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                displayView(i);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }





    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.refresh:
                /*PlaceholderFragment fragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.main_container);
                mPullToRefreshLayout.setRefreshing(true);
                pendingRequest+=2;
                refreshBoxOffice();
                refreshUpcoming();*/
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public static class PlaceholderFragment extends Fragment implements OnRefreshListener {


        private ArrayList boxList, upcomingList;
        Type typeList = new TypeToken<List <Movie>>(){}.getType();
        ListView mainList;
        MovieAdapter boxAdapter, upcomingAdapter;
        MergeAdapter mergeAdapter;

        private PullToRefreshLayout mPullToRefreshLayout;
        private int pendingRequest = 0;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_fragment, container, false);
            loadUI(rootView);
            loadData();
            return rootView;
        }

        private void loadUI(View V) {
            mergeAdapter = new MergeAdapter();
            mainList = (ListView) V.findViewById(R.id.listMain);

            View header1 = View.inflate(getActivity(),R.layout.header,null);
            ((TextView)header1.findViewById(R.id.tvHeader)).setText(R.string.box_office);
            header1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullBox(v);
                }
            });
            mergeAdapter.addView(header1);

            boxList = new ArrayList<Movie>();
            boxAdapter = new MovieAdapter(getActivity(),boxList);
            mergeAdapter.addAdapter(boxAdapter);

            View header2 = View.inflate(getActivity(),R.layout.header,null);
            ((TextView)header2.findViewById(R.id.tvHeader)).setText(R.string.upcoming);
            header2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullUpcoming(v);
                }
            });
            mergeAdapter.addView(header2);

            upcomingList = new ArrayList<Movie>();
            upcomingAdapter = new MovieAdapter(getActivity(),upcomingList);
            mergeAdapter.addAdapter(upcomingAdapter);

            // Now find the PullToRefreshLayout to setup
            mPullToRefreshLayout = (PullToRefreshLayout) V.findViewById(R.id.ptr_layout);
            // Now setup the PullToRefreshLayout
            ActionBarPullToRefresh.from(getActivity())
                    // Mark All Children as pullable
                    .allChildrenArePullable()
                            // Set the OnRefreshListener
                    .listener(this)
                            // Finally commit the setup to our PullToRefreshLayout
                    .setup(mPullToRefreshLayout);

            mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(view.getContext(), MovieActivity.class);
                    // save id
                    Movie m = (Movie)mergeAdapter.getItem(position);
                    myIntent.putExtra("movie",m);

                    startActivity(myIntent);
                }
            });

            mainList.setAdapter(mergeAdapter);
        }

        private void loadData() {
            String cachedEntries = null;
            Gson gson = new Gson();

            try {
                cachedEntries = (String) InternalStorage.readObject(getActivity(), "boxlist");
                boxList.addAll((Collection) gson.fromJson(cachedEntries,typeList));
                boxAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                refreshBoxOffice();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                cachedEntries = (String) InternalStorage.readObject(getActivity(), "upcominglist");
                upcomingList.addAll((Collection) gson.fromJson(cachedEntries,typeList));
                upcomingAdapter.notifyDataSetChanged();
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
                            Gson gson = new Gson();
                            upcomingList.clear();
                            upcomingList.addAll((Collection) gson.fromJson(movies.toString(), typeList));
                            upcomingAdapter.notifyDataSetChanged();
                            try {
                                InternalStorage.writeObject(getActivity(), "upcominglist", movies.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            pendingRequest--;
                            if (pendingRequest==0) mPullToRefreshLayout.setRefreshComplete();
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
            VolleySingleton.getInstance(getActivity()).getRequestQueue().add(getRequest);
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
                            Gson gson = new Gson();
                            boxList.clear();
                            boxList.addAll((Collection) gson.fromJson(movies.toString(), typeList));
                            boxAdapter.notifyDataSetChanged();
                            try {
                                InternalStorage.writeObject(getActivity(), "boxlist", movies.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            pendingRequest--;
                            if (pendingRequest==0) mPullToRefreshLayout.setRefreshComplete();
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
            VolleySingleton.getInstance(getActivity()).getRequestQueue().add(getRequest);
        }

        public boolean openFullBox(View v) {
            String URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey="+apikey+"&country=ES";
            Intent myIntent = new Intent(v.getContext(), ListActivity.class);
            myIntent.putExtra("URL",URL);
            myIntent.putExtra("title",getString(R.string.box_office));
            startActivity(myIntent);
            return false;
        }

        public boolean openFullUpcoming(View v) {
            String URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?apikey="+apikey+"&country=ES";
            Intent myIntent = new Intent(v.getContext(), ListActivity.class);
            myIntent.putExtra("URL",URL);
            myIntent.putExtra("title",getString(R.string.upcoming));
            startActivity(myIntent);
            return false;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onRefreshStarted(View view) {
            pendingRequest+=2;
            refreshBoxOffice();
            refreshUpcoming();
        }
    }

}