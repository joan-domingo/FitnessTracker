package cat.xojan.fittracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.menu.AttributionFragment;
import cat.xojan.fittracker.menu.PreferenceActivity;
import cat.xojan.fittracker.sessionlist.SessionListFragment;


public class MainActivity extends ActionBarActivity {

    private boolean authInProgress = false;
    private GoogleApiClient mClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                                    FitnessController.getInstance().setVars(MainActivity.this, mClient);
                                    setInitialFragment();
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
                            new GoogleApiClient.OnConnectionFailedListener() {
                                // Called whenever the API client fails to connect.
                                @Override
                                public void onConnectionFailed(ConnectionResult result) {
                                    Log.i(Constant.TAG, "Connection failed. Cause: " + result.toString());
                                    if (!result.hasResolution()) {
                                        // Show the localized error dialog
                                        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                                MainActivity.this, 0).show();
                                        return;
                                    }
                                    // The failure has a resolution. Resolve it.
                                    // Called typically when the app is not yet authorized, and an
                                    // authorization dialog is displayed to the user.
                                    if (!authInProgress) {
                                        try {
                                            Log.i(Constant.TAG, "Attempting to resolve failed connection");
                                            authInProgress = true;
                                            result.startResolutionForResult(MainActivity.this,
                                                    Constant.REQUEST_OAUTH);
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.e(Constant.TAG,
                                                    "Exception while starting resolution activity", e);
                                        }
                                    }
                                }
                            }
                    )
                    .build();
    }

    private void setInitialFragment() {
        if (getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(Constant.PREFERENCE_FIRST_RUN, true)) {
            showSettingsDialog();
        } else {
            addInitialFragment();
        }
    }

    private void addInitialFragment() {
        Fragment workoutFragment = getSupportFragmentManager().findFragmentByTag(Constant.WORKOUT_FRAGMENT_TAG);
        Fragment resultFragment = getSupportFragmentManager().findFragmentByTag(Constant.RESULT_FRAGMENT_TAG);

        if (workoutFragment != null || resultFragment != null || getSupportFragmentManager().getBackStackEntryCount() > 0) {
            //nothing to do
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SessionListFragment())
                    .commitAllowingStateLoss();
        }

    }

    private void showSettingsDialog() {
        String[] unitArray = getResources().getStringArray(R.array.measure_unit_entries);
        final String[] unitArrayValues = getResources().getStringArray(R.array.measure_unit_entries_values);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_unit)
                .setItems(unitArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String unit = unitArrayValues[which];
                        getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .edit()
                                .putString(Constant.PREFERENCE_MEASURE_UNIT, unit)
                                .commit();
                        showDateFormatDialog();
                    }
                });
        builder.create().show();
    }

    private void showDateFormatDialog() {
        String[] dateFormatEntries = getResources().getStringArray(R.array.date_format_entries);
        final String[] dateFormatEntriesValues = getResources().getStringArray(R.array.date_format_entries_values);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_date_format)
                .setItems(dateFormatEntries, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String dateFormat = dateFormatEntriesValues[which];
                        getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean(Constant.PREFERENCE_FIRST_RUN, false)
                                .putString(Constant.PREFERENCE_DATE_FORMAT, dateFormat)
                                .commit();
                        addInitialFragment();
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(false);
        MenuItem music = menu.findItem(R.id.action_music);
        music.setVisible(false);

        return true;
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
                //TODO
                long eventtime = SystemClock.uptimeMillis();
                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);
                break;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        Fragment workoutFragment = getSupportFragmentManager().findFragmentByTag(Constant.WORKOUT_FRAGMENT_TAG);
        Fragment resultFragment = getSupportFragmentManager().findFragmentByTag(Constant.RESULT_FRAGMENT_TAG);

        if (workoutFragment != null || resultFragment != null) {
            //nothing to do
        } else {
            super.onBackPressed();
        }
    }
}
