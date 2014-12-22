package cat.xojan.fittracker;

import android.content.Context;

import com.google.android.gms.fitness.FitnessActivities;

public enum ActivityType {
    running(FitnessActivities.RUNNING, R.string.running),
    walking(FitnessActivities.WALKING, R.string.walking),
    biking(FitnessActivities.BIKING, R.string.biking);
    //swimming(FitnessActivities.SWIMMING, R.string.swimming);

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

    public static int getDrawable(String activity) {
        if (activity.equals(running.fitnessActivity)) {
            return R.drawable.ic_running30;
        } else if (activity.equals(walking.fitnessActivity)) {
            return R.drawable.ic_walking3;
        } else if (activity.equals(biking.fitnessActivity)) {
            return R.drawable.ic_biking2;
        }

        return R.drawable.ic_walking3;
    }
}
