package cat.xojan.fittracker.presentation.activity;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import java.util.Collections;
import java.util.List;

import cat.xojan.fittracker.WearFitTrackerApp;
import dagger.ObjectGraph;

public class BaseActivity extends WearableActivity {

    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WearFitTrackerApp application = (WearFitTrackerApp) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());

        // Inject ourselves so subclasses will have dependencies fulfilled when this method returns.
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Collections.emptyList();
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }
}
