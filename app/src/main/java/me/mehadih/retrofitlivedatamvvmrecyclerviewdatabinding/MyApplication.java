package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Application class for dependency injection setup
 * Using Hilt for modern dependency injection (2025 best practice)
 */
@HiltAndroidApp
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}

