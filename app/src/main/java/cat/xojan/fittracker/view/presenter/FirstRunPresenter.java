package cat.xojan.fittracker.view.presenter;

import android.content.Context;

import cat.xojan.fittracker.domain.FirstRunInteractor;

public class FirstRunPresenter {

    private FirstRunInteractor mFirstRunInteractor;

    public FirstRunPresenter(FirstRunInteractor firstRunInteractor) {
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
}
