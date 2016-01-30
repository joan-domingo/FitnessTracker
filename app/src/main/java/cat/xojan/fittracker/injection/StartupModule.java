package cat.xojan.fittracker.injection;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.repository.GoogleFitStorage;
import cat.xojan.fittracker.data.repository.SharedPreferencesStorage;
import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.domain.PreferencesInteractor;
import cat.xojan.fittracker.injection.module.AppModule;
import cat.xojan.fittracker.presentation.startup.StartupActivity;
import cat.xojan.fittracker.presentation.startup.StartupPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class StartupModule {
    private final Activity mActivity;

    public StartupModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    public Context provideActivityContext() {
        return mActivity.getBaseContext();
    }

    @Provides
    public Activity provideActivity() {
        return mActivity;
    }

    @Provides
    StartupPresenter provideStartupPresenter(FitnessDataInteractor fitnessDataInteractor,
                                             PreferencesInteractor preferencesInteractor) {
        return new StartupPresenter(fitnessDataInteractor, preferencesInteractor, mActivity);
    }

    @Provides
    @Singleton
    FitnessDataInteractor provideFitnessDataInteractor() {
        return new FitnessDataInteractor(new GoogleFitStorage());
    }

    @Provides
    @Singleton
    PreferencesInteractor providePreferencesInteractor(Context context) {
        return new PreferencesInteractor(new SharedPreferencesStorage(context));
    }
}
