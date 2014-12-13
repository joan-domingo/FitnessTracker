package cat.xojan.fittracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import cat.xojan.fittracker.googlefit.FitnessController;


public class MainActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static Handler handler = null;
    private static MainActivity instance = null;

    public MainActivity() {
    }

    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }

    public static Handler getHandler() {
        return MainActivity.instance.handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.sessions_list);
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Context context = this;
        // specify an adapter
        handler = new Handler () {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.MESSAGE_SESSIONS_READ:
                        mAdapter = new SessionAdapter(FitnessController.getInstance().getReadSessions(), context);
                        mRecyclerView.setAdapter(mAdapter);
                        break;
                }
            }
        };

        initControllers(savedInstanceState);
    }

    private void initControllers(Bundle savedInstanceState) {
        //google fit
        FitnessController.getInstance().setVars(this);
        FitnessController.getInstance().setAuthInProgress(savedInstanceState);
        FitnessController.getInstance().init();
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
}
