package cat.xojan.fittracker;

import android.content.Context;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static String millisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(millis));
    }

    public static String millisToDate(long startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(startTime);
    }

    public static String checkSessionName(String name) {
        if (TextUtils.isEmpty(name)) {
            return Utils.millisToDay(Calendar.getInstance().getTimeInMillis()) + " workout";
        } else
            return name;
    }

    private static String millisToDay(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        return sdf.format(timeInMillis);
    }

    public static String checkSessionDescription(String description) {
        if (TextUtils.isEmpty(description)) {
            return Calendar.getInstance().getTime() + " workout";
        } else
            return description;
    }

    public static String getRightSpeed(float value, Context context) {
        String measureUnit = context.getSharedPreferences(Constant.PACKAGE_SPECIFIC_PART, Context.MODE_PRIVATE)
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

    public static String getRightPace(float value, Context context) {
        String measureUnit = context.getSharedPreferences(Constant.PACKAGE_SPECIFIC_PART, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            return Utils.speedToPaceInMi(value) + " " + context.getString(R.string.pmi);
        } else {
            return Utils.speedToPaceInKm(value) + " " + context.getString(R.string.pkm);
        }
    }

    private static String speedToPaceInKm(float value) {
        float secPerMeter = 1 / value;
        long secPerKm = (long) (secPerMeter * 1000);

        long second = secPerKm % 60;
        long minute = (secPerKm * 60) % 60;
        long hour = (secPerKm * 60 * 60) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private static String speedToPaceInMi(float value) {
        float secPerMeter = 1 / value;
        long secPerKm = (long) (secPerMeter * 1609.344);

        long second = secPerKm % 60;
        long minute = (secPerKm * 60) % 60;
        long hour = (secPerKm * 60 * 60) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static String getRightDistance(float value, Context context, long startTime, long endTime) {
        long totalTime = endTime - startTime;
        double distance = (value / 1000) * totalTime;

        String measureUnit = context.getSharedPreferences(Constant.PACKAGE_SPECIFIC_PART, Context.MODE_PRIVATE)
                .getString(Constant.PREFERENCE_MEASURE_UNIT, "");

        if (measureUnit.equals(Constant.DISTANCE_MEASURE_MILE)) {
            distance = distance / 1609.344;
            return String.format("%.2f", distance) + " " + context.getString(R.string.mi);
        } else {
            distance = distance / 1000;
            return String.format("%.2f", distance) + " " + context.getString(R.string.km);
        }
    }
}
