package cat.xojan.fittracker.injection;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.repository.GoogleFitStorage;
import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.injection.module.AppModule;
import cat.xojan.fittracker.presentation.home.HomeActivity;
import cat.xojan.fittracker.presentation.home.HomePresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {
    private final HomeActivity mActivity;

    public HomeModule(HomeActivity activity) {
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

    @Provides
    @Singleton
    public HomePresenter provideHomePresenter(FitnessDataInteractor fitnessDataInteractor) {
        return new HomePresenter(fitnessDataInteractor, mActivity);
    }

    @Provides
    @Singleton
    public FitnessDataInteractor provideFitnessDataInteractor() {
        return new FitnessDataInteractor(new GoogleFitStorage());
    }
}
