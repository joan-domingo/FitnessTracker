package cat.xojan.fittracker.injection.module;

import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.presentation.workout.MapPresenter;
import cat.xojan.fittracker.presentation.workout.WorkoutPresenter;
import cat.xojan.fittracker.util.LocationFetcher;
import dagger.Module;
import dagger.Provides;

@Module
public class WorkoutModule {

    public WorkoutModule() {

    }

    @Provides
    @PerActivity
    WorkoutPresenter provideWorkoutPresenter() {
        return new WorkoutPresenter();
    }

    @Provides
    @PerActivity
    MapPresenter provideMapPresenter(LocationFetcher locationFetcher) {
        return new MapPresenter(locationFetcher);
    }
}
