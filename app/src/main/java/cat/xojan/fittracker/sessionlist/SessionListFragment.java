package cat.xojan.fittracker.sessionlist;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.ActivityType;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.MainActivity;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.util.Utils;
import cat.xojan.fittracker.workout.WorkoutFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SessionListFragment extends Fragment {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private Button mDateEndButton;
    private Button mDateStartButton;
    private SwipeRefreshLayout swipeLayout;
    private static final String TAG = "RecyclerViewFragment";
    private SessionReadResult mSessionReadResult;

    public SessionListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_session_list, container, false);
        Context mContext = getActivity().getBaseContext();
        view.setTag(TAG);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_awesome_toolbar);
        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.sessions_loading_spinner);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.sessions_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        showProgressBar(true);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.accent);
        swipeLayout.setOnRefreshListener(() -> {
            FitnessController.getInstance().setEndTime(Calendar.getInstance());
            readSessions();
        });



        ImageButton newWorkout = (ImageButton) view.findViewById(R.id.fab_add);
        newWorkout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.choose_activity)
                    .setItems(ActivityType.getStringArray(getActivity().getBaseContext()), (dialog, which) -> {
                        String activity = ActivityType.values()[which].getActivity();
                        FitnessController.getInstance().setFitnessActivity(activity);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new WorkoutFragment(), Constant.WORKOUT_FRAGMENT_TAG)
                                .commit();
                    });
            builder.create().show();
        });

        mDateEndButton = (Button) view.findViewById(R.id.date_range_end);
        mDateStartButton = (Button) view.findViewById(R.id.date_range_start);

        readSessions();

        return view;
    }

    private void readSessions() {
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(FitnessController.getInstance().getStartTime(),
                        FitnessController.getInstance().getEndTime(), TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .readSessionsFromAllApps()
                .enableServerQueries()
                .build();

        Observable.just(readRequest)
                .subscribeOn(Schedulers.newThread())
                .subscribe(request -> {
                    mSessionReadResult = Fitness.SessionsApi
                            .readSession(MainActivity.mClient, readRequest)
                            .await(1, TimeUnit.MINUTES);

                    Observable.just(mSessionReadResult)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                mRecyclerView.setAdapter(new SessionAdapter(getActivity(), result));
                                showProgressBar(false);
                            });
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        mDateEndButton.setText(Utils.getRightDate(FitnessController.getInstance().getEndTime(), getActivity()));
        mDateEndButton.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment() {

                public void onDateSet(DatePicker view, int year, int month, int day) {
                    // Do something with the date chosen by the user
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    FitnessController.getInstance().setEndTime(calendar);
                    mDateEndButton.setText(Utils.getRightDate(FitnessController.getInstance().getEndTime(), getActivity()));
                    showProgressBar(true);
                    readSessions();
                }
            };
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.PARAMETER_DATE, FitnessController.getInstance().getEndTime());
            newFragment.setArguments(bundle);
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        mDateStartButton.setText(Utils.getRightDate(FitnessController.getInstance().getStartTime(), getActivity()));
        mDateStartButton.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment() {

                public void onDateSet(DatePicker view, int year, int month, int day) {
                    // Do something with the date chosen by the user
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    FitnessController.getInstance().setStartTime(calendar);
                    mDateStartButton.setText(Utils.getRightDate(FitnessController.getInstance().getStartTime(), getActivity()));
                    showProgressBar(true);
                    readSessions();
                }
            };
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.PARAMETER_DATE, FitnessController.getInstance().getStartTime());
            newFragment.setArguments(bundle);
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

    }

    private void showProgressBar(boolean b) {
        if (b) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            swipeLayout.setRefreshing(false);
        }
    }
}
