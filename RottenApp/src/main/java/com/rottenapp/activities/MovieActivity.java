package com.rottenapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rottenapp.R;
import com.rottenapp.data.Global;
import com.rottenapp.data.MySQLiteHelper;
import com.rottenapp.models.Movie;

public class MovieActivity extends BaseActivity {

    Movie currentMovie;
    MySQLiteHelper db;

    public String getApikey() {
        return apikey;
    }

    public Movie getCurrentMovie() {
        return currentMovie;
    }

    String apikey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferencesActivity.THEME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);
        Intent i = getIntent();
        currentMovie = i.getParcelableExtra("movie");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final Global global = (Global)getApplicationContext();
        apikey = global.getApikey();

        db = new MySQLiteHelper(this);
        db.getWritableDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
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

        if (currentMovie.getRelease_dates().getTheater().getTime()>System.currentTimeMillis()) {
            menu.add(Menu.NONE, 1, Menu.NONE, R.string.add_calendar);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,currentMovie.getRelease_dates().getTheater().getTime());
                intent.putExtra(CalendarContract.Events.TITLE, currentMovie.getTitle());
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                startActivity(intent);
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

}
