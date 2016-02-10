package cat.xojan.fittracker.navigation;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xojan.fittracker.presentation.home.HomeActivity;
import cat.xojan.fittracker.presentation.startup.StartupActivity;
import cat.xojan.fittracker.presentation.workout.WorkoutActivity;

/**
 * Class used to navigate through the application.
 */
@Singleton
public class Navigator {

    @Inject
    public Navigator() {
        //empty
    }

    /**
     * Goes to home activity.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToHomeActivity(Context context) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, HomeActivity.class);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to Startup activity.
     */
    public void navigateToStartupActivity(Context context) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, StartupActivity.class);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to Workout activity.
     */
    public void navigateToWorkoutActivity(Context context, String fitnessActivity) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, WorkoutActivity.class);
            intentToLaunch.putExtra(WorkoutActivity.FITNESS_ACTIVITY, fitnessActivity);
            context.startActivity(intentToLaunch);
        }
    }
}