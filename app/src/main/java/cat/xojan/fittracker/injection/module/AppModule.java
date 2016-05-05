package cat.xojan.fittracker.injection.module;

import android.content.Context;
import android.location.LocationManager;

import javax.inject.Singleton;

import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.data.db.dao.DaoSession;
import cat.xojan.fittracker.data.repository.DbWorkoutStorage;
import cat.xojan.fittracker.data.repository.SharedPreferencesStorage;
import cat.xojan.fittracker.domain.repository.PreferencesRepository;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.domain.repository.WorkoutRepository;
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
    private final DaoSession mDaoSession;

    public AppModule(FitTrackerApp application, DaoSession daoSession) {
        mApplication = application;
        mDaoSession = daoSession;
    }

    @Provides
    Navigator provideNavigator() {
        return new Navigator();
    }

    @Provides
    @Singleton
    LocationFetcher provideLocationFetcher() {
        LocationManager locationManager = (LocationManager) mApplication.
                getSystemService(Context.LOCATION_SERVICE);
        return new LocationFetcher(locationManager);
    }

    @Provides
    @Singleton
    WorkoutRepository provideWorkoutRepository() {
        return new DbWorkoutStorage(mDaoSession.getWorkoutDao(), mDaoSession.getLocationDao());
    }

    @Provides
    WorkoutInteractor provideWorkoutInteractor(WorkoutRepository workoutRepository) {
        return new WorkoutInteractor(workoutRepository);
    }

    @Provides
    UnitDataInteractor provideUnitDataInteractor(PreferencesRepository preferencesRepository) {
        return new UnitDataInteractor(preferencesRepository);
    }

    @Provides
    @Singleton
    PreferencesRepository providePreferencesRepository() {
        return new SharedPreferencesStorage(mApplication.getBaseContext());
    }
}
