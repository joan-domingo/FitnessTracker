package cat.xojan.fittracker.presentation.sessiondetails;

import android.view.View;

import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.domain.interactor.UnitDataInteractor;
import cat.xojan.fittracker.domain.interactor.WorkoutInteractor;
import cat.xojan.fittracker.presentation.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Presenter for {@link SessionDetailsActivity}.
 */
public class SessionDetailsPresenter implements BasePresenter {

    private final UnitDataInteractor mUnitDataInteractor;
    private final WorkoutInteractor mWorkoutInteractor;
    private ViewListener mListener;

    interface ViewListener {
        void updateData(Workout workout);

        void onWorkoutDeleted();
    }

    public SessionDetailsPresenter(UnitDataInteractor unitDataInteractor,
                                   WorkoutInteractor workoutInteractor) {
        mUnitDataInteractor = unitDataInteractor;
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

    public void listenToUpdates(ViewListener listener) {
        mListener = listener;
    }

    public void loadSessionData(long workoutId) {
        mWorkoutInteractor.loadWorkout(workoutId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Workout>() {
                    @Override
                    public void call(Workout workout) {
                        mListener.updateData(workout);
                    }
                });
    }

    public void deleteSession(Workout workout) {
        mWorkoutInteractor.deleteWorkout(workout)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mListener.onWorkoutDeleted();
                    }
                });
    }
}
