package cat.xojan.fittracker.presentation.startup;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.injection.StartupModule;
import cat.xojan.fittracker.presentation.BaseActivity;

/**
 * Start up activity. Shows splash screen and ask for google fit permissions if necessary.
 */
public class StartupActivity extends BaseActivity {

    @Inject
    StartupPresenter mPresenter;

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new StartupModule(this));
    }

    @Override
    protected void onGoogleApiClientConnected(GoogleApiClient googleApiClient) {
        mPresenter.updateUserFitnessData(googleApiClient);
    }
}
