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
        void startChrono();
        void stopChrono();
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

    public void actionButtonClicked() {
        if (mStartTime == 0) {
            startWorkout();
        } else {
            stopWorkout();
        }
    }

    private void stopWorkout() {
        mEndTime = getCurrentTime();
        mListener.stopChrono();
    }

    private void startWorkout() {
        mStartTime = getCurrentTime();
        mListener.startChrono();
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
