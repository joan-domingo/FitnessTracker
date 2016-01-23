package cat.xojan.fittracker.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import java.util.Collections;
import java.util.List;

import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.presentation.menu.AttributionActivity;
import cat.xojan.fittracker.presentation.menu.PreferenceActivity;
import dagger.ObjectGraph;

public class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private GoogleApiClient mClient;
    private ObjectGraph activityGraph;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Put application specific code here.
        FitTrackerApp application = (FitTrackerApp) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());

        // Inject ourselves so subclasses will have dependencies fulfilled when this method returns.
        activityGraph.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();
    }

    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.SESSIONS_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, 0, this)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google api client connected");
        // Now you can make calls to the Fitness APIs.
        onGoogleApiClientConnected(mClient);
    }

    protected void onGoogleApiClientConnected(GoogleApiClient googleApiClient) {}

    @Override
    public void onConnectionSuspended(int i) {
        // If your connection to the sensor gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Google Play services connection failed. Cause: " +
                connectionResult.toString());
        Toast.makeText(this, "Exception while connecting to Google Play services: " +
                connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Collections.emptyList();
    }

    protected void showProgress() {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.wait));
    }

    protected void dismissProgress() {
        mProgressDialog.dismiss();
    }

    protected GoogleApiClient getGoogleApiClient() {
        return mClient;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, PreferenceActivity.class);
                startActivity(settingsIntent);
                break;
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
            case R.id.action_attributions:
                Intent attributionIntent = new Intent(this, AttributionActivity.class);
                startActivity(attributionIntent);
                break;
            case R.id.action_music:
                long eventtime = SystemClock.uptimeMillis();
                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }
}
