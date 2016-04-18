package cat.xojan.fittracker.presentation.workout;

import android.util.Log;

import java.util.Calendar;

import javax.inject.Inject;

import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Workout presenter.
 */
public class WorkoutPresenter implements BasePresenter {

    private static final String TAG = WorkoutPresenter.class.getSimpleName();

    private final WorkoutInteractor mWorkoutInteractor;
    private long mStartTime;
    private long mEndTime;
    private Subscription mSubscription;

    @Inject
    public WorkoutPresenter(WorkoutInteractor workoutInteractor) {
        mWorkoutInteractor = workoutInteractor;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    public void saveWorkout(long distance, String activityType) {
        Workout workout = new Workout(
                Calendar.getInstance().getTimeInMillis(),
                "workout test",
                mEndTime - mStartTime,
                mStartTime,
                mEndTime,
                distance,
                activityType);

        mSubscription = mWorkoutInteractor.saveWorkout(workout)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SaveWorkoutSubscriber());
    }

    public void stopWorkout() {
        mEndTime = getCurrentTime();
    }

    public void startWorkout() {
        mStartTime = getCurrentTime();
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Save Workout subscriber.
     */
    private class SaveWorkoutSubscriber extends Subscriber<Void> {

        @Override
        public void onCompleted() {
            Log.i(TAG, "Workout saved successfully");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Void aVoid) {
            //ignore
        }
    }
}
