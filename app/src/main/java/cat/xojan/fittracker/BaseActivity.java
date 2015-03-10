package cat.xojan.fittracker;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.controller.FitnessController;
import cat.xojan.fittracker.daggermodules.MainModule;
import dagger.ObjectGraph;

public abstract class BaseActivity extends ActionBarActivity {

    private ObjectGraph activityGraph;
    private boolean authInProgress = false;
    private GoogleApiClient mClient;

    @Inject
    FitnessController fitnessController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the activity graph by .plus-ing our modules onto the application graph.
        FitTrackerApp application = (FitTrackerApp) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());

        // Inject ourselves so subclasses will have dependencies fulfilled when this method returns.
        activityGraph.inject(this);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(Constant.AUTH_PENDING);
        }

        buildFitnessClient();
    }

    private void buildFitnessClient() {
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(Constant.TAG, "Connected!!!");
                                fitnessController.setClient(mClient);
                                setFragment();
                            }

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
                        }
                )
                .addOnConnectionFailedListener(
                        result -> {
                            Log.i(Constant.TAG, "Connection failed. Cause: " + result.toString());
                            if (!result.hasResolution()) {
                                // Show the localized error dialog
                                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
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
                                    result.startResolutionForResult(BaseActivity.this,
                                            Constant.REQUEST_OAUTH);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e(Constant.TAG,
                                            "Exception while starting resolution activity", e);
                                }
                            }
                        }
                )
                .build();
    }

    protected abstract void setFragment();

    @Override
    protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Arrays.asList(new MainModule(this));
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(Constant.TAG, "Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            } else if (!mClient.isConnected()) {
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constant.AUTH_PENDING, authInProgress);
    }
}
