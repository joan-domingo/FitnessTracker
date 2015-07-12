package cat.xojan.fittracker.ui.controller;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class DistanceController {

    public static final String DISTANCE_MEASURE_KM = "Km";
    public static final String DISTANCE_MEASURE_MILE = "Mi";

    public static final String SHARED_PREFERENCES = "cat.xojan.fittracker_preferences";
    public static final String PREFERENCE_MEASURE_UNIT = "unit_measure";

    private static DistanceController instance;
    private Context mContext;

    public static DistanceController getInstance() {
        if (instance == null) {
            instance = new DistanceController();
        }
        return instance;
    }

    private float mSegmentDistance;
    private float mSessionDistance;
    private int mSegmentUnitCounter = 1;
    private float mAuxDistance = 0;
    private TextView mDistanceView;

    public void init(Context context, TextView distanceView) {
        mContext = context;
        mDistanceView = distanceView;
    }

    public void lap() {
        mAuxDistance = 0;
        mSegmentDistance = 0;
        updateDistanceView();
        mSegmentUnitCounter = 1;
    }

    public void resume() {
        mAuxDistance = mSegmentDistance + mAuxDistance;
        mSegmentDistance = 0;
        mSegmentUnitCounter = 1;
        updateDistanceView();
    }

    private void updateDistanceView() {
        String measureUnit = mContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCE_MEASURE_UNIT, "");

        float distance = mSegmentDistance + mAuxDistance;

        if (measureUnit.equals(DISTANCE_MEASURE_MILE)) {
            double miles = mSegmentDistance / 1609.344;
            if (miles >= mSegmentUnitCounter) {
                float mod = (float) ((miles % mSegmentUnitCounter) * 1000);
                mSessionDistance = mSessionDistance - mod;
                mSegmentDistance = mSegmentDistance - mod;
                distance = distance - mod;
                mSegmentUnitCounter++;
            }
            String milesString = String.format("%.2f", distance /  1609.344);
            mDistanceView.setText(milesString + " " + DISTANCE_MEASURE_MILE);
        } else {
            float segmentKm = mSegmentDistance / 1000;
            if (segmentKm >= mSegmentUnitCounter) {
                float mod = ((segmentKm % mSegmentUnitCounter) * 1000);
                mSessionDistance = mSessionDistance - mod;
                mSegmentDistance = mSegmentDistance - mod;
                distance = distance - mod;
                mSegmentUnitCounter++;
            }
            String kmString = String.format("%.2f", distance / 1000);
            mDistanceView.setText(kmString + " " + DISTANCE_MEASURE_KM);
        }
    }

    public float getTotalDistance() {
        return mSessionDistance;
    }

    public void updateDistance(LatLng mOldPosition, LatLng currentPosition) {
        //update distance
        mSessionDistance = mSessionDistance + (float) SphericalUtil
                .computeDistanceBetween(mOldPosition, currentPosition); //return meters
        mSegmentDistance = mSegmentDistance + (float) SphericalUtil
                .computeDistanceBetween(mOldPosition, currentPosition); //return meters

        //update view
        updateDistanceView();
    }
}
