package cat.xojan.fittracker;

/**
 * Created by Joan on 13/12/2014.
 */
public class Utils {
    public static String millisToTime(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }
}
