package cat.xojan.fittracker.data;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import cat.xojan.fittracker.domain.SessionDataRepository;

public class WearableDataApiRequester implements SessionDataRepository {

    @Override
    public void saveSessionData(GoogleApiClient googleApiClient, PutDataRequest dataRequest) {
        //uses the data layer API to store the data for later retrieval by the phone app
        Wearable.DataApi.putDataItem(googleApiClient, dataRequest);
    }
}
