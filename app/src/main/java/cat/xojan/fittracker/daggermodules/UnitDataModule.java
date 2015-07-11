package cat.xojan.fittracker.daggermodules;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.SharedPreferencesStorage;
import cat.xojan.fittracker.domain.UnitDataInteractor;
import cat.xojan.fittracker.domain.UnitDataRepository;
import cat.xojan.fittracker.ui.presenter.UnitDataPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                UnitDataRepository.class,
                UnitDataInteractor.class,
                UnitDataPresenter.class
        }
)
public class UnitDataModule {

    @Provides
    @Singleton
    public UnitDataRepository provideFirstRunRepository() {
        return new SharedPreferencesStorage();
    }

    @Provides
    @Singleton
    public UnitDataInteractor provideFirstRunInteractor(UnitDataRepository firstRunRepository) {
        return new UnitDataInteractor(firstRunRepository);
    }

    @Provides
    public UnitDataPresenter provideFirstRunPresenter(UnitDataInteractor firstRunInteractor) {
        return new UnitDataPresenter(firstRunInteractor);
    }
}
