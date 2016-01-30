package cat.xojan.fittracker.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import cat.xojan.fittracker.FitTrackerApp;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.injection.component.AppComponent;
import cat.xojan.fittracker.injection.module.BaseActivityModule;
import cat.xojan.fittracker.presentation.menu.AttributionActivity;
import cat.xojan.fittracker.presentation.menu.PreferenceActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectComponent(((FitTrackerApp) getApplication()).getAppComponent(),
                new BaseActivityModule(this));
    }

    protected abstract void injectComponent(AppComponent appComponent,
                                            BaseActivityModule baseActivityModule);

    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.wait));
    }

    protected void dismissProgress() {
        mProgressDialog.dismiss();
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
                Intent attributionIntent = new Intent(this, AttributionActivity.class);
                startActivity(attributionIntent);
                break;
            case R.id.action_music:
                long eventtime = SystemClock.uptimeMillis();
                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
