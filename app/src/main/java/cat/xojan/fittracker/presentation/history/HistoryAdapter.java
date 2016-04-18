package cat.xojan.fittracker.presentation.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.util.Utils;

/**
 * Workout history adapter.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private static RecyclerViewClickListener mClickListener;
    private final List<Workout> mWorkouts;
    private final Context mContext;

    public void destroy() {
        mClickListener = null;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTitle;
        public TextView mActivity;
        public TextView mDistance;
        public TextView mTime;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.text);
            mActivity = (TextView) v.findViewById(R.id.activity);
            mDistance = (TextView) v.findViewById(R.id.distance);
            mTime = (TextView) v.findViewById(R.id.time);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public HistoryAdapter(List<Workout> sessions, RecyclerViewClickListener listener,
                          Context context) {
        mWorkouts = sessions;
        mClickListener = listener;
        mContext = context;
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
        Workout workout = mWorkouts.get(position);

        holder.mTitle.setText(workout.getTitle());
        holder.mDistance.setText(Utils.getRightDistance(workout.getDistance(), mContext));
        holder.mTime.setText(Utils.millisToTime(workout.getWorkoutTime()));
        holder.mActivity.setText(workout.getType().toUpperCase().substring(0, 1));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mWorkouts.size();
    }

    /*package*/ interface RecyclerViewClickListener {
        void onItemClick(int position, View v);
    }
}
