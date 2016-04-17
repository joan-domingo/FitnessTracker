package cat.xojan.fittracker.injection.module;

import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.presentation.history.HistoryPresenter;
import cat.xojan.fittracker.presentation.home.HomePresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    public HomeModule() {

    }

    @Provides
    @PerActivity
    HomePresenter provideHomePresenter() {
        return new HomePresenter();
    }

    @Provides
    @PerActivity
    HistoryPresenter provideHistoryPresenter(WorkoutInteractor workoutInteractor) {
        return new HistoryPresenter(workoutInteractor);
    }
}
