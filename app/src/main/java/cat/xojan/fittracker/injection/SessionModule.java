package cat.xojan.fittracker.injection;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.presentation.activity.SessionActivity;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SessionActivity.class
        },
        includes = {
                SessionDataModule.class,
                UnitDataModule.class
        },
        addsTo = AppModule.class,
        library = true
)
public class SessionModule {
    private final Activity mActivity;

    public SessionModule(Activity activity) {
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
