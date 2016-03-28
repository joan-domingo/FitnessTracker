package cat.xojan.fittracker.presentation.listener;

import cat.xojan.fittracker.domain.SessionReadResult;

public interface UiContentUpdater {
    public void setSessionData(SessionReadResult sessionReadResult);
}
