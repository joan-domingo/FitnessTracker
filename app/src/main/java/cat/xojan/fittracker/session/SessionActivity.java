package cat.xojan.fittracker.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;

import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.util.Utils;

public class SessionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_activity);

        Log.i(Constant.TAG, "on create session activity");
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String supportedType = DataType.getMimeType(DataType.AGGREGATE_ACTIVITY_SUMMARY);

        if (Intent.ACTION_VIEW.equals(action) &&
                supportedType.compareTo(type) == 0) {

            // Get the intent extras
            long startTime = Fitness.getStartTime(intent, TimeUnit.MILLISECONDS);
            Log.i(Constant.TAG, "Start time: " + Utils.millisToTime(startTime));
            long endTime = Fitness.getEndTime(intent, TimeUnit.MILLISECONDS);
            Log.i(Constant.TAG, "End time: " + Utils.millisToTime(endTime));
            DataSource dataSource = DataSource.extract(intent);
            Session session = Session.extract(intent);

            // Show the session in your app

        }
    }
}
