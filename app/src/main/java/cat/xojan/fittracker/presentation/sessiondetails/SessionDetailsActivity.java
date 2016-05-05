package cat.xojan.fittracker.presentation.sessiondetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.domain.ActivityType;

/**
 * Workout details.
 */
public class SessionDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_ACTIVITY = "extra_activity";

    @Bind(R.id.text)
    TextView mTitle;
    @Bind(R.id.activity)
    ImageView mFitnessActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        mTitle.setText(extras.getString(EXTRA_TITLE));
        mFitnessActivity.setBackground(getResources()
                .getDrawable(ActivityType.toDrawable(extras.getString(EXTRA_ACTIVITY))));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
