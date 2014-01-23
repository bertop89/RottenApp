package com.rottenapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rottenapp.R;
import com.rottenapp.activities.ActivityFragment;

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
        final Bundle bundle = new Bundle();
        switch (i) {
            case 0:
                bundle.putString("type","TopBox");
                return Fragment.instantiate(mContext, ActivityFragment.class.getName(),bundle);
            case 1:
                bundle.putString("type","UpcomingBox");
                return Fragment.instantiate(mContext, ActivityFragment.class.getName(),bundle);
            case 2:
                bundle.putString("type","TopDVD");
                return Fragment.instantiate(mContext, ActivityFragment.class.getName(),bundle);
            case 3:
                bundle.putString("type","UpcomingDVD");
                return Fragment.instantiate(mContext, ActivityFragment.class.getName(),bundle);
            default:
                bundle.putString("type","TopBox");
                return Fragment.instantiate(mContext, ActivityFragment.class.getName(),bundle);
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.box_office);
            case 1:
                return mContext.getString(R.string.upcoming);
            case 2:
                return mContext.getString(R.string.top_dvd);
            case 3:
                return mContext.getString(R.string.upcoming_dvd);
            default:
                return "Error";
        }
    }
}
