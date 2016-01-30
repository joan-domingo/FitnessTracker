package cat.xojan.fittracker.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.FitTrackerApp;
import dagger.Module;
import dagger.Provides;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module
public class AppModule {

    private final FitTrackerApp mApplication;

    public AppModule(FitTrackerApp application) {
        mApplication = application;
    }
}
