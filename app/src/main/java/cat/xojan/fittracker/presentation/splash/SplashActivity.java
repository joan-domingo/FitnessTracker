package cat.xojan.fittracker.presentation.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.injection.component.AppComponent;
import cat.xojan.fittracker.injection.component.DaggerSplashComponent;
import cat.xojan.fittracker.injection.component.SplashComponent;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.injection.module.SplashModule;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.presentation.home.HomeActivity;
import cat.xojan.fittracker.presentation.startup.StartupActivity;

/**
 * Displays splash screen. The splash view has to be ready immediately.
 * The splash screen’s background is specified in the activity’s theme background.
 */
public class SplashActivity extends BaseActivity {

    @Inject
    UserData mUserData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiClient googleClient = mUserData.getGoogleApiClient();
        Intent intent;

        if (googleClient == null) {
            intent = new Intent(this, StartupActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void injectComponent(AppComponent appComponent,
                                   BaseActivityModule baseActivityModule) {
        SplashComponent component = DaggerSplashComponent.builder()
                .appComponent(appComponent)
                .baseActivityModule(baseActivityModule)
                .splashModule(new SplashModule())
                .build();

        component.inject(this);
    }
}