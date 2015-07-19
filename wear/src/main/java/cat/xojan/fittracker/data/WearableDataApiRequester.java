package cat.xojan.fittracker.data;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import cat.xojan.fittracker.domain.SessionDataRepository;

public class WearableDataApiRequester implements SessionDataRepository {

    private static final String TAG = WearableDataApiRequester.class.getSimpleName();

    @Override
    public void saveSessionData(GoogleApiClient googleApiClient, PutDataRequest dataRequest) {
        //uses the data layer API to store the data for later retrieval by the phone app
        Wearable.DataApi.putDataItem(googleApiClient, dataRequest)
            .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    if (!dataItemResult.getStatus().isSuccess()) {
                        Log.e(TAG, "Failed to set the data, "
                                + "status: " + dataItemResult.getStatus()
                                .getStatusCode());
                    } else {
                        Log.e(TAG, "Data succesfully inserted");
                    }
                }
            });
    }
}
