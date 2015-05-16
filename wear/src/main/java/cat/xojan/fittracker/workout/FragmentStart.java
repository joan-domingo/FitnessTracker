package cat.xojan.fittracker.workout;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;

public class FragmentStart extends Fragment {

    @OnClick(R.id.button_start)
    public void goToWorkoutFragment() {
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new WorkoutFragment())
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
