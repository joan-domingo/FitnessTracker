package cat.xojan.fittracker.view.presenter;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.domain.SessionDataInteractor;
import cat.xojan.fittracker.view.UiContentUpdater;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SessionPresenter {

    private final SessionDataInteractor mSessionDataInteractor;

    public SessionPresenter(SessionDataInteractor sessionDataInteractor) {
        mSessionDataInteractor = sessionDataInteractor;
    }

    public SessionReadResult getSessionsData(GoogleApiClient googleApiClient) {
        SessionReadRequest sessionReadRequest = new SessionReadRequest.Builder()
                .setTimeInterval(getStartTime(), getEndTime(), TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .readSessionsFromAllApps()
                .enableServerQueries()
                .build();

        return mSessionDataInteractor.getSessionsData(sessionReadRequest, googleApiClient);
    }

    private long getEndTime() {
        Calendar date = Calendar.getInstance();
        return date.getTimeInMillis();
    }

    private long getStartTime() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, -1);
        return date.getTimeInMillis();
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
}
