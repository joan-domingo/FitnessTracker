package cat.xojan.fittracker.presentation.startup;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Session;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.domain.PreferencesInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import cat.xojan.fittracker.presentation.home.HomeActivity;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Presenter for StartUp activity.
 */
public class StartupPresenter implements BasePresenter {

    private final FitnessDataInteractor mFitnessDataInteractor;
    private final PreferencesInteractor mPreferencesInteractor;
    private final Activity mActivity;

    private Subscription mSubscription;

    @Inject
    public StartupPresenter(FitnessDataInteractor fitnessDataInteractor,
                            PreferencesInteractor preferencesInteractor,
                            Activity activity) {
        mFitnessDataInteractor = fitnessDataInteractor;
        mPreferencesInteractor = preferencesInteractor;
        mActivity = activity;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mSubscription.unsubscribe();
    }

    public void updateUserFitnessData() {
        Date lastUpdate = mPreferencesInteractor.getLastUpdate();
        mSubscription = mFitnessDataInteractor.updateData(lastUpdate)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new UpdateFitnessDataObserver());
    }

    public class UpdateFitnessDataObserver implements Observer<List<Session>> {
        @Override
        public void onCompleted() {
            startHomeActivity();
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Session> sessions) {

        }
    }

    private void startHomeActivity() {
        Intent intent = new Intent(mActivity, HomeActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish();
    }
}
