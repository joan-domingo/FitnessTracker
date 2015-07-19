package cat.xojan.fittracker.modules;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import cat.xojan.fittracker.data.WearableDataApiRequester;
import cat.xojan.fittracker.domain.SessionDataInteractor;
import cat.xojan.fittracker.domain.SessionDataRepository;
import cat.xojan.fittracker.ui.activity.SaveSessionActivity;
import cat.xojan.fittracker.ui.presenter.SessionDataPresenter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                SaveSessionActivity.class,
                SessionDataPresenter.class,
                SessionDataInteractor.class,
                SessionDataRepository.class
        },
        addsTo = AppModule.class,
        library = true
)
public class SaveSessionModule {

    private Activity mActivity;

    public SaveSessionModule(Activity activity) {
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
    public SessionDataPresenter provideSessionDataPresenter(SessionDataInteractor sessionDataInteractor) {
        return new SessionDataPresenter(sessionDataInteractor);
    }

    @Provides
    @Singleton
    public SessionDataInteractor provideSessionDataInteractor(SessionDataRepository sessionDataRepository) {
        return new SessionDataInteractor(sessionDataRepository);
    }

    @Provides
    @Singleton
    public SessionDataRepository provdieSessionDataRepository() {
        return new WearableDataApiRequester();
    }
}
