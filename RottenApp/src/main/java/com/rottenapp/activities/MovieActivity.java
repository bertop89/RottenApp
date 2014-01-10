package com.rottenapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rottenapp.adapters.CastAdapter;

import com.rottenapp.adapters.SimilarAdapter;
import com.rottenapp.data.Global;
import com.rottenapp.helpers.ExpandableHeightGridView;

import com.rottenapp.models.Cast;
import com.rottenapp.models.Movie;
import com.rottenapp.data.MySQLiteHelper;
import com.rottenapp.R;
import com.rottenapp.helpers.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieActivity extends Activity {

    Movie currentMovie;
    ImageView ivCritics, ivAudience,ivPoster;
    ProgressBar progressBar;
    TextView tvTitle, tvYear, tvCritics, tvAudience, tvSynopsis, tvRating, tvRuntime, tvTheater, tvDVD, tvEmptySimilar, tvEmptyCast;
    View headerCast, headerSimilar;
    MySQLiteHelper db;
    private String apikey;

    ExpandableHeightGridView castView;
    ArrayList<Cast> cast = new ArrayList<Cast>();

    ExpandableHeightGridView gridSimilar;
    ArrayList<Movie> similarList;
    private Type typeList = new TypeToken<List<Movie>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);
        Intent i = getIntent();
        currentMovie = i.getParcelableExtra("movie");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final Global global = (Global)getApplicationContext();
        apikey = global.getApikey();

        representUI();
        representRatings();
        representSimilar();
        db = new MySQLiteHelper(this);
        db.getWritableDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    private void representSimilar() {
        String URL = currentMovie.getLinks().getSimilar() + "?limit=4&apikey="+apikey;
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

                        similarList = new ArrayList<Movie>();
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                        similarList = gson.fromJson(movies.toString(),typeList);
                        gridSimilar.setFocusable(false);
                        gridSimilar.setAdapter(new SimilarAdapter(getApplicationContext(), similarList));
                        gridSimilar.setEmptyView(tvEmptySimilar);
                        gridSimilar.setExpanded(true);
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
        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(getRequest);


    }

    public void representUI() {
        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        progressBar = (ProgressBar) findViewById(R.id.progressPoster);
        ivCritics = (ImageView) findViewById(R.id.ivCritics);
        ivAudience = (ImageView) findViewById(R.id.ivAudience);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvYear = (TextView) findViewById(R.id.tvYear);
        tvCritics = (TextView) findViewById(R.id.tvCritics);
        tvAudience = (TextView) findViewById(R.id.tvAudience);
        tvSynopsis = (TextView) findViewById(R.id.tvSynopsis);
        tvRating = (TextView) findViewById(R.id.tvRatingValue);
        tvRuntime = (TextView) findViewById(R.id.tvRunningValue);
        tvTheater = (TextView) findViewById(R.id.tvTheaterReleaseDate);
        tvDVD = (TextView) findViewById(R.id.tvDVDReleaseDate);

        tvEmptyCast = (TextView)findViewById(R.id.empty_cast);
        tvEmptyCast.setText(R.string.empty_cast);
        headerCast = findViewById(R.id.castSeparator);
        headerCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullCast(v);
            }
        });
        castView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        castView.setFocusable(false);
        cast = currentMovie.getAbridged_cast();
        castView.setAdapter(new CastAdapter(this,cast));
        castView.setEmptyView(tvEmptyCast);
        castView.setExpanded(true);


        tvTitle.setText(currentMovie.getTitle());
        tvYear.setText("("+currentMovie.getYear()+")");
        String critic_score = currentMovie.getRatings().getCritics_score();
        if (!critic_score.equals("-1")) {
            tvCritics.setText(critic_score+"% "+ getResources().getString(R.string.critics));
        } else {
            tvCritics.setText(getResources().getString(R.string.no_score));
        }
        String audience_score = currentMovie.getRatings().getAudience_score();
        if (!audience_score.equals("-1")) {
            tvAudience.setText(audience_score+"% "+ getResources().getString(R.string.audience));
        } else {
            tvCritics.setText(getResources().getString(R.string.no_score));
        }
        String synopsis = currentMovie.getSynopsis();
        if (synopsis.equals("")) {
            tvSynopsis.setText(R.string.no_synopsis);
        } else {
            tvSynopsis.setText(synopsis);
        }
        String mpaa = currentMovie.getMpaa_rating();
        if (mpaa.equals("Unrated")) {
            tvRating.setText(R.string.unrated);
        } else {
            tvRating.setText(mpaa);
        }

        tvRuntime.setText(currentMovie.getRuntime()+" min");

        SimpleDateFormat iso = new SimpleDateFormat("dd/MM/yyyy");
        Date theater = currentMovie.getRelease_dates().getTheater();
        if (theater.getTime()==0) {
            tvTheater.setText(R.string.unknown);
        } else {
            tvTheater.setText(iso.format(theater));
        }
        Date dvd = currentMovie.getRelease_dates().getDvd();
        if (dvd.getTime()==0) {
            tvDVD.setText(R.string.unknown);
        } else {
            tvDVD.setText(iso.format(dvd));
        }

        VolleySingleton.getInstance(this).getImageLoader().get(currentMovie.getPosters().getDetailed(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer.getBitmap() != null) {
                    ivPoster.setImageBitmap(imageContainer.getBitmap());
                    progressBar.setVisibility(View.GONE);
                    ivPoster.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ivPoster.setImageResource(R.drawable.rotten);
            }
        });

        headerSimilar = findViewById(R.id.similarSeparator);
        headerSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullSimilar(v);
            }
        });
        gridSimilar = (ExpandableHeightGridView) findViewById(R.id.gridSimilar);
        gridSimilar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), MovieActivity.class);
                // save id
                Movie m = (Movie)gridSimilar.getItemAtPosition(position);
                myIntent.putExtra("movie",m);

                startActivity(myIntent);
            }
        });
        tvEmptySimilar = (TextView)findViewById(R.id.empty_similar);
        tvEmptySimilar.setText(R.string.empty_similar);

    }

    public void representRatings () {
        String critics = currentMovie.getRatings().getCritics_rating();
        if (critics!=null) {
            if (critics.equals("Certified Fresh")) {
                ivCritics.setImageResource(R.drawable.fresh);
            } else if (critics.equals("Fresh")) {
                ivCritics.setImageResource(R.drawable.ic_launcher);
            } else if (critics.equals("Rotten")) {
                ivCritics.setImageResource(R.drawable.rotten);
            }
        } else {
            ivCritics.setImageResource(R.drawable.more);
        }

        String audience = currentMovie.getRatings().getAudience_rating();
        if (audience!=null) {
            if (audience.equals("Upright")) {
                ivAudience.setImageResource(R.drawable.bucket);
            } else if (audience.equals("Spilled")) {
                ivAudience.setImageResource(R.drawable.spilled);
            }
        } else {
            ivAudience.setImageResource(R.drawable.more);
        }
    }

    public void openFullPoster(View v) {
        Intent myIntent = new Intent(v.getContext(), FullImageActivity.class);
        myIntent.putExtra("url",currentMovie.getPosters().getDetailed());
        startActivity(myIntent);
    }

    public void openFullCritics(View v) {
        Intent myIntent = new Intent(v.getContext(), CriticsActivity.class);
        myIntent.putExtra("URL",currentMovie.getLinks().getReviews()+"?apikey="+apikey);
        startActivity(myIntent);
    }

    private void openFullCast(View v) {
        Intent myIntent = new Intent(v.getContext(), CastActivity.class);
        myIntent.putExtra("URL",currentMovie.getLinks().getCast()+"?apikey="+apikey);
        startActivity(myIntent);
    }

    private void openFullSimilar(View v) {
        Intent myIntent = new Intent(v.getContext(), SimilarActivity.class);
        myIntent.putExtra("URL",currentMovie.getLinks().getSimilar()+"?apikey="+apikey);
        startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movie, menu);

        MenuItem favItem = menu.findItem(R.id.action_favourite);
        if (db.checkMovie(currentMovie)) {
            favItem.setChecked(true);
            favItem.setIcon(R.drawable.ic_action_important);
        } else {
            favItem.setChecked(false);
            favItem.setIcon(R.drawable.ic_action_not_important);
        }

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
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
            case R.id.action_favourite:
                if (!item.isChecked()) {
                    item.setIcon(R.drawable.ic_action_important);
                    db.addMovie(currentMovie);
                } else {
                    item.setIcon(R.drawable.ic_action_not_important);
                    db.deleteMovie(currentMovie);
                }
                item.setChecked(!item.isChecked());
                return true;
            case R.id.menu_item_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String text = currentMovie.getTitle() + " - " + currentMovie.getLinks().getAlternate();
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                // Always use string resources for UI text.
                // This says something like "Share this photo with"
                String title = getResources().getString(R.string.chooser_title);
                // Create and start the chooser
                Intent chooser = Intent.createChooser(sendIntent, title);
                startActivity(chooser);
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
            View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
            return rootView;
        }
    }

}
