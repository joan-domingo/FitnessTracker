package cat.xojan.fittracker.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.ui.activity.SaveSessionActivity;
import cat.xojan.fittracker.ui.controller.DistanceController;
import cat.xojan.fittracker.ui.controller.FitnessController;
import cat.xojan.fittracker.ui.controller.TimeController;

public class ResultFragment extends Fragment {

    private static final String SHARED_PREFERENCES = "cat.xojan.fittracker_preferences";
    private static final String DISTANCE_MEASURE_KM = "Km";
    private static final String DISTANCE_MEASURE_MILE = "Mi";
    public static final String PREFERENCE_MEASURE_UNIT = "unit_measure";
    public static final String PREFERENCE_DATE_FORMAT = "date_format";

    private TimeController mTimeController;
    private DistanceController mDistanceController;

    @Bind(R.id.total_distance)
    TextView mTotalDistanceView;

    @Bind(R.id.total_time)
    TextView mTotalTimeView;

    @OnClick(R.id.save_button)
    public void onSaveButtonClicked() {
        Intent intent = new Intent(getActivity(), SaveSessionActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.exit_button)
    public void onExitButtonClicked() {
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, view);

        mTimeController = TimeController.getInstance();
        mDistanceController = DistanceController.getInstance();

        mTotalDistanceView.setText(getRightDistance(mDistanceController.getTotalDistance(),
                getActivity()));
        mTotalTimeView.setText(getTimeDifference(mTimeController.getSessionEndTime(),
                mTimeController.getSessionStartTime()));

        return view;
    }

    private String getRightDistance(float value, Context context) {
        double distance = value;
        String measureUnit = context.getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE)
                .getString(PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(DistanceController.DISTANCE_MEASURE_MILE)) {
            distance = distance / 1609.344;
            return String.format("%.2f", distance) + " " + context.getString(R.string.mi);
        } else {
            distance = distance / 1000;
            return String.format("%.2f", distance) + " " + context.getString(R.string.km);
        }
    }

    private String getTimeDifference(long endTime, long startTime) {
        long result = endTime - startTime;

        long second = (result / 1000) % 60;
        long minute = (result / (1000 * 60)) % 60;
        long hour = (result / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
