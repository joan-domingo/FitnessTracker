package cat.xojan.fittracker.presentation.home;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Session;

import java.util.Calendar;
import java.util.List;

import cat.xojan.fittracker.domain.FitnessDataInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Joan on 24/01/2016.
 */
public class HomePresenter implements BasePresenter {
    private final FitnessDataInteractor mFitnessDataInteractor;
    private final HomeActivity mActivity;
    private Subscription mSubscription;

    public HomePresenter(FitnessDataInteractor fitnessDataInteractor, HomeActivity activity) {
        mFitnessDataInteractor = fitnessDataInteractor;
        mActivity = activity;
    }

    @Override
    public void resume() {
        /*if (mSessionList = null) {
            fetchSessionData();
        }*/
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    public void showData(GoogleApiClient googleApiClient) {
        // mActivity.showProgress();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 12, 12);
        mSubscription = mFitnessDataInteractor.updateData(calendar.getTime(), googleApiClient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FitnessDataSubscriber());
    }

    private class FitnessDataSubscriber implements Observer<List<Session>>{
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Session> sessions) {

        }
    }
}
