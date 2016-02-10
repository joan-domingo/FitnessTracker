package cat.xojan.fittracker.injection.module;

import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.presentation.workout.WorkoutPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class WorkoutModule {

    public WorkoutModule() {

    }

    @Provides
    @PerActivity
    WorkoutPresenter provideStartUpPresenter() {
        return new WorkoutPresenter();
    }
}
