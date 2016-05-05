package cat.xojan.fittracker.presentation.history;

import java.util.List;

import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Workout history presenter.
 */
public class HistoryPresenter implements BasePresenter {

    private final WorkoutInteractor mWorkoutInteractor;
    private Subscription mSubscription;
    private Listener mListener;
    private final UnitDataInteractor mUnitDataInteractor;

    interface Listener {
        void onWorkoutsLoaded(List<Workout> workouts, DistanceUnit distanceUnit);
    }

    public HistoryPresenter(WorkoutInteractor workoutInteractor,
                            UnitDataInteractor unitDataInteractor) {
        mWorkoutInteractor = workoutInteractor;
        mUnitDataInteractor = unitDataInteractor;
    }

    @Override
    public void resume() {
        loadWorkouts();
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mListener = null;
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    public void loadWorkouts() {
        mSubscription = mWorkoutInteractor.loadAllWorkouts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new LoadWorkoutsSubscriber());

    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    /**
     * Load workouts subscriber.
     */
    private class LoadWorkoutsSubscriber extends Subscriber<List<Workout>> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(final List<Workout> workouts) {
            mUnitDataInteractor.getDistanceUnit()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<DistanceUnit>() {
                        @Override
                        public void call(DistanceUnit distanceUnit) {
                            mListener.onWorkoutsLoaded(workouts, distanceUnit);
                        }
                    });
        }
    }
}
