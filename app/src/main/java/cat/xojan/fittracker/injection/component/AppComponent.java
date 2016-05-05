package cat.xojan.fittracker.injection.component;

import javax.inject.Singleton;

import cat.xojan.fittracker.domain.repository.PreferencesRepository;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.domain.repository.WorkoutRepository;
import cat.xojan.fittracker.injection.module.AppModule;
import cat.xojan.fittracker.navigation.Navigator;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.util.LocationFetcher;
import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(BaseActivity baseActivity);

    //Exposed to sub-graphs.
    Navigator navigator();
    LocationFetcher locationFetcher();

    WorkoutRepository workoutRepository();
    WorkoutInteractor workoutInteractor();

    UnitDataInteractor unitDataInteractor();
    PreferencesRepository preferencesRepository();
}