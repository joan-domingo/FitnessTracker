package cat.xojan.fittracker.workout;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import cat.xojan.fittracker.Constant;

/**
 * Created by Joan on 14/12/2014.
 */
public class DistanceController {

    private TextView mDistanceView;
    private Context mContext;
    private float mSegmentDistance;
    private float mSessionDistance;

    private static DistanceController instance = null;

    public DistanceController() {}

    public static DistanceController getInstance() {
        if(instance == null) {
            instance = new DistanceController();
        }
        return instance;
    }

    public void init(TextView distanceView, Activity activity) {
        mDistanceView = distanceView;
        mContext = activity;

        mSegmentDistance = mSessionDistance = 0;
        updateDistanceView();
    }

    private void updateDistanceView() {
        String measureUnit = mContext.getSharedPreferences(Constant.PACKAGE_SPECIFIC_PART, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            String miles = String.format("%.2f", mSegmentDistance /  1609.344);
            mDistanceView.setText(miles + " " + Constant.DISTANCE_MEASURE_MILE);
        } else {
            String km = String.format("%.2f", mSegmentDistance / 1000);
            mDistanceView.setText(km + " " + Constant.DISTANCE_MEASURE_KM);
        }
    }

    public void updateDistance(LatLng oldPosition, LatLng currentPosition) {
        //update distance
        mSessionDistance = mSessionDistance + (float) SphericalUtil.computeDistanceBetween(oldPosition, currentPosition); //return meters
        mSegmentDistance = mSegmentDistance + (float) SphericalUtil.computeDistanceBetween(oldPosition, currentPosition); //return meters

        //update view
        updateDistanceView();
    }

    public float getSessionDistance() {
        return mSessionDistance;
    }

    public float getSegmentDistance() {
        return mSegmentDistance;
    }

    public void lap() {
        mSegmentDistance = 0;
    }

    public void resume() {
        mSegmentDistance = 0;
    }
}
