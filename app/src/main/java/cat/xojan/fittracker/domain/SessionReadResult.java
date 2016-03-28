package cat.xojan.fittracker.domain;

import java.util.List;

import cat.xojan.fittracker.presentation.controller.DataSet;

/**
 * Created by Joan on 28/03/2016.
 */
public class SessionReadResult {
    private List<Session> sessions;

    public List<Session> getSessions() {
        return sessions;
    }

    public List<DataSet> getDataSet(Session mSession) {
        return null;
    }
}
