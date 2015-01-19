package cat.xojan.fittracker.sessionlist;

import android.content.Context;
import android.content.pm.PackageManager;
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
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.util.Utils;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private final Context context;
    private final List<Session> mDataset;
    private final List<Float> mDistance;
    private final List<Integer> mDuration;

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
        public ImageView mIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = (RelativeLayout) itemView;

            mName = (TextView) itemView.findViewById(R.id.session_name);
            mDescription = (TextView) itemView.findViewById(R.id.session_description);
            mActivity = (ImageView) itemView.findViewById(R.id.session_activity);
            mSummary = (TextView) itemView.findViewById(R.id.session_summary);
            mDay = (TextView) itemView.findViewById(R.id.session_day);
            mIcon = (ImageView) itemView.findViewById(R.id.app_icon);

            // hidden
            mIdentifier = (TextView) itemView.findViewById(R.id.session_identifier);
            mStartTime = (TextView) itemView.findViewById(R.id.session_start_time);
            mEndTime = (TextView) itemView.findViewById(R.id.session_end_time);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public SessionAdapter(List<Session> DataSet, List<Float> distances, Context context, List<Integer> durationList) {
        mDataset = DataSet;
        this.context = context;
        mDistance = distances;
        mDuration = durationList;
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

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mDataset.get(position).getName());
        holder.mDescription.setText(mDataset.get(position).getDescription());
        holder.mActivity.setImageDrawable(getActivityDrawable(mDataset.get(position).getActivity()));
        if (mDuration.get(position) == 0) {
            holder.mSummary.setText(Utils.getTimeDifference(mDataset.get(position).getEndTime(TimeUnit.MILLISECONDS),
                    mDataset.get(position).getStartTime(TimeUnit.MILLISECONDS)) + " / " + Utils.getRightDistance(mDistance.get(position), context));
        } else {
            holder.mSummary.setText(Utils.getTimeDifference(mDuration.get(position), 0) +
                    " / " + Utils.getRightDistance(mDistance.get(position), context));
        }
        holder.mIdentifier.setText(mDataset.get(position).getIdentifier());
        holder.mStartTime.setText(String.valueOf(mDataset.get(position).getStartTime(TimeUnit.MILLISECONDS)));
        holder.mEndTime.setText(String.valueOf(mDataset.get(position).getEndTime(TimeUnit.MILLISECONDS)));
        holder.mDay.setText(Utils.millisToDayComplete(mDataset.get(position).getStartTime(TimeUnit.MILLISECONDS)));

        if (mDataset.get(position).getAppPackageName().equals(Constant.PACKAGE_SPECIFIC_PART)) {
            holder.mIcon.setImageResource(R.drawable.ic_launcher);
        } else {
            try {
                PackageManager pkgManager = context.getPackageManager();
                Drawable appIcon = pkgManager.getApplicationIcon(mDataset.get(position).getAppPackageName());
                holder.mIcon.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e) {}
        }
    }

    private Drawable getActivityDrawable(String activity) {
        int drawable = ActivityType.getDrawable(activity);
        return context.getResources().getDrawable(drawable);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
