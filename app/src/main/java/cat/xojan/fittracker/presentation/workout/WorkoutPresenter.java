package cat.xojan.fittracker.presentation.workout;

import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogSubscriber;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.Session;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import cat.xojan.fittracker.presentation.startup.FitnessDataListener;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Workout presenter.
 */
public class WorkoutPresenter implements BasePresenter {

    private final WorkoutInteractor mWorkoutInteractor;
    private Listener mListener;
    private long mStartTime;
    private long mEndTime;
    private Subscription mSubscription;

    @Inject
    public WorkoutPresenter(WorkoutInteractor workoutInteractor) {
        mWorkoutInteractor = workoutInteractor;
    }

    interface Listener {
        void startWorkout();
        void stopWorkout();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mListener = null;
        mSubscription.unsubscribe();
    }

    public void saveWorkout(Workout workout) {
        mSubscription = mWorkoutInteractor.saveWorkout(workout)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SaveWorkoutSubscriber());
    }

    private void stopWorkout() {
        mEndTime = getCurrentTime();
        mListener.stopWorkout();
    }

    private void startWorkout() {
        mStartTime = getCurrentTime();
        mListener.startWorkout();
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @RxLogSubscriber
    public class SaveWorkoutSubscriber extends Subscriber<Void> {

        @Override
        public void onCompleted() {
            Log.i("joan", "workout saved");
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Void aVoid) {
            //ignore
        }
    }
}
