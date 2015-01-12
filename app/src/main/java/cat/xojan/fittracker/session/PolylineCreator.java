package cat.xojan.fittracker.session;

import android.os.AsyncTask;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class PolylineCreator extends AsyncTask<Void, Void, Void> {
    public PolylineOptions polylineOptions = new PolylineOptions();
    private final List<DataPoint> mLocationDataPoints;
    LatLng oldPosition = null;
    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    float elevationGain = 0;
    float elevationLoss = 0;
    float oldAltitude = 0;

    public PolylineCreator(List<DataPoint> locationDataPoints) {
        mLocationDataPoints = locationDataPoints;
    }


    @Override
    protected Void doInBackground(Void... params) {

        for (DataPoint dp : mLocationDataPoints) {
            //elevation
            float currentAltitude = dp.getValue(Field.FIELD_ALTITUDE).asFloat();

            //position
            LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(), dp.getValue(Field.FIELD_LONGITUDE).asFloat());
            boundsBuilder.include(currentPosition);

            if (oldPosition != null) {
                //create polyline with last location
                polylineOptions.add(currentPosition);

                //estimate altitude gain/loss
                float elevation = currentAltitude - oldAltitude;
                if (elevation >= 0) {
                    elevationGain = elevationGain + elevation;
                } else {
                    elevationLoss = elevationLoss + (-elevation);
                }
            }
            oldPosition = currentPosition;
            oldAltitude = currentAltitude;
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
