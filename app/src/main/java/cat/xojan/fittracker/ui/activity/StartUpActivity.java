package cat.xojan.fittracker.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.daggermodules.StartUpModule;
import cat.xojan.fittracker.domain.ActivityType;
import cat.xojan.fittracker.ui.adapter.SessionAdapter;
import cat.xojan.fittracker.ui.fragment.DatePickerFragment;
import cat.xojan.fittracker.ui.listener.DateSelectedListener;
import cat.xojan.fittracker.ui.listener.UiContentUpdater;
import cat.xojan.fittracker.ui.presenter.SessionPresenter;
import cat.xojan.fittracker.ui.presenter.UnitDataPresenter;
import cat.xojan.fittracker.util.Utils;

public class StartUpActivity extends BaseActivity
        implements UiContentUpdater,
        DateSelectedListener {

    @Inject
    SessionPresenter mSessionPresenter;
    @Inject
    UnitDataPresenter mUnitDataPresenter;

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

        if (mUnitDataPresenter.showFirstRunSettingsDialogs(this)) {
            showSettingsDialog();
            mUnitDataPresenter.FirstRunDone(this);
        }

        showProgress();
        setUpView();
    }

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new StartUpModule(this));
    }

    @Override
    protected void onFitnessClientConnected(GoogleApiClient fitnessClient) {
        mSessionPresenter.setEndTime(Calendar.getInstance().getTimeInMillis());
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
                    startWorkoutActivity(ActivityType.values()[which].getActivity());
                });
        builder.create().show();
    }

    private void startWorkoutActivity(String activity) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra(WorkoutActivity.FITNESS_ACTIVITY, activity);
        startActivity(intent);
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

    private void showSettingsDialog() {
        String[] unitArray = getResources().getStringArray(R.array.measure_unit_entries);
        final String[] unitArrayValues = getResources().getStringArray(R.array.measure_unit_entries_values);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_unit)
                .setItems(unitArray, (dialog, which) -> {
                    mUnitDataPresenter.saveMeasureUnit(unitArrayValues[which], this);
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
                    mUnitDataPresenter.saveDateFormat(dateFormatEntriesValues[which], this);
                });
        builder.create().show();
    }
}