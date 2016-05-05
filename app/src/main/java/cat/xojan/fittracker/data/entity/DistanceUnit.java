package cat.xojan.fittracker.data.entity;

/**
 * Distance Unit types.
 */
public enum DistanceUnit {
    KILOMETER, MILE;

    public static DistanceUnit from(String unit) {
        switch (unit) {
            case "MILE":
                return MILE;
            default:
                return KILOMETER;
        }
    }
}
