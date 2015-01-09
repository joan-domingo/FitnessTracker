package cat.xojan.fittracker.workout;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;

import cat.xojan.fittracker.R;

public class NotificationController {

    private static NotificationController instance = null;

    public static NotificationController getInstance() {
        if (instance == null) {
            return new NotificationController();
        } else {
            return instance;
        }
    }

    public void showNotification(Context context) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.ic_launcher))
                        .setContentTitle(context.getText(R.string.app_name))
                        .setContentText(context.getText(R.string.app_name) + " " + context.getText(R.string.is_tracking_you))
                        .setOngoing(true)
                        .setAutoCancel(false);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = ((ActionBarActivity) context).getIntent();

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void dismissNotification(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.cancel(0);
    }
}
