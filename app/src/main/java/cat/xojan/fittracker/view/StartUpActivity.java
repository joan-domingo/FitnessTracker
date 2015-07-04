package cat.xojan.fittracker.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.result.SessionReadResult;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.view.presenter.SessionPresenter;

public class StartUpActivity extends BaseActivity implements UiContentUpdater {

    @Inject
    SessionPresenter mSessionPresenter;

    @Bind(R.id.startup_recycler_view)
    RecyclerView mRecyclerView;

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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void setSessionData(SessionReadResult sessionReadResult) {
        mRecyclerView.setAdapter(new SessionAdapter(getBaseContext(), sessionReadResult));
        dismissProgress();
    }
}
