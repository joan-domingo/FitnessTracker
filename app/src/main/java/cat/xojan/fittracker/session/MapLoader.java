package cat.xojan.fittracker.session;

import android.content.Context;
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

import cat.xojan.fittracker.R;

public class MapLoader extends AsyncTask<List<DataPoint>, Void, PolylineOptions>{

    private final GoogleMap mMap;
    private final LatLngBounds.Builder mBoundsBuilder;
    private final Context mContext;
    private List<DataPoint> mSpeedDataPoints;
    private List<DataPoint> mLocationDataPoints;

    public MapLoader(GoogleMap map, Context context) {
        mMap = map;
        mBoundsBuilder = new LatLngBounds.Builder();
        mContext = context;
    }

    @Override
    protected PolylineOptions doInBackground(List<DataPoint>... params) {
        if (params[0] == null) {
            return null;
        }
        mLocationDataPoints = params[0];
        PolylineOptions trackOptions = new PolylineOptions();

        if (params[1] != null && params[1].size() > 0) {
            mSpeedDataPoints = params[1];

        }

        for (DataPoint dp : mLocationDataPoints) {

            LatLng currentPosition = new LatLng(dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                    dp.getValue(Field.FIELD_LONGITUDE).asFloat());
            mBoundsBuilder.include(currentPosition);
            trackOptions.add(currentPosition);
            trackOptions.color(mContext.getResources().getColor(R.color.grey));
        }

        return trackOptions;
    }

    @Override
    protected void onPostExecute(PolylineOptions trackOptions) {
        if (trackOptions != null) {
            mMap.addPolyline(trackOptions
                    .geodesic(true)
                    .width(6)
                    .color(Color.BLACK));
        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 5));

                    if (mSpeedDataPoints != null) {
                        new SpeedMapLoader(mMap, mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                mLocationDataPoints,
                                mSpeedDataPoints);
                    }
                }
            });
    }


}
