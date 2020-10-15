package com.seclab.nmaping.content;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context mContText;

    @Override
    public void onCreate() {
        super.onCreate();
        mContText =  getApplicationContext();
    }

    public static Context getmContText() {
        return mContText;
    }
}

