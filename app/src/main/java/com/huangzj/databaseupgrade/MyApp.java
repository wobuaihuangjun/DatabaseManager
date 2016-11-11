package com.huangzj.databaseupgrade;

import android.app.Application;

import timber.log.Timber;

public class MyApp extends Application {

    private static MyApp instance;

    public static MyApp get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = (MyApp) getApplicationContext();

        Timber.plant(new Timber.DebugTree());
        DatabaseManager.getInstance().init(instance);
    }
}
