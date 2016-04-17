package cat.xojan.fittracker.presentation.history;

import java.util.List;

import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Workout history presenter.
 */
public class HistoryPresenter implements BasePresenter {

    interface Listener {
        void onWorkoutsLoaded(List<Workout> workouts);
    }

    private final WorkoutInteractor mWorkoutInteractor;
    private Subscription mSubscription;
    private Listener mListener;

    public HistoryPresenter(WorkoutInteractor workoutInteractor) {
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
        mListener = null;
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
        public void onNext(List<Workout> workouts) {
            mListener.onWorkoutsLoaded(workouts);
        }
    }
}
