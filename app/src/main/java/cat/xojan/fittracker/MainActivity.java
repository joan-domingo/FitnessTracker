package cat.xojan.fittracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.menu.AttributionFragment;
import cat.xojan.fittracker.session.SessionListFragment;
import cat.xojan.fittracker.menu.PreferenceActivity;


public class MainActivity extends ActionBarActivity {

    private SessionListFragment mSessionListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControllers(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSessionListFragment = new SessionListFragment();

        if (getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(Constant.PREFERENCE_FIRST_RUN, true)) {
            showSettingsDialog();
        }
        initView();
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

                        getSupportFragmentManager().beginTransaction()
                                .detach(mSessionListFragment)
                                .attach(mSessionListFragment)
                                .commit();
                    }
                });
        builder.create().show();
    }

    private void initView() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mSessionListFragment)
                .commit();
    }

    private void initControllers(Bundle savedInstanceState) {
        //google fit
        FitnessController.getInstance().setVars(this);
        FitnessController.getInstance().setAuthInProgress(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(false);

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
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);
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
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FitnessController.getInstance().connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FitnessController.getInstance().disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            //if no email account is selected
            finish();
        }
        FitnessController.getInstance().onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FitnessController.getInstance().onSaveInstanceState(outState);
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
