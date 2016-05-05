package cat.xojan.fittracker.presentation.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.injection.component.HomeComponent;
import cat.xojan.fittracker.presentation.BaseFragment;

/**
 * Distance settings.
 */
public class SettingsFragment extends BaseFragment {

    @Inject
    HomePresenter mPresenter;

    @Bind(R.id.distance)
    RadioGroup mDistanceRadioGroup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(HomeComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        mPresenter.getDistanceUnit(mDistanceRadioGroup);
        mDistanceRadioGroup.setOnCheckedChangeListener(new DistanceRadioGroupChangeListener());

        return view;
    }

    /**
     * Reacts when the distance radio group listener changes.
     */
    private class DistanceRadioGroupChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.kilometers:
                    mPresenter.setDistanceUnit(DistanceUnit.KILOMETER);
                    break;
                default:
                    mPresenter.setDistanceUnit(DistanceUnit.MILE);
                    break;
            }
        }
    }
}
