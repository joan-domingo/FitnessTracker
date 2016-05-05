package cat.xojan.fittracker.domain;

import cat.xojan.fittracker.R;

public enum ActivityType {
    Running,
    Walking,
    Biking,
    Other;

    public static int toDrawable(String type) {
        switch (type) {
            case "Running":
                return R.drawable.run_button_view;
            case "Biking":
                return R.drawable.bike_button_view;
            case "Waling":
                return R.drawable.walk_button_view;
            default:
                return R.drawable.run_button_view;
        }
    }
}
