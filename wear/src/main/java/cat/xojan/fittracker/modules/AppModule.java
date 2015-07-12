package cat.xojan.fittracker.modules;

import android.app.Application;

import javax.inject.Singleton;

import cat.xojan.fittracker.WearFitTrackerApp;
import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AppModule {

    private final WearFitTrackerApp application;

    public AppModule(WearFitTrackerApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplicationContext() {
        return application;
    }

}
