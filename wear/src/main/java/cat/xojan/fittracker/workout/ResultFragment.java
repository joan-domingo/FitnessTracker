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

public class ResultFragment extends Fragment {

    private SaveButtonListener mCallback;

    public interface SaveButtonListener {
        void saveSessionData();
    }

    @OnClick(R.id.save_button)
    public void onSaveButtonClicked() {
        //SessionInsertRequest insertRequest = FitnessController.getInstance().saveSession();
        //UtilityService.saveSession(getActivity(), UtilityService.SAVE_SESSION);
        mCallback.saveSessionData();
        getActivity().finish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (SaveButtonListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
