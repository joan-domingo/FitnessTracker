package cat.xojan.fittracker.domain;

import android.content.Context;

import java.util.Date;

public interface PreferencesRepository {
    boolean getIsFirstRun(Context context);
    void setIsFirstRun(boolean isFirstRun, Context context);
    String getMeasureUnit(Context context);
    void setMeasureUnit(String measureUnit, Context context);
    String getDateFormat(Context context);
    void setDateFormat(String dateFormat, Context context);

    Date getLastFitnessDataUpdate();
}
