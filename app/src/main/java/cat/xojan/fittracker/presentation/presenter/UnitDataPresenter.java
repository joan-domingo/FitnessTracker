package cat.xojan.fittracker.presentation.presenter;

import android.content.Context;

import cat.xojan.fittracker.domain.UnitDataInteractor;

public class UnitDataPresenter {

    private UnitDataInteractor mFirstRunInteractor;

    public UnitDataPresenter(UnitDataInteractor firstRunInteractor) {
        mFirstRunInteractor = firstRunInteractor;
    }

    public boolean showFirstRunSettingsDialogs(Context context) {
        return mFirstRunInteractor.isFirstRun(context);
    }

    public void FirstRunDone(Context context) {
        mFirstRunInteractor.setFirstRun(false, context);
    }

    public void saveMeasureUnit(String measureUnit, Context context) {
        mFirstRunInteractor.setMeasureUnit(measureUnit, context);
    }

    public void saveDateFormat(String dateFormat, Context context) {
        mFirstRunInteractor.setDataFormat(dateFormat, context);
    }

    public String getMeasureUnit(Context context) {
        return mFirstRunInteractor.getMeasureUnit(context);
    }
}
