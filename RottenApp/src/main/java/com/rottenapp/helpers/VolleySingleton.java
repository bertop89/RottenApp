package com.rottenapp.helpers;

/**
 * Created by Alberto Polidura on 28/11/13.
 */

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.rottenapp.data.BitmapLruImageCache;

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
    private ImageLoader.ImageCache mImageCache;

    private VolleySingleton(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
        mImageCache = new BitmapLruImageCache(DISK_IMAGECACHE_SIZE);
        mImageLoader = new ImageLoader(this.mRequestQueue, mImageCache);
    }

    public static VolleySingleton getInstance(Context context){
        if(mInstance == null){
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }

}
