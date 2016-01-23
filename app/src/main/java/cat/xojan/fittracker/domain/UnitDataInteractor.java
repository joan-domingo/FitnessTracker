package cat.xojan.fittracker.domain;

import android.content.Context;

public class UnitDataInteractor {

    private PreferencesRepository mUnitDataRepository;

    public UnitDataInteractor(PreferencesRepository firstRunRepository) {
        mUnitDataRepository = firstRunRepository;
    }

    public boolean isFirstRun(Context context) {
        return mUnitDataRepository.getIsFirstRun(context);
    }

    public void setFirstRun(boolean isFirstRun, Context context) {
        mUnitDataRepository.setIsFirstRun(isFirstRun, context);
    }

    public void setMeasureUnit(String measureUnit, Context context) {
        mUnitDataRepository.setMeasureUnit(measureUnit, context);
    }

    public void setDataFormat(String dateFormat, Context context) {
        mUnitDataRepository.setDateFormat(dateFormat, context);
    }

    public String getMeasureUnit(Context context) {
        return mUnitDataRepository.getMeasureUnit(context);
    }
}
