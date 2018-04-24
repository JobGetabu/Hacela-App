package com.job.hacelaapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Job on Tuesday : 4/24/2018.
 */
public class HacelaApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //TODO
    //set disk caching with the picasso library
    //firebase is cached by default

    //TODO
    //retrofit setup
}
