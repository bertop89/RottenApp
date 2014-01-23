package com.rottenapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.rottenapp.R;
import com.rottenapp.helpers.Utils;

/**
 * Created by Alberto Polidura on 18/01/14.
 */
public class PreferencesActivity extends Activity {

    SettingsFragment settingsFragment;
    public Intent returnIntent;

    public static int THEME = R.style.AppTheme;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferencesActivity.THEME);
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Display the fragment as the main content.
        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
        returnIntent = new Intent(this, MainActivity.class);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, returnIntent);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            ListPreference themePref = (ListPreference)findPreference("theme");
            // Set summary to be the user-description for the selected value
            themePref.setSummary(themePref.getEntry().toString().replaceAll("%", "%%"));


        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("theme")) {

                ListPreference themePref = (ListPreference)findPreference(key);

                //Updates the THEME variable with the new value
                Utils.updateTheme(themePref.getValue());

                //Set summary to be the user-description for the selected value
                themePref.setSummary(themePref.getEntry().toString().replaceAll("%", "%%"));

                //Restarts current and MainActivity
                getActivity().setResult(RESULT_OK, ((PreferencesActivity)getActivity()).returnIntent);
                getActivity().finish();
                Intent intent = new Intent(getActivity(), PreferencesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);

            }
        }


    }
}
