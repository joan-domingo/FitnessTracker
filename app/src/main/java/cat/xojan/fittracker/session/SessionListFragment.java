package cat.xojan.fittracker.session;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import cat.xojan.fittracker.ActivityType;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.workout.WorkoutFragment;

public class SessionListFragment extends Fragment {

    private static Handler handler;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    public static Handler getHandler() {
        return handler;
    }

    public SessionListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_session_list, container, false);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Context mContext = getActivity().getBaseContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sessions_list);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mProgressBar = (ProgressBar) view.findViewById(R.id.sessions_loading_spinner);
        mProgressBar.setVisibility(View.VISIBLE);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.MESSAGE_SESSIONS_READ:
                        RecyclerView.Adapter mAdapter = new SessionAdapter(FitnessController.getInstance().getReadSessions(), getActivity().getBaseContext());
                        mRecyclerView.setAdapter(mAdapter);
                        mProgressBar.setVisibility(View.GONE);
                        break;
                    case Constant.GOOGLE_API_CLIENT_CONNECTED:
                        FitnessController.getInstance().readLastSessions();
                }
            }
        };

        if(!FitnessController.getInstance().isConnected())
            FitnessController.getInstance().init();
        else {
            if (getActivity().getSharedPreferences(Constant.PACKAGE_SPECIFIC_PART, Context.MODE_PRIVATE)
                    .getBoolean(Constant.PARAMETER_RELOAD_LIST, false)) {
                getActivity().getSharedPreferences(Constant.PACKAGE_SPECIFIC_PART, Context.MODE_PRIVATE)
                        .edit().putBoolean(Constant.PARAMETER_RELOAD_LIST, false).apply();
                FitnessController.getInstance().readLastSessions();
            } else {
                //TODO
                handler.sendEmptyMessage(Constant.MESSAGE_SESSIONS_READ);
            }
        }

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
                                        .replace(R.id.fragment_container, new WorkoutFragment())
                                        .commit();
                            }
                });
                builder.create().show();
            }
        });

        return view;
    }
}
