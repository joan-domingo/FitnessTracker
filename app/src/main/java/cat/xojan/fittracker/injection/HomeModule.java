package cat.xojan.fittracker.injection;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.presentation.home.HomeActivity;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                HomeActivity.class
        },
        includes = {
                SessionDataModule.class,
                UnitDataModule.class
        },
        addsTo = AppModule.class,
        library = true
)
public class HomeModule {
    private final Activity mActivity;

    public HomeModule(Activity activity) {
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
