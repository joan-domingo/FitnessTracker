package cat.xojan.fittracker.injection.module;

import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.data.repository.GoogleFitStorage;
import cat.xojan.fittracker.domain.FitnessDataInteractor;
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

    @Provides
    @Singleton
    UserData provideUserData() {
        return new UserData(mApplication);
    }
}
