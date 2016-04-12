package cat.xojan.fittracker.presentation.workout;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.injection.component.WorkoutComponent;
import cat.xojan.fittracker.presentation.BaseFragment;


public class WorkoutFragment extends BaseFragment implements WorkoutPresenter.Listener {

    @Inject
    WorkoutPresenter mPresenter;

    @OnClick(R.id.action_button)
    public void actionButtonClicked() {
        mPresenter.actionButtonClicked();
    }
    @Bind(R.id.chronometer)
    Chronometer mChrono;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(WorkoutComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workout_fragment, container, false);
        ButterKnife.bind(this, view);
        mPresenter.setListener(this);
        mChrono.setText("00:00:00");
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.destroy();
    }

    @Override
    public void startChrono() {
        mChrono.setOnChronometerTickListener(new ChronometerTickListener());
        mChrono.setBase(SystemClock.elapsedRealtime());
        mChrono.start();
    }

    @Override
    public void stopChrono() {
        mChrono.stop();
    }

    private class ChronometerTickListener implements Chronometer.OnChronometerTickListener {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long t = SystemClock.elapsedRealtime() - chronometer.getBase();
            int h = (int) (t / 3600000);
            int m = (int) (t - h * 3600000) / 60000;
            int s = (int) (t - h * 3600000 - m * 60000) / 1000;
            String hh = h < 10 ? "0" + h : h + "";
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";
            chronometer.setText(hh + ":" + mm + ":" + ss);
        }
    }
}
