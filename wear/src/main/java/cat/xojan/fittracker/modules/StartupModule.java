package cat.xojan.fittracker.modules;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.presentation.activity.StartupActivity;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                StartupActivity.class
        },
        addsTo = AppModule.class,
        library = true
)
public class StartupModule {

    private Activity mActivity;

    public StartupModule(Activity activity) {
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
