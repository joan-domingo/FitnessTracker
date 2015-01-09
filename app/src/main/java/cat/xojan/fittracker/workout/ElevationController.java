package cat.xojan.fittracker.workout;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import cat.xojan.fittracker.util.Utils;

public class ElevationController {

    private TextView mView;
    private Context mContext;
    private float mSessionElevationGain;
    private float mSessionElevationLoss;
    private float mSegmentElevationGain;
    private float mSegmentElevationLoss;
    private float mOldAltitude;

    public ElevationController() {}

    private static ElevationController instance = null;

    public static ElevationController getInstance() {
        if(instance == null) {
            instance = new ElevationController();
        }
        return instance;
    }

    public void init(TextView elevationGainView, FragmentActivity activity) {
        mView = elevationGainView;
        mContext = activity;

        mView.setText(Utils.getRightElevation(0f, mContext));

        mSessionElevationGain = 0;
        mSegmentElevationGain = 0;
        mSessionElevationLoss = 0;
        mSegmentElevationLoss = 0;
    }

    public void updateElevationGain(Location location) {
        float currentAltitude = (float) location.getAltitude();
        float altitudeResult = currentAltitude - mOldAltitude;

        if (altitudeResult >= 0) {
            mSessionElevationGain = mSessionElevationGain + altitudeResult;
            mSegmentElevationGain = mSegmentElevationGain + altitudeResult;
            mView.setText(Utils.getRightElevation(mSegmentElevationGain, mContext));
        } else {
            mSessionElevationLoss = mSessionElevationLoss + (-altitudeResult);
            mSegmentElevationLoss = mSegmentElevationLoss + (-altitudeResult);
        }
        mOldAltitude = currentAltitude;
    }

    public void lap() {
        mSegmentElevationGain = 0;
        mSegmentElevationLoss = 0;
        mView.setText(Utils.getRightElevation(mSegmentElevationGain, mContext));
    }

    public float getTotalElevationGain() {
        return mSessionElevationGain;
    }

    public float getTotalElevationLoss() {
        return mSessionElevationLoss;
    }

    public void setFirstAltitude(Location currentLocation) {
        mOldAltitude = (float) currentLocation.getAltitude();
    }
}
