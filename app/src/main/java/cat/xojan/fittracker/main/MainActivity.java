package cat.xojan.fittracker.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import cat.xojan.fittracker.BaseActivity;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.main.fragments.SessionListFragment;
import cat.xojan.fittracker.menu.AttributionFragment;
import cat.xojan.fittracker.menu.PreferenceActivity;

public class MainActivity extends BaseActivity {

    @Inject
    SessionListFragment sessionListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void setFragment() {
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

        if (workoutFragment == null || resultFragment == null || getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, sessionListFragment, Constant.LIST_FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }

    }

    private void showSettingsDialog() {
        String[] unitArray = getResources().getStringArray(R.array.measure_unit_entries);
        final String[] unitArrayValues = getResources().getStringArray(R.array.measure_unit_entries_values);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_unit)
                .setItems(unitArray, (dialog, which) -> {
                    String unit = unitArrayValues[which];
                    getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                            .edit()
                            .putString(Constant.PREFERENCE_MEASURE_UNIT, unit)
                            .commit();
                    showDateFormatDialog();
                });
        builder.create().show();
    }

    private void showDateFormatDialog() {
        String[] dateFormatEntries = getResources().getStringArray(R.array.date_format_entries);
        final String[] dateFormatEntriesValues = getResources().getStringArray(R.array.date_format_entries_values);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_date_format)
                .setItems(dateFormatEntries, (dialog, which) -> {
                    String dateFormat = dateFormatEntriesValues[which];
                    getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(Constant.PREFERENCE_FIRST_RUN, false)
                            .putString(Constant.PREFERENCE_DATE_FORMAT, dateFormat)
                            .commit();
                    addInitialFragment();
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
    public void onBackPressed() {
        Fragment workoutFragment = getSupportFragmentManager().findFragmentByTag(Constant.WORKOUT_FRAGMENT_TAG);
        Fragment resultFragment = getSupportFragmentManager().findFragmentByTag(Constant.RESULT_FRAGMENT_TAG);

        if (workoutFragment == null || resultFragment == null) {
            super.onBackPressed();
        }
    }
}
