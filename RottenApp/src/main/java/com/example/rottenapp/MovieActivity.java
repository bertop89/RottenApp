package com.example.rottenapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class MovieActivity extends Activity {

    Movie currentMovie;
    ImageView ivCritics, ivAudience;
    NetworkImageView ivPoster;
    TextView tvTitle, tvYear, tvCritics, tvAudience, tvSynopsis, tvRating, tvGenre, tvRuntime, tvRelease, tvDirector;
    MySQLiteHelper db;
    private final String apikey = "d2uywhtvna2y9fhm4eq4ydzc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_main);
        Intent i = getIntent();
        currentMovie = i.getParcelableExtra("movie");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ivPoster = (NetworkImageView) findViewById(R.id.ivPoster);
        ivCritics = (ImageView) findViewById(R.id.ivCritics);
        ivAudience = (ImageView) findViewById(R.id.ivAudience);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvYear = (TextView) findViewById(R.id.tvYear);
        tvCritics = (TextView) findViewById(R.id.tvCritics);
        tvAudience = (TextView) findViewById(R.id.tvAudience);
        tvSynopsis = (TextView) findViewById(R.id.tvSynopsis);
        tvRating = (TextView) findViewById(R.id.tvRatingValue);
        tvGenre = (TextView) findViewById(R.id.tvGenreValue);
        tvRuntime = (TextView) findViewById(R.id.tvRunningValue);
        tvRelease = (TextView) findViewById(R.id.tvReleaseValue);
        tvDirector = (TextView) findViewById(R.id.tvDiretorValue);
        representUI();
        representRatings();
        db = new MySQLiteHelper(this);
        db.getWritableDatabase();
    }

    public void representUI() {
        tvTitle.setText(currentMovie.getTitle());
        tvYear.setText("("+currentMovie.getYear()+")");
        String critic_score = currentMovie.getRatings().getCritics_score();
        if (!critic_score.equals("-1")) {
            tvCritics.setText(critic_score+"% "+ getResources().getString(R.string.critics));
        }
        String audience_score = currentMovie.getRatings().getAudience_score();
        if (!audience_score.equals("-1")) {
            tvAudience.setText(audience_score+"% "+ getResources().getString(R.string.audience));
        }
        tvSynopsis.setText(currentMovie.getSynopsis());
        tvRating.setText(currentMovie.getMpaa_rating());
        tvRuntime.setText(currentMovie.getRuntime()+" min");
        ivPoster.setImageUrl(currentMovie.getPosters().getProfile(),VolleySingleton.getInstance(this).getImageLoader());
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
        }

        String audience = currentMovie.getRatings().getAudience_rating();
        if (audience!=null) {
            if (audience.equals("Upright")) {
                ivAudience.setImageResource(R.drawable.bucket);
            } else if (audience.equals("Spilled")) {
                ivAudience.setImageResource(R.drawable.spilled);
            }
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
                Intent upIntent = new Intent(this, ListActivity.class);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            View rootView = inflater.inflate(R.layout.movie_fragment_main, container, false);
            return rootView;
        }
    }

}
