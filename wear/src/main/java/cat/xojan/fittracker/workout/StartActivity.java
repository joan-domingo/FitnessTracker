package cat.xojan.fittracker.workout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import cat.xojan.fittracker.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        String activyType = getIntent().getStringExtra("EXTRA_ACTIVITY_TYPE");

        ImageView icon = (ImageView) findViewById(R.id.icon);
        switch (activyType) {
            case "Running":
                icon.setImageResource(R.drawable.ic_running30);
                break;
            case "Walking":
                icon.setImageResource(R.drawable.ic_walking3);
                break;
            case "Biking":
                icon.setImageResource(R.drawable.ic_biking2);
                break;
        }
    }
}
