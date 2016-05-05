package cat.xojan.fittracker.injection.module;

import android.app.Activity;

import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.presentation.workout.MapPresenter;
import cat.xojan.fittracker.presentation.workout.WorkoutPresenter;
import cat.xojan.fittracker.util.LocationFetcher;
import dagger.Module;
import dagger.Provides;

@Module
public class WorkoutModule {

    private final Activity mActivity;

    public WorkoutModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @PerActivity
    WorkoutPresenter provideWorkoutPresenter(WorkoutInteractor workoutInteractor,
                                             UnitDataInteractor unitDataInteractor) {
        return new WorkoutPresenter(workoutInteractor, unitDataInteractor);
    }

    @Provides
    @PerActivity
    MapPresenter provideMapPresenter(LocationFetcher locationFetcher) {
        return new MapPresenter(locationFetcher, mActivity);
    }
}
