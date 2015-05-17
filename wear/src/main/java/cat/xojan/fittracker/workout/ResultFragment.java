package cat.xojan.fittracker.workout;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.fitness.request.SessionInsertRequest;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.service.UtilityService;
import cat.xojan.fittracker.workout.controller.FitnessController;

public class ResultFragment extends Fragment {

    @OnClick(R.id.save_button)
    public void onSaveButtonClicked() {
        SessionInsertRequest insertRequest = FitnessController.getInstance().saveSession();
        sendIntentToSaveSessionInApp(insertRequest);
        getActivity().finish();
    }

    private void sendIntentToSaveSessionInApp(SessionInsertRequest insertRequest) {
        UtilityService.saveSession(getActivity(), UtilityService.SAVE_SESSION, insertRequest);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
