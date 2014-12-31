package cat.xojan.fittracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.SessionListFragment;
import cat.xojan.fittracker.settings.PreferenceActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControllers(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(Constant.PREFERENCE_FIRST_RUN, true))
            showSettingsDialog();
        initView();
    }

    private void showSettingsDialog() {
        String[] unitArray = {getText(R.string.kilometres_metres).toString(), getText(R.string.miles_feet).toString()};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_unit)
                .setItems(unitArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String unit = which == 0 ? Constant.DISTANCE_MEASURE_KM : Constant.DISTANCE_MEASURE_MILE;
                        getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean(Constant.PREFERENCE_FIRST_RUN, false)
                                .putString(Constant.PREFERENCE_MEASURE_UNIT, unit)
                                .commit();
                    }
                });
        builder.create().show();
    }

    private void initView() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new SessionListFragment())
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
        FitnessController.getInstance().onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FitnessController.getInstance().onSaveInstanceState(outState);
    }
}
