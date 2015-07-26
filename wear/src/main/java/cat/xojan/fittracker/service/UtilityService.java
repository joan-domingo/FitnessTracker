package cat.xojan.fittracker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.R;

public class UtilityService extends IntentService {

    private static final String TAG = "UtilityService";

    public static final String LAUNCH_HANDHELD_APP = "/launch_handheld_app";

    private static final String ACTION_START_DEVICE_ACTIVITY = "start_device_activity";

    private static final String EXTRA_START_PATH = "start_path";

    private static final long GET_CAPABILITY_TIMEOUT_S = 10;

    public static final int GOOGLE_API_CLIENT_TIMEOUT_S = 10; // 10 seconds

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UtilityService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "wear: Handling Intent");
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_START_DEVICE_ACTIVITY.equals(action)) {
            startDeviceActivityInternal(intent.getStringExtra(EXTRA_START_PATH));
        }
    }

    /**
     * Trigger a message that asks the master device to start an activity.
     *
     * @param context the context
     * @param path the path that will be sent via the wearable message API
     */
    public static void startDeviceActivity(Context context, String path) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_START_DEVICE_ACTIVITY);
        intent.putExtra(EXTRA_START_PATH, path);
        context.startService(intent);
    }

    /**
     * Sends the actual message to the handheld app
     *
     * @param path the path to pass to the wearable message API
     */
    private void startDeviceActivityInternal(String path) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {
            CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(
                    googleApiClient,
                    getApplicationContext().getString(R.string.start_handheld_app_capability_name),
                    CapabilityApi.FILTER_REACHABLE)
                    .await(GET_CAPABILITY_TIMEOUT_S, TimeUnit.SECONDS);
            if (result.getStatus().isSuccess()) {
                Set<Node> nodes = result.getCapability().getNodes();
                for (Node node : nodes) {
                    Wearable.MessageApi.sendMessage(
                            googleApiClient, node.getId(), path, null);
                }
            } else {
                Log.e(TAG, "startDeviceActivityInternal() Failed to get capabilities, status: "
                        + result.getStatus().getStatusMessage());
            }

            googleApiClient.disconnect();
        }
    }
}
