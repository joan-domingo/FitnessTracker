package cat.xojan.fittracker.daggermodules;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.BaseActivity;
import cat.xojan.fittracker.main.MainActivity;
import cat.xojan.fittracker.main.controllers.FitnessController;
import cat.xojan.fittracker.main.fragments.SessionListFragment;
import cat.xojan.fittracker.main.fragments.WorkoutFragment;
import cat.xojan.fittracker.main.fragments.sessionlist.SessionAdapter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class,
                SessionListFragment.class,
                FitnessController.class,
                WorkoutFragment.class
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
    public SessionListFragment provideSessionListFragment() {
        return new SessionListFragment();
    }

    @Provides @Singleton
    public WorkoutFragment workoutFragment() {
        return new WorkoutFragment();
    }

    /*@Provides
    @Singleton
    public LocationManager provideLocationManager(Context appContext) {
        return (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
    }*/
}
