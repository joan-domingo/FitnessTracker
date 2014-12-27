package cat.xojan.fittracker.session;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.fitness.data.Session;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.ActivityType;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.Utils;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private final Context context;
    private final List<Session> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public RelativeLayout mView;
        public TextView mName;
        public TextView mDescription;
        public ImageView mActivity;
        public TextView mSummary;
        public TextView mIdentifier;
        public TextView mStartTime;
        public TextView mEndTime;
        public TextView mDay;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = (RelativeLayout) itemView;

            mName = (TextView) itemView.findViewById(R.id.session_name);
            mDescription = (TextView) itemView.findViewById(R.id.session_description);
            mActivity = (ImageView) itemView.findViewById(R.id.session_activity);
            mSummary = (TextView) itemView.findViewById(R.id.session_summary);
            mDay = (TextView) itemView.findViewById(R.id.session_day);

            // hidden
            mIdentifier = (TextView) itemView.findViewById(R.id.session_identifier);
            mStartTime = (TextView) itemView.findViewById(R.id.session_start_time);
            mEndTime = (TextView) itemView.findViewById(R.id.session_end_time);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public SessionAdapter(List<Session> DataSet, Context context) {
        mDataset = DataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position % 2 != 0)
            holder.mView.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
        else
            holder.mView.setBackgroundColor(context.getResources().getColor(R.color.grey));

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mDataset.get(position).getName());
        holder.mDescription.setText(mDataset.get(position).getDescription());
        holder.mActivity.setImageDrawable(getActivityDrawable(mDataset.get(position).getActivity()));
        holder.mSummary.setText(Utils.millisToTime(mDataset.get(position).getStartTime(TimeUnit.MILLISECONDS)) + " - " +
                Utils.millisToTime(mDataset.get(position).getEndTime(TimeUnit.MILLISECONDS)));
        holder.mIdentifier.setText(mDataset.get(position).getIdentifier());
        holder.mStartTime.setText(String.valueOf(mDataset.get(position).getStartTime(TimeUnit.MILLISECONDS)));
        holder.mEndTime.setText(String.valueOf(mDataset.get(position).getEndTime(TimeUnit.MILLISECONDS)));
        holder.mDay.setText(Utils.millisToDayComplete(mDataset.get(position).getStartTime(TimeUnit.MILLISECONDS)));
    }

    private Drawable getActivityDrawable(String activity) {
        int drawable = ActivityType.getDrawable(activity);
        return context.getDrawable(drawable);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
