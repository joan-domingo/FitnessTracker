package cat.xojan.fittracker.daggermodules;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import javax.inject.Singleton;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.view.activity.StartUpActivity;
import cat.xojan.fittracker.view.activity.WorkoutActivity;
import cat.xojan.fittracker.view.controller.DistanceController;
import cat.xojan.fittracker.view.controller.FitnessController;
import cat.xojan.fittracker.view.controller.MapController;
import cat.xojan.fittracker.view.controller.NotificationController;
import cat.xojan.fittracker.view.controller.SpeedController;
import cat.xojan.fittracker.view.controller.TimeController;
import cat.xojan.fittracker.view.fragment.ResultFragment;
import cat.xojan.fittracker.view.fragment.WorkoutMapFragment;
import cat.xojan.fittracker.view.presenter.SessionPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                StartUpActivity.class
        },
        includes = SessionModule.class,
        addsTo = AppModule.class,
        library = true
)
public class StartUpModule {
    private final Activity mActivity;

    public StartUpModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @Singleton
    public Context provideActivityContext() {
        return mActivity.getBaseContext();
    }

    @Provides
    @Singleton
    public Activity provideActivity() {
        return mActivity;
    }
}
