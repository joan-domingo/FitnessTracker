package cat.xojan.fittracker;

import com.google.android.gms.fitness.request.SessionInsertRequest;

import javax.inject.Inject;

import cat.xojan.fittracker.main.controllers.FitnessController;

public class SaveSessionActivity extends BaseActivity {

    public static final String PARAM_INSERT_REQUEST = "session_insert_request";

    @Inject
    FitnessController mFitnessController;

    @Override
    protected void setFragment() {
        SessionInsertRequest sessionInsertRequest = getIntent().getParcelableExtra(PARAM_INSERT_REQUEST);
    }
}
