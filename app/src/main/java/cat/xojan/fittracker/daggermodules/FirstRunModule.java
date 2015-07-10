package cat.xojan.fittracker.daggermodules;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.SharedPreferencesStorage;
import cat.xojan.fittracker.domain.FirstRunInteractor;
import cat.xojan.fittracker.domain.FirstRunRepository;
import cat.xojan.fittracker.view.presenter.FirstRunPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                FirstRunRepository.class,
                FirstRunInteractor.class,
                FirstRunPresenter.class
        }
)
public class FirstRunModule {

    @Provides
    @Singleton
    public FirstRunRepository provideFirstRunRepository() {
        return new SharedPreferencesStorage();
    }

    @Provides
    @Singleton
    public FirstRunInteractor provideFirstRunInteractor(FirstRunRepository firstRunRepository) {
        return new FirstRunInteractor(firstRunRepository);
    }

    @Provides
    public FirstRunPresenter provideFirstRunPresenter(FirstRunInteractor firstRunInteractor) {
        return new FirstRunPresenter(firstRunInteractor);
    }
}
