package cat.xojan.fittracker.domain;

import cat.xojan.fittracker.R;

public enum ActivityType {
    Running,
    Walking,
    Biking,
    Other;

    private ActivityType icon;

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

    public int getIcon() {
        if (this.equals(Running)) {
            return R.drawable.ic_running30;
        }
        return R.drawable.ic_walking3;
    }
}
