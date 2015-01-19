package cat.xojan.fittracker.workout;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import cat.xojan.fittracker.util.Utils;

public class SpeedController {

    private static SpeedController instance = null;
    private TextView mSpeedView;
    private TextView mPaceView;
    private Context mContext;
    private double mMaxSpeed;
    private double mMinSpeed;

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

        mMaxSpeed = 0;
        mMinSpeed = 100000;
    }

    public void updateSpeed() {
        long timeInMillis = TimeController.getInstance().getSegmentTime();
        long timeInSeconds = timeInMillis / 1000;

        double distanceInMeters = DistanceController.getInstance().getSegmentDistance(); //return meters

        if (timeInSeconds > 0 && distanceInMeters > 0) {
            double speed = distanceInMeters / timeInSeconds;

            mMaxSpeed = speed > mMaxSpeed ? speed : mMaxSpeed;
            mMinSpeed = speed < mMinSpeed ? speed : mMinSpeed;

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
}
