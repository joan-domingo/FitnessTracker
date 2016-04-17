package cat.xojan.fittracker.data.entity;

import android.content.Context;

import java.util.List;

import cat.xojan.fittracker.domain.Session;

/**
 * Contains user's app session data.
 */
public class UserData {

    private final Context mContext;
    private List<Session> fitnessSessions;

    public UserData(Context context) {
        mContext = context;
    }

    public void setFitnessSessions(List<Session> fitnessSessions) {
        this.fitnessSessions = fitnessSessions;
    }
}
