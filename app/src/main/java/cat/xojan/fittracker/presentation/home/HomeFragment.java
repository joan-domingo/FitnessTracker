package cat.xojan.fittracker.presentation.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.entity.ActivityType;
import cat.xojan.fittracker.injection.component.HomeComponent;
import cat.xojan.fittracker.navigation.Navigator;
import cat.xojan.fittracker.presentation.BaseFragment;

public class HomeFragment extends BaseFragment {

    @Inject
    Navigator mNavigator;

    @Inject
    HomePresenter mPresenter;

    @OnClick(R.id.walk)
    void walk() {
        mNavigator.navigateToWorkoutActivity(getActivity(), ActivityType.Walking);
    }

    @OnClick(R.id.run)
    void run() {
        mNavigator.navigateToWorkoutActivity(getActivity(), ActivityType.Running);
    }

    @OnClick(R.id.bike)
    void bike() {
        mNavigator.navigateToWorkoutActivity(getActivity(), ActivityType.Biking);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(HomeComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
