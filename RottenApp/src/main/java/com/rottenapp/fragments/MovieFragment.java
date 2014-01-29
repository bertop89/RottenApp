package com.rottenapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rottenapp.R;
import com.rottenapp.activities.CastActivity;
import com.rottenapp.activities.CriticsActivity;
import com.rottenapp.activities.FullImageActivity;
import com.rottenapp.activities.MovieActivity;
import com.rottenapp.activities.SimilarActivity;
import com.rottenapp.adapters.CastAdapter;
import com.rottenapp.adapters.SimilarAdapter;
import com.rottenapp.helpers.ExpandableHeightGridView;
import com.rottenapp.helpers.Utils;
import com.rottenapp.helpers.VolleySingleton;
import com.rottenapp.models.Cast;
import com.rottenapp.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alberto Polidura on 29/01/14.
 */
public class MovieFragment extends Fragment {

    Movie currentMovie;
    ImageView ivCritics, ivAudience,ivPoster;
    ProgressBar progressBar;
    TextView tvTitle, tvYear, tvCritics, tvAudience, tvSynopsis, tvRating, tvRuntime, tvTheater, tvDVD, tvEmptySimilar, tvEmptyCast;
    Button bGoogle, bYoutube, bAmazon, bImdb, bRottenTomatoes;
    View headerCast, headerSimilar;

    private String apikey;

    ExpandableHeightGridView castView;
    ArrayList<Cast> cast = new ArrayList<Cast>();

    ExpandableHeightGridView gridSimilar;
    ArrayList<Movie> similarList;
    private Type typeList = new TypeToken<List<Movie>>(){}.getType();

    public MovieFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        representUI(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentMovie = ((MovieActivity)getActivity()).getCurrentMovie();
        apikey = ((MovieActivity)getActivity()).getApikey();
        representData();
        representRatings();
        representSimilar();
    }

    public void representData() {
        castView.setFocusable(false);
        cast = currentMovie.getAbridged_cast();
        castView.setAdapter(new CastAdapter(getActivity(),cast));
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
        if (dvd.getTime()==0 || dvd.getTime()==-3600000) {
            tvDVD.setText(R.string.unknown);
        } else {
            tvDVD.setText(iso.format(dvd));
        }

        VolleySingleton.getInstance(getActivity()).getImageLoader().get(currentMovie.getPosters().getDetailed(), new ImageLoader.ImageListener() {
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


        Utils.setupGoogleButton(currentMovie.getTitle(), bGoogle);
        Utils.setupYoutubeButton(currentMovie.getTitle(),bYoutube);
        Utils.setupAmazonButton(currentMovie.getTitle(),bAmazon);
        Utils.setupImdbButton(currentMovie.getTitle(),bImdb);
        Utils.setupRottenTomatoesButton(currentMovie.getLinks().getAlternate(),bRottenTomatoes);
    }

    public void representUI(View v) {
        ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
        progressBar = (ProgressBar) v.findViewById(R.id.progressPoster);
        ivCritics = (ImageView) v.findViewById(R.id.ivCritics);
        ivAudience = (ImageView) v.findViewById(R.id.ivAudience);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvYear = (TextView) v.findViewById(R.id.tvYear);
        tvCritics = (TextView) v.findViewById(R.id.tvCritics);
        tvAudience = (TextView) v.findViewById(R.id.tvAudience);
        tvSynopsis = (TextView) v.findViewById(R.id.tvSynopsis);
        tvRating = (TextView) v.findViewById(R.id.tvRatingValue);
        tvRuntime = (TextView) v.findViewById(R.id.tvRunningValue);
        tvTheater = (TextView) v.findViewById(R.id.tvTheaterReleaseDate);
        tvDVD = (TextView) v.findViewById(R.id.tvDVDReleaseDate);
        bGoogle = (Button) v.findViewById(R.id.buttonGoogle);
        bYoutube = (Button) v.findViewById(R.id.buttonYoutube);
        bAmazon = (Button) v.findViewById(R.id.buttonAmazon);
        bImdb = (Button) v.findViewById(R.id.buttonImdb);
        bRottenTomatoes = (Button) v.findViewById(R.id.buttonRotten);


        tvEmptyCast = (TextView)v.findViewById(R.id.empty_cast);
        tvEmptyCast.setText(R.string.empty_cast);
        headerCast = v.findViewById(R.id.castSeparator);
        headerCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullCast(v);
            }
        });
        castView = (ExpandableHeightGridView) v.findViewById(R.id.gridView);




        headerSimilar = v.findViewById(R.id.similarSeparator);
        headerSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullSimilar(v);
            }
        });
        gridSimilar = (ExpandableHeightGridView) v.findViewById(R.id.gridSimilar);
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
        tvEmptySimilar = (TextView)v.findViewById(R.id.empty_similar);
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

    private void representSimilar() {
        String URL = currentMovie.getLinks().getSimilar() + "?limit=3&apikey="+apikey;
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
                        gridSimilar.setAdapter(new SimilarAdapter(getActivity(), similarList));
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
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(getRequest);


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

}
