package cat.xojan.fittracker.injection.module;

import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
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
    HomePresenter provideHomePresenter(UnitDataInteractor unitDataInteractor) {
        return new HomePresenter(unitDataInteractor);
    }

    @Provides
    @PerActivity
    HistoryPresenter provideHistoryPresenter(WorkoutInteractor workoutInteractor,
                                             UnitDataInteractor unitDataInteractor) {
        return new HistoryPresenter(workoutInteractor, unitDataInteractor);
    }
}
