package cat.xojan.fittracker;

import android.content.Context;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.workout.MapController;

public class Utils {
    public static String millisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(millis));
    }

    public static String millisToDay(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(timeInMillis);
    }

    public static String millisToDayComplete(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd");
        return sdf.format(timeInMillis);
    }

    /**
     * @param value metres/second
     * @param context activity context
     * @return string
     */
    public static String getRightSpeed(float value, Context context) {
        String measureUnit = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
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
        String measureUnit = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            return Utils.speedToPaceInMi(value) + context.getString(R.string.pmi);
        } else {
            return Utils.speedToPaceInKm(value) + context.getString(R.string.pkm);
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

    public static String getRightDistance(float value, Context context) {
        double distance = value;
        String measureUnit = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            distance = distance / 1609.344;
            return String.format("%.2f", distance) + " " + context.getString(R.string.mi);
        } else {
            distance = distance / 1000;
            return String.format("%.2f", distance) + " " + context.getString(R.string.km);
        }
    }

    public static String getRightElevation(float elevation, Context context) {
        String measureUnit = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            double feet = elevation * 3.2808399;
            return String.format("%.2f", feet) + " " + context.getString(R.string.feet);
        } else {
            return String.format("%.2f", elevation) + " " + context.getString(R.string.meters);
        }
    }

    public static String getRightDate(long dateInMillis, Context context) {
        String dateFormat = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_DATE_FORMAT, "");

        if (dateFormat.equals(Constant.DATE_FORMAT_DMY)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(dateInMillis);
        } else if (dateFormat.equals(Constant.DATE_FORMAT_MDY)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            return sdf.format(dateInMillis);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(dateInMillis);
        }
    }
}