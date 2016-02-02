package cat.xojan.fittracker.presentation.startup;

import com.google.android.gms.fitness.data.Session;

import java.util.List;

/**
 * Created by Joan on 02/02/2016.
 */
public interface FitnessDataListener {
    void onSuccessfullyUpdated(List<Session> sessions);
    void onError(Throwable e);
}
