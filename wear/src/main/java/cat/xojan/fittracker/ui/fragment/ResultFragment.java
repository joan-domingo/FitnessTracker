package cat.xojan.fittracker.ui.fragment;

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

    private SaveButtonListener mSaveButtonListener;

    public interface SaveButtonListener {
        void saveSessionData();
    }

    @OnClick(R.id.save_button)
    public void onSaveButtonClicked() {
        mSaveButtonListener.saveSessionData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSaveButtonListener = (SaveButtonListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
