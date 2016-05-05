package cat.xojan.fittracker.injection.module;

import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.presentation.home.HomePresenter;
import cat.xojan.fittracker.presentation.sessiondetails.SessionDetailsPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class SessionDetailsModule {

    public SessionDetailsModule() {

    }

    @Provides
    @PerActivity
    SessionDetailsPresenter provideSessionDetailsPresenter(UnitDataInteractor unitDataInteractor,
                                                 WorkoutInteractor workoutInteractor) {
        return new SessionDetailsPresenter(unitDataInteractor, workoutInteractor);
    }
}
