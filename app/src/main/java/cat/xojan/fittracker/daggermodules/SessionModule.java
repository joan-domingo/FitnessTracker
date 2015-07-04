package cat.xojan.fittracker.daggermodules;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import javax.inject.Singleton;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.GoogleFitSessionStorage;
import cat.xojan.fittracker.domain.SessionDataInteractor;
import cat.xojan.fittracker.domain.SessionRepository;
import cat.xojan.fittracker.main.ResultFragment;
import cat.xojan.fittracker.main.SessionListFragment;
import cat.xojan.fittracker.main.WorkoutFragment;
import cat.xojan.fittracker.main.controllers.DistanceController;
import cat.xojan.fittracker.main.controllers.FitnessController;
import cat.xojan.fittracker.main.controllers.MapController;
import cat.xojan.fittracker.main.controllers.NotificationController;
import cat.xojan.fittracker.main.controllers.SpeedController;
import cat.xojan.fittracker.main.controllers.TimeController;
import cat.xojan.fittracker.view.BaseActivity;
import cat.xojan.fittracker.view.StartUpActivity;
import cat.xojan.fittracker.view.presenter.SessionPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SessionRepository.class,
                SessionDataInteractor.class,
                SessionPresenter.class,
                StartUpActivity.class
        },
        addsTo = AppModule.class,
        library = true
)
public class SessionModule {

    private final BaseActivity activity;

    public SessionModule(BaseActivity baseActivity) {
        this.activity = baseActivity;

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

    @Provides
    @Singleton
    public SessionRepository provideSessionRepository() {
        return new GoogleFitSessionStorage();
    }

    @Provides
    public SessionDataInteractor provideSessionDataInteractor(SessionRepository sessionRepository) {
        return new SessionDataInteractor(sessionRepository);
    }

    @Provides
    public SessionPresenter provideSessionPresenter(SessionDataInteractor sessionDataInteractor) {
        return new SessionPresenter(sessionDataInteractor);
    }

    @Provides @Singleton
    public FitnessController provideFitnessController(TimeController timeController) {
        return new FitnessController(activity, timeController);
    }

    @Provides @Singleton
    public NotificationController provideNotificationController(NotificationManager notificationManager) {
        return new NotificationController(activity, notificationManager);
    }

    @Provides @Singleton
    public MapController provideMapController(GoogleMap googleMap, FitnessController fitnessController,
                                              LocationManager locationManager,
                                              DistanceController distanceController,
                                              SpeedController speedController) {
        return new MapController(activity, googleMap, fitnessController, locationManager,
                distanceController, speedController);
    }

    @Provides
    @Singleton
    public DistanceController provideDistanceController(GoogleMap map, LocationManager locationManager) {
        return new DistanceController(activity, map, locationManager);
    }

    @Provides @Singleton
    public TimeController provideTimeController() {
        return new TimeController();
    }

    @Provides @Singleton
    public SpeedController provideSpeedController(FitnessController fitnessController,
                                                  DistanceController distanceController,
                                                  TimeController timeController) {
        return new SpeedController(activity, fitnessController, distanceController, timeController);
    }

    @Provides @Singleton
    public SessionListFragment provideSessionListFragment() {
        return new SessionListFragment();
    }

    @Provides @Singleton
    public WorkoutFragment workoutFragment() {
        return new WorkoutFragment();
    }

    @Provides @Singleton
    public ResultFragment resultFragment() {
        return new ResultFragment();
    }

    @Provides @Singleton
    public LocationManager provideLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides @Singleton
    public NotificationManager provideNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides @Singleton
    public GoogleMap provideGoogleMap(Activity activity) {
        return ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.workout_map))
                .getMap();
    }
}
