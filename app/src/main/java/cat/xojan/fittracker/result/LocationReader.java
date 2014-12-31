package cat.xojan.fittracker.result;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationReader extends AsyncTask<LatLng, Void, String> {

    private final FragmentActivity mFragmentActivity;

    public LocationReader(FragmentActivity activity) {
        mFragmentActivity = activity;
    }

    @Override
    protected String doInBackground(LatLng... params) {
        if (params == null || params.length == 0) {
            return null;
        }
        String cityName = null;
        Geocoder gcd = new Geocoder(mFragmentActivity.getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(params[0].latitude,
                    params[0].longitude, 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            cityName = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            onResult(s);
        }
    }

    public void onResult(String cityName) {

    }
}
