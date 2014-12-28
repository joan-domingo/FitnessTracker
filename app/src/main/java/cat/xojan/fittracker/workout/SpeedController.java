package cat.xojan.fittracker.workout;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.sql.Time;
import java.util.Calendar;

import cat.xojan.fittracker.Utils;

public class SpeedController {

    private static SpeedController instance = null;
    private TextView mSpeedView;
    private TextView mPaceView;
    private Context mContext;
    private long mOldTime;

    public static SpeedController getInstance() {
        if(instance == null) {
            instance = new SpeedController();
        }
        return instance;
    }

    public void init(TextView paceView, TextView speedView, FragmentActivity activity) {
        mPaceView = paceView;
        mSpeedView = speedView;
        mContext = activity;

        mPaceView.setText("00:00:00");
        mSpeedView.setText(Utils.getRightSpeed(0f, mContext));
    }

    public void setStartTime() {
        mOldTime = Calendar.getInstance().getTimeInMillis();
    }

    public void updateSpeed(LatLng oldPosition, LatLng currentPosition) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long timeInMillis = currentTime - mOldTime;
        long timeInSeconds = timeInMillis / 1000;

        double distanceInMeters = SphericalUtil.computeDistanceBetween(oldPosition, currentPosition); //return meters

        if (timeInSeconds > 0 && distanceInMeters > 0) {
            double speed = distanceInMeters / timeInSeconds;

            mSpeedView.setText(Utils.getRightSpeed((float) speed, mContext));
            mPaceView.setText(Utils.getRightPace((float) speed, mContext));
        }

        mOldTime = currentTime;
    }
}
