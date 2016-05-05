package cat.xojan.fittracker.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.domain.repository.PreferencesRepository;

public class SharedPreferencesStorage implements PreferencesRepository {

    public static final String SHARED_PREFERENCES = "cat.xojan.fittracker_preferences";
    private static final String PREFERENCE_FIRST_RUN = "first_run";
    public static final String PREFERENCE_DISTANCE_UNIT = "unit_measure";
    public static final String PREFERENCE_DATE_FORMAT = "date_format";
    private static final String PREFERENCE_LAST_UPDATE = "last_update";
    private final Context mContext;

    public SharedPreferencesStorage(Context context) {
        mContext = context;
    }

    @Override
    public DistanceUnit getMeasureUnit() {
        return DistanceUnit.from(getSharedPreferences().getString(PREFERENCE_DISTANCE_UNIT, ""));
    }

    @Override
    public void setMeasureUnit(DistanceUnit distanceUnit) {
        getSharedPreferences().edit()
                .putString(PREFERENCE_DISTANCE_UNIT, distanceUnit.name())
                .commit();
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    /*@Override
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
        long date = 1;/* mContext.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getLong(PREFERENCE_LAST_UPDATE, 1);
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(date);
    return cal.getTime();
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
    }*/
}
