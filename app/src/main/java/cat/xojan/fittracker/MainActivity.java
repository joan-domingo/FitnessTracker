package cat.xojan.fittracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.SessionListFragment;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControllers(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new SessionListFragment())
                .addToBackStack(Constant.TAG_SESSION_LIST)
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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

//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().findFragmentByTag(Constant.TAG_WORKOUT) == null)
//            super.onBackPressed();
//    }
}
