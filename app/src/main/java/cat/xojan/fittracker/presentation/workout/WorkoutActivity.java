package cat.xojan.fittracker.presentation.workout;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.injection.component.DaggerWorkoutComponent;
import cat.xojan.fittracker.injection.component.WorkoutComponent;
import cat.xojan.fittracker.injection.module.WorkoutModule;
import cat.xojan.fittracker.presentation.BaseActivity;

/**
 * Created by Joan on 10/02/2016.
 */
public class WorkoutActivity extends BaseActivity {

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    private WorkoutComponent mComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeInjector();
        ButterKnife.bind(this);

        setTitle(getIntent().getExtras().getString(FITNESS_ACTIVITY));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initializeInjector() {
        mComponent = DaggerWorkoutComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .workoutModule(new WorkoutModule())
                .build();
        mComponent.inject(this);
    }
}