package cat.xojan.fittracker.session;

import android.os.AsyncTask;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PolylineCreator extends AsyncTask<Void, Void, Void> {
    private final List<DataPoint> mDistanceDataPoints;
    private final int mNumSegments;
    private final List<DataPoint> mLocationDataPoints;
    LatLng oldPosition = null;
    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    float elevationGain = 0;
    float elevationLoss = 0;
    float oldAltitude = 0;
    public List<PolylineOptions> mPolyList;
    public List<MarkerOptions> mMarkerList;

    public PolylineCreator(List<DataPoint> locationDataPoints, List<DataPoint> distanceDataPoints, int numSegments) {
        mLocationDataPoints = locationDataPoints;
        mDistanceDataPoints = distanceDataPoints;
        mNumSegments = numSegments;
    }


    @Override
    protected Void doInBackground(Void... params) {
        mPolyList = new ArrayList<>();
        mMarkerList = new ArrayList<>();

        for (int i = 0; i < mNumSegments; i++) {
            PolylineOptions polylineOptions = new PolylineOptions();

            long startTime = mDistanceDataPoints.get(i).getStartTime(TimeUnit.MILLISECONDS);
            long endTime = mDistanceDataPoints.get(i).getEndTime(TimeUnit.MILLISECONDS);
            boolean first = true;

            for (DataPoint dp : mLocationDataPoints) {
                long dataPointStart = dp.getStartTime(TimeUnit.MILLISECONDS);
                long dataPointEnd = dp.getEndTime(TimeUnit.MILLISECONDS);
                if (dataPointStart >= startTime && dataPointEnd <= endTime) {

                    if (first) {
                        mMarkerList.add(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .position(new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                                        dp.getValue(Field.FIELD_LONGITUDE).asFloat())));
                        first = false;
                    }

                    //elevation
                    float currentAltitude = dp.getValue(Field.FIELD_ALTITUDE).asFloat();

                    //position
                    LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(), dp.getValue(Field.FIELD_LONGITUDE).asFloat());
                    boundsBuilder.include(currentPosition);

                    //create polyline with last location
                    polylineOptions.add(currentPosition);

                    //estimate altitude gain/loss
                    float elevation = currentAltitude - oldAltitude;
                    if (elevation >= 0) {
                        elevationGain = elevationGain + elevation;
                    } else {
                        elevationLoss = elevationLoss + (-elevation);
                    }

                    oldPosition = currentPosition;
                    oldAltitude = currentAltitude;
                }
                mPolyList.add(polylineOptions);
            }
            mMarkerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .position(oldPosition));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onResult();
    }

    public void onResult() {}
}
