package cat.xojan.fittracker.workout;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Calendar;

import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.util.Utils;

public class SpeedController {

    private static SpeedController instance = null;
    private TextView mSpeedView;
    private TextView mPaceView;
    private Context mContext;
    private long lastSpeedUpdated;
    private LatLng mOldPosition;
    /*private double mMaxSpeed;
    private double mMinSpeed;*/

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

        mPaceView.setText(Utils.getRightPace(0f, mContext));
        mSpeedView.setText(Utils.getRightSpeed(0f, mContext));

        /*mMaxSpeed = 0;
        mMinSpeed = 100000;*/
        lastSpeedUpdated = 0;
    }

    public void updateSpeed() {
        long timeInMillis = TimeController.getInstance().getSegmentTime();
        long timeInSeconds = timeInMillis / 1000;

        double distanceInMeters = DistanceController.getInstance().getSegmentDistance(); //return meters

        if (timeInSeconds > 0 && distanceInMeters > 0) {
            double speed = distanceInMeters / timeInSeconds;

            /*mMaxSpeed = speed > mMaxSpeed ? speed : mMaxSpeed;
            mMinSpeed = speed < mMinSpeed ? speed : mMinSpeed;*/

            mSpeedView.setText(Utils.getRightSpeed((float) speed, mContext));
            mPaceView.setText(Utils.getRightPace((float) speed, mContext));
        } else {
            mSpeedView.setText(Utils.getRightSpeed(0f, mContext));
            mPaceView.setText(Utils.getRightPace(0f, mContext));
        }
    }

    public void reset() {
        updateSpeed();
    }

    public void storeSpeed(LatLng oldPosition, LatLng currentPosition) {
        long now = Calendar.getInstance().getTimeInMillis();
        if (lastSpeedUpdated == 0) {
            mOldPosition = oldPosition;
            lastSpeedUpdated = now;
        } else if (now - lastSpeedUpdated >= 10000) {
            double distance = SphericalUtil.computeDistanceBetween(mOldPosition, currentPosition);
            long time = now - lastSpeedUpdated;
            double speed = distance / (time / 1000);
            if (time > 0 && distance > 0)
                FitnessController.getInstance().saveSpeed(lastSpeedUpdated, now, speed);
            else
                FitnessController.getInstance().saveSpeed(lastSpeedUpdated, now, 0f);

            lastSpeedUpdated = now;
            mOldPosition = currentPosition;
        }
    }
}
