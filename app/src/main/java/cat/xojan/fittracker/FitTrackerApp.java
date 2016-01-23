package cat.xojan.fittracker;

import android.app.Application;

import java.util.Collections;
import java.util.List;

import cat.xojan.fittracker.injection.AppModule;
import dagger.ObjectGraph;

public class FitTrackerApp extends Application {
    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationGraph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Collections.singletonList(new AppModule(this));
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }


}
