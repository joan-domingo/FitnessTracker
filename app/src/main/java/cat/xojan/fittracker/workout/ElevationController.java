package cat.xojan.fittracker.workout;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import cat.xojan.fittracker.Utils;

public class ElevationController {

    private TextView mView;
    private Context mContext;
    private double mSessionElevationGain;
    private double mSessionElevationLoss;
    private double mSegmentElevationGain;
    private double mSegmentElevationLoss;

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

        mSessionElevationGain = mSegmentElevationGain = 0;
        mSessionElevationLoss = mSegmentElevationLoss = 0;
    }

    public void updateElevationGain(double oldAltitude, double currentAltitude) {
        double altitudeResult = currentAltitude - oldAltitude;
        if (altitudeResult >= 0) {
            mSessionElevationGain = mSessionElevationGain + altitudeResult;
            mSegmentElevationGain = mSegmentElevationGain + altitudeResult;
            mView.setText(Utils.getRightElevation((float) mSegmentElevationGain, mContext));
        } else {
            mSessionElevationLoss = mSessionElevationLoss + (-altitudeResult);
            mSegmentElevationLoss = mSegmentElevationLoss + (-altitudeResult);
        }
    }

    public void lap() {
        mSegmentElevationGain = mSegmentElevationLoss = 0;
        mView.setText(Utils.getRightElevation((float) mSegmentElevationGain, mContext));
    }

    public float getTotalElevationGain() {
        return (float) mSessionElevationGain;
    }

    public float getTotalElevationLoss() {
        return (float) mSessionElevationLoss;
    }
}
