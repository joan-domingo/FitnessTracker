package cat.xojan.fittracker.presentation.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.domain.ActivityType;
import cat.xojan.fittracker.injection.component.HomeComponent;
import cat.xojan.fittracker.navigation.Navigator;
import cat.xojan.fittracker.presentation.BaseFragment;
import cat.xojan.fittracker.presentation.view.TriangleScreen;

import static cat.xojan.fittracker.presentation.view.TriangleScreen.*;

public class HomeFragment extends BaseFragment implements
        FitnessActivityClickListener {

    @Inject
    Navigator mNavigator;
    @Inject
    HomePresenter mPresenter;
    /*@Bind(R.id.triangle_view)
    TriangleScreen mTriangleView;*/

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
        //mTriangleView.setFitnessActivityClickListener(this);
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

    @Override
    public void onClick(ActivityType activityType) {
        mNavigator.navigateToWorkoutActivity(getActivity(), activityType);
    }
}
