package com.rottenapp.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.rottenapp.R;
import com.rottenapp.helpers.VolleySingleton;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullImageActivity extends Activity {

    ImageView fullPoster;
    PhotoViewAttacher mAttacher;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferencesActivity.THEME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        String url = getIntent().getStringExtra("url");
        fullPoster = (ImageView)findViewById(R.id.fullPoster);
        progressBar = (ProgressBar)findViewById(R.id.progressImage);
        VolleySingleton.getInstance(this).getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer.getBitmap() != null) {
                    progressBar.setVisibility(View.GONE);
                    fullPoster.setImageBitmap(imageContainer.getBitmap());
                    mAttacher = new PhotoViewAttacher(fullPoster);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                fullPoster.setImageResource(R.drawable.rotten);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = new Intent(this, MovieActivity.class);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, upIntent);
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
            View rootView = inflater.inflate(R.layout.full_image, container, false);
            return rootView;
        }
    }

}
