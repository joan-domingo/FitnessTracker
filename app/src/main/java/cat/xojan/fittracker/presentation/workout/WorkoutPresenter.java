package cat.xojan.fittracker.presentation.workout;

import java.util.Calendar;

import cat.xojan.fittracker.presentation.BasePresenter;

/**
 * Workout presenter.
 */
public class WorkoutPresenter implements BasePresenter {

    private Listener mListener;
    private long mStartTime;
    private long mEndTime;

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
}
