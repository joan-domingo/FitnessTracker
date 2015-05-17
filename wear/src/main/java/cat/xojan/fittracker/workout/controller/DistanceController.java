package cat.xojan.fittracker.workout.controller;

import android.content.Context;
import android.location.Location;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import cat.xojan.fittracker.Constant;

public class DistanceController {

    private static DistanceController instance;
    private Context mContext;
    private Location mFirstLocation;

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
        String measureUnit = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        float distance = mSegmentDistance + mAuxDistance;

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            double miles = mSegmentDistance / 1609.344;
            if (miles >= mSegmentUnitCounter) {
                float mod = (float) ((miles % mSegmentUnitCounter) * 1000);
                mSessionDistance = mSessionDistance - mod;
                mSegmentDistance = mSegmentDistance - mod;
                distance = distance - mod;
                mSegmentUnitCounter++;
            }
            String milesString = String.format("%.2f", distance /  1609.344);
            mDistanceView.setText(milesString + " " + Constant.DISTANCE_MEASURE_MILE);
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
            mDistanceView.setText(kmString + " " + Constant.DISTANCE_MEASURE_KM);
        }
    }

    public float getTotalDistance() {
        return mSessionDistance;
    }

    public void updateDistance(LatLng mOldPosition, LatLng currentPosition) {
        //update distance
        mSessionDistance = mSessionDistance + (float) SphericalUtil.computeDistanceBetween(mOldPosition, currentPosition); //return meters
        mSegmentDistance = mSegmentDistance + (float) SphericalUtil.computeDistanceBetween(mOldPosition, currentPosition); //return meters

        //update view
        updateDistanceView();
    }

    public void setFirstLocation(Location location) {
        mFirstLocation = location;
    }

    public Location getFirstLocation() {
        return mFirstLocation;
    }
}
