package cat.xojan.fittracker.util;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cat.xojan.fittracker.BuildConfig;
import cat.xojan.fittracker.data.entity.DistanceUnit;

public class Utils {

    private static final String MILES = "mi";
    private static final String KILOMETERS = "km";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LOCATIONS = "locations";

    public static String millisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(millis));
    }

    public static String millisToDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(new Date(millis));
    }

    public static String formatDistance(double distance, DistanceUnit distanceUnit) {
        switch (distanceUnit) {
            case MILE:
                distance = distance / 1609.344;
                return String.format("%.2f", distance) + " " + MILES;
            default:
                distance = distance / 1000;
                return String.format("%.2f", distance) + " " + KILOMETERS;
        }
    }

    public static int dpToPixel(int i, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (120 * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Nullable
    public static String locationsToJson(List<Location> locationList) {
        JSONObject locationsJson = new JSONObject();
        try {
            JSONArray locationJsonArray = new JSONArray();

            for (Location location : locationList) {
                JSONObject locationDetailsJson = new JSONObject();
                locationDetailsJson.put(KEY_LONGITUDE, location.getLongitude());
                locationDetailsJson.put(KEY_LATITUDE, location.getLatitude());
                locationJsonArray.put(locationDetailsJson);
            }
            locationsJson.put(KEY_LOCATIONS, locationJsonArray);
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            } else {
                Crashlytics.logException(e);
            }
            return null;
        }
        return locationsJson.toString();
    }

    public static List<Location> jsonToLocations(String locationsJson) {
        List<Location> locationList = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(locationsJson);
            JSONArray locationsJsonArray = json.getJSONArray(KEY_LOCATIONS);
            for(int i = 0; i < locationsJsonArray.length(); i++) {
                JSONObject obj = locationsJsonArray.getJSONObject(i);
                Location location = new Location("GPS");
                location.setLatitude(obj.getDouble(KEY_LATITUDE));
                location.setLongitude(obj.getDouble(KEY_LONGITUDE));
                locationList.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationList;
    }
}