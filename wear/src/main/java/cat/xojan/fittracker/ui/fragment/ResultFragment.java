package cat.xojan.fittracker.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.ui.activity.SaveSessionActivity;

public class ResultFragment extends Fragment {

    @OnClick(R.id.save_button)
    public void onSaveButtonClicked() {
        Intent intent = new Intent(getActivity(), SaveSessionActivity.class);
        getActivity().startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
