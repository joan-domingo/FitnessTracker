package cat.xojan.fittracker.util;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * Location utilities.
 */
public class LocationUtils {

    /**
     * Converts {@link Location} to {@link LatLng}.
     */
    public static LatLng locationToLatLng(@NonNull Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
