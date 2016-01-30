package cat.xojan.fittracker.injection.module;

import android.app.Activity;
import android.content.Context;

import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.data.repository.GoogleFitStorage;
import cat.xojan.fittracker.data.repository.SharedPreferencesStorage;
import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.domain.PreferencesInteractor;
import cat.xojan.fittracker.injection.component.PerActivity;
import cat.xojan.fittracker.presentation.home.HomePresenter;
import cat.xojan.fittracker.presentation.startup.StartupPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    public HomeModule() {

    }

    @Provides
    @PerActivity
    FitnessDataInteractor provideFitnessDataInteractor(UserData userData) {
        return new FitnessDataInteractor(new GoogleFitStorage(), userData);
    }

    @Provides
    @PerActivity
    HomePresenter provideStartUpPresenter(FitnessDataInteractor fitnessDataInteractor,
                                          Activity activity) {
        return new HomePresenter(fitnessDataInteractor, activity);
    }
}
