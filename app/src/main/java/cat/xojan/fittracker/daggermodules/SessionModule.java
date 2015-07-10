package cat.xojan.fittracker.daggermodules;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.GoogleFitSessionStorage;
import cat.xojan.fittracker.domain.SessionDataInteractor;
import cat.xojan.fittracker.domain.SessionRepository;
import cat.xojan.fittracker.view.presenter.SessionPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SessionRepository.class,
                SessionDataInteractor.class,
                SessionPresenter.class
        }
)
public class SessionModule {

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
