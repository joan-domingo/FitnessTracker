package cat.xojan.fittracker.presentation.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.injection.component.HomeComponent;
import cat.xojan.fittracker.presentation.BaseFragment;
import cat.xojan.fittracker.presentation.activity.SessionActivity;
import cat.xojan.fittracker.presentation.sessiondetails.SessionDetailsActivity;

/**
 * Created by Joan on 03/02/2016.
 */
public class HistoryFragment extends BaseFragment implements
        HistoryAdapter.RecyclerViewClickListener {

    @Inject
    UserData mUserData;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HistoryAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(HomeComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new HistoryAdapter(null/*mUserData.getFitnessSessions()*/, this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(int position, View v) {
        TextView title = (TextView) v.findViewById(R.id.text);
        TextView sessionActivity = (TextView) v.findViewById(R.id.activity);

        Intent intent = new Intent(getActivity(), SessionDetailsActivity.class);
        intent.putExtra(SessionDetailsActivity.EXTRA_TITLE, title.getText());
        intent.putExtra(SessionDetailsActivity.EXTRA_ACTIVITY, sessionActivity.getText());
        Pair<View, String> p1 = Pair.create((View)title, "title");
        Pair<View, String> p2 = Pair.create((View)sessionActivity, "activity");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), p1, p2);
        startActivity(intent, options.toBundle());
    }
}
