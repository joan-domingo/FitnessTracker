package cat.xojan.fittracker.main.controllers;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import cat.xojan.fittracker.Constant;

public class DistanceController {

    private TextView mDistanceView;
    private Context mContext;
    private float mSegmentDistance;
    private float mSessionDistance;
    private int mUnitCounter = 1;
    private int mSegmentUnitCounter = 1;
    private float mAuxDistance = 0;

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
        mUnitCounter = 1;
        mAuxDistance = 0;
        
        updateDistanceView();

    }

    private void updateDistanceView() {
        String measureUnit = mContext.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        float distance = mSegmentDistance + mAuxDistance;

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            double miles = distance / 1609.344;
            //check counter
            if (miles >= mUnitCounter) {
                MapController.getInstance().addKmMarker(mUnitCounter + " " + Constant.DISTANCE_MEASURE_MILE);
                mUnitCounter++;
            }
            miles = mSegmentDistance / 1609.344;
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
            float kms = distance / 1000;
            //check counter
            if (kms >= mUnitCounter) {
                MapController.getInstance().addKmMarker(mUnitCounter + " " + Constant.DISTANCE_MEASURE_KM);
                mUnitCounter++;
            }
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
        mAuxDistance = 0;
        mSegmentDistance = 0;
        updateDistanceView();
        mUnitCounter = 1;
        mSegmentUnitCounter = 1;
    }

    public void resume() {
        mAuxDistance = mSegmentDistance + mAuxDistance;
        mSegmentDistance = 0;
        mSegmentUnitCounter = 1;
        updateDistanceView();
    }
}
