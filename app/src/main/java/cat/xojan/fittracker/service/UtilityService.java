package cat.xojan.fittracker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class UtilityService extends IntentService {

    private static GoogleApiClient mGoogleApiClient;

    public static final int GOOGLE_API_CLIENT_TIMEOUT_S = 10; // 10 seconds

    public static final String SEND_CLIENT = "/send_client";
    private static final String EXTRA_START_PATH = "start_path";
    private static final String ACTION_SEND_GOOGLE_CLIENT = "send_google_client";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public UtilityService() {
        super("UtilityService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("UtilityService", "app: Handling Intent");
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_SEND_GOOGLE_CLIENT.equals(action)) {
            sendGoogleApiClientInternal(intent.getStringExtra(EXTRA_START_PATH));
        }
    }

    private void sendGoogleApiClientInternal(String path) {
        GoogleApiClient googleApiClient = mGoogleApiClient;

        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {
            // Loop through all nodes and send the message
            for (String s : getNodes(googleApiClient)) {
                Wearable.MessageApi.sendMessage(
                        googleApiClient, s, path, null);
            }
            googleApiClient.disconnect();
        }
    }

    public static void sendGoogleApiClient(Context context, String path, GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_SEND_GOOGLE_CLIENT);
        intent.putExtra(EXTRA_START_PATH, path);
        context.startService(intent);
    }

    /**
     * Get a list of all wearable nodes that are connected synchronously.
     * Only call this method from a background thread (it should never be
     * called from the main/UI thread as it blocks).
     */
    public static Collection<String> getNodes(GoogleApiClient client) {
        Collection<String> results= new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(client).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }
}
