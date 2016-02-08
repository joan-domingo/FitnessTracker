package cat.xojan.fittracker.presentation.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import javax.inject.Inject;

import cat.xojan.fittracker.BuildConfig;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.injection.component.DaggerSplashComponent;
import cat.xojan.fittracker.injection.component.SplashComponent;
import cat.xojan.fittracker.injection.module.SplashModule;
import cat.xojan.fittracker.navigation.Navigator;
import cat.xojan.fittracker.presentation.BaseActivity;

/**
 * Displays splash screen. The splash view has to be ready immediately.
 * The splash screen’s background is specified in the activity’s theme background.
 */
public class SplashActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 30;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Inject
    Navigator mNavigator;
    private boolean mHasPermissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeInjector();
        checkPermissions();
    }

    private void initializeInjector() {
        SplashComponent component = DaggerSplashComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .splashModule(new SplashModule())
                .build();

        component.inject(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted,
                // the permission request is cancelled and you
                // receive empty arrays.
            } else {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions were granted.
                    grantPermissions();
                } else {
                    // permission denied.
                    showDeniedPermissionNotification();
                }
            }
        }
    }

    private void grantPermissions() {
        mHasPermissions = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //workaround for https://code.google.com/p/android-developer-preview/issues/detail?id=2823
        if (mHasPermissions) {
            goToStartup();
            mHasPermissions = false;
        }
    }

    private void checkPermissions() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        } else {
            goToStartup();
        }
    }

    private void goToStartup() {
        mNavigator.navigateToStartupActivity(this);
        finish();
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showDeniedPermissionNotification() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                R.string.permissions_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        snackbar.show();
    }
}