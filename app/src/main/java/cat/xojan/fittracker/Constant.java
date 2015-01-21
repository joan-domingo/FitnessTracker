package cat.xojan.fittracker;

public class Constant {

    public static final String TAG = "cat.xojan.fittracker";

    public static final int MESSAGE_SESSIONS_READ = 0x0001;
    public static final int GOOGLE_API_CLIENT_CONNECTED = 0x0002;
    public static final int MESSAGE_SINGLE_SESSION_READ = 0x1001;
    public static final int MESSAGE_SESSION_DELETED = 0x1002;

    public static final String DISTANCE_MEASURE_KM = "Km";
    public static final String DISTANCE_MEASURE_MILE = "Mi";

    public static final String DATE_FORMAT_DMY = "dmy";
    public static final String DATE_FORMAT_MDY = "mdy";
    public static final String DATE_FORMAT_YMD = "ymd";

    public static final String PACKAGE_SPECIFIC_PART = "cat.xojan.fittracker";
    public static final String SHARED_PREFERENCES = "cat.xojan.fittracker_preferences";

    public static final String PREFERENCE_MEASURE_UNIT = "unit_measure";
    public static final String PREFERENCE_DATE_FORMAT = "date_format";
    public static final String PREFERENCE_FIRST_RUN = "first_run";

    public static final String PARAMETER_SESSION_ID = "sessionId";
    public static final String PARAMETER_SESSION_NAME = "sessionName";
    public static final String PARAMETER_START_TIME = "startTime";
    public static final String PARAMETER_END_TIME = "endTime";
    public static final String PARAMETER_RELOAD_LIST = "reloadSessionList";
    public static final String PARAMETER_DATE = "date";

    public static final String WORKOUT_FRAGMENT_TAG = "workoutFragment";
    public static final String RESULT_FRAGMENT_TAG = "resultFragment";

}