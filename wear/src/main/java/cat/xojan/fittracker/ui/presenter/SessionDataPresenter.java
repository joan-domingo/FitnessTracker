package cat.xojan.fittracker.ui.presenter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataRequest;

import cat.xojan.fittracker.domain.SessionDataInteractor;

public class SessionDataPresenter {

    private final SessionDataInteractor mSessionDataInteractor;

    public SessionDataPresenter(SessionDataInteractor sessionDataInteractor) {
        mSessionDataInteractor = sessionDataInteractor;
    }

    public void saveSessionData(GoogleApiClient googleApiClient, PutDataRequest putDataRequest) {
        mSessionDataInteractor.saveSessionData(googleApiClient, putDataRequest);
    }
}
