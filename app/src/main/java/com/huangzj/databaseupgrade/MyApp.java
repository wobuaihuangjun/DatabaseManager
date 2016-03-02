package com.huangzj.databaseupgrade;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

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
    }

    /**
     * @return 得到需要分配的缓存大小，这里用八分之一的大小来做
     * @description
     */
    public int getMemoryCacheSize() {
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass
                = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        return 1024 * 1024 * memClass / 8;
    }
}
