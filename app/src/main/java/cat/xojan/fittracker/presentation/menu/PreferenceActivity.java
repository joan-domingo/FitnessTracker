package cat.xojan.fittracker.presentation.menu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import cat.xojan.fittracker.R;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_preferences_toolbar);
        toolbar.setTitle(R.string.settings);
        toolbar.setNavigationOnClickListener(v -> finish());

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new UserSettingFragment())
                .commit();
    }
}
