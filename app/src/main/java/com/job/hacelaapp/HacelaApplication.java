package com.job.hacelaapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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


    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    //firebase is cached by default

    /**UPDATE
     *
     *  FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
            firestore.setFirestoreSettings(settings);

     With this change, timestamps stored in Cloud Firestore will be read back as
     com.google.firebase.Timestamp objects instead of as system java.util.Date objects.
     So you will also need to update code expecting a java.util.Date to instead expect a Timestamp. For example:

         // Old:
         java.util.Date date = snapshot.getDate("created_at");
         // New:
         Timestamp timestamp = snapshot.getTimestamp("created_at");
         java.util.Date date = timestamp.toDate();

     **/
    public static Picasso picassoWithCache;

    @Override
    public void onCreate() {
        super.onCreate();

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (mAuth != null){
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            mFirestore.setFirestoreSettings(settings);
        }

       /* //set disk caching with the picasso library
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/

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
