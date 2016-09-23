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
import java.util.Locale;

import cat.xojan.fittracker.BuildConfig;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.data.repository.SharedPreferencesStorage;

public class Utils {

    private static final String DATE_FORMAT_DMY = "dmy";
    private static final String DATE_FORMAT_MDY = "mdy";
    private static final String DATE_FORMAT_YMD = "ymd";
    private static final String MILES = "mi";
    private static final String KILOMETERS = "km";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LOCATIONS = "locations";

    public static String millisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(millis));
    }

    public static String millisToDay(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(timeInMillis);
    }

    public static String millisToDayComplete(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd", Locale.getDefault());
        return sdf.format(timeInMillis);
    }

    /**
     * @param value metres/second
     * @param context activity context
     * @return string
     */
    public static String getRightSpeed(float value, Context context) {
       String measureUnit = context.getSharedPreferences(SharedPreferencesStorage.SHARED_PREFERENCES,
               Context.MODE_PRIVATE)
                .getString(SharedPreferencesStorage.PREFERENCE_DISTANCE_UNIT, "");

        if (measureUnit.equals(DistanceUnit.MILE)) {
            return String.format("%.2f", Utils.ms2Mph(value)) + " " + context.getString(R.string.mph);
        } else {
            return String.format("%.2f", Utils.ms2KmH(value)) + " " + context.getString(R.string.kph);
        }
    }

    private static double ms2Mph(float value) {
        return value * 2.23693629;
    }

    private static double ms2KmH(float value) {
        return value * 3.6;
    }

    public static String getTimeDifference(long endTime, long startTime) {
        long result = endTime - startTime;

        long second = (result / 1000) % 60;
        long minute = (result / (1000 * 60)) % 60;
        long hour = (result / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static String secondsToTime(long result) {
        long second = result % 60;
        long minute = (result / (60)) % 60;
        long hour = (result / (60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * speed to pace converter
     * @param value speed (m/s)
     * @param context activity context
     * @return string
     */
    public static String getRightPace(float value, Context context) {
        String measureUnit = context.getSharedPreferences(SharedPreferencesStorage.SHARED_PREFERENCES,
                Context.MODE_PRIVATE)
                .getString(SharedPreferencesStorage.PREFERENCE_DISTANCE_UNIT, "");

        if (measureUnit.equals(DistanceUnit.MILE)) {
            return Utils.speedToPaceInMi(value) + " " + context.getString(R.string.pmi);
        } else {
            return Utils.speedToPaceInKm(value) + " " + context.getString(R.string.pkm);
        }
    }

    private static String speedToPaceInKm(float value) {
        float kmPerSecond = value / 1000;
        float secPerKm = 1 / kmPerSecond;

        if (value == 0)
            return secondsToTime((long) 0.00);
        else
            return secondsToTime((long) secPerKm);
    }

    private static String speedToPaceInMi(float value) {
        double milesPerSecond = value / 1609.344;
        double secondsPerMile = 1 / milesPerSecond;

        if (value == 0)
            return secondsToTime((long) 0.00);
        else
            return secondsToTime((long) secondsPerMile);
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

    public static String getRightDate(long dateInMillis, Context context) {
        String dateFormat = context.getSharedPreferences(SharedPreferencesStorage.SHARED_PREFERENCES,
                Context.MODE_PRIVATE)
                .getString(SharedPreferencesStorage.PREFERENCE_DATE_FORMAT, "");

        if (dateFormat.equals(DATE_FORMAT_DMY)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(dateInMillis);
        } else if (dateFormat.equals(DATE_FORMAT_MDY)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            return sdf.format(dateInMillis);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(dateInMillis);
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