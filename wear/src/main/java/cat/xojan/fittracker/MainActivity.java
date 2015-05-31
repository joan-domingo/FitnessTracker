package cat.xojan.fittracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import cat.xojan.fittracker.main.MainGridPagerAdapter;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
        setContentView(R.layout.activity_main);

        final Resources res = getResources();

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                pager.setPageMargins(rowMargin, colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                pager.onApplyWindowInsets(insets);
                return insets;
            }
        });

        pager.setAdapter(new MainGridPagerAdapter(getFragmentManager()));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityDispatcher();
    }

    private void ActivityDispatcher() {
        Class<?> activityClass;
        SharedPreferences prefs = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        try {
            activityClass = Class.forName(prefs.getString(Constant.LAST_ACTIVITY, ""));
            if (!activityClass.getName().equals(MainActivity.class.getName())) {
                startActivity(new Intent(this, activityClass));
            }
        } catch (ClassNotFoundException e) {}
    }
}
