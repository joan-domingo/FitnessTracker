package cat.xojan.fittracker.modules;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.presentation.activity.WorkoutActivity;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                WorkoutActivity.class
        },
        addsTo = AppModule.class,
        library = true
)
public class WorkoutModule {

    private Activity mActivity;

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
}
