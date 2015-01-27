package cat.xojan.fittracker;

public class Constant {

    public static final String TAG = "cat.xojan.fittracker";

    public static final int MESSAGE_READ_SESSIONS = 0x0001;
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

    public static final String PARAMETER_DATE = "date";

    public static final String WORKOUT_FRAGMENT_TAG = "workoutFragment";
    public static final String RESULT_FRAGMENT_TAG = "resultFragment";

    public static final String AUTH_PENDING = "auth_state_pending";
    public static final String IS_READING = "session_reader_active";
    public static final int REQUEST_OAUTH = 1;

    public static final String EXTRA_SESSION = "extra_session";
    public static final String EXTRA_START = "extra_start_time";
    public static final String EXTRA_END = "extra_end_time";
}