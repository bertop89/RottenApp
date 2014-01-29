package com.rottenapp.data;

import android.app.Application;
import android.preference.PreferenceManager;

import com.rottenapp.helpers.Utils;

/**
 * Created by Alberto Polidura on 15/12/13.
 */
public class Global extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.updateTheme(PreferenceManager.getDefaultSharedPreferences(this).
                getString("theme", "0"));
    }

    public String getApikey() {
        return "d2uywhtvna2y9fhm4eq4ydzc";
    }
}
