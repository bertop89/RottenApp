package com.rottenapp.helpers;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.rottenapp.R;
import com.rottenapp.activities.PreferencesActivity;

/**
 * Created by Alberto Polidura on 19/01/14.
 */
public class Utils {

    private static final String YOUTUBE_SEARCH = "http://www.youtube.com/results?search_query=%s";
    private static final String AMAZON_SEARCH = "http://www.amazon.com/gp/search?ie=UTF8&keywords=";
    private static final String IMDB_SEARCH = "http://www.imdb.com/find?q=";

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

    public static void setupGoogleButton(final String query, View button) {
        if (button == null) {
            // Return if the button isn't initialized
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                v.getContext().startActivity(intent);
            }
        });
    }

    public static void setupYoutubeButton(final String query, View button) {
        if (button == null) {
            // Return if the button isn't initialized
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch a web search
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(String.format(YOUTUBE_SEARCH, Uri.encode(query))));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                v.getContext().startActivity(intent);
            }
        });
    }

    public static void setupAmazonButton(final String query, View button) {
        if (button == null) {
            // Return if the button isn't initialized
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch a web search
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(AMAZON_SEARCH + query));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                v.getContext().startActivity(intent);
            }
        });
    }

    public static void setupImdbButton(final String query, View button) {
        if (button == null) {
            // Return if the button isn't initialized
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch a web search
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(IMDB_SEARCH + query));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                v.getContext().startActivity(intent);
            }
        });
    }

    public static void setupRottenTomatoesButton(final String url, View button) {
        if (button == null) {
            // Return if the button isn't initialized
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch a web search
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                v.getContext().startActivity(intent);
            }
        });
    }
}
