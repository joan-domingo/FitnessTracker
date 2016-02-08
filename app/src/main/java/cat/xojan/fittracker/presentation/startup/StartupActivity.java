package cat.xojan.fittracker.presentation.startup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Session;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.injection.component.DaggerStartupComponent;
import cat.xojan.fittracker.injection.component.StartupComponent;
import cat.xojan.fittracker.injection.module.StartupModule;
import cat.xojan.fittracker.presentation.BaseActivity;

/**
 * Start up activity. Shows splash screen and ask for google fit permissions if necessary.
 */
public class StartupActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FitnessDataListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Inject
    UserData mUserData;
    @Inject
    StartupPresenter mPresenter;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.error_message)
    TextView mErrorView;

    private GoogleApiClient mClient;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google api client connected");
        // Now you can make calls to the Fitness APIs.
        onGoogleApiClientConnected(mClient);
    }

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
    public void onSuccessfullyUpdated(List<Session> sessions) {
        mUserData.setFitnessSessions(sessions);
        mNavigator.navigateToHomeActivity(this);
        finish();
    }

    @Override
    public void onError(Throwable e) {
        mProgressBar.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setText(e.getMessage());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);

        initInjector();
        initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mErrorView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();
    }

    @Override
    protected void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initInjector() {
        StartupComponent component = DaggerStartupComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .startupModule(new StartupModule())
                .build();
        component.inject(this);
    }

    private void initPresenter() {
        mPresenter.setListener(this);
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

    private void onGoogleApiClientConnected(GoogleApiClient googleApiClient) {
        mPresenter.updateUserFitnessData(googleApiClient);
    }
}
