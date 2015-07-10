package cat.xojan.fittracker.data;

import android.content.Context;

import cat.xojan.fittracker.domain.FirstRunRepository;

public class SharedPreferencesStorage implements FirstRunRepository {

    private static final String SHARED_PREFERENCES = "cat.xojan.fittracker_preferences";
    private static final String PREFERENCE_FIRST_RUN = "first_run";

    private static final String DISTANCE_MEASURE_KM = "Km";
    private static final String DISTANCE_MEASURE_MILE = "Mi";

    private static final String DATE_FORMAT_DMY = "dmy";
    private static final String DATE_FORMAT_MDY = "mdy";
    private static final String DATE_FORMAT_YMD = "ymd";

    private static final String PREFERENCE_MEASURE_UNIT = "unit_measure";
    private static final String PREFERENCE_DATE_FORMAT = "date_format";

    @Override
    public boolean getIsFirstRun(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PREFERENCE_FIRST_RUN, true);
    }

    @Override
    public void setIsFirstRun(boolean isFirstRun, Context context) {
        context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putBoolean(PREFERENCE_FIRST_RUN, isFirstRun).commit();
    }

    @Override
    public String getMeasureUnit(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCE_MEASURE_UNIT, "");
    }

    @Override
    public void setMeasureUnit(String measureUnit, Context context) {
        context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putString(PREFERENCE_MEASURE_UNIT, measureUnit).commit();
    }

    @Override
    public String getDateFormat(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCE_DATE_FORMAT, "");
    }

    @Override
    public void setDateFormat(String dateFormat, Context context) {
        context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putString(PREFERENCE_DATE_FORMAT, dateFormat).commit();
    }
}
