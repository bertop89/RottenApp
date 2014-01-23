package com.rottenapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rottenapp.activities.MainActivity;
import com.rottenapp.fragments.FavouritesFragment;

/**
 * Created by Alberto Polidura on 23/01/14.
 */
public class TabStripAdapter extends FragmentPagerAdapter {

    Context mContext;

    public TabStripAdapter(FragmentManager fm, Context c) {
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return Fragment.instantiate(mContext, MainActivity.PlaceholderFragment.class.getName());
            default:
                return Fragment.instantiate(mContext, FavouritesFragment.class.getName());
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Section " + (position + 1);
    }
}
