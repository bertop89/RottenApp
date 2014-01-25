package com.rottenapp.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.rottenapp.R;
import com.rottenapp.fragments.FavouritesFragment;

/**
 * Created by Alberto Polidura on 22/01/14.
 */
public class FavouritesActivity extends BaseTopActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_activity);
        setupNavDrawer();

        Fragment fragment = new FavouritesFragment();
        String tag="favourites";
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment,tag).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDrawerSelectedItem(1);
    }


}
