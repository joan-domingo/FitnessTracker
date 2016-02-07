package cat.xojan.fittracker.presentation.home;

import android.app.Activity;

import com.google.android.gms.fitness.data.Session;

import java.util.Calendar;
import java.util.List;

import cat.xojan.fittracker.data.UserData;
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

    private final UserData mUserData;

    public HomePresenter(UserData userData) {
        mUserData = userData;
    }

    @Override
    public void resume() {
        calculateDataOverview();
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    public void calculateDataOverview() {
    }
}
