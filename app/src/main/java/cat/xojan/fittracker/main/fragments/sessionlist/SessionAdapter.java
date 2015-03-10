package cat.xojan.fittracker.main.fragments.sessionlist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.main.ActivityType;
import cat.xojan.fittracker.util.Utils;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private final Context mContext;
    private List<Session> mSession = null;
    private List<Float> mDistance;
    private final SessionReadResult mSessionReadResult;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.session_name) TextView mName;
        @InjectView(R.id.session_description) TextView mDescription;
        @InjectView(R.id.session_activity) ImageView mActivity;
        @InjectView(R.id.session_summary) TextView mSummary;
        @InjectView(R.id.session_day) TextView mDay;
        @InjectView(R.id.app_icon) ImageView mIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            itemView.setOnClickListener(v -> {
                Session session = mSession.get(getPosition());
                long startTime = session.getStartTime(TimeUnit.MILLISECONDS);
                long endTime = session.getEndTime(TimeUnit.MILLISECONDS);

                HistoryApi.ViewIntentBuilder intentBuilder = new HistoryApi.ViewIntentBuilder(mContext,
                        DataType.TYPE_ACTIVITY_SEGMENT)
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .setPreferredApplication(Constant.PACKAGE_SPECIFIC_PART);

                for (DataSet ds : mSessionReadResult.getDataSet(session, DataType.TYPE_ACTIVITY_SEGMENT)) {
                    if (ds.getDataType().equals(DataType.TYPE_ACTIVITY_SEGMENT))
                        intentBuilder.setDataSource(ds.getDataSource());
                }

                Intent fitIntent = intentBuilder.build();
                fitIntent.putExtra(Constant.EXTRA_SESSION, session.getIdentifier());
                fitIntent.putExtra(Constant.EXTRA_START, startTime);
                fitIntent.putExtra(Constant.EXTRA_END, endTime);
                mContext.startActivity(fitIntent);
            });
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public SessionAdapter(Context context, SessionReadResult sessionReadResult) {
        mContext = context;
        mSessionReadResult = sessionReadResult;
        mSession = sessionReadResult.getSessions();
        if (mSession.size() > 1 && mSession.get(0).getStartTime(TimeUnit.MILLISECONDS) < mSession.get(mSession.size() - 1).getStartTime(TimeUnit.MILLISECONDS))
            Collections.reverse(mSession);
        mDistance = getDistanceList(sessionReadResult);
    }

    private List<Float> getDistanceList(SessionReadResult sessionReadResult) {
        List<Float> sessionDistance = new ArrayList<>(sessionReadResult.getSessions().size());

        for (Session s : sessionReadResult.getSessions()) {
            float distance = 0;
            for (DataSet ds : sessionReadResult.getDataSet(s, DataType.TYPE_DISTANCE_DELTA)) {
                for (DataPoint dp : ds.getDataPoints()) {
                    distance = distance + dp.getValue(Field.FIELD_DISTANCE).asFloat();
                }
            }
            sessionDistance.add(distance);
        }
        return sessionDistance;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mSession.get(position).getName());
        holder.mDescription.setText(mSession.get(position).getDescription());
        holder.mActivity.setImageDrawable(getActivityDrawable(mSession.get(position).getActivity()));
        holder.mSummary.setText(Utils.getTimeDifference(mSession.get(position).getEndTime(TimeUnit.MILLISECONDS),
                    mSession.get(position).getStartTime(TimeUnit.MILLISECONDS)) + " / " + Utils.getRightDistance(mDistance.get(position), mContext));
        holder.mDay.setText(Utils.millisToDayComplete(mSession.get(position).getStartTime(TimeUnit.MILLISECONDS)));

        if (mSession.get(position).getAppPackageName().equals(Constant.PACKAGE_SPECIFIC_PART)) {
            holder.mIcon.setImageResource(R.drawable.ic_launcher);
        } else {
            try {
                PackageManager pkgManager = mContext.getPackageManager();
                Drawable appIcon = pkgManager.getApplicationIcon(mSession.get(position).getAppPackageName());
                holder.mIcon.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                Log.i(Constant.TAG, "Package name not found");
            }
        }
    }

    private Drawable getActivityDrawable(String activity) {
        int drawable = ActivityType.getDrawable(activity);
        return mContext.getResources().getDrawable(drawable);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSession.size();
    }
}
