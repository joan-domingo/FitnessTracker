package cat.xojan.fittracker.domain;

import java.util.Date;

/**
 * Created by Joan on 23/01/2016.
 */
public class PreferencesInteractor {

    private PreferencesRepository mPreferencesRepository;


    public PreferencesInteractor(PreferencesRepository preferencesRepository) {
        mPreferencesRepository = preferencesRepository;
    }

    public Date getLastUpdate() {
        return mPreferencesRepository.getLastFitnessDataUpdate();
    }
}
