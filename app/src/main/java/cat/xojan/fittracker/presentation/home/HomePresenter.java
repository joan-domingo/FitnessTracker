package cat.xojan.fittracker.presentation.home;

import android.app.Activity;
import android.view.View;

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
    private final Activity mActivity;
    private Subscription mSubscription;
    private int mView;

    public HomePresenter(FitnessDataInteractor fitnessDataInteractor, Activity activity) {
        mFitnessDataInteractor = fitnessDataInteractor;
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

    }

    public void showData() {
        // mActivity.showProgress();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 12, 12);
        mSubscription = mFitnessDataInteractor.updateData(calendar.getTime())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FitnessDataSubscriber());
    }

    public void showHomeFragment() {
        mActivity.getFragmentManager().beginTransaction()
                .replace(mView, new HomeFragment())
                .commit();
    }

    public void setUpView(int view) {
        mView = view;
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
