package com.rottenapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rottenapp.R;
import com.rottenapp.adapters.CastAdapter;
import com.rottenapp.helpers.VolleySingleton;
import com.rottenapp.models.Cast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alberto Polidura on 23/12/13.
 */
public class CastActivity extends Activity {

    private ArrayList castList;
    Type typeList = new TypeToken<List<Cast>>(){}.getType();
    GridView gridView;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = (GridView) findViewById(R.id.gvMainGrid);
        loading = (ProgressBar) findViewById(R.id.progress);
        loading.setVisibility(View.VISIBLE);
        getRequest(getIntent().getStringExtra("URL"));
    }

    private void getRequest(String input) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, input, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONArray critics = new JSONArray();
                        try {
                            critics = response.getJSONArray("cast");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        castList = new ArrayList<Cast>();
                        Gson gson = new Gson();
                        castList = gson.fromJson(critics.toString(),typeList);
                        loading.setVisibility(View.GONE);
                        gridView.setAdapter(new CastAdapter(CastActivity.this,castList));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = new Intent(this, MovieActivity.class);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
