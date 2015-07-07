package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import javax.inject.Inject;

public class SessionDataInteractor {

    private SessionRepository mSessionRepository;

    public SessionDataInteractor(SessionRepository sessionRepository) {
        mSessionRepository = sessionRepository;
    }

    public SessionReadResult getSessionsData(SessionReadRequest sessionReadRequest,
                                             GoogleApiClient googleApiClient) {
        return mSessionRepository.getSessions(sessionReadRequest, googleApiClient);
    }

    public void insertSessionInsertRequest(SessionInsertRequest sessionInsertRequest,
                                           GoogleApiClient googleApiClient) {
        mSessionRepository.saveSession(sessionInsertRequest, googleApiClient);
    }
}
