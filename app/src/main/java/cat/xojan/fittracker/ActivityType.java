package cat.xojan.fittracker;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.fitness.FitnessActivities;

/**
 * Created by Joan on 20/12/2014.
 */
public enum ActivityType {
    running(FitnessActivities.RUNNING, R.string.running),
    walking(FitnessActivities.WALKING, R.string.walking),
    biking(FitnessActivities.BIKING, R.string.biking),
    swimming(FitnessActivities.SWIMMING, R.string.swimming);

    String fitnessActivity;
    int activityString;

    ActivityType(String fitnessActivity, int activityString) {
        this.fitnessActivity = fitnessActivity;
        this.activityString = activityString;
    }

    public static String[] getStringArray(Context context) {
        ActivityType[] tmp = ActivityType.values();
        String[] result = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            result[i] = context.getResources().getString(tmp[i].activityString);
        }
        return result;
    }

    public String getActivity() {
        return fitnessActivity;
    }
}
