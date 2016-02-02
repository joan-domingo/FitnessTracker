package cat.xojan.fittracker.injection.module;

import android.app.Activity;
import android.content.Context;

import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.data.repository.GoogleFitStorage;
import cat.xojan.fittracker.data.repository.SharedPreferencesStorage;
import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.domain.PreferencesInteractor;
import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.presentation.startup.StartupPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class StartupModule {

    public StartupModule() {

    }

    @Provides
    @PerActivity
    FitnessDataInteractor provideFitnessDataInteractor(UserData userData) {
        return new FitnessDataInteractor(new GoogleFitStorage(), userData);
    }

    @Provides
    @PerActivity
    PreferencesInteractor providePreferencesInteractor(Context context) {
        return new PreferencesInteractor(new SharedPreferencesStorage(context));
    }

    @Provides
    @PerActivity
    StartupPresenter provideStartUpPresenter(FitnessDataInteractor fitnessDataInteractor,
                                             PreferencesInteractor preferencesInteractor) {
        return new StartupPresenter(fitnessDataInteractor, preferencesInteractor);
    }
}
