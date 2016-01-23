package cat.xojan.fittracker.presentation.activity;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.domain.ActivityType;
import cat.xojan.fittracker.modules.SaveSessionModule;
import cat.xojan.fittracker.presentation.controller.FitnessController;
import cat.xojan.fittracker.presentation.presenter.SessionDataPresenter;

public class SaveSessionActivity extends BaseActivity
        implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Inject
    SessionDataPresenter mSessionDataPresenter;
    private GoogleApiClient mGoogleApiClient;
    private FitnessController mFitnessController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
        setContentView(R.layout.activity_save);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mFitnessController = FitnessController.getInstance();

        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {

                finish();
            }
        }, 3000);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSessionDataPresenter.saveSessionData(mGoogleApiClient, getPutDataRequest());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new SaveSessionModule(this));
    }

    private PutDataRequest getPutDataRequest() {
        return mFitnessController.getSessionData(getName(), getDescription());
    }

    private String getName() {
        return getString(R.string.workout) + " "
                + millisToDay(Calendar.getInstance().getTimeInMillis());
    }

    private String getDescription() {
        return getString(ActivityType
                .getRightLanguageString(mFitnessController.getFitnessActivity()));
    }

    private static String millisToDay(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(timeInMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
