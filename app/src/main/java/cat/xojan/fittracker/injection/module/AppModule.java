package cat.xojan.fittracker.injection.module;

import android.content.Context;
import android.location.LocationManager;

import javax.inject.Singleton;

import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.navigation.Navigator;
import cat.xojan.fittracker.util.LocationFetcher;
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

    @Provides
    Navigator provideNavigator() {
        return new Navigator();
    }

    @Provides
    @Singleton
    LocationManager provideLocationManager() {
        return (LocationManager) mApplication.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    LocationFetcher provideLocationFetcher(LocationManager locationManager) {
        return new LocationFetcher(locationManager);
    }
}
