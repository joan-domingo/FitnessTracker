package cat.xojan.fittracker.injection.component;

import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.injection.module.WorkoutModule;
import cat.xojan.fittracker.presentation.workout.WorkoutActivity;
import cat.xojan.fittracker.presentation.workout.WorkoutActivityTest;
import dagger.Component;

@PerActivity
@Component(
        dependencies = AppComponent.class,
        modules = {
                BaseActivityModule.class,
                WorkoutModule.class
        }
)
public interface WorkoutComponent extends BaseActivityComponent {
    void inject(WorkoutActivity workoutActivity);
    //void inject(HomeFragment homeFragment);
}
