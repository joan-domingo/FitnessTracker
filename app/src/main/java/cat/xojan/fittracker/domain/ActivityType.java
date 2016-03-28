package cat.xojan.fittracker.domain;

import android.content.Context;

import cat.xojan.fittracker.R;

public enum ActivityType {
    running(R.string.running),
    walking(R.string.walking),
    biking(R.string.biking);

    int activityString;

    ActivityType(int activityString) {
        this.activityString = activityString;
    }

    public static String[] getStringArray(Context context) {
        ActivityType[] tmp = values();
        String[] result = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            result[i] = context.getResources().getString(tmp[i].activityString);
        }
        return result;
    }

    public static int getDrawable(String activity) {
        /*if (activity.equals(running.fitnessActivity)) {
            return R.drawable.ic_running30;
        } else if (activity.equals(walking.fitnessActivity)) {
            return R.drawable.ic_walking3;
        } else if (activity.equals(biking.fitnessActivity)) {
            return R.drawable.ic_biking2;
        }*/

        return R.drawable.ic_walking3;
    }


    public static int getRightLanguageString(String activity) {
        /*if (activity.equals(running.fitnessActivity)) {
            return running.activityString;
        } else if (activity.equals(walking.fitnessActivity)) {
            return walking.activityString;
        } else if (activity.equals(biking.fitnessActivity)) {
            return biking.activityString;
        }*/

        return R.string.workout;
    }
}
