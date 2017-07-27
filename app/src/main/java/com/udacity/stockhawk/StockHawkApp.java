package com.udacity.stockhawk;

import android.app.Application;

import timber.log.Timber;

public class StockHawkApp extends Application {

    public static final String SYMBOL_EXTRA_KEY = "SYMBOL";
    public static final String HISTORY_EXTRA_KEY = "HISTORY";

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }
}
