package cat.xojan.fittracker.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import cat.xojan.fittracker.view.activity.StartUpActivity;

public class WearableListener extends WearableListenerService {

    private static final String TAG = WearableListener.class.getSimpleName();
    private static final String LAUNCH_HANDHELD_APP = "/launch_handheld_app";
    private static final String SAVE_SESSION = "/save_session";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, "onMessageReceived: " + messageEvent);

        if (LAUNCH_HANDHELD_APP.equals(messageEvent.getPath())) {
            Intent i = new Intent();
            i.setClass(this, StartUpActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else if (SAVE_SESSION.equals(messageEvent.getPath())) {
            Intent i = new Intent();
            i.setClass(this, StartUpActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}
