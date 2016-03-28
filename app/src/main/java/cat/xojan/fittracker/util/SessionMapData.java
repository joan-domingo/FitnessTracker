package cat.xojan.fittracker.util;

import android.graphics.Color;

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

public class SessionMapData {

    private LatLngBounds.Builder mBoundsBuilder;
    private ArrayList<PolylineOptions> mPolyList;
    private ArrayList<MarkerOptions> mMarkerList;

    public SessionMapData(){
        mBoundsBuilder = new LatLngBounds.Builder();
        mPolyList = new ArrayList<>();
        mMarkerList = new ArrayList<>();
    }

    public LatLngBounds.Builder getBoundsBuilder() {
        return mBoundsBuilder;
    }

    public ArrayList<PolylineOptions> getPolyList() {
        return mPolyList;
    }

    public ArrayList<MarkerOptions> getMarkerList() {
        return mMarkerList;
    }

    /*public void readMapData(List<DataPoint> mSegmentDataPoints, List<DataPoint> mLocationDataPoints) {
        mBoundsBuilder = new LatLngBounds.Builder();
        mPolyList = new ArrayList<>();
        mMarkerList = new ArrayList<>();

        for (DataPoint segment : mSegmentDataPoints) {
            PolylineOptions trackOptions = new PolylineOptions();
            boolean first = true;
            LatLng lastPosition = null;

            for (DataPoint dp : mLocationDataPoints) {
                if (dp.getStartTime(TimeUnit.MILLISECONDS) >= segment.getStartTime(TimeUnit.MILLISECONDS) &&
                        dp.getStartTime(TimeUnit.MILLISECONDS) <= segment.getEndTime(TimeUnit.MILLISECONDS)) {
                    LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                            dp.getValue(Field.FIELD_LONGITUDE).asFloat());
                    mBoundsBuilder.include(currentPosition);
                    trackOptions.add(currentPosition);
                    if (first) {
                        mMarkerList.add(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .position(new LatLng(currentPosition.latitude, currentPosition.longitude)));
                        first = false;
                    }
                    lastPosition = currentPosition;
                }
            }
            if (lastPosition != null) {
                mMarkerList.add(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(new LatLng(lastPosition.latitude, lastPosition.longitude)));
            }
            mPolyList.add(trackOptions);
        }
    }*/

    public void setDataIntoMap(GoogleMap map, SessionMapData mapData) {

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapData.getBoundsBuilder().build(), 5));
        for (PolylineOptions pl : mapData.getPolyList()) {
            map.addPolyline(pl
                    .geodesic(true)
                    .width(6)
                    .color(Color.BLACK));
        }
        for (MarkerOptions mo : mapData.getMarkerList()) {
            map.addMarker(mo);
        }
    }
}
