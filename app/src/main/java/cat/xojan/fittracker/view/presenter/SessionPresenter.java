package cat.xojan.fittracker.view.presenter;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.domain.SessionDataInteractor;
import cat.xojan.fittracker.view.listener.OnSessionInsertListener;
import cat.xojan.fittracker.view.listener.UiContentUpdater;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SessionPresenter {

    private final SessionDataInteractor mSessionDataInteractor;
    private long mStartTime;
    private long mEndTime;

    public SessionPresenter(SessionDataInteractor sessionDataInteractor) {
        mSessionDataInteractor = sessionDataInteractor;
    }

    private SessionReadResult getSessionsData(GoogleApiClient googleApiClient) {
        SessionReadRequest sessionReadRequest = new SessionReadRequest.Builder()
                .setTimeInterval(getStartTime(), getEndTime(), TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .readSessionsFromAllApps()
                .enableServerQueries()
                .build();

        return mSessionDataInteractor.getSessionsData(sessionReadRequest, googleApiClient);
    }

    public long getEndTime() {
        if (mEndTime == 0) {
            mEndTime = Calendar.getInstance().getTimeInMillis();
        }
        return mEndTime;
    }

    public long getStartTime() {
        if (mStartTime == 0) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.MONTH, -1);
            mStartTime = date.getTimeInMillis();
        }
        return mStartTime;
    }

    public void readSessions(GoogleApiClient fitnessClient, UiContentUpdater uiContentUpdater) {
        Observable.just(fitnessClient)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<GoogleApiClient>() {
                    public SessionReadResult sessionReadResult;

                    @Override
                    public void onCompleted() {
                        Observable.just(sessionReadResult)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(uiContentUpdater::setSessionData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Constant.TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(GoogleApiClient googleApiClient) {
                        sessionReadResult = getSessionsData(googleApiClient);
                    }
                });
    }

    public void setStartTime(long timeInMillis) {
        mStartTime = timeInMillis;
    }

    public void setEndTime(long timeInMillis) {
        mEndTime = timeInMillis;
    }

    public void insertSession(SessionInsertRequest sessionInsertRequest,
                              GoogleApiClient googleApiClient, OnSessionInsertListener listener) {
        Observable.just(googleApiClient)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<GoogleApiClient>() {

                    @Override
                    public void onCompleted() {
                        listener.insertCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Constant.TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(GoogleApiClient googleApiClient) {
                        mSessionDataInteractor.insertSessionInsertRequest(sessionInsertRequest,
                                googleApiClient);
                    }
                });
    }
}
