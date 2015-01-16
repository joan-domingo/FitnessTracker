package cat.xojan.fittracker.session;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapLoader extends AsyncTask<List<DataPoint>, Void, PolylineOptions>{

    private final GoogleMap mMap;
    private final LatLngBounds.Builder mBoundsBuilder;

    public MapLoader(GoogleMap map) {
        mMap = map;
        mBoundsBuilder = new LatLngBounds.Builder();
    }

    @Override
    protected PolylineOptions doInBackground(List<DataPoint>... params) {
        if (params[0] == null) {
            return null;
        }

        PolylineOptions trackOptions = new PolylineOptions();

        for (DataPoint dp : params[0]) {
            //position
            LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                    dp.getValue(Field.FIELD_LONGITUDE).asFloat());
            mBoundsBuilder.include(currentPosition);
            trackOptions.add(currentPosition);
        }

        return trackOptions;
    }

    @Override
    protected void onPostExecute(PolylineOptions trackOptions) {
        if (trackOptions != null) {
            mMap.addPolyline(trackOptions
                            .geodesic(true)
                            .width(4)
                            .color(Color.BLACK));
        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 5));
                }
            });
    }
}
