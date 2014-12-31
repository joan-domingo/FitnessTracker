package cat.xojan.fittracker.session;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import java.util.Calendar;

import cat.xojan.fittracker.ActivityType;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.Utils;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.workout.WorkoutFragment;

public class SessionListFragment extends Fragment {

    private static Handler handler;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private Calendar mStartDate;
    private Calendar mEndDate;

    public static Handler getHandler() {
        return handler;
    }

    public SessionListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_session_list, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_awesome_toolbar);
        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) view.findViewById(R.id.sessions_loading_spinner);
        mProgressBar.setVisibility(View.VISIBLE);

        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Context mContext = getActivity().getBaseContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sessions_list);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.GOOGLE_API_CLIENT_CONNECTED:
                        if (getActivity() != null) {
                            FitnessController.getInstance().readLastSessions(mStartDate.getTimeInMillis(), mEndDate.getTimeInMillis());
                            break;
                        }
                    case Constant.MESSAGE_SESSIONS_READ:
                        if (getActivity() != null) {
                            RecyclerView.Adapter mAdapter = new SessionAdapter(FitnessController.getInstance().getReadSessions(),
                                    FitnessController.getInstance().getDistances(), getActivity().getBaseContext());
                            mRecyclerView.setAdapter(mAdapter);
                            mProgressBar.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        };

        FitnessController.getInstance().init();

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView IdentifierView = (TextView) view.findViewById(R.id.session_identifier);
                        TextView startTime = (TextView) view.findViewById(R.id.session_start_time);
                        TextView endTime = (TextView) view.findViewById(R.id.session_end_time);

                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.PARAMETER_SESSION_ID, IdentifierView.getText().toString());
                        bundle.putLong(Constant.PARAMETER_START_TIME, Long.valueOf(startTime.getText().toString()));
                        bundle.putLong(Constant.PARAMETER_END_TIME, Long.valueOf(endTime.getText().toString()));

                        SessionFragment sessionFragment = new SessionFragment();
                        sessionFragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, sessionFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                })
        );

        ImageButton newWorkout = (ImageButton) view.findViewById(R.id.fab_add);
        newWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.choose_activity)
                        .setItems(ActivityType.getStringArray(getActivity().getBaseContext()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String activity = ActivityType.values()[which].getActivity();
                                FitnessController.getInstance().setFitnessActivity(activity);

                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, new WorkoutFragment(), "workoutFragment")
                                        .commit();
                            }
                });
                builder.create().show();
            }
        });

        final Button dateEndButton = (Button) view.findViewById(R.id.date_range_end);
        mEndDate = Calendar.getInstance();
        dateEndButton.setText(Utils.millisToDate(mEndDate.getTimeInMillis()));
        dateEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment() {

                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // Do something with the date chosen by the user
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        mEndDate = calendar;
                        dateEndButton.setText(Utils.millisToDate(mEndDate.getTimeInMillis()));
                        mProgressBar.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(Constant.GOOGLE_API_CLIENT_CONNECTED);
                    }
                };
                Bundle bundle = new Bundle();
                bundle.putLong(Constant.PARAMETER_DATE, mEndDate.getTimeInMillis());
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        final Button dateStartButton = (Button) view.findViewById(R.id.date_range_start);
        mStartDate = Calendar.getInstance();
        mStartDate.add(Calendar.MONTH, -1);
        dateStartButton.setText(Utils.millisToDate(mStartDate.getTimeInMillis()));
        dateStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment() {

                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // Do something with the date chosen by the user
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        mStartDate = calendar;
                        dateStartButton.setText(Utils.millisToDate(mStartDate.getTimeInMillis()));
                        mProgressBar.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(Constant.GOOGLE_API_CLIENT_CONNECTED);
                    }
                };
                Bundle bundle = new Bundle();
                bundle.putLong(Constant.PARAMETER_DATE, mStartDate.getTimeInMillis());
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        return view;
    }
}
