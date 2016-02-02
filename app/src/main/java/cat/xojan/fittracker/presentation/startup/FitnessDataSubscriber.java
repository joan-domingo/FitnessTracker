package cat.xojan.fittracker.presentation.startup;

import com.fernandocejas.frodo.annotation.RxLogSubscriber;
import com.google.android.gms.fitness.data.Session;

import java.util.List;

import rx.Subscriber;

@RxLogSubscriber
public class FitnessDataSubscriber extends Subscriber<List<Session>> {

    private final FitnessDataListener mListener;

    public FitnessDataSubscriber(FitnessDataListener listener) {
        mListener = listener;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        mListener.onError(e);
    }

    @Override
    public void onNext(List<Session> sessions) {
        mListener.onSuccessfullyUpdated(sessions);
    }
}
