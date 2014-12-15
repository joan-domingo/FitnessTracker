package cat.xojan.fittracker.session;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.workout.WorkoutFragment;

/**
 * Created by Joan on 14/12/2014.
 */
public class SessionListFragment extends Fragment {

    public SessionListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_session_list, container, false);
        Context mContext = getActivity().getBaseContext();

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.sessions_list);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new SessionAdapter(FitnessController.getInstance().getReadSessions(), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView IdentifierView = (TextView) view.findViewById(R.id.session_identifier);
                        String sessionId = IdentifierView.getText().toString();

                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.PARAMETER_SESSION_ID, sessionId);

                        SessionFragment sessionFragment = new SessionFragment();
                        sessionFragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, sessionFragment)
                                .addToBackStack(Constant.TAG_WORKOUT)
                                .commit();
                    }
                })
        );

        ImageButton newWorkout = (ImageButton) view.findViewById(R.id.fab_add);
        newWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new WorkoutFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
