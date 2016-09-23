package cat.xojan.fittracker.presentation.workout;

import android.location.Location;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.BuildConfig;
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

        void updateActionButton();
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

    void setupListener(WorkoutPresenterListener listener) {
        mListener = listener;
    }

    void saveWorkout(long distance, String activityType, List<Location> locationList) {
        Workout workout = new Workout(
                Calendar.getInstance().getTimeInMillis(),
                activityType.toUpperCase() + " " + Utils.millisToDate(mStartTime),
                mEndTime - mStartTime,
                mStartTime,
                mEndTime,
                distance,
                activityType,
                Utils.locationsToJson(locationList));

        mSubscription = mWorkoutInteractor.saveWorkout(workout)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SaveWorkoutSubscriber());
    }

    void stopWorkout() {
        mEndTime = getCurrentTime();
    }

    void startWorkout() {
        mStartTime = getCurrentTime();
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    void updateDistanceView(final double distance, final TextView distanceView) {
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
    private class SaveWorkoutSubscriber extends Subscriber<Workout> {

        @Override
        public void onCompleted() {
            // ignore
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            if (!BuildConfig.DEBUG) {
                Crashlytics.logException(e);
            }
            mListener.finishWorkout();
        }

        @Override
        public void onNext(final Workout workout) {
            mListener.updateActionButton();
        }
    }
}
