package cat.xojan.fittracker.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import cat.xojan.fittracker.data.entity.DistanceUnit;
import cat.xojan.fittracker.domain.repository.PreferencesRepository;

public class SharedPreferencesStorage implements PreferencesRepository {

    public static final String SHARED_PREFERENCES = "cat.xojan.fittracker_preferences";
    public static final String PREFERENCE_DISTANCE_UNIT = "unit_measure";
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
}
