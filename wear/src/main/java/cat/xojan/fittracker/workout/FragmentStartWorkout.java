package cat.xojan.fittracker.workout;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.workout.controller.TimeController;

public class FragmentStartWorkout extends Fragment {

    private WorkoutStartListener mCallback;

    public interface WorkoutStartListener {
        public void notifyWorkoutStart();
    }

    @OnClick(R.id.button_start)
    public void goToWorkoutFragment() {
        TimeController.getInstance().setSessionStart();
        mCallback.notifyWorkoutStart();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new WorkoutFragment())
                .commit();
    }

    @OnClick(R.id.button_exit)
    public void exitWorkout() {
        getActivity().finish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (WorkoutStartListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
