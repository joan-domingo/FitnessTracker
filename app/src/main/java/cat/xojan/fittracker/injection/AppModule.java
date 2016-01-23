package cat.xojan.fittracker.injection;

import android.app.Application;

import javax.inject.Singleton;

import cat.xojan.fittracker.FitTrackerApp;
import dagger.Module;
import dagger.Provides;

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

}
