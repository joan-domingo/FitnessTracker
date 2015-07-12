package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataRequest;

public class SessionDataInteractor {

    SessionDataRepository mSessionDataRepository;

    public SessionDataInteractor (SessionDataRepository sessionDataRepository) {
        mSessionDataRepository = sessionDataRepository;
    }

    public void saveSessionData(GoogleApiClient googleApiClient, PutDataRequest putDataRequest) {
        mSessionDataRepository.saveSessionData(googleApiClient, putDataRequest);
    }
}
