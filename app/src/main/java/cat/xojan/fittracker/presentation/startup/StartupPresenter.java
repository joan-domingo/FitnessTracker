package cat.xojan.fittracker.presentation.startup;

import javax.inject.Inject;

import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.domain.PreferencesInteractor;
import cat.xojan.fittracker.injection.PerActivity;
import cat.xojan.fittracker.presentation.BasePresenter;
import rx.Subscription;

/**
 * Presenter for StartUp activity.
 */
@PerActivity
public class StartupPresenter implements BasePresenter {

    private final FitnessDataInteractor mFitnessDataInteractor;
    private final PreferencesInteractor mPreferencesInteractor;
    private Subscription mSubscription;

    @Inject
    public StartupPresenter(FitnessDataInteractor fitnessDataInteractor,
                            PreferencesInteractor preferencesInteractor) {
        mFitnessDataInteractor = fitnessDataInteractor;
        mPreferencesInteractor = preferencesInteractor;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        //mSubscription.unsubscribe();
    }

    /*public void updateUserFitnessData(GoogleApiClient googleApiClient) {
        Date lastUpdate = mPreferencesInteractor.getLastUpdate();
        mSubscription = mFitnessDataInteractor.updateData(lastUpdate, googleApiClient)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new FitnessDataSubscriber(mListener));
    }*/
}
