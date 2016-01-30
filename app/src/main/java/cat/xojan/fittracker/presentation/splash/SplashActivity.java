package cat.xojan.fittracker.presentation.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cat.xojan.fittracker.presentation.startup.StartupActivity;

/**
 * Displays splash screen. The splash view has to be ready immediately.
 * The splash screen’s background is specified in the activity’s theme background.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
        finish();
    }
}
