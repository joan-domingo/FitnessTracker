package cat.xojan.fittracker.domain;

import android.content.Context;

import cat.xojan.fittracker.R;

public enum ActivityType {
    RUNNING(R.string.running),
    WALKING(R.string.walking),
    BIKING(R.string.biking),
    OTHER(R.string.other_activity);

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
        /*if (activity.equals(RUNNING.fitnessActivity)) {
            return R.drawable.ic_running30;
        } else if (activity.equals(WALKING.fitnessActivity)) {
            return R.drawable.ic_walking3;
        } else if (activity.equals(BIKING.fitnessActivity)) {
            return R.drawable.ic_biking2;
        }*/

        return R.drawable.ic_walking3;
    }


    public static int getRightLanguageString(String activity) {
        /*if (activity.equals(RUNNING.fitnessActivity)) {
            return RUNNING.activityString;
        } else if (activity.equals(WALKING.fitnessActivity)) {
            return WALKING.activityString;
        } else if (activity.equals(BIKING.fitnessActivity)) {
            return BIKING.activityString;
        }*/

        return R.string.workout;
    }
}
