package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.io.IOException;

public interface SessionRepository {
    /*package*/ void saveSession(SessionInsertRequest sessionInsertRequest,
                                 GoogleApiClient googleApiClient);
    /*package*/ void deleteSession(DataDeleteRequest dataDeleteRequest,
                                   GoogleApiClient googleApiClient);
    /*package*/ SessionReadResult getSessions(SessionReadRequest sessionReadRequest,
                                          GoogleApiClient googleApiClient);
    //TODO daily Totals?
}
