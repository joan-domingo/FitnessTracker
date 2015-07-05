package cat.xojan.fittracker.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import java.util.Collections;
import java.util.List;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.daggermodules.SessionModule;
import cat.xojan.fittracker.menu.AttributionFragment;
import cat.xojan.fittracker.menu.PreferenceActivity;
import dagger.ObjectGraph;

public class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_OAUTH = 1;
    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mFitnessClient;
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

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();
    }

    private void buildFitnessClient() {
        mFitnessClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(Constant.TAG, "Fitness client Connected!!!");
        onFitnessClientConnected(mFitnessClient);
    }

    protected void onFitnessClientConnected(GoogleApiClient mFitnessClient) {}

    @Override
    public void onConnectionSuspended(int i) {
        // If your connection to the sensor gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(Constant.TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(Constant.TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(Constant.TAG, "Connection failed. Cause: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // Show the localized error dialog
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                    BaseActivity.this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization dialog is displayed to the user.
        if (!authInProgress) {
            try {
                Log.i(Constant.TAG, "Attempting to resolve failed connection");
                authInProgress = true;
                connectionResult.startResolutionForResult(BaseActivity.this,
                        REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Log.e(Constant.TAG,
                        "Exception while starting resolution activity", e);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(Constant.TAG, "Connecting...");
        mFitnessClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFitnessClient.isConnected()) {
            mFitnessClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mFitnessClient.isConnecting() && !mFitnessClient.isConnected()) {
                    mFitnessClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Collections.singletonList(new SessionModule(this));
    }

    protected void showProgress() {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.wait));
    }

    protected void dismissProgress() {
        mProgressDialog.dismiss();
    }

    protected GoogleApiClient getFitnessCient() {
        return mFitnessClient;
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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AttributionFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.action_music:
                long eventtime = SystemClock.uptimeMillis();
                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
