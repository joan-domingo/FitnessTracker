package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;

public interface SessionRepository {
    /*package*/ void saveSession(SessionInsertRequest sessionInsertRequest,
                                 GoogleApiClient googleApiClient);
    /*package*/ void deleteSession(DataDeleteRequest dataDeleteRequest,
                                   GoogleApiClient googleApiClient);
    /*package*/ SessionReadResult getSessions(SessionReadRequest sessionReadRequest,
                                          GoogleApiClient googleApiClient);
    //TODO daily Totals?
}
