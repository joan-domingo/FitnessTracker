package cat.xojan.fittracker.session;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.Utils;
import cat.xojan.fittracker.googlefit.FitnessController;

/**
 * Created by Joan on 14/12/2014.
 */
public class SessionFragment extends Fragment {

    private static Handler handler;
    public static Handler getHandler() {
        return handler;
    }
    private ProgressBar mProgressBar;
    private LinearLayout mSessionView;
    private Session mSession;
    private List<DataSet> mDataSets;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_session, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_loading_spinner);
        mSessionView = (LinearLayout) view.findViewById(R.id.fragment_session_container);
        showProgressBar(true);
        /**
         * session contains:
         * name, description, identifier, package name, activity type, start time, end time
         */

        Bundle bundle = this.getArguments();
        String sessionId = bundle.getString(Constant.PARAMETER_SESSION_ID, "");
        long startTime = bundle.getLong(Constant.PARAMETER_START_TIME, 0);
        long endTime = bundle.getLong(Constant.PARAMETER_END_TIME, 0);

        FitnessController.getInstance().readSession(sessionId, startTime, endTime);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.MESSAGE_SINGLE_SESSION_READ:
                        mSession = FitnessController.getInstance().getSingleSession();
                        mDataSets = FitnessController.getInstance().getSingleSessionDataSets();
                        fillViewContent(view);
                        break;
                }
            }
        };

        return view;
    }

    private void fillViewContent(View view) {
        ((TextView)view.findViewById(R.id.fragment_session_name)).setText(mSession.getName());
        ((TextView)view.findViewById(R.id.fragment_session_description)).setText(mSession.getDescription());
        ((TextView)view.findViewById(R.id.fragment_session_date)).setText(Utils.millisToDate(mSession.getStartTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_start)).setText(Utils.millisToTime(mSession.getStartTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_end)).setText(Utils.millisToTime(mSession.getEndTime(TimeUnit.MILLISECONDS)));
        ((TextView)view.findViewById(R.id.fragment_session_total_time)).setText(Utils.millisToTime(mSession.getEndTime(TimeUnit.MILLISECONDS) - mSession.getStartTime(TimeUnit.MILLISECONDS)));

        for (DataSet ds : mDataSets) {
            for (DataPoint dp : ds.getDataPoints()) {
                for (Field field : dp.getDataType().getFields()) {
                    ((TextView)view.findViewById(R.id.fragment_session_total_speed)).setText(dp.getValue(field) + " m/s");
                }
            }
        }


        showProgressBar(false);
    }

    private void showProgressBar(boolean b) {
        if (b) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSessionView.setVisibility(View.GONE);
        }
        else {
            mProgressBar.setVisibility(View.GONE);
            mSessionView.setVisibility(View.VISIBLE);
        }
    }
}
