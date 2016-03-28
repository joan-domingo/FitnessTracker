package cat.xojan.fittracker.presentation.startup;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.UserData;
import cat.xojan.fittracker.injection.component.DaggerStartupComponent;
import cat.xojan.fittracker.injection.component.StartupComponent;
import cat.xojan.fittracker.injection.module.StartupModule;
import cat.xojan.fittracker.navigation.Navigator;
import cat.xojan.fittracker.presentation.BaseActivity;

/**
 * Start up activity. Shows splash screen and ask for google fit permissions if necessary.
 */
public class StartupActivity extends BaseActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Inject
    UserData mUserData;
    @Inject
    StartupPresenter mPresenter;
    @Inject
    Navigator mNavigator;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.error_message)
    TextView mErrorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);

        initInjector();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mErrorView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mNavigator.navigateToHomeActivity(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initInjector() {
        StartupComponent component = DaggerStartupComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .startupModule(new StartupModule())
                .build();
        component.inject(this);
    }
}
