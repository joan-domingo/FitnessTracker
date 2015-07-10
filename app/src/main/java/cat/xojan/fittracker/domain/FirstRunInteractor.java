package cat.xojan.fittracker.domain;

import android.content.Context;

public class FirstRunInteractor {

    private FirstRunRepository mFirstRunRepository;

    public FirstRunInteractor(FirstRunRepository firstRunRepository) {
        mFirstRunRepository = firstRunRepository;
    }

    public boolean isFirstRun(Context context) {
        return mFirstRunRepository.getIsFirstRun(context);
    }

    public void setFirstRun(boolean isFirstRun, Context context) {
        mFirstRunRepository.setIsFirstRun(isFirstRun, context);
    }

    public void setMeasureUnit(String measureUnit, Context context) {
        mFirstRunRepository.setMeasureUnit(measureUnit, context);
    }

    public void setDataFormat(String dateFormat, Context context) {
        mFirstRunRepository.setDateFormat(dateFormat, context);
    }
}
