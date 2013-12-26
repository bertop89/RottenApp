package com.example.rottenapp.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Alberto Polidura on 25/12/13.
 */
public class URLHelper {

    private static final String APIKEY = "d2uywhtvna2y9fhm4eq4ydzc";
    private static final String QUERY_KEY_PARAM = "&q=";
    private static final String DEFAULT_ENCODING = "utf-8";
    private static final String PAGE_LIMIT_PARAM = "&page_limit=";
    private static final String LIMIT_PARAM = "&limit=";

    private static final String MOVIE = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=";
    private static final String UPCOMING = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?apikey=";
    private static final String BOX_OFFICE ="http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey=";
    private static final String TOP_DVD = "http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/top_rentals.json?apikey=";
    private static final String UPCOMING_DVD = "http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/upcoming.json?apikey=";


    public static String getSearchURL(String query) {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(MOVIE).append(APIKEY).append(QUERY_KEY_PARAM).append(URLEncoder.encode(query, DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getUpcomingURL(int num_results) {
        StringBuffer sb = new StringBuffer();
        if (num_results==0) {
            sb.append(UPCOMING).append(APIKEY);
        } else {
            sb.append(UPCOMING).append(APIKEY).append(PAGE_LIMIT_PARAM).append(num_results);
        }
        return sb.toString();
    }

    public static String getTopBoxOffice(int num_results) {
        StringBuffer sb = new StringBuffer();
        if (num_results==0) {
            sb.append(BOX_OFFICE).append(APIKEY);
        } else {
            sb.append(BOX_OFFICE).append(APIKEY).append(LIMIT_PARAM).append(num_results);
        }
        return sb.toString();
    }

    public static String getTopDVD(int num_results) {
        StringBuffer sb = new StringBuffer();
        if (num_results==0) {
            sb.append(TOP_DVD).append(APIKEY);
        } else {
            sb.append(TOP_DVD).append(APIKEY).append(LIMIT_PARAM).append(num_results);
        }
        return sb.toString();
    }

    public static String getUpcomingDVD(int num_results) {
        StringBuffer sb = new StringBuffer();
        if (num_results==0) {
            sb.append(UPCOMING_DVD).append(APIKEY);
        } else {
            sb.append(UPCOMING_DVD).append(APIKEY).append(PAGE_LIMIT_PARAM).append(num_results);
        }
        return sb.toString();
    }
}
