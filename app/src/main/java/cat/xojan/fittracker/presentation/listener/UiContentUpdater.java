package cat.xojan.fittracker.presentation.listener;

import com.google.android.gms.fitness.result.SessionReadResult;

public interface UiContentUpdater {
    public void setSessionData(SessionReadResult sessionReadResult);
}
