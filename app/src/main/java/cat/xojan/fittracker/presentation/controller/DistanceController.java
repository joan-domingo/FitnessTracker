package cat.xojan.fittracker.presentation.controller;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

public class DistanceController {

    public static final String DISTANCE_MEASURE_KM = "Km";
    public static final String DISTANCE_MEASURE_MILE = "Mi";

    private final Context mContext;
    private final GoogleMap mMap;
    private final LocationManager mLocationManger;

    private float mSegmentDistance;
    private float mSessionDistance;
    private int mUnitCounter = 1;
    private int mSegmentUnitCounter = 1;
    private float mAuxDistance = 0;
    private TextView mDistanceView;

    public DistanceController(Context context, GoogleMap map, LocationManager locationManager) {
        mContext = context;
        mMap = map;
        mLocationManger = locationManager;

        mSegmentDistance = mSessionDistance = 0;
        mUnitCounter = 1;
        mAuxDistance = 0;
    }

    public void init(TextView distanceView) {
        mDistanceView = distanceView;
        mSegmentDistance = mSessionDistance = 0;
        updateDistanceView();
    }

    private void updateDistanceView() {
        String measureUnit = "";// mUnitDataPresenter.getMeasureUnit(mContext);

        float distance = mSegmentDistance + mAuxDistance;

        if (measureUnit.equals(DISTANCE_MEASURE_MILE)) {
            double miles = distance / 1609.344;
            //check counter
            if (miles >= mUnitCounter) {
                addKmMarker(mUnitCounter + " " + DISTANCE_MEASURE_MILE);
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
            String milesString = String.format("%.2f", distance / 1609.344);
            mDistanceView.setText(milesString + " " + DISTANCE_MEASURE_MILE);
        } else {
            float kms = distance / 1000;
            //check counter
            if (kms >= mUnitCounter) {
                addKmMarker(mUnitCounter + " " + DISTANCE_MEASURE_KM);
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
            mDistanceView.setText(kmString + " " + DISTANCE_MEASURE_KM);
        }
    }

    private void addKmMarker(String s) {
        Location location = mLocationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(s));
    }

    public void updateDistance(LatLng oldPosition, LatLng currentPosition) {
        //update distance
        mSessionDistance = mSessionDistance+ (float) SphericalUtil
                .computeDistanceBetween(oldPosition, currentPosition); //return meters
        mSegmentDistance = mSegmentDistance + (float) SphericalUtil
                .computeDistanceBetween(oldPosition, currentPosition); //return meters

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
