package cat.xojan.fittracker.presentation.startup;

import java.util.List;

import cat.xojan.fittracker.domain.Session;

/**
 * Created by Joan on 02/02/2016.
 */
public interface FitnessDataListener {
    void onSuccessfullyUpdated(List<Session> sessions);
    void onError(Throwable e);
}
