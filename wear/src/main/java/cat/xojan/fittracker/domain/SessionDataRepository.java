package cat.xojan.fittracker.domain;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataRequest;

public interface SessionDataRepository {
    void saveSessionData(GoogleApiClient googleApiClient, PutDataRequest putDataRequest);
}
