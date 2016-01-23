package cat.xojan.fittracker.data;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import cat.xojan.fittracker.domain.PreferencesRepository;

public class SharedPreferencesStorage implements PreferencesRepository {

    public static final String SHARED_PREFERENCES = "cat.xojan.fittracker_preferences";
    private static final String PREFERENCE_FIRST_RUN = "first_run";
    public static final String PREFERENCE_MEASURE_UNIT = "unit_measure";
    public static final String PREFERENCE_DATE_FORMAT = "date_format";
    private static final String PREFERENCE_LAST_UPDATE = "last_update";
    private final Context mContext;

    public SharedPreferencesStorage(Context context) {
        mContext = context;
    }

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

    @Override
    public Date getLastFitnessDataUpdate() {
        long date = mContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getLong(PREFERENCE_LAST_UPDATE, 1);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.getTime();
    }
}
