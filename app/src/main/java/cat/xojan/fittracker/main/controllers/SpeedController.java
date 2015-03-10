package cat.xojan.fittracker.main.controllers;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Calendar;

import cat.xojan.fittracker.util.Utils;

public class SpeedController {

    private final FitnessController fitController;
    private final DistanceController distanceController;
    private final TimeController timeController;

    private TextView mSpeedView;
    private TextView mPaceView;
    private final Context mContext;
    private long lastSpeedUpdated;
    private LatLng mOldPosition;

    public SpeedController(Context context, FitnessController fitnessController,
                           DistanceController distanceController, TimeController timeController) {
        mContext = context;
        this.fitController = fitnessController;
        this.distanceController = distanceController;
        this.timeController = timeController;
    }

    public void init(TextView paceView, TextView speedView) {
        mPaceView = paceView;
        mSpeedView = speedView;

        mPaceView.setText(Utils.getRightPace(0f, mContext));
        mSpeedView.setText(Utils.getRightSpeed(0f, mContext));

        /*mMaxSpeed = 0;
        mMinSpeed = 100000;*/
        lastSpeedUpdated = 0;
    }

    public void updateSpeed() {
        long timeInMillis = timeController.getSegmentTime();
        long timeInSeconds = timeInMillis / 1000;

        double distanceInMeters = distanceController.getSegmentDistance(); //return meters

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
                fitController.saveSpeed(lastSpeedUpdated, now, speed);
            else
                fitController.saveSpeed(lastSpeedUpdated, now, 0f);

            lastSpeedUpdated = now;
            mOldPosition = currentPosition;
        }
    }
}
