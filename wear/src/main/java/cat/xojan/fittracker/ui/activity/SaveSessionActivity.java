package cat.xojan.fittracker.ui.activity;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.modules.SaveSessionModule;
import cat.xojan.fittracker.ui.controller.FitnessController;
import cat.xojan.fittracker.ui.presenter.SessionDataPresenter;

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
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mFitnessController = FitnessController.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
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
        return mFitnessController.getSessionData();
    }
}
