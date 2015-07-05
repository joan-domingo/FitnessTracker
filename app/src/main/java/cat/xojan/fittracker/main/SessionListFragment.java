package cat.xojan.fittracker.main;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.BaseFragment;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.main.controllers.FitnessController;
import cat.xojan.fittracker.util.Utils;

public class SessionListFragment extends BaseFragment {

    @Inject FitnessController fitController;
    @Inject Context mContext;
    @Inject
    WorkoutFragment workoutFragment;

    @Bind(R.id.sessions_list) RecyclerView mRecyclerView;
    @Bind(R.id.date_range_end) Button mDateEndButton;
    @Bind(R.id.date_range_start) Button mDateStartButton;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeLayout;
    @Bind(R.id.my_awesome_toolbar) Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_session_list, container, false);
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeLayout.setColorSchemeResources(R.color.accent);
        swipeLayout.setOnRefreshListener(() -> {
            fitController.setEndTime(Calendar.getInstance());
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDateEndButton.setText(Utils.getRightDate(fitController.getEndTime(), getActivity()));
        mDateStartButton.setText(Utils.getRightDate(fitController.getStartTime(), getActivity()));

    }







    private void showProgressDialog(boolean b) {
        if (b) {
            //((MainActivity) getActivity()).showDialog();
        } else {
            //((MainActivity) getActivity()).dismissDialog();
            swipeLayout.setRefreshing(false);
        }
    }

}
