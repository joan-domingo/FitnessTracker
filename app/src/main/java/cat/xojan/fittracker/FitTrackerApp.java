package cat.xojan.fittracker;

import android.app.Application;

import cat.xojan.fittracker.injection.component.AppComponent;
import cat.xojan.fittracker.injection.component.DaggerAppComponent;
import cat.xojan.fittracker.injection.module.AppModule;

public class FitTrackerApp extends Application {

    private AppComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mComponent;
    }
}
