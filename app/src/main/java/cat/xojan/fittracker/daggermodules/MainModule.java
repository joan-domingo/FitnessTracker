package cat.xojan.fittracker.daggermodules;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import javax.inject.Singleton;

import cat.xojan.fittracker.BaseActivity;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.MainActivity;
import cat.xojan.fittracker.controller.FitnessController;
import cat.xojan.fittracker.sessionlist.SessionListFragment;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class,
                SessionListFragment.class,
                FitnessController.class
        },
        addsTo = AppModule.class,
        library = true
)
public class MainModule {

    private final BaseActivity activity;

    public MainModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    public Context provideActivityContext() {
        return activity.getBaseContext();
    }

    @Provides
    @Singleton
    public Activity provideActivity() {
        return activity;
    }

    @Provides @Singleton
    public FitnessController provideFitnessController() {
        return new FitnessController(activity);
    }

    @Provides @Singleton
    public SessionListFragment provideHostedFragment() {
        return new SessionListFragment();
    }

    /*@Provides
    @Singleton
    public LocationManager provideLocationManager(Context appContext) {
        return (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
    }*/
}
