package cat.xojan.fittracker;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
    public static String millisToTime(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
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
}
