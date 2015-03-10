package cat.xojan.fittracker.daggermodules;

import android.app.Application;
import android.location.LocationManager;

import javax.inject.Singleton;

import cat.xojan.fittracker.FitTrackerApp;
import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;

@Module(library = true)
public class AppModule {

    private final FitTrackerApp application;

    public AppModule(FitTrackerApp application) {
        this.application = application;
    }

    @Provides @Singleton
    Application provideApplicationContext() {
        return application;
    }

    @Provides @Singleton
    LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }

}
