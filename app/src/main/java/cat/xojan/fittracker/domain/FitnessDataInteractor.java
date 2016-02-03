package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.Date;
import java.util.List;

import cat.xojan.fittracker.data.UserData;
import rx.Observable;
import rx.Subscriber;

/**
 * Fitness data interactor. Reads and writes fitness session data from the user to
 * the google fitness repository.
 */
public class FitnessDataInteractor {

    private final GoogleFitRepository mGoogleFitRepository;

    public FitnessDataInteractor(GoogleFitRepository googleFitRepository) {
        mGoogleFitRepository = googleFitRepository;
    }

    /**
     * Read user's fitness data.
     */
    public Observable<List<Session>> updateData(Date lastUpdate, GoogleApiClient googleApiClient) {
        return Observable.create(new Observable.OnSubscribe<List<Session>>() {
            @Override
            public void call(Subscriber<? super List<Session>> subscriber) {
                try {
                    SessionReadResult result = mGoogleFitRepository.readHistory(lastUpdate,
                                    googleApiClient);
                    subscriber.onNext(result.getSessions());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
