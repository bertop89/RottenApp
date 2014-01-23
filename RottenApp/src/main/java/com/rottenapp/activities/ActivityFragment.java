package com.rottenapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rottenapp.R;
import com.rottenapp.adapters.MovieAdapter;
import com.rottenapp.helpers.InternalStorage;
import com.rottenapp.helpers.URLHelper;
import com.rottenapp.helpers.VolleySingleton;
import com.rottenapp.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Alberto Polidura on 23/01/14.
 */
public class ActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private GridView mGridView;
    private MovieAdapter mAdapter;
    private ArrayList mList;
    private Type typeList = new TypeToken<List<Movie>>(){}.getType();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_layout, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.lvMainList);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mList = new ArrayList<Movie>();
        mAdapter = new MovieAdapter(getActivity(),mList,0);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        refreshData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent myIntent = new Intent(view.getContext(), MovieActivity.class);
        // save id
        Movie m = (Movie)mAdapter.getItem(position);
        myIntent.putExtra("movie",m);

        startActivity(myIntent);
    }

    public void refreshData() {
        final String type = getArguments().getString("type");
        String cachedEntries = null;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        try {
            cachedEntries = (String) InternalStorage.readObject(getActivity(), type);
            mList.addAll((Collection) gson.fromJson(cachedEntries, typeList));
            mAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            String URL = null;
            if (type.equals("TopBox")) {
                URL = URLHelper.getTopBoxOffice(5);
            } else if (type.equals("UpcomingBox")) {
                URL = URLHelper.getUpcomingURL(5);
            } else if (type.equals("TopDVD")) {
                URL = URLHelper.getTopDVD(5);
            } else if (type.equals("UpcomingDVD")) {
                URL = URLHelper.getUpcomingDVD(5);
            }
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
                            mList.clear();
                            mList.addAll((Collection) gson.fromJson(movies.toString(), typeList));
                            mAdapter.notifyDataSetChanged();
                            try {
                                InternalStorage.writeObject(getActivity(), type, movies.toString());
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
            VolleySingleton.getInstance(getActivity()).getRequestQueue().add(getRequest);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
