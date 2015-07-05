package cat.xojan.fittracker.daggermodules;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.GoogleFitSessionStorage;
import cat.xojan.fittracker.domain.SessionDataInteractor;
import cat.xojan.fittracker.domain.SessionRepository;
import cat.xojan.fittracker.view.activity.BaseActivity;
import cat.xojan.fittracker.view.activity.StartUpActivity;
import cat.xojan.fittracker.view.activity.WorkoutActivity;
import cat.xojan.fittracker.view.presenter.SessionPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SessionRepository.class,
                SessionDataInteractor.class,
                SessionPresenter.class,
                StartUpActivity.class
        },
        addsTo = AppModule.class,
        library = true
)
public class SessionModule {

    private final Activity mActivity;

    public SessionModule(Activity activity) {
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
    public SessionRepository provideSessionRepository() {
        return new GoogleFitSessionStorage();
    }

    @Provides
    public SessionDataInteractor provideSessionDataInteractor(SessionRepository sessionRepository) {
        return new SessionDataInteractor(sessionRepository);
    }

    @Provides
    public SessionPresenter provideSessionPresenter(SessionDataInteractor sessionDataInteractor) {
        return new SessionPresenter(sessionDataInteractor);
    }
}
