package cat.xojan.fittracker.presentation.home;

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
import cat.xojan.fittracker.injection.HomeModule;
import cat.xojan.fittracker.domain.ActivityType;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.presentation.activity.WorkoutActivity;
import cat.xojan.fittracker.presentation.adapter.SessionAdapter;
import cat.xojan.fittracker.presentation.fragment.DatePickerFragment;
import cat.xojan.fittracker.presentation.listener.DateSelectedListener;
import cat.xojan.fittracker.presentation.listener.UiContentUpdater;
import cat.xojan.fittracker.presentation.presenter.SessionPresenter;
import cat.xojan.fittracker.presentation.presenter.UnitDataPresenter;
import cat.xojan.fittracker.util.Utils;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new HomeModule(this));
    }
}