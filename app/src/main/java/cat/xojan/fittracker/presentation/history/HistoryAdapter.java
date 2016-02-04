package cat.xojan.fittracker.presentation.history;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.fitness.data.Session;

import java.util.List;

import cat.xojan.fittracker.R;

/**
 * Created by Joan on 04/02/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<Session> mSessions;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitle;
        public TextView mActivity;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.text);
            mActivity = (TextView) v.findViewById(R.id.activity);

        }
    }

    public HistoryAdapter(List<Session> sessions) {
        mSessions = sessions;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTitle.setText(mSessions.get(position).getName());

        String activity = mSessions.get(position).getActivity();
        if (activity != null) {
            holder.mActivity.setText(activity.toUpperCase().substring(0, 1));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSessions.size();
    }
}
