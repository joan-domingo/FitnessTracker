package cat.xojan.fittracker.main.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cat.xojan.fittracker.R;

public class HistoryFragment extends Fragment implements WearableListView.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) view.findViewById(R.id.wearable_list);
        listView.setGreedyTouchMode(true);

        // Assign an adapter to the list
        ListAdapter adapter = new ListAdapter(getActivity());
        listView.setAdapter(adapter);

        // Set a click listener
        listView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
