package cat.xojan.fittracker.presentation.workout;

import android.widget.TextView;

import java.util.Calendar;

import javax.inject.Inject;

import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import cat.xojan.fittracker.util.Utils;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Workout presenter.
 */
public class WorkoutPresenter implements BasePresenter {

    private final WorkoutInteractor mWorkoutInteractor;
    private final UnitDataInteractor mUnitDataInteractor;
    private long mStartTime;
    private long mEndTime;
    private Subscription mSubscription;
    private WorkoutPresenterListener mListener;

    interface WorkoutPresenterListener {
        void finishWorkout();
    }


    @Inject
    public WorkoutPresenter(WorkoutInteractor workoutInteractor,
                            UnitDataInteractor unitDataInteractor) {
        mWorkoutInteractor = workoutInteractor;
        mUnitDataInteractor = unitDataInteractor;
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
        mListener = null;
    }

    public void setupListener(WorkoutPresenterListener listener) {
        mListener = listener;
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

    public void updateDistanceView(final double distance, final TextView distanceView) {
        mUnitDataInteractor.getDistanceUnit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DistanceUnit>() {
                    @Override
                    public void call(DistanceUnit distanceUnit) {
                        distanceView.setText(Utils.formatDistance(distance, distanceUnit));
                    }
                });
    }

    /**
     * Save Workout subscriber.
     */
    private class SaveWorkoutSubscriber extends Subscriber<Void> {

        @Override
        public void onCompleted() {
            mListener.finishWorkout();
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
