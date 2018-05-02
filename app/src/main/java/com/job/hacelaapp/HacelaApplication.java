package com.job.hacelaapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;


/**
 * Created by Job on Tuesday : 4/24/2018.
 */
public class HacelaApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    //firebase is cached by default
    public static Picasso picassoWithCache;

    @Override
    public void onCreate() {
        super.onCreate();

        //set disk caching with the picasso library
        /*
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        */

        //hacking disk cache
        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);

        //Initialize Picasso with cache
        picassoWithCache = new Picasso.Builder(this).downloader(new OkHttp3Downloader(okHttpClientBuilder.build())).build();
    }


    //TODO
    //retrofit setup
}
