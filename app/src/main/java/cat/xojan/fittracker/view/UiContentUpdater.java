package cat.xojan.fittracker.view;

import com.google.android.gms.fitness.result.SessionReadResult;

public interface UiContentUpdater {
    public void setSessionData(SessionReadResult sessionReadResult);
}
