package cat.xojan.fittracker.injection;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.repository.GoogleFitSessionStorage;
import cat.xojan.fittracker.domain.SessionDataInteractor;
import cat.xojan.fittracker.domain.SessionRepository;
import cat.xojan.fittracker.presentation.presenter.SessionPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class SessionDataModule {

    @Provides
    @Singleton
    public SessionRepository provideSessionRepository() {
        return new GoogleFitSessionStorage();
    }

    @Provides
    @Singleton
    public SessionDataInteractor provideSessionDataInteractor(SessionRepository sessionRepository) {
        return new SessionDataInteractor(sessionRepository);
    }

    @Provides
    public SessionPresenter provideSessionPresenter(SessionDataInteractor sessionDataInteractor) {
        return new SessionPresenter(sessionDataInteractor);
    }
}
