package com.rottenapp.helpers;

import com.rottenapp.R;
import com.rottenapp.activities.PreferencesActivity;

/**
 * Created by Alberto Polidura on 19/01/14.
 */
public class Utils {

    public static synchronized void updateTheme(String themeIndex) {
        int theme = Integer.valueOf(themeIndex);
        switch (theme) {
            case 0:
                PreferencesActivity.THEME = R.style.AppTheme;
                break;
            case 1:
                PreferencesActivity.THEME = R.style.DarkAppTheme;
                break;
            default:
                PreferencesActivity.THEME = R.style.AppTheme;
                break;
        }
    }
}
