package cat.xojan.fittracker.daggermodules;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import javax.inject.Singleton;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.ui.activity.WorkoutActivity;
import cat.xojan.fittracker.ui.controller.DistanceController;
import cat.xojan.fittracker.ui.controller.FitnessController;
import cat.xojan.fittracker.ui.controller.MapController;
import cat.xojan.fittracker.ui.controller.NotificationController;
import cat.xojan.fittracker.ui.controller.SpeedController;
import cat.xojan.fittracker.ui.controller.TimeController;
import cat.xojan.fittracker.ui.fragment.ResultFragment;
import cat.xojan.fittracker.ui.fragment.WorkoutMapFragment;
import cat.xojan.fittracker.ui.presenter.SessionPresenter;
import cat.xojan.fittracker.ui.presenter.UnitDataPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                WorkoutActivity.class,
                WorkoutMapFragment.class,
                ResultFragment.class,
                NotificationController.class,
                TimeController.class,
                SpeedController.class,
                MapController.class,
                DistanceController.class,
                FitnessController.class,
                SessionPresenter.class
        },
        includes = {
                SessionModule.class,
                UnitDataModule.class,
        },
        addsTo = AppModule.class,
        library = true
)
public class WorkoutModule {
    private final Activity mActivity;

    public WorkoutModule(Activity activity) {
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

    @Provides
    public LocationManager provideLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides @Singleton
    public FitnessController provideFitnessController(TimeController timeController) {
        return new FitnessController(mActivity, timeController);
    }



    @Provides @Singleton
    public MapController provideMapController(GoogleMap googleMap, FitnessController fitnessController,
                                              LocationManager locationManager,
                                              DistanceController distanceController,
                                              SpeedController speedController) {
        return new MapController(mActivity, googleMap, fitnessController, locationManager,
                distanceController, speedController);
    }

    @Provides
    @Singleton
    public DistanceController provideDistanceController(GoogleMap map,
                                                        LocationManager locationManager,
                                                        UnitDataPresenter unitDataPresenter) {
        return new DistanceController(mActivity, map, locationManager, unitDataPresenter);
    }

    @Provides @Singleton
    public TimeController provideTimeController() {
        return new TimeController();
    }

    @Provides @Singleton
    public SpeedController provideSpeedController(FitnessController fitnessController,
                                                  DistanceController distanceController,
                                                  TimeController timeController) {
        return new SpeedController(mActivity, fitnessController, distanceController, timeController);
    }



    @Provides @Singleton
    public GoogleMap provideGoogleMap(Activity activity) {
        return ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.workout_map))
                .getMap();
    }

    @Provides
    public NotificationManager provideNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Singleton
    public NotificationController provideNotificationController(
            NotificationManager notificationManager) {
        return new NotificationController(mActivity, notificationManager);
    }
}
