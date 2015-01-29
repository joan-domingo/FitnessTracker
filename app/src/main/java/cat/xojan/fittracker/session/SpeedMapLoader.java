package cat.xojan.fittracker.session;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.R;

public class SpeedMapLoader extends AsyncTask<List<DataPoint>, Void, Boolean> {
    private final Context mContext;
    private final GoogleMap mMap;
    private List<PolylineOptions> polyList;
    private List<MarkerOptions> mMarkerList;
    private List<DataPoint> mSpeedDataPoints;
    private int mSpeedIndex;
    private List<DataPoint> mLocationDataPoints;
    private List<DataPoint> mSegmentDataPoints;
    private LatLngBounds.Builder mBoundsBuilder;

    public SpeedMapLoader(GoogleMap map, Context context) {
        mContext = context;
        mMap = map;
        mMarkerList = new ArrayList<>();
        mBoundsBuilder = new LatLngBounds.Builder();
    }

    @Override
    protected Boolean doInBackground(List<DataPoint>... params) {
        if (params[0] == null || params[1] == null || params[2] == null) {
            return false;
        }

        mLocationDataPoints = params[0];
        mSegmentDataPoints = params[1];
        mSpeedDataPoints = params[2];

        mSpeedIndex = 0;
        float speed = 0;
        float minSpeed = 1000;
        float maxSpeed = 0;
        polyList = new ArrayList<>();

        for (DataPoint dp : mSpeedDataPoints) {
            speed = speed + dp.getValue(Field.FIELD_SPEED).asFloat();
            if (dp.getValue(Field.FIELD_SPEED).asFloat() > maxSpeed) {
                maxSpeed = dp.getValue(Field.FIELD_SPEED).asFloat();
            }
            if (dp.getValue(Field.FIELD_SPEED).asFloat() < minSpeed) {
                minSpeed = dp.getValue(Field.FIELD_SPEED).asFloat();
            }
        }

        //average speed
        float avgSpeed = speed / mSpeedDataPoints.size();
        if (avgSpeed == speed) {
            return false;
        }

        /*LatLng oldPosition = null;
        long startTime = mLocationDataPoints.get(0).getStartTime(TimeUnit.MILLISECONDS) < mSpeedDataPoints.get(0).getStartTime(TimeUnit.MILLISECONDS) ?
                mLocationDataPoints.get(0).getStartTime(TimeUnit.MILLISECONDS) :
                mSpeedDataPoints.get(0).getStartTime(TimeUnit.MILLISECONDS);
        long endTime = 0;

        for (int i = 0; i < mSpeedDataPoints.size(); i++) {
            if (i + 1 < mSpeedDataPoints.size()) {
                endTime = mSpeedDataPoints.get(i + 1).getStartTime(TimeUnit.MILLISECONDS);
            }
            for (DataPoint locationDP : mLocationDataPoints) {

                if (locationDP.getStartTime(TimeUnit.MILLISECONDS) >= startTime &&
                        locationDP.getStartTime(TimeUnit.MILLISECONDS) < endTime) {
                    LatLng currentPosition = new LatLng(locationDP.getValue(Field.FIELD_LATITUDE).asFloat(),
                            locationDP.getValue(Field.FIELD_LONGITUDE).asFloat());
                    if (oldPosition != null) {
                        float currentSpeed = mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat();
                        setPolylineColor(maxSpeed, minSpeed, avgSpeed, oldPosition, currentPosition, currentSpeed);
                    }
                    oldPosition = currentPosition;
                }
            }
            if (i + 1 < mSpeedDataPoints.size())
                startTime = mSpeedDataPoints.get(i + 1).getStartTime(TimeUnit.MILLISECONDS);
        }*/



        for (DataPoint segment : mSegmentDataPoints) {
            LatLng oldPosition = null;
            for (DataPoint dp : mLocationDataPoints) {
                if (dp.getStartTime(TimeUnit.MILLISECONDS) >= segment.getStartTime(TimeUnit.MILLISECONDS) &&
                        dp.getStartTime(TimeUnit.MILLISECONDS) <= segment.getEndTime(TimeUnit.MILLISECONDS)) {
                    LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                            dp.getValue(Field.FIELD_LONGITUDE).asFloat());
                    mBoundsBuilder.include(currentPosition);
                    if (oldPosition != null) {
                        float currentSpeed = getCurrentSpeed(dp.getStartTime(TimeUnit.MILLISECONDS));
                        setPolylineColor(maxSpeed, minSpeed, avgSpeed, oldPosition, currentPosition, currentSpeed);
                    } else {
                        mMarkerList.add(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .position(new LatLng(currentPosition.latitude, currentPosition.longitude)));
                    }
                    oldPosition = currentPosition;
                }
            }
            if (oldPosition != null) {
                mMarkerList.add(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(new LatLng(oldPosition.latitude, oldPosition.longitude)));
            }
        }

        return true;
    }

    private float getCurrentSpeed(long startTime) {
        float speed = 0;
        for (int i = mSpeedIndex; i < mSpeedDataPoints.size(); i++) {
            if (mSpeedDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS) >= startTime) {
                speed = mSpeedDataPoints.get(i).getValue(Field.FIELD_SPEED).asFloat();
                mSpeedIndex = i;
                break;
            }
        }
        return speed;
    }

    private void setPolylineColor(float maxSpeed, float minSpeed, float avgSpeed, LatLng oldPosition, LatLng currentPosition, float currentSpeed) {
        polyList.add(createThickPolyline(oldPosition, currentPosition, Color.BLACK));
        if (currentSpeed >= minSpeed && currentSpeed < ((avgSpeed + minSpeed) / 4)) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_slowest)));
        } else if (currentSpeed >= ((avgSpeed + minSpeed) / 4) && currentSpeed < ((avgSpeed + minSpeed) / 2)) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_super_slow)));
        } else if (currentSpeed >= ((avgSpeed + minSpeed) / 2) && currentSpeed < (((avgSpeed + minSpeed) * 3) / 4)) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_slow)));
        } else if (currentSpeed >= (((avgSpeed + minSpeed) * 3) / 4) && currentSpeed < avgSpeed) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_quite_slow)));
        } else if (currentSpeed >= avgSpeed && currentSpeed < ((maxSpeed + avgSpeed) / 4)) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_quite_fast)));
        } else if (currentSpeed >= ((maxSpeed + avgSpeed) / 4) && currentSpeed < ((avgSpeed + maxSpeed) / 2)) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_fast)));
        } else if (currentSpeed >= ((avgSpeed + maxSpeed) / 2) && currentSpeed < (((avgSpeed + maxSpeed) * 3) / 4)) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_super_fast)));
        } else if (currentSpeed >= (((avgSpeed + maxSpeed) * 3) / 4) && currentSpeed <= maxSpeed) {
            polyList.add(createPolyline(oldPosition, currentPosition, mContext.getResources().getColor(R.color.poly_fastest)));
        }
    }

    private PolylineOptions createPolyline(LatLng oldPosition, LatLng currentPosition, int color) {
        return new PolylineOptions()
                .geodesic(true)
                .add(oldPosition)
                .add(currentPosition)
                .width(8)
                .color(color);
    }

    private PolylineOptions createThickPolyline(LatLng oldPosition, LatLng currentPosition, int color) {
        return new PolylineOptions()
                .geodesic(true)
                .add(oldPosition)
                .add(currentPosition)
                .width(12)
                .color(color);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            mMap.clear();
            for (PolylineOptions pl : polyList) {
                mMap.addPolyline(pl);
            }
            for (MarkerOptions mo : mMarkerList) {
                mMap.addMarker(mo);
            }
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 5));
                }
            });
        }
    }
}
