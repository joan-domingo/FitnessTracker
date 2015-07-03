package cat.xojan.fittracker.service;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import cat.xojan.fittracker.SaveSessionActivity;

public class NodeListenerService extends WearableListenerService {

    private static final String TAG = "NodeListenerService";
    private static final String SEND_CLIENT = "/send_client";

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "You have been disconnected.");
        if(!hasGps()) {
            // Notify user to bring tethered handset
            // Fall back to functionality that does not use location
            //TODO
        }
    }

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, "onMessageReceived: " + messageEvent);

        if (SEND_CLIENT.equals(messageEvent.getPath())) {
            Intent i = new Intent();
            i.setClass(this, SaveSessionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}
