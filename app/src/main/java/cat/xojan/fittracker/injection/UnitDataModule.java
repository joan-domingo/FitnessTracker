package cat.xojan.fittracker.injection;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.SharedPreferencesStorage;
import cat.xojan.fittracker.domain.PreferencesRepository;
import cat.xojan.fittracker.domain.UnitDataInteractor;
import cat.xojan.fittracker.presentation.presenter.UnitDataPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                PreferencesRepository.class,
                UnitDataInteractor.class,
                UnitDataPresenter.class
        }
)
public class UnitDataModule {

    @Provides
    @Singleton
    public PreferencesRepository provideFirstRunRepository() {
        return new SharedPreferencesStorage(null);
    }

    @Provides
    @Singleton
    public UnitDataInteractor provideFirstRunInteractor(PreferencesRepository firstRunRepository) {
        return new UnitDataInteractor(firstRunRepository);
    }

    @Provides
    public UnitDataPresenter provideFirstRunPresenter(UnitDataInteractor firstRunInteractor) {
        return new UnitDataPresenter(firstRunInteractor);
    }
}
