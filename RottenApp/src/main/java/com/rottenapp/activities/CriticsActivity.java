package com.rottenapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rottenapp.models.Critic;
import com.rottenapp.R;
import com.rottenapp.helpers.VolleySingleton;
import com.rottenapp.adapters.CriticsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alberto Polidura on 10/12/13.
 */
public class CriticsActivity extends Activity {

    private ArrayList criticList;
    Type typeList = new TypeToken<List<Critic>>(){}.getType();
    ListView listView;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        listView = (ListView) findViewById(R.id.lvMainList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Critic critic = (Critic)criticList.get(i);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(critic.getLinks().getReview()));
                startActivity(intent);
            }
        });
        loading = (ProgressBar) findViewById(R.id.progress);
        loading.setVisibility(View.VISIBLE);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
                            critics = response.getJSONArray("reviews");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        criticList = new ArrayList<Critic>();
                        Gson gson = new Gson();
                        criticList = gson.fromJson(critics.toString(),typeList);
                        loading.setVisibility(View.GONE);
                        listView.setAdapter(new CriticsAdapter(CriticsActivity.this,criticList));
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
