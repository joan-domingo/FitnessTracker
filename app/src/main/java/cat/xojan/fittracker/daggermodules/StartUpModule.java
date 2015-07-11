package cat.xojan.fittracker.daggermodules;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.ui.activity.StartUpActivity;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                StartUpActivity.class
        },
        includes = {
                SessionModule.class,
                UnitDataModule.class
        },
        addsTo = AppModule.class,
        library = true
)
public class StartUpModule {
    private final Activity mActivity;

    public StartUpModule(Activity activity) {
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
