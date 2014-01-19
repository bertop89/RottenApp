package com.rottenapp.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.commonsware.cwac.merge.MergeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rottenapp.R;
import com.rottenapp.adapters.MovieAdapter;
import com.rottenapp.adapters.NavDrawerListAdapter;
import com.rottenapp.fragments.FavouritesFragment;
import com.rottenapp.helpers.InternalStorage;
import com.rottenapp.helpers.URLHelper;
import com.rottenapp.helpers.VolleySingleton;
import com.rottenapp.models.Movie;
import com.rottenapp.models.NavDrawerItem;

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

    private SearchView searchView;
    private String[] mDrawerArray;
    private TypedArray navMenuIcons;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ArrayList<NavDrawerItem> navDrawerItems;

    private Handler messageHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferencesActivity.THEME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        loadDrawer();
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String tag = null;
        switch (position) {
            case 0:
                fragment = new PlaceholderFragment();
                tag = "main";
                break;
            case 1:
                fragment = new FavouritesFragment();
                tag = "favourites";
                break;
        default:
            break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment,tag).commit();

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
                Intent myIntent = new Intent(this, PreferencesActivity.class);
                startActivityForResult(myIntent, 1);
                return true;
            case R.id.refresh:
                PlaceholderFragment fragment = (PlaceholderFragment) getFragmentManager().findFragmentByTag("main");
                try {
                    fragment.mPullToRefreshLayout.setRefreshing(true);
                    fragment.pendingRequest+=4;
                    fragment.refreshBoxOffice();
                    fragment.refreshUpcoming();
                    fragment.refreshDVDUpcoming();
                    fragment.refreshTopDVD();
                } catch (NullPointerException e) {

                } finally {
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){

                // Workaround based on http://stackoverflow.com/questions/10844112/runtimeexception-performing-pause-of-activity-that-is-not-resumed
                final Activity ctx = this;
                messageHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ctx.recreate();
                    }
                }, 1);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        searchView.setQuery("", false);
        searchView.setIconified(true);
        String URL = URLHelper.getSearchURL(s);
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


        private ArrayList topList, upcomingList, topDVDList, upcomingDVDList;
        Type typeList = new TypeToken<List <Movie>>(){}.getType();
        ListView mainList;
        MovieAdapter boxAdapter, upcomingAdapter, topDVDAdapter, upcomingDVDAdapter;
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

            createBox();

            createUpcoming();

            createTopDVD();

            createUpcomingDVD();

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

        private void createUpcoming() {
            View header2 = View.inflate(getActivity(), R.layout.header,null);
            ((TextView)header2.findViewById(R.id.tvHeader)).setText(R.string.upcoming);
            header2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullUpcoming(v);
                }
            });
            mergeAdapter.addView(header2);

            upcomingList = new ArrayList<Movie>();
            upcomingAdapter = new MovieAdapter(getActivity(),upcomingList,0);
            mergeAdapter.addAdapter(upcomingAdapter);
        }

        private void createBox() {
            View header1 = View.inflate(getActivity(), R.layout.header,null);
            ((TextView)header1.findViewById(R.id.tvHeader)).setText(R.string.box_office);
            header1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullBox(v);
                }
            });
            mergeAdapter.addView(header1);

            topList = new ArrayList<Movie>();
            boxAdapter = new MovieAdapter(getActivity(), topList,0);
            mergeAdapter.addAdapter(boxAdapter);
        }

        private void createTopDVD() {
            View header1 = View.inflate(getActivity(), R.layout.header,null);
            ((TextView)header1.findViewById(R.id.tvHeader)).setText(R.string.top_dvd);
            header1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullTopDVD(v);
                }
            });
            mergeAdapter.addView(header1);

            topDVDList = new ArrayList<Movie>();
            topDVDAdapter = new MovieAdapter(getActivity(), topDVDList,1);
            mergeAdapter.addAdapter(topDVDAdapter);
        }

        private void createUpcomingDVD() {
            View header1 = View.inflate(getActivity(), R.layout.header,null);
            ((TextView)header1.findViewById(R.id.tvHeader)).setText(R.string.upcoming_dvd);
            header1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullUpcomingDVD(v);
                }
            });
            mergeAdapter.addView(header1);

            upcomingDVDList = new ArrayList<Movie>();
            upcomingDVDAdapter = new MovieAdapter(getActivity(), upcomingDVDList,1);
            mergeAdapter.addAdapter(upcomingDVDAdapter);
        }

        private void loadData() {
            String cachedEntries = null;
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

            try {
                cachedEntries = (String) InternalStorage.readObject(getActivity(), "boxlist");
                topList.addAll((Collection) gson.fromJson(cachedEntries, typeList));
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

            try {
                cachedEntries = (String) InternalStorage.readObject(getActivity(), "topdvdlist");
                topDVDList.addAll((Collection) gson.fromJson(cachedEntries,typeList));
                topDVDAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                refreshTopDVD();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                cachedEntries = (String) InternalStorage.readObject(getActivity(), "upcomingdvdlist");
                upcomingDVDList.addAll((Collection) gson.fromJson(cachedEntries,typeList));
                upcomingDVDAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                refreshDVDUpcoming();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        public void refreshUpcoming() {
            String URL = URLHelper.getUpcomingURL(3);
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
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
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
            String URL = URLHelper.getTopBoxOffice(3);
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
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                            topList.clear();
                            topList.addAll((Collection) gson.fromJson(movies.toString(), typeList));
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

        public void refreshTopDVD() {
            String URL = URLHelper.getTopDVD(3);
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
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                            topDVDList.clear();
                            topDVDList.addAll((Collection) gson.fromJson(movies.toString(), typeList));
                            topDVDAdapter.notifyDataSetChanged();
                            try {
                                InternalStorage.writeObject(getActivity(), "topdvdlist", movies.toString());
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

        public void refreshDVDUpcoming() {
            String URL = URLHelper.getUpcomingDVD(3);
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
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                            upcomingDVDList.clear();
                            upcomingDVDList.addAll((Collection) gson.fromJson(movies.toString(), typeList));
                            upcomingDVDList = new ArrayList<Movie>(upcomingDVDList.subList(0,3));
                            upcomingDVDAdapter.notifyDataSetChanged();
                            try {
                                InternalStorage.writeObject(getActivity(), "upcomingdvdlist", movies.toString());
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
            String URL = URLHelper.getTopBoxOffice(0);
            Intent myIntent = new Intent(v.getContext(), ListActivity.class);
            myIntent.putExtra("URL",URL);
            myIntent.putExtra("type",0);
            myIntent.putExtra("title",getString(R.string.box_office));
            startActivity(myIntent);
            return false;
        }

        public boolean openFullUpcoming(View v) {
            String URL = URLHelper.getUpcomingURL(0);
            Intent myIntent = new Intent(v.getContext(), ListActivity.class);
            myIntent.putExtra("URL",URL);
            myIntent.putExtra("type",0);
            myIntent.putExtra("title",getString(R.string.upcoming));
            startActivity(myIntent);
            return false;
        }

        public boolean openFullTopDVD(View v) {
            String URL = URLHelper.getTopDVD(0);
            Intent myIntent = new Intent(v.getContext(), ListActivity.class);
            myIntent.putExtra("URL",URL);
            myIntent.putExtra("type",1);
            myIntent.putExtra("title",getString(R.string.top_dvd));
            startActivity(myIntent);
            return false;
        }

        public boolean openFullUpcomingDVD(View v) {
            String URL = URLHelper.getUpcomingDVD(0);
            Intent myIntent = new Intent(v.getContext(), ListActivity.class);
            myIntent.putExtra("URL",URL);
            myIntent.putExtra("type",1);
            myIntent.putExtra("title",getString(R.string.upcoming_dvd));
            startActivity(myIntent);
            return false;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onRefreshStarted(View view) {
            pendingRequest+=4;
            refreshBoxOffice();
            refreshUpcoming();
            refreshDVDUpcoming();
            refreshTopDVD();
        }
    }

}