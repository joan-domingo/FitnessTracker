package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;

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

    public void deleteSessionRequest(DataDeleteRequest dataDeleteRequest,
                                     GoogleApiClient googleApiClient) {
        mSessionRepository.deleteSession(dataDeleteRequest, googleApiClient);
    }
}
