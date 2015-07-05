package cat.xojan.fittracker.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.result.SessionReadResult;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.main.ActivityType;
import cat.xojan.fittracker.util.Utils;
import cat.xojan.fittracker.view.presenter.SessionPresenter;

public class StartUpActivity extends BaseActivity
        implements UiContentUpdater,
        DateSelectedListener {

    @Inject
    SessionPresenter mSessionPresenter;

    @Bind(R.id.startup_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.date_range_end)
    Button mDateEndButton;
    @Bind(R.id.date_range_start)
    Button mDateStartButton;
    @Bind(R.id.my_awesome_toolbar)
    Toolbar toolbar;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);

        showProgress();
        setUpView();
    }

    @Override
    protected void onFitnessClientConnected(GoogleApiClient fitnessClient) {
        mSessionPresenter.readSessions(fitnessClient, this);
    }

    private void setUpView() {
        setSupportActionBar(toolbar);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDateStartButton.setText(Utils.getRightDate(mSessionPresenter.getStartTime(), this));
        mDateEndButton.setText(Utils.getRightDate(mSessionPresenter.getEndTime(), this));
    }

    public void setSessionData(SessionReadResult sessionReadResult) {
        mRecyclerView.setAdapter(new SessionAdapter(getBaseContext(), sessionReadResult));
        dismissProgress();
    }

    @OnClick(R.id.date_range_start)
    public void openStartDateCalendar(Button button) {
        showFragment(mSessionPresenter.getStartTime(), button);
    }

    @OnClick(R.id.date_range_end)
    public void openEndDateCalendar(Button button) {
        showFragment( mSessionPresenter.getEndTime(), button);
    }

    @OnClick(R.id.fab_add)
    public void newWorkout(ImageButton imageButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_activity)
                .setItems(ActivityType.getStringArray(getBaseContext()), (dialog, which) -> {
                    String activity = ActivityType.values()[which].getActivity();
                    //TODO set fitness activity
                    //GOTO workout activity
                });
        builder.create().show();
    }

    private void showFragment(long time, Button button) {
        mButton = button;
        DialogFragment newFragment = DatePickerFragment.newInstance(time);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(long time) {
        mButton.setText(Utils.getRightDate(time, this));
        if (mButton == mDateEndButton) {
            mSessionPresenter.setEndTime(time);
        } else {
            mSessionPresenter.setStartTime(time);
        }
        mSessionPresenter.readSessions(getFitnessCient(), this);
        showProgress();
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
}