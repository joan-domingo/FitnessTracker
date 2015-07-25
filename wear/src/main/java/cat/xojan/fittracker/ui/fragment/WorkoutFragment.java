package cat.xojan.fittracker.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.ui.controller.DistanceController;
import cat.xojan.fittracker.ui.controller.FitnessController;
import cat.xojan.fittracker.ui.controller.TimeController;

public class WorkoutFragment extends Fragment {

    public TrackingStateListener mCallback;

    public interface TrackingStateListener {
        void isTracking(boolean isTracking);
        void removeLocationUpdates();
    }

    @Bind(R.id.chrono)
    Chronometer mChronometerView;

    @Bind(R.id.distance)
    TextView mDistanceView;

    @Bind(R.id.bar_lap_pause)
    LinearLayout mLapPauseView;

    @Bind(R.id.bar_resume_finish)
    LinearLayout mResumeFinishView;

    private TimeController mTimeController;
    private FitnessController mFitnessController;
    private DistanceController mDistanceController;

    private static final String TAG = WorkoutFragment.class.getSimpleName();

    @OnClick(R.id.button_lap)
    public void onLapButtonClicked(Button lapButton){
        mTimeController.lapFinish();
        mFitnessController.saveSegment(false);
        mTimeController.lapStart();
        mDistanceController.lap();
    }

    @OnClick(R.id.button_pause)
    public void onPauseButtonClicked(Button lapButton){
        showLapPause(false);
        mCallback.isTracking(false);

        mTimeController.pause();
        mFitnessController.saveSegment(false);
    }

    @OnClick(R.id.button_resume)
    public void onResumeClicked(Button resumeButton) {
        showLapPause(true);
        mCallback.isTracking(true);

        mTimeController.resume();
        mFitnessController.saveSegment(true);
        mDistanceController.resume();
    }

    @OnClick(R.id.button_finish)
    public void onFinishButtonClicked(Button finishButton) {
        //remove location listener
        mCallback.removeLocationUpdates();

        //show results
        getActivity().getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ResultFragment())
                .commit();

        mTimeController.finish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (TrackingStateListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        ButterKnife.bind(this, view);

        mTimeController = TimeController.getInstance();
        mTimeController.initChronometer(mChronometerView);
        showLapPause(true);

        mDistanceController = DistanceController.getInstance();
        mDistanceController.init(getActivity(), mDistanceView);

        mFitnessController = FitnessController.getInstance();

        return view;
    }

    private void showLapPause(boolean b) {
        if (b) {
            mLapPauseView.setVisibility(View.VISIBLE);
            mResumeFinishView.setVisibility(View.GONE);
        } else {
            mLapPauseView.setVisibility(View.GONE);
            mResumeFinishView.setVisibility(View.VISIBLE);
        }
    }
}
